package com.fenixcommunity.centralspace.app.service;

import com.fenixcommunity.centralspace.app.configuration.annotation.MethodMonitoring;
import com.fenixcommunity.centralspace.domain.model.mounted.account.Account;
import com.fenixcommunity.centralspace.domain.repository.mounted.AccountRepository;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Log4j2
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account save(final Account account) {
        return accountRepository.save(account);
    }

    public void delete(final Account account) {
        accountRepository.delete(account);
    }

    public void deleteById(final Long id) {
        accountRepository.deleteById(id);
    }

    public Optional<Account> findById(final Long id) {
        //TODO of null co zrobic?
        return accountRepository.findById(id);
    }

    @MethodMonitoring
    public List<Account> findAll() {
        return (List<Account>) accountRepository.findAll();
    }

    public Account findByLogin(final String login) {
        return accountRepository.findByLogin(login);
    }
}
