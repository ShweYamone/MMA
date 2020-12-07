package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Filter{
    @SerializedName("filterLogic")
    public String filterLogic;
    @SerializedName("filterParams")
    public List<FilterParam> filterParams;

    public Filter(String filterLogic, List<FilterParam> filterParams) {
        this.filterLogic = filterLogic;
        this.filterParams = filterParams;
    }
}
