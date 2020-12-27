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

    public static FilterModelBody createFilterModel(String filterExpression, String enumValue, List<String> textValue, int pageNumber) {
        List<FilterParam> filterParamList = new ArrayList<>();
        TextValue textValue1 = new TextValue(textValue);
        textValue1.setIslist(true);
        filterParamList.add(new FilterParam("equals", "enum", "serviceOrderType", 0, new EnumValue(enumValue)));
        filterParamList.add(new FilterParam(filterExpression, "textArray", "serviceOrderStatus", 0, textValue1));

        Filter filter = new Filter("FILTER_LOGIC_AND", filterParamList);
        PaginationParam paginationParam = new PaginationParam(pageNumber);
        List<SortingParam> sortingParamList = new ArrayList<>();
        sortingParamList.add(new SortingParam("creationDate", 0, "desc"));
        FilterModelBody filterModelBody = new FilterModelBody(filter, paginationParam, sortingParamList );

        return filterModelBody;
    }

    public static FilterModelBody createFilterModelWithScheduleType(String scheduleType, String filterExpression, String enumValue, List<String> textValue, int pageNumber) {
        List<FilterParam> filterParamList = new ArrayList<>();
        TextValue textValue1 = new TextValue(textValue);
        textValue1.setIslist(true);
        filterParamList.add(new FilterParam("equals", "enum", "serviceOrderType", 0, new EnumValue(enumValue)));
        filterParamList.add(new FilterParam(filterExpression, "textArray", "serviceOrderStatus", 0, textValue1));
        filterParamList.add(new FilterParam("equals", "enum", "preventativeMaintenanceCheckType", 0, new EnumValue(scheduleType)));
        Filter filter = new Filter("FILTER_LOGIC_AND", filterParamList);
        PaginationParam paginationParam = new PaginationParam(pageNumber);
        List<SortingParam> sortingParamList = new ArrayList<>();
        sortingParamList.add(new SortingParam("creationDate", 0, "desc"));
        FilterModelBody filterModelBody = new FilterModelBody(filter, paginationParam, sortingParamList );

        return filterModelBody;
        /*
        * {
        "filterExpression": "equals",
        "filterType": "enum",
        "key": "preventativeMaintenanceCheckType",
        "order": 0,
        "enumValue": {
          "value": "MONTHLY"
        }
      }*/
    }

}

