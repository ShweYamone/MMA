package com.freelance.solutionhub.mma.model;

import java.io.Serializable;

public class PaginationParam implements Serializable {
    public int pageNumber;
    public int pageSize;

    public PaginationParam(int pageNumber) {
        this.pageNumber = 1;
        this.pageSize = 10;
    }
}