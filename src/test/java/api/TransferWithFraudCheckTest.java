package api;

import api.models.TransferResponse;
import api.models.comparison.ModelAssertions;
import base.BaseTest;
import common.annotations.AccountSession;
import common.annotations.FraudCheckMock;
import common.annotations.UserSession;
import common.context.SessionStorage;
import common.extensions.FraudCheckWireMockExtansion;
import common.extensions.TimingExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

@ExtendWith({TimingExtension.class, FraudCheckWireMockExtansion.class})
public class TransferWithFraudCheckTest extends BaseTest {

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    @UserSession(value = 2, auth = 0)
    @AccountSession(2)
    public void testTransferWithFraudCheck() {
        var user = SessionStorage.getSteps(1);
        var account1 = SessionStorage.getAccount(1);
        var account2 = SessionStorage.getAccount(2);

        BigDecimal transferAmount = BigDecimal.valueOf(Math.random() * (account1.getBalance().doubleValue() - 0.1) + 0.1);
        TransferResponse transferResponse = user.transferWithFraudCheck(
                account1.getId(),
                account2.getId(),
                transferAmount
        );

        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(transferAmount)
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}
