package com.nqtam_lab04.nqtam_lab04.nqtException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class nqtValidationExceptionHandler {

    // Bắt ngoại lệ xảy ra khi validation thất bại (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Tạo một Map để lưu trữ các lỗi: Key là tên trường, Value là thông báo lỗi
        Map<String, String> errors = new HashMap<>();

        // Lấy tất cả các lỗi từ kết quả Binding và lặp qua từng lỗi
        ex.getBindingResult().getAllErrors().forEach((error) -> {

            // Lấy tên trường bị lỗi
            String fieldName = ((FieldError) error).getField();

            // Lấy thông báo lỗi mặc định từ annotation (@NotBlank, @Min, v.v.)
            String errorMessage = error.getDefaultMessage();

            // Thêm lỗi vào Map
            errors.put(fieldName, errorMessage);
        });

        // Trả về đối tượng ResponseEntity chứa Map lỗi và mã trạng thái HTTP 400 (BAD_REQUEST)
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}