package com.yschool.ydisk.model;

import com.yschool.ydisk.entity.SystemItem;

import java.util.List;

public class SystemItemHistoryResponse {
    private List<SystemItemHistoryUnit> items;

    public SystemItemHistoryResponse() {
    }

    public SystemItemHistoryResponse(List<SystemItemHistoryUnit> items) {
        this.items = items;
    }

    public List<SystemItemHistoryUnit> getItems() {
        return items;
    }

    public void setItems(List<SystemItemHistoryUnit> items) {
        this.items = items;
    }
}
