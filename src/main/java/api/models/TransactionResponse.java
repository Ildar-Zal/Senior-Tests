package api.models;


import api.models.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse extends BaseModel {

    private Integer id;
    private BigDecimal amount;
    private TransactionType type;
    private String timestamp;
    private Integer relatedAccountId;
}
