package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterModel implements Serializable {

    @SerializedName("filter")
    public Filter filter;
    @SerializedName("paginationParam")
    public PaginationParam paginationParam;
    @SerializedName("sortingParams")
    public List<SortingParam> sortingParams;

    public FilterModel(Filter filter, PaginationParam paginationParam, List<SortingParam> sortingParams) {
        this.filter = filter;
        this.paginationParam = paginationParam;
        this.sortingParams = sortingParams;
    }
}

