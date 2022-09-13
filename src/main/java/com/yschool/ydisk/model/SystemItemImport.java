package com.yschool.ydisk.model;

public class SystemItemImport {
    private String id;
    private String url;
    private String parentId;
    private String type;
    private long size;

    public SystemItemImport() {
    }

    public SystemItemImport(String id, String url, String parentId, String type, long size) {
        this.id = id;
        this.url = url;
        this.parentId = parentId;
        this.type = type;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getParentId() {
        return parentId;
    }

    public String getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
