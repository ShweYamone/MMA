package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

public class SortingParam{
    @SerializedName("key")
    public String key;
    @SerializedName("order")
    public int order;
    @SerializedName("sortType")
    public String sortType;

    public SortingParam(String key, int order, String sortType) {
        this.key = key;
        this.order = order;
        this.sortType = sortType;
    }
}
