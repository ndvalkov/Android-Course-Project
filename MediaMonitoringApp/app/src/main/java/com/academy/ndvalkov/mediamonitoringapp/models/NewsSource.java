package com.academy.ndvalkov.mediamonitoringapp.models;

import java.io.Serializable;

public class NewsSource implements Serializable {
    public NewsSource() {
    }

    public NewsSource(String name, String description, String url, String category) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.category = category;
    }

    private String name;
    private String description;
    private String url;
    private String category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
