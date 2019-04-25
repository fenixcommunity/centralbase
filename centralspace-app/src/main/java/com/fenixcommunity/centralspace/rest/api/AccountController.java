package com.fenixcommunity.centralspace.rest.api;

import com.fenixcommunity.centralspace.exception.rest.ResourceNotFoundException;
import com.fenixcommunity.centralspace.model.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    // zobacz inne narzedzia jpa repo
    // Optional

    @RequestMapping("/writeparameter")
    public String writeParameter(@RequestParam(value = "parameter", defaultValue = "unknown") String parameter) {
        return parameter;
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Account account = findAccountById(id);
        return ResponseEntity.ok().body(account);
    }

    @PostMapping("/account")
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountRepository.save(account);
    }

    @PutMapping("/account/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable(name = "id") Long id, @Valid @RequestBody Account requestAccount) throws ResourceNotFoundException {
        Account account = findAccountById(id);

        account.setLogin(requestAccount.getLogin());
        final Account updatedAccount = accountRepository.save(account);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/account/{id}")
    public Map<String, Boolean> deleteAccount(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Account account = findAccountById(id);

        accountRepository.delete(account);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    private Account findAccountById(Long id) throws ResourceNotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
    }
}