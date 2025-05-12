package org.example.yhw.bookstorecrud.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Zin Ko Win
 */


@Data
public class SearchData {
    private LocalDate fromDate;
    private LocalDate toDate;
    private int status;
}
