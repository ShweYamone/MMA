package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

public class PaginationParam {
    @SerializedName("pageNumber")
    int pageNumber;

    @SerializedName("pageSize")
    int pageSize;

    public PaginationParam(int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = 10;
    }
}
