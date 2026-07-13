package br.com.fourteca.exception;

import br.com.fourteca.response.ErrorResponse;
import br.com.fourteca.response.ValidationErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request - Erros de validação
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .codError(MethodArgumentNotValidException.class.getSimpleName())
                .msgError("Erro de validação de dados.")
                .details(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 Bad Request - Regras de negócio que impedem a ação
    @ExceptionHandler({LimiteDeEmprestimosExcedidoException.class, LivroDisponivelException.class, ReservaNaoPodeSerCanceladaException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestBusinessExceptions(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .codError(ex.getClass().getSimpleName())
                .msgError(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 404 Not Found
    @ExceptionHandler({LivroNaoEncontradoException.class, LeitorNaoEncontradoException.class, EmprestimoInexistenteException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .codError(ex.getClass().getSimpleName())
                .msgError(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 409 Conflict
    @ExceptionHandler({LivroJaCadastradoException.class, LeitorJaCadastradoException.class, ReservaJaExistenteException.class, LeitorInadimplenteException.class, EmprestimoJaDevolvidoException.class, MultaNaoPendenteException.class})
    public ResponseEntity<ErrorResponse> handleConflictExceptions(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .codError(ex.getClass().getSimpleName())
                .msgError(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .codError(ex.getClass().getSimpleName())
                .msgError("Ocorreu um erro inesperado no servidor.")
                .details(ex.getMessage()) // Adiciona a mensagem da exceção para depuração
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
