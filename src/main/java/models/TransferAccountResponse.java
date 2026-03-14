package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferAccountResponse extends BaseModel {

    private BigDecimal amount;
    private String message;
    private Integer senderAccountId;
    private Integer receiverAccountId;
}
