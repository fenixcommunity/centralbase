package com.fenixcommunity.centralspace.app.exception.rest;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
// mozna dac @Builder
class ErrorDetails {
    //    @Getter, @Setter(AccessLevel.PROTECTED)
    private ZonedDateTime timestamp;
    private String message;
    private String details;
}
