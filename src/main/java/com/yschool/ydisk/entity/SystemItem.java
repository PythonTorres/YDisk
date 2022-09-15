package com.yschool.ydisk.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yschool.ydisk.model.SystemItemImport;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="systemitems")
public class SystemItem {
    @Id
    private String id;
    private String url;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private Date date;
    private String parentId;
    private String type;
    private Long size;
    @OneToMany
    private List<SystemItem> children;

    public SystemItem() {
    }

    public SystemItem(String id, String url, Date date, String parentId, String type, Long size) {
        this.id = id;
        this.url = url;
        this.date = date;
        this.parentId = parentId;
        this.type = type;
        this.size = size;
    }

    public SystemItem(SystemItemImport systemItemImport, Date date) {
        this.id = systemItemImport.getId();
        this.url = systemItemImport.getUrl();
        this.date = date;
        this.parentId = systemItemImport.getParentId();
        this.type = systemItemImport.getType();
        this.size = systemItemImport.getSize();
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public String getParentId() {
        return parentId;
    }

    public String getType() {
        return type;
    }

    public Long getSize() {
        return size;
    }

    public List<SystemItem> getChildren() {
        return children;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setChildren(List<SystemItem> children) {
        this.children = children;
    }
}
