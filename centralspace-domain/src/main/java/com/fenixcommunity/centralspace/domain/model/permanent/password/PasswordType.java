package com.fenixcommunity.centralspace.domain.model.permanent.password;

public enum PasswordType {
    TO_CENTRALSPACE {
        @Override
        public String getInformation() {
            return "Password to centralspace";
        }
    }, TO_REPOSITORY {
        @Override
        public String getInformation() {
            return "Password to repository";
        }
    };

    public abstract String getInformation();
}
