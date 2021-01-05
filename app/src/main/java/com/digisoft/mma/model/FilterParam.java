package com.digisoft.mma.model;

import com.google.gson.annotations.SerializedName;

public class FilterParam{
    @SerializedName("filterExpression")
    public String filterExpression;
    @SerializedName("filterType")
    public String filterType;
    @SerializedName("key")
    public String key;
    @SerializedName("order")
    public int order;
    @SerializedName("enumValue")
    public EnumValue enumValue;
    @SerializedName("textArrayValue")
    public TextValue textValueList;

    @SerializedName("textValue")
    public TextValue textValue;

    public FilterParam(String filterExpression, String filterType, String key, int order, EnumValue enumValue) {
        this.filterExpression = filterExpression;
        this.filterType = filterType;
        this.key = key;
        this.order = order;
        this.enumValue = enumValue;
    }

    public FilterParam(String filterExpression, String filterType, String key, int order, TextValue textValue) {
        this.filterExpression = filterExpression;
        this.filterType = filterType;
        this.key = key;
        this.order = order;
        if (textValue.islist)
            this.textValueList = textValue;
        else
            this.textValue = textValue;
    }



}
