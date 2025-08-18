package br.com.erudio.exception.handler;

import br.com.erudio.exception.ExcepetionResponse;
import br.com.erudio.exception.UnsupportedMathOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExcepetionResponse> handleAllException(Exception ex, WebRequest request) {
        ExcepetionResponse response = new ExcepetionResponse(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedMathOperationException.class)
    public final ResponseEntity<ExcepetionResponse> handleBadRequestException(Exception ex, WebRequest request) {
        ExcepetionResponse response = new ExcepetionResponse(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
