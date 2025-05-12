package org.example.yhw.bookstorecrud.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zin Ko Win
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ValidationError {
    @JsonIgnore
    private String object;

    @JsonIgnore
    private Object rejectedValue;

    private String field;
    private String message;
}
