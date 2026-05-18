package com.ailtonmartins.transactionservice.domain.model;

import com.ailtonmartins.transactionservice.domain.exception.TransactionAlreadyFinishedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    @Test
    void deveCriarTransferenciaPendente() {
        UUID requesterUserId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID destinationAccountId = UUID.randomUUID();

        Transaction transaction = new Transaction(
                requesterUserId,
                sourceAccountId,
                destinationAccountId,
                new BigDecimal("100.00")
        );

        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getRequesterUserId()).isEqualTo(requesterUserId);
        assertThat(transaction.getSourceAccountId()).isEqualTo(sourceAccountId);
        assertThat(transaction.getDestinationAccountId()).isEqualTo(destinationAccountId);
        assertThat(transaction.getAmount()).isEqualByComparingTo("100.00");
        assertThat(transaction.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(transaction.isPending()).isTrue();
    }

    @Test
    void deveConcluirTransacaoPendente() {
        Transaction transaction = transaction();

        transaction.complete();

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(transaction.getFailureReason()).isNull();
        assertThat(transaction.isFinished()).isTrue();
    }

    @Test
    void deveFalharTransacaoPendenteComMotivo() {
        Transaction transaction = transaction();

        transaction.fail("Saldo insuficiente");

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(transaction.getFailureReason()).isEqualTo("Saldo insuficiente");
        assertThat(transaction.isFinished()).isTrue();
    }

    @Test
    void deveLancarExcecaoQuandoValorNaoForPositivo() {
        assertThatThrownBy(() -> new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.ZERO
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("amount deve ser maior que zero");
    }

    @Test
    void deveLancarExcecaoQuandoContaOrigemEDestinoForemIguais() {
        UUID accountId = UUID.randomUUID();

        assertThatThrownBy(() -> new Transaction(
                UUID.randomUUID(),
                accountId,
                accountId,
                new BigDecimal("100.00")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Conta de origem e destino nao podem ser iguais");
    }

    @Test
    void naoDeveAlterarTransacaoJaFinalizada() {
        Transaction transaction = transaction();
        transaction.complete();

        assertThatThrownBy(() -> transaction.fail("Erro no processamento"))
                .isInstanceOf(TransactionAlreadyFinishedException.class)
                .hasMessage("Transacao ja finalizada para o id: " + transaction.getId());
    }

    private static Transaction transaction() {
        return new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );
    }
}
