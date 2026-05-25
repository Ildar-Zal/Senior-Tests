package api.database;

import api.configs.Config;
import api.dao.TransactionDao;
import api.dao.UserDao;
import api.dao.AccountDao;
import api.models.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DBRequest {
    private RequestType requestType;
    private String table;
    private List<Condition> conditions;
    private Class<?> extractAsClass;

    public enum RequestType {
        SELECT, INSERT, UPDATE, DELETE
    }

    public <T> T extractAs(Class<T> clazz) {
        this.extractAsClass = clazz;
        return executeQuery(clazz);
    }

    public <T> List<T> extractAsList(Class<T> clazz) {
        this.extractAsClass = clazz;
        return executeQueryForList(clazz);
    }

    private <T> T executeQuery(Class<T> clazz) {
        String sql = buildSQL();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set parameters for conditions
            if (conditions != null) {
                for (int i = 0; i < conditions.size(); i++) {
                    statement.setObject(i + 1, conditions.get(i).getValue());
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (clazz == UserDao.class) {
                    return (T) mapToUserDao(resultSet);
                }
                if (clazz == AccountDao.class) {
                    return (T) mapToAccountDao(resultSet);
                }
                if (clazz == TransactionDao.class) {
                    return (T) mapToTransactionDao(resultSet);
                }
                // Add more mappings as needed
                throw new UnsupportedOperationException("Mapping for " + clazz.getSimpleName() + " not implemented");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    private <T> List<T> executeQueryForList(Class<T> clazz) {
        String sql = buildSQL();
        List<T> resultList = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (conditions != null) {
                for (int i = 0; i < conditions.size(); i++) {
                    statement.setObject(i + 1, conditions.get(i).getValue());
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                // В цикле читаем ВСЕ строки из базы данных
                while (resultSet.next()) {
                    if (clazz == UserDao.class) {
                        resultList.add((T) mapSingleUser(resultSet)); // ИСПОЛЬЗУЕМ ОДИНОЧНЫЙ МАППЕР
                    } else if (clazz == AccountDao.class) {
                        resultList.add((T) mapSingleAccount(resultSet)); // ИСПОЛЬЗУЕМ ОДИНОЧНЫЙ МАППЕР
                    } else if (clazz == TransactionDao.class) {
                        resultList.add((T) mapSingleTransaction(resultSet)); // ИСПОЛЬЗУЕМ ОДИНОЧНЫЙ МАППЕР
                    } else {
                        throw new UnsupportedOperationException("Mapping for " + clazz.getSimpleName() + " not implemented");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
        return resultList;
    }

    // НОВЫЕ МЕТОДЫ: Просто маппят текущую позицию курсора (БЕЗ resultSet.next())
    private UserDao mapSingleUser(ResultSet resultSet) throws SQLException {
        return UserDao.builder()
                .id(resultSet.getLong("id"))
                .username(resultSet.getString("username"))
                .password(resultSet.getString("password"))
                .role(resultSet.getString("role"))
                .name(resultSet.getString("name"))
                .build();
    }

    private TransactionDao mapSingleTransaction(ResultSet resultSet) throws SQLException {
        String typeStr = resultSet.getString("type");

        // Безопасно переводим String в Enum (если в БД null, то и в DAO запишем null)
        TransactionType transactionType = (typeStr != null) ? TransactionType.valueOf(typeStr) : null;
        return TransactionDao.builder()
                .id(resultSet.getInt("id"))
                .amount(resultSet.getBigDecimal("amount"))
                .type(transactionType)
                .accountId(resultSet.getInt("account_id"))
                .relatedAccountId(resultSet.getInt("related_account_id"))
                .build();
    }

    private AccountDao mapSingleAccount(ResultSet resultSet) throws SQLException {
        return AccountDao.builder()
                .id(resultSet.getLong("id"))
                .accountNumber(resultSet.getString("account_number"))
                .balance(resultSet.getDouble("balance"))
                .customerId(resultSet.getLong("customer_id"))
                .build();
    }

    // СТАРЫЕ МЕТОДЫ (для метода extractAs): Сначала двигают курсор, потом маппят
    private UserDao mapToUserDao(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return mapSingleUser(resultSet);
        }
        return null;
    }

    private TransactionDao mapToTransactionDao(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return mapSingleTransaction(resultSet);
        }
        return null;
    }

    private AccountDao mapToAccountDao(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return mapSingleAccount(resultSet);
        }
        return null;
    }

    private String buildSQL() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT:
                sql.append("SELECT * FROM ").append(table);
                if (conditions != null && !conditions.isEmpty()) {
                    sql.append(" WHERE ");
                    for (int i = 0; i < conditions.size(); i++) {
                        if (i > 0) sql.append(" AND ");
                        sql.append(conditions.get(i).getColumn()).append(" ").append(conditions.get(i).getOperator()).append(" ?");
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Request type " + requestType + " not implemented");
        }

        return sql.toString();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.getProperty("db.url"),
                Config.getProperty("db.username"),
                Config.getProperty("db.password")
        );
    }

    public static DBRequestBuilder builder() {
        return new DBRequestBuilder();
    }

    public static class DBRequestBuilder {
        private RequestType requestType;
        private String table;
        private List<Condition> conditions = new ArrayList<>();
        private Class<?> extractAsClass;

        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public DBRequestBuilder table(String table) {
            this.table = table;
            return this;
        }

        public <T> T extractAs(Class<T> clazz) {
            this.extractAsClass = clazz;
            DBRequest request = DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .extractAsClass(extractAsClass)
                    .build();
            return request.extractAs(clazz);
        }

        public <T> List<T> extractAsList(Class<T> clazz) {
            this.extractAsClass = clazz;
            DBRequest request = DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .extractAsClass(extractAsClass)
                    .build();

            // Вызываем внутренний метод экстракции, который умеет возвращать List
            return request.extractAsList(clazz);
        }
    }
}