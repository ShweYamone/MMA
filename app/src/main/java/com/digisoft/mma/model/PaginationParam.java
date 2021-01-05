package com.digisoft.mma.model;

import java.io.Serializable;

public class PaginationParam implements Serializable {
    public int pageNumber;
    public int pageSize;

    public PaginationParam(int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = 10;
    }
}