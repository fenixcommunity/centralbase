package com.fenixcommunity.centralspace.app.rest.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Value
@Builder
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ErrorDetails {

    private List<String> collectedErrors;
    private ZonedDateTime timestamp;
    @ApiModelProperty(notes = "Debug information (e.g., stack trace), not visible if runtime environment is 'production'", required = false)
    private String message;
    private String details;
    private String logRef;

    public static String toStringModel() {
        return "Model:" +
                "\nErrorDetails {" +
                "\ncollectedErrors: List(String)" +
                "\ntimestamp: String" +
                "\nmessage: String" +
                "\ndetails: String" +
                "\nlogRef: random String" +
                "\n}";
    }
}
