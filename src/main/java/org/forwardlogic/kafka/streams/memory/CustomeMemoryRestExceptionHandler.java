package org.forwardlogic.kafka.streams.memory;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomeMemoryRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UsedMemoryController.BadArgumentsException.class})
    protected ResponseEntity<String> handleBadArgumentException(UsedMemoryController.BadArgumentsException badArgumentException) {
        return new ResponseEntity<String>(badArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}