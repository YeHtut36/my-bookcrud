package org.example.yhw.bookstorecrud.queryCriteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.yhw.bookstorecrud.query.annotations.Query;
import org.example.yhw.bookstorecrud.query.enums.Type;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCriteria {

    @Query(blurry = "title,author.name")
    private String blurry;

    @Query(propName = "id",type = Type.IN_LONG)
    private List<Long> id;

    @Query(propName = "publicationDate", type = Type.BETWEEN)
    private List<String> publicationDate;

    public BookCriteria(String blurry) {
        this.blurry = blurry;
    }

}
