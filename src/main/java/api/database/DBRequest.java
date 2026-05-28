package api.database;

import api.configs.Config;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
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

    private <T> T mapSingleRow(ResultSet resultSet, Class<T> clazz) {
        try {
            // 1. Создаем экземпляр класса через конструктор по умолчанию
            T dto = clazz.getDeclaredConstructor().newInstance();

            // 2. Получаем список всех колонок из ResultSet один раз для текущей строки
            List<String> dbColumns = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                dbColumns.add(metaData.getColumnLabel(i).toLowerCase());
            }

            // 3. Идем по всем полям класса (включая private)
            for (Field field : clazz.getDeclaredFields()) {
                // Пропускаем статические и синтетические (сгенерированные компилятором) поля
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }

                field.setAccessible(true);

                // Конвертируем имя поля camelCase -> snake_case (например: accountNumber -> account_number)
                String columnName = camelToSnake(field.getName());

                // Проверяем, есть ли колонка в нашей выборке из БД
                if (dbColumns.contains(columnName)) {
                    Object value = resultSet.getObject(columnName);

                    if (value != null) {
                        // Обработка специфичных типов (Enum, BigDecimal, касты чисел)
                        if (field.getType().isEnum()) {
                            Class<Enum> enumType = (Class<Enum>) field.getType();
                            value = Enum.valueOf(enumType, value.toString());
                        }
                        else if (field.getType() == BigDecimal.class && !(value instanceof BigDecimal)) {
                            value = new BigDecimal(value.toString());
                        }
                        else if (field.getType() == Long.class || field.getType() == long.class) {
                            value = ((Number) value).longValue();
                        }
                        else if (field.getType() == Integer.class || field.getType() == int.class) {
                            value = ((Number) value).intValue();
                        }

                        // Записываем значение в поле объекта
                        field.set(dto, value);
                    }
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map ResultSet to class: " + clazz.getSimpleName(), e);
        }
    }

    private String camelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    private <T> T executeQuery(Class<T> clazz) {
        String sql = buildSQL();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (conditions != null) {
                for (int i = 0; i < conditions.size(); i++) {
                    statement.setObject(i + 1, conditions.get(i).getValue());
                }
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSingleRow(resultSet, clazz);
                }
                return null;
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
                while (resultSet.next()) {
                    resultList.add(mapSingleRow(resultSet, clazz));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
        return resultList;
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

            return request.extractAsList(clazz);
        }
    }
}