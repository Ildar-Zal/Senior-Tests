package api.requests.skelethon;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {

    AUTH_LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),
    ADMIN_USERS(
            "/admin/users",
            CreateUserRequest.class,
            UserResponse.class
    ),
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            AccountResponse.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            AccountResponse.class
    ),
    ACCOUNTS_DEPOSIT(
            "/accounts/deposit",
            DepositAccountRequest.class,
            AccountResponse.class
    ),
    ACCOUNTS_TRANSFER(
            "/accounts/transfer",
            TransferAccountRequest.class,
            TransferAccountResponse.class
    ),
    UPDATE_CUSTOMER_PROFILE(
            "/customer/profile",
            UpdateCustomerProfileRequest.class,
            UpdateCustomerProfileResponse.class
    ),
    GET_CUSTOMER_PROFILE(
            "/customer/profile",
            BaseModel.class,
            UserResponse.class
    );

    private final String url;
    private Class<? extends BaseModel> RequestModel;
    private Class<? extends BaseModel> ResponseModel;
}
