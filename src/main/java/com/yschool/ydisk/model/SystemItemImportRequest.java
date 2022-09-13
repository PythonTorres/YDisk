package com.yschool.ydisk.model;

import java.util.List;

public class SystemItemImportRequest {
    private List<SystemItemImport> items;
    private String updateDate;

    public SystemItemImportRequest() {
    }

    public SystemItemImportRequest(List<SystemItemImport> items, String updateDate) {
        this.items = items;
        this.updateDate = updateDate;
    }

    public List<SystemItemImport> getItems() {
        return items;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setItems(List<SystemItemImport> items) {
        this.items = items;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
