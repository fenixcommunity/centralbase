package com.fenixcommunity.centralspace.app.service.serviceconnector.microsoft;

import static lombok.AccessLevel.PRIVATE;

import com.fenixcommunity.centralspace.app.service.serviceconnector.RemoteService;
import com.fenixcommunity.centralspace.app.service.serviceconnector.credential.Credential;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor @FieldDefaults(level = PRIVATE, makeFinal = true)
public class MicrosoftRemoteService implements RemoteService {
    @Override
    public boolean testConnection(final Credential credential) {
        return true;
    }
}
