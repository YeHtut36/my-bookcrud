package org.example.yhw.bookstorecrud.vo;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author Zin Ko Win
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Search {

    /**
     * Global search value. To be applied to all columns which have searchable as true.
     */
    @NotNull
    private String value;

    /**
     * true if the global filter should be treated as a regular expression for advanced searching,
     * false otherwise. Note that normally server-side processing scripts will not perform regular
     * expression searching for performance reasons on large data sets, but it is technically possible
     * and at the discretion of your script.
     */
    @NotNull
    private Boolean regex;

}
