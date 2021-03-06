package com.fenixcommunity.centralspace.app.service.security.helper;

import static com.fenixcommunity.centralspace.utilities.validator.ValidatorType.NOT_NULL;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;

import javax.annotation.PostConstruct;

import com.fenixcommunity.centralspace.app.configuration.security.SecurityUserGroup;
import com.fenixcommunity.centralspace.utilities.common.StringTool;
import com.fenixcommunity.centralspace.utilities.validator.Validator;
import com.fenixcommunity.centralspace.utilities.validator.ValidatorFactory;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(access = PACKAGE) @FieldDefaults(level = PRIVATE, makeFinal = true)
public class SecurityHelperService {
    private final AuthenticationFacade authenticationFacade;
    private final Validator validator;

    @Autowired
    SecurityHelperService(AuthenticationFacade authenticationFacade, ValidatorFactory validatorFactory) {
        this.authenticationFacade = authenticationFacade;
        this.validator = validatorFactory.getInstance(NOT_NULL);
    }

    @PostConstruct
    public void initComponent() {
        validator.validateWithException(authenticationFacade);
    }

    public boolean isValidSecurityRole() {
        final var authentication = authenticationFacade.getAuthentication();
        validator.validateAllWithException(authentication, authentication.getName());
        final var role = authentication.getName();
        return isValidEnum(SecurityUserGroup.class, role);
    }

    //or to char array
    public String generateSecurePassword() {
        return StringTool.generateSecurePassword();
    }
}