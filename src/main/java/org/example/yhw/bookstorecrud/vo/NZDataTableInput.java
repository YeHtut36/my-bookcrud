package org.example.yhw.bookstorecrud.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author Zin Ko Win
 */

@Data
public class NZDataTableInput {

    private Integer pageIndex = 1;

    private Integer pageSize = Integer.MAX_VALUE;

    @JsonProperty("queryCriteria")
    private JsonNode queryCriteria;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortField;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortOrder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String searchValue;

    public Pageable getPageable() {
        int startIndex = pageIndex - 1;
        Sort.Direction dir = Sort.Direction.valueOf(sortOrder.toUpperCase());
        return PageRequest.of(startIndex, pageSize, Sort.by(dir, sortField));
    }
}
