package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PMServiceListModel{

    @SerializedName("pageNumber")
    int pageNumber;

    @SerializedName("pageSize")
    int pageSize;

    @SerializedName("totalPages")
    int totalPages;

    @SerializedName("numberOfElements")
    int numberOfElements;

    @SerializedName("totalElements")
    int totalElements;

    @SerializedName("items")
    List<PMServiceInfoModel> items;


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

    public List<PMServiceInfoModel> getItems() {
        return items;
    }

    public void setItems(List<PMServiceInfoModel> items) {
        this.items = items;
    }
}
