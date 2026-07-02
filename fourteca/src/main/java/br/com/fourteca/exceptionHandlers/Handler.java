package br.com.fourteca.exceptionHandlers;

import br.com.fourteca.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class Handler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorObject<?>> handleBaseException(BaseException e) {
        log.error("Um erro aconteceu: {}", e.getMessage());
        final ErrorEnum errorEnum = e.getErrorEnum();
        final ErrorObject<?> errorObject = ErrorObject
                .builder()
                .codError(errorEnum.getErrorCode())
                .msgError(errorEnum.getErrorMessage())
                .build();
        return ResponseEntity
                .status(errorEnum.getHttpStatus())
                .body(errorObject);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorObject<Object>> handleException(Exception e) {
        log.error("Um erro aconteceu: {}", e.getMessage());
        log.error(e.toString());
        final ErrorObject<Object> errorObject = ErrorObject
                .builder()
                .codError(ErrorEnum.ERRO_GENERICO.getErrorCode())
                .msgError(ErrorEnum.ERRO_GENERICO.getErrorMessage())
                .build();
        return ResponseEntity
                .internalServerError()
                .body(errorObject);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject<List<String>>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Falha de validação nos argumentos: {}", e.getMessage());
        List<String> errosValidacao = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> String.format("O campo '%s' %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        ErrorObject<List<String>> errorObject = ErrorObject.<List<String>>builder()
                .codError(ErrorEnum.ERRO_VALIDACAO.getErrorCode())
                .msgError("Erro de validação nos campos preenchidos.")
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorObject);
    }
}
