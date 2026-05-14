package api.models;

import api.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerProfileRequest extends BaseModel {

    @GeneratingRule(regex = "[a-zA-Z]{3,10} s[a-zA-Z]{3,10}")
    private String name;
}
