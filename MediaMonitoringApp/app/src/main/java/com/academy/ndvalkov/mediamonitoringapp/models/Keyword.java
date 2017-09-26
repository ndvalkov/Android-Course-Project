package com.academy.ndvalkov.mediamonitoringapp.models;

import com.yalantis.beamazingtoday.interfaces.BatModel;

public class Keyword implements BatModel {

    private final String value;
    private boolean isChecked;

    public Keyword(String value) {
        this.value = value;
    }

    @Override
    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public String getText() {
        return this.value;
    }

    @Override
    public void setChecked(boolean b) {
        this.isChecked = b;
    }
}