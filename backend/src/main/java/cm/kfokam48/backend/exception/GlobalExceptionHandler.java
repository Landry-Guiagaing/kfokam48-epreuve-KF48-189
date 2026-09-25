package cm.kfokam48.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurResponse> handleApi(ApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErreurResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErreurResponse("VALIDATION_ERREUR", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurResponse> handleGeneric(Exception ex) {
        // B4 : jamais de stack trace renvoyée au client
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErreurResponse("ERREUR_INTERNE",
                        "Une erreur interne est survenue."));
    }

        @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErreurResponse> handleDataIntegrity(
            org.springframework.dao.DataIntegrityViolationException ex) {
        // Cas typique : violation d'une contrainte UNIQUE en cas de concurrence.
        // On renvoie un 409 avec un code métier, jamais une 500.
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.contains("PRESENCE")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErreurResponse("DEJA_PRESENT",
                            "Vous avez déjà marqué votre présence."));
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErreurResponse("CONFLIT",
                        "Conflit d'intégrité des données."));
    }
}