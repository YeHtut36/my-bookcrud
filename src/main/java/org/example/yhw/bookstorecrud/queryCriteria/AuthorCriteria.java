package org.example.yhw.bookstorecrud.queryCriteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.yhw.bookstorecrud.query.annotations.Query;
import org.example.yhw.bookstorecrud.query.enums.Type;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCriteria {

    @Query(propName = "name", type = Type.INNER_LIKE)
    private String name;

    private String searchValue;
}
