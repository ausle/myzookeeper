package com.asule.entity;

import java.io.Serializable;

public class Items implements Serializable{
    private String id;

    private String name;

    private Integer counts;

    private Integer buyCounts;

    public Integer getBuyCounts() {
        return buyCounts;
    }

    public void setBuyCounts(Integer buyCounts) {
        this.buyCounts = buyCounts;
    }

    public Items(String id, String name, Integer counts) {
        this.id = id;
        this.name = name;
        this.counts = counts;
    }

    public Items() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }
}