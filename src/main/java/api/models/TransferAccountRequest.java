package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferAccountRequest extends BaseModel {

    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;
}
