package api.dao;

import api.models.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDao {
    private Integer id;
    private BigDecimal amount;
    private TransactionType type;
    private Integer accountId;
    private Integer relatedAccountId;
}
