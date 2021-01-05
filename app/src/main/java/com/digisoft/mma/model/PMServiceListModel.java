package com.digisoft.mma.model;


import java.io.Serializable;
import java.util.List;

public class PMServiceListModel implements Serializable {

    int pageNumber;

    int pageSize;

    int totalPages;

    int numberOfElements;

    int totalElements;

    List<ServiceInfoModel> items;

    /*
    "pageNumber": 1,
            "pageSize": 2,
            "totalPages": 3,
            "numberOfElements": 2,
            "totalElements": 6,
            "items":*/

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public List<ServiceInfoModel> getItems() {
        return items;
    }

    public void setItems(List<ServiceInfoModel> items) {
        this.items = items;
    }
}
