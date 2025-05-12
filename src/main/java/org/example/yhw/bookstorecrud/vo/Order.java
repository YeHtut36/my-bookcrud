package org.example.yhw.bookstorecrud.vo;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Zin Ko Win
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Column to which ordering should be applied. This is an index reference to the columns array of
     * information that is also submitted to the server.
     */
    @NotNull
    @Min(0)
    private Integer column;

    /**
     * Ordering direction for this column. It will be asc or desc to indicate ascending ordering or
     * descending ordering, respectively.
     */
    @NotNull
    @Pattern(regexp = "(desc|asc)")
    private String dir;

}
