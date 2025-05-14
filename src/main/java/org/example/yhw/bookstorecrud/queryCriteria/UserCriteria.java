package org.example.yhw.bookstorecrud.queryCriteria;

import lombok.Data;
import org.example.yhw.bookstorecrud.query.annotations.Query;
import org.example.yhw.bookstorecrud.query.enums.Type;

@Data
public class UserCriteria {

    @Query(propName = "username", type = Type.INNER_LIKE)
    private String username;
}
