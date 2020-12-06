package com.freelance.solutionhub.mma.model;

import java.io.Serializable;

public class PaginationParam implements Serializable {

    int pageNumber;
    int pageSize;

    public PaginationParam(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }


}
