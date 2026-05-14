package models;

import generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositAccountRequest extends BaseModel {

    private Integer id;
    @GeneratingRule(min = 0.01, max = 5000)
    private BigDecimal balance;
}
