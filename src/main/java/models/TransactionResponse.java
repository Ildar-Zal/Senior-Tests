package models;

import lombok.*;
import models.enums.TransactionType;

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
