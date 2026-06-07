package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class UpdateCustomerProfileResponse extends BaseModel {

    private String message;

    @JsonUnwrapped
    private Customer customer;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Customer {
        private Integer id;
        private String username;
        private String password;
        private String name;
        private String role;
        private List<Accounts> accounts;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Data
        private static class Accounts {
            private Integer id;
            private String accountNumber;
            private BigDecimal balance;
            private List<Transactions> transactions;

            @JsonIgnoreProperties(ignoreUnknown = true)
            @Data
            private static class Transactions {
                private Integer id;
                private BigDecimal amount;
                private String type;
                private String timestamp;
                private Integer relatedAccountId;
            }
        }
    }
}
