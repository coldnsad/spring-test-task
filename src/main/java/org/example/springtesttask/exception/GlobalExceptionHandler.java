package org.example.springtesttask.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.springtesttask.dto.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchWalletException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchWallet(NoSuchWalletException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    //Ошибки на этапе создания dto из json
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidJson(MethodArgumentNotValidException ex){
        HashMap<String, String> errorsResponse = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(er -> {
            String fieldName = ((FieldError) er).getField();
            String msg = er.getDefaultMessage();
            errorsResponse.put(fieldName, msg);
        });
        return errorsResponse;
    }

    //Ошибки на этапе парсинга входящего json
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonErrors(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException) {
            Map<Object, Object> errorsResponse = new HashMap<>();
            InvalidFormatException matchEx = (InvalidFormatException) ex.getCause();
            // Проверяем, что ошибка связана с Enum
            if (matchEx.getTargetType() != null && matchEx.getTargetType().isEnum()) {
                String message = String.format("Значение '%s' недопустимо. Разрешенные значения: %s",
                        matchEx.getValue(),
                        Arrays.toString(matchEx.getTargetType().getEnumConstants()));
                errorsResponse.put(matchEx.getValue(), message);
            }
            // Проверяем, что ошибка связана с UUID
            else if (matchEx.getTargetType() != null && matchEx.getTargetType() == UUID.class) {
                String message = "Некорректный UUID формат";
                errorsResponse.put(matchEx.getValue(), message);
            // Проверяем, что ошибка связана с Amount
            }else if (matchEx.getTargetType() != null && matchEx.getTargetType() == Integer.class) {
                String message = "Неверный формат поля Amount, допустимы только целочисленные значения";
                errorsResponse.put(matchEx.getValue(), message);
            }
            return ResponseEntity.badRequest().body(errorsResponse);
        }
        return ResponseEntity.badRequest().body("Некорректный JSON");
    }

    //Ошибки pathVariable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put((String) ex.getValue(), "Некорректный UUID формат");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
