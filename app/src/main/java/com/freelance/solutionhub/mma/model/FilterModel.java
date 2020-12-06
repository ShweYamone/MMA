package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FilterModel {

    @SerializedName("filter")
    Filter filter;

    @SerializedName("paginationParam")
    PaginationParam paginationParam;

    @SerializedName("sortingParams")
    SortingParams sortingParams;


    public FilterModel(PaginationParam paginationParam) {
        this.filter = new Filter();
        this.paginationParam = paginationParam;
        this.sortingParams = new SortingParams();
    }

}

class Filter {
    @SerializedName("filterLogic")
    String filterLogic;

    @SerializedName("filterParams")
    List<FilterParams> filterParams;

    public Filter() {
        this.filterLogic = "FILTER_LOGIC_AND";
        this.filterParams = new ArrayList<>();
        this.filterParams.add(new FilterParams(new EnumValue()));
        this.filterParams.add(new FilterParams(new TextValue()));
    }
}

class FilterParams {
    @SerializedName("filterExpression")
    String filterExpression;

    @SerializedName("filterType")
    String filterType;

    @SerializedName("key")
    String key;

    @SerializedName("order")
    int order;

    @SerializedName("enumValue")
    EnumValue enumValue;

    @SerializedName("textValue")
    TextValue textValue;

    public FilterParams(EnumValue enumValue) {
        this.filterExpression = "equals";
        this.filterType = "enum";
        this.key = "serviceOrderType";
        this.order = 0;
        this.enumValue = enumValue;
    }

    public FilterParams(TextValue textValue) {
        this.filterExpression = "ne";
        this.filterType = "text";
        this.key = "serviceOrderStatus";
        this.order = 0;
        this.textValue = textValue;
    }
}

class SortingParams {
    @SerializedName("key")
    String key;

    @SerializedName("order")
    int order;

    @SerializedName("sortType")
    String sortType;

    public SortingParams() {
        this.key = "creationDate";
        this.order = 0;
        this.sortType = "desc";
    }
}

class EnumValue {
    @SerializedName("value")
    String value;

    public EnumValue() {
        this.value = "PREVENTATIVE";
    }
}

class TextValue {
    @SerializedName("value")
    String value;

    public TextValue() {
        this.value = "JOBDONE";
    }
}
