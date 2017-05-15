package com.sleepeasysoftware.platetoccd.model;

import java.util.Optional;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class OutputDataRow {


    private int rowCount;
    private String plateName;
    private String well;
    private Optional<String> data;

    public OutputDataRow(int rowCount, String plateName, String well, Optional<String> data) {
        this.rowCount = rowCount;
        this.plateName = plateName;
        this.well = well;
        this.data = data;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getPlateName() {
        return plateName;
    }

    public String getWell() {
        return well;
    }

    public Optional<String> getData() {
        return data;
    }
}
