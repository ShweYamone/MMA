package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TextValue implements Serializable {
    @SerializedName("list")
    public List<String> list;

    @SerializedName("textValue")
    public String textValue;

    public boolean islist;

    public TextValue(List<String> list) {
        this.list = list;
    }

    public TextValue(String textValue) {
        this.textValue = textValue;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public boolean isIslist() {
        return islist;
    }

    public void setIslist(boolean islist) {
        this.islist = islist;
    }
}
