package com.yschool.ydisk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yschool.ydisk.entity.SystemItem;

import java.util.Date;

public class SystemItemHistoryUnit {
    String id;
    String url;
    String parentId;
    String type;
    Long size;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    Date date;

    public SystemItemHistoryUnit() {
    }

    public SystemItemHistoryUnit(SystemItem systemItem) {
        id = systemItem.getId();
        url = systemItem.getUrl();
        parentId = systemItem.getParentId();
        type = systemItem.getType();
        size = systemItem.getSize();
        date = systemItem.getDate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
