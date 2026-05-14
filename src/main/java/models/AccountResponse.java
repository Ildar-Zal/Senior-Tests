package models;

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
public class AccountResponse extends BaseModel {

    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private List<Transactions> transactions;

    @Data
    private static class Transactions {
        private Integer id;
        private Double amount;
        private String type;
        private String timestamp;
        private Integer relatedAccountId;
    }
}
