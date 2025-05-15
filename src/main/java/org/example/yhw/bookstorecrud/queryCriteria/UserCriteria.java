package org.example.yhw.bookstorecrud.queryCriteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.yhw.bookstorecrud.query.annotations.Query;
import org.example.yhw.bookstorecrud.query.enums.Type;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCriteria {

    @Query(propName = "username", type = Type.INNER_LIKE)
    private String username;
}
