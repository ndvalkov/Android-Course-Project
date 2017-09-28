package com.academy.ndvalkov.mediamonitoringapp.models;

import com.orm.SugarRecord;

public class MonitoringConfig extends SugarRecord {
    private String sourceId;
    private String source;
    private String category;
    private String vendor;
    private String primaryKeywords;
    private String secondaryKeywords;
    private String dateCreated;

    public MonitoringConfig() {

    }

    public MonitoringConfig(String sourceId,
                            String source,
                            String category,
                            String vendor,
                            String primaryKeywords,
                            String secondaryKeywords,
                            String dateCreated) {
        this.sourceId = sourceId;
        this.source = source;
        this.category = category;
        this.vendor = vendor;
        this.primaryKeywords = primaryKeywords;
        this.secondaryKeywords = secondaryKeywords;
        this.dateCreated = dateCreated;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPrimaryKeywords() {
        return primaryKeywords;
    }

    public void setPrimaryKeywords(String primaryKeywords) {
        this.primaryKeywords = primaryKeywords;
    }

    public String getSecondaryKeywords() {
        return secondaryKeywords;
    }

    public void setSecondaryKeywords(String secondaryKeywords) {
        this.secondaryKeywords = secondaryKeywords;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
