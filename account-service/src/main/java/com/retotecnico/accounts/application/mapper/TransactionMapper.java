package com.retotecnico.accounts.application.mapper;

import com.retotecnico.accounts.application.dto.TransactionResponse;
import com.retotecnico.accounts.domain.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    default TransactionResponse toResponse(Transaction transaction, String accountNumber) {
        return new TransactionResponse(
                transaction.getId(),
                accountNumber,
                transaction.getDate(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalance()
        );
    }
}
