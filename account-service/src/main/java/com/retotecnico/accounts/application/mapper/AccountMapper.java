package com.retotecnico.accounts.application.mapper;

import com.retotecnico.accounts.application.dto.AccountRequest;
import com.retotecnico.accounts.application.dto.AccountResponse;
import com.retotecnico.accounts.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentBalance", source = "initialBalance")
    Account toEntity(AccountRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    void updateEntity(@MappingTarget Account account, AccountRequest request);

    AccountResponse toResponse(Account account);
}
