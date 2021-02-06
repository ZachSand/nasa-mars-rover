package com.github.zachsand.nasa.mars.rover.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * {@link GlobalErrorAttributes} used to override {@link DefaultErrorAttributes#getErrorAttributes(ServerRequest, ErrorAttributeOptions)}
 * in order to inject the errors from application. Most errors in the application issue a {@link Mono#error(Throwable)} with
 * a {@link ResponseStatusException} with the appropriate {@link org.springframework.http.HttpStatus} and message.
 *
 * Sort of naive approach at global error handling.
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(
                request, options);

        Throwable throwable = super.getError(request);
        if(throwable instanceof ResponseStatusException) {
            ResponseStatusException exception = (ResponseStatusException) throwable;
            map.put("status", exception.getStatus());
            map.put("message", exception.getMessage());
        }
        return map;
    }

}