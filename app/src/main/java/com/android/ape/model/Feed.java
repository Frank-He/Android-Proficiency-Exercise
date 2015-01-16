package com.android.ape.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Feed extends DataSupport {

    private String title;

    private List<Row> rows = new ArrayList<Row>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
