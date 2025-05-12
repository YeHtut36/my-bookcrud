package org.example.yhw.bookstorecrud.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author Zin Ko Win
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a", timezone = "Asia/Singapore")
    private LocalDateTime timestamp;

    private String message;

    private T data;

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .message(message != null ? message : status.getReasonPhrase())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .message(message != null ? message : status.getReasonPhrase())
                .build();
    }

    public ApiResponse<T> withTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        return this;
    }
}
