package com.fenixcommunity.centralspace.app.rest.mapper.account.modelmapper;

import com.fenixcommunity.centralspace.app.rest.dto.account.ContactDetailsDto;
import com.fenixcommunity.centralspace.domain.model.permanent.account.Address;
import org.modelmapper.Converter;

class AccountModelMapperHelper {
    static Converter<ContactDetailsDto, Address> mapContactDetailsToAddress() {
        return ctx -> ctx.getSource() != null ? Address.builder().country(ctx.getSource().getCountry()).build() : null;
    }
}