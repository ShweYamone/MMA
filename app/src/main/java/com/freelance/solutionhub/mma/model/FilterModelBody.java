package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterModelBody implements Serializable {

    @SerializedName("filter")
    public Filter filter;
    @SerializedName("paginationParam")
    public PaginationParam paginationParam;
    @SerializedName("sortingParams")
    public List<SortingParam> sortingParams;

    public FilterModelBody(Filter filter, PaginationParam paginationParam, List<SortingParam> sortingParams) {
        this.filter = filter;
        this.paginationParam = paginationParam;
        this.sortingParams = sortingParams;
    }

    public static FilterModelBody createFilterModel(String filterExpression, String enumValue, String textValue, int pageNumber) {
        List<FilterParam> filterParamList = new ArrayList<>();
        filterParamList.add(new FilterParam("equals", "enum", "serviceOrderType", 0, new EnumValue(enumValue)));
        filterParamList.add(new FilterParam(filterExpression, "text", "serviceOrderStatus", 0, new TextValue(textValue)));

        Filter filter = new Filter("FILTER_LOGIC_AND", filterParamList);
        PaginationParam paginationParam = new PaginationParam(pageNumber);
        List<SortingParam> sortingParamList = new ArrayList<>();
        sortingParamList.add(new SortingParam("creationDate", 0, "desc"));
        FilterModelBody filterModelBody = new FilterModelBody(filter, paginationParam, sortingParamList );

        return filterModelBody;
    }
}

