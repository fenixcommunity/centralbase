package com.fenixcommunity.centralspace.app.rest.api;

import com.fenixcommunity.centralspace.app.service.PasswordService;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/password")

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PasswordController {

    private final PasswordService passwordService;

    @Autowired
    public PasswordController(final PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    //TODO statusy do enum lub obiektu import static javax.ws.rs.core.Response.Status.ACCEPTED, BAD_REQUEST;

    //todo
//    @RequestMapping("/passwordexample")
//    public String writeParameter(@RequestParam(value = "parameter") String parameter) {
//        Validator validator = validatorFactory.getInstance(PASSWORD_HIGH);
//        validator.validateWithException(parameter);
//
//        Password password = Password.builder()
//                .password(parameter)
//                .build();
//        passwordService.save(password);
//        return "success";
//    }

}
