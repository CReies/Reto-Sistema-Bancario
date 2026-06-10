package com.retotecnico.accounts.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.retotecnico.accounts.application.dto.AccountRequest;
import com.retotecnico.accounts.application.dto.TransactionRequest;
import com.retotecnico.accounts.domain.model.ClientProjection;
import com.retotecnico.accounts.application.port.out.ClientProjectionRepositoryPort;
import com.retotecnico.accounts.application.service.account.CreateAccountService;
import com.retotecnico.accounts.application.service.transaction.CreateTransactionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class AccountIntegrationTest {

    @Autowired
    private ClientProjectionRepositoryPort clientRepository;

    @Autowired
    private CreateAccountService createAccountService;

    @Autowired
    private CreateTransactionService createTransactionService;

    @Test
    void createsAccountAndRegistersMovementUsingProjectedClient() {
        ClientProjection client = new ClientProjection();
        client.setClientId("CLI-INT");
        client.setName("Marianela Montalvo");
        client.setIdentification("999888777");
        client.setActive(true);
        StepVerifier.create(clientRepository.save(client)
                        .then(createAccountService.execute(new AccountRequest("225487", "Corriente", new BigDecimal("100.00"), true, "CLI-INT")))
                        .then(createTransactionService.execute(new TransactionRequest("225487", "Deposit", new BigDecimal("600.00"), null))))
                .assertNext(transaction -> assertThat(transaction.balance()).isEqualByComparingTo("700.00"))
                .verifyComplete();
    }
}
