package models;

import generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {

    @GeneratingRule(regex = "^[a-zA-Z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[a-z]{4}[A-Z]{3}[0-9]{3}[$%&]{2}$")
    private String password;
    @GeneratingRule(regex = "^USER$")
    private String role;
}
