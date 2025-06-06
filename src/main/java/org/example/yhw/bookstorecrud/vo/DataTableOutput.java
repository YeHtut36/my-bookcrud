package org.example.yhw.bookstorecrud.vo;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

/**
 * @author Zin Ko Win
 */

@Data
public class DataTableOutput<T> {

    /**
     * The draw counter that this object is a response to - from the draw parameter sent as part of
     * the data request. Note that it is strongly recommended for security reasons that you cast this
     * parameter to an integer, rather than simply echoing back to the client what it sent in the draw
     * parameter, in order to prevent Cross Site Scripting (XSS) attacks.
     */
    @JsonView(View.class)
    private int draw;

    /**
     * Total records, before filtering (i.e. the total number of records in the database)
     */
    @JsonView(View.class)
    private long recordsTotal = 0L;

    /**
     * Total records, after filtering (i.e. the total number of records after filtering has been
     * applied - not just the number of records being returned for this page of data).
     */
    @JsonView(View.class)
    private long recordsFiltered = 0L;

    /**
     * The data to be displayed in the table. This is an array of data source objects, one for each
     * row, which will be used by DataTables. Note that this parameter's name can be changed using the
     * ajaxDT option's dataSrc property.
     */
    @JsonView(View.class)
    private List<T> data = Collections.emptyList();

    /**
     * Optional: If an error occurs during the running of the server-side processing script, you can
     * inform the user of this error by passing back the error message to be displayed using this
     * parameter. Do not include if there is no error.
     */
    @JsonView(View.class)
    private String error;

    public interface View {
    }


    public static <T> DataTableOutput<T> of(Page<T> logs, Long totalCount) {
        DataTableOutput<T> dataTableOutput = new DataTableOutput<>();
        dataTableOutput.setRecordsTotal(totalCount);
        dataTableOutput.setRecordsFiltered(logs.getTotalElements());
        dataTableOutput.setData(logs.getContent());
        return dataTableOutput;
    }

    public static <E> DataTableOutput<E> of(Page<E> logs, Long totalCount, NZDataTableInput dataTablesInput) {
        DataTableOutput<E> dataTableOutput = new DataTableOutput<>();
        dataTableOutput.setRecordsTotal(totalCount);
        dataTableOutput.setRecordsFiltered(logs.getTotalElements());
        dataTableOutput.setData(logs.getContent());
        dataTableOutput.setDraw(dataTablesInput.getPageIndex());
        return dataTableOutput;
    }

    public static <E> DataTableOutput<E> of(Page<E> logs, Long totalCount, DataTableInput dataTablesInput) {
        DataTableOutput<E> dataTableOutput = new DataTableOutput<>();
        dataTableOutput.setRecordsTotal(totalCount);
        dataTableOutput.setRecordsFiltered(logs.getTotalElements());
        dataTableOutput.setData(logs.getContent());
        dataTableOutput.setDraw(dataTablesInput.getDraw());
        return dataTableOutput;
    }

    public static <E> DataTableOutput<E> of(List<E> logs, Long totalCount, DataTableInput dataTablesInput) {
        DataTableOutput<E> dataTableOutput = new DataTableOutput<>();
        dataTableOutput.setRecordsTotal(totalCount);
        dataTableOutput.setRecordsFiltered(logs.size());
        dataTableOutput.setData(logs);
        dataTableOutput.setDraw(dataTablesInput.getDraw());
        return dataTableOutput;
    }
}
