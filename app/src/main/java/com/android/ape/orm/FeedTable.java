package com.android.ape.orm;

import java.util.List;

public class FeedTable {

    private String title;

    private List<RowTable> rows;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RowTable> getRows() {
        return rows;
    }

    public void setRows(List<RowTable> rows) {
        this.rows = rows;
    }
}
