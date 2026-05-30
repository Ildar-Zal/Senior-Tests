package api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse extends BaseModel {

    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private List<Transactions> transactions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class Transactions {
        private Integer id;
        private Double amount;
        private String type;
        private String timestamp;
        private Integer relatedAccountId;
    }

    public boolean isValid() {
        // 1. Проверяем баланс: он не должен быть null или строго равен нулю
        boolean isBalanceReady = balance != null && balance.compareTo(BigDecimal.ZERO) != 0;

        // 2. Проверяем транзакции: список не пустой, и последняя транзакция уже имеет сумму > 0
//        boolean isTransactionsReady = transactions != null && !transactions.isEmpty()
//                && transactions.get(transactions.size() - 1).getAmount() > 0;
//
//        return isBalanceReady && isTransactionsReady;
        return isBalanceReady;
    }
}
