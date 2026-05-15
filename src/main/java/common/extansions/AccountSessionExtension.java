package common.extansions;

import api.generators.RandomModelGenerator;
import api.models.AccountResponse;
import api.models.DepositAccountRequest;
import common.SessionStorage.SessionStorage;
import common.annotations.AccountSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AccountSessionExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AccountSession annotation = context.getRequiredTestMethod().getAnnotation(AccountSession.class);
        if (annotation != null) {
            int accountCount = annotation.value();
            int depositCount = annotation.deposit();

            for (int i = 0; i < accountCount; i++) {
                var user = SessionStorage.getSteps(i + 1);
                var account = user.createAccount();
                if (depositCount != 0) {
                    user.depositAccount(account, RandomModelGenerator.generate(DepositAccountRequest.class).getBalance().doubleValue());
                    depositCount--;
                }
            }
        }
    }
}
