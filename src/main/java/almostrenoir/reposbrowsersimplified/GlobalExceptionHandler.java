package almostrenoir.reposbrowsersimplified;

import almostrenoir.reposbrowsersimplified.shared.errorresult.HttpErrorResult;
import almostrenoir.reposbrowsersimplified.shared.exceptions.DataNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    protected Mono<ResponseEntity<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        HttpErrorResult httpErrorResult = new HttpErrorResult(400, errorMessages.toString());
        return Mono.just(ResponseEntity.status(httpErrorResult.status()).body(httpErrorResult));
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<HttpErrorResult> handleDataNotFoundException(DataNotFoundException ex) {
        HttpErrorResult httpErrorResult = new HttpErrorResult(404, ex.getMessage());
        return ResponseEntity.status(httpErrorResult.status()).body(httpErrorResult);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResult> handleUnexpectedException(Exception ex) {
        log.error("An unexpected error has occurred: {}", ex.getMessage());
        HttpErrorResult httpErrorResult = new HttpErrorResult(500, "An unexpected error has occurred");
        return ResponseEntity.status(httpErrorResult.status()).body(httpErrorResult);
    }

}
