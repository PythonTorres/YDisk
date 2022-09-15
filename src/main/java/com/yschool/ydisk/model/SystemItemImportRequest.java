package com.yschool.ydisk.model;

import java.util.Date;
import java.util.List;

public class SystemItemImportRequest {
    private List<SystemItemImport> items;
    private Date updateDate;

    public SystemItemImportRequest() {
    }

    public SystemItemImportRequest(List<SystemItemImport> items, Date updateDate) {
        this.items = items;
        this.updateDate = updateDate;
    }

    public List<SystemItemImport> getItems() {
        return items;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setItems(List<SystemItemImport> items) {
        this.items = items;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
