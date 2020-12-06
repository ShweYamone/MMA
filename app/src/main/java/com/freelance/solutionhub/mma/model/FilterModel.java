package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterModel implements Serializable {


    public class EnumValue{
        //@SerializedName("value")
        public String value;

        public EnumValue() {
            this.value = "PREVENTATIVE";
        }

    }

    public class TextValue{
        //@SerializedName("value")
        public String value;

        public TextValue() {
            this.value = "JOBDONE";
        }
    }

    public class FilterParam{
        //@SerializedName("filterExpression")
        public String filterExpression;
        //@SerializedName("filterType")
        public String filterType;
        //@SerializedName("key")
        public String key;
        //@SerializedName("order")
        public int order;
        //@SerializedName("enumValue")
        public EnumValue enumValue;
        //@SerializedName("textValue")
        public TextValue textValue;

        public FilterParam(EnumValue enumValue) {
            this.filterExpression = "equals";
            this.filterType = "enum";
            this.key = "serviceOrderType";
            this.order = 0;
            this.enumValue = enumValue;
        }

        public FilterParam(TextValue textValue) {
            this.filterExpression = "ne";
            this.filterType = "text";
            this.key = "serviceOrderStatus";
            this.order = 0;
            this.textValue = textValue;
        }
    }

    public class Filter{
        //@SerializedName("filterLogic")
        public String filterLogic;
        //@SerializedName("filterParams")
        public List<FilterParam> filterParams;

        public Filter() {
            this.filterLogic = "FILTER_LOGIC_AND";
            List<FilterParam> temp = new ArrayList<>();
            temp.add(new FilterParam(new EnumValue()));
            temp.add(new FilterParam(new TextValue()));
            this.filterParams = new ArrayList<>();
        }
    }

    public class SortingParam{
        //@SerializedName("key")
        public String key;
        //@SerializedName("order")
        public int order;
        //@SerializedName("sortType")
        public String sortType;

        public SortingParam() {
            this.key = "creationDate";
            this.order = 0;
            this.sortType = "desc";
        }
    }

    //@SerializedName("filter")
    public Filter filter;
    //@SerializedName("paginationParam")
    public PaginationParam paginationParam;
    //@SerializedName("sortingParams")
    public List<SortingParam> sortingParams;

    public FilterModel(PaginationParam param) {
        this.filter = new Filter();
        this.paginationParam = new PaginationParam(1);
        List<SortingParam> temp = new ArrayList<>();
        temp.add(new SortingParam());
        this.sortingParams = temp;
    }

    /*
    @SerializedName("filter")
    Filter filter;

    @SerializedName("paginationParam")
    PaginationParam paginationParam;

    @SerializedName("sortingParams")
    SortingParams sortingParams;

    public FilterModel() {
        this.filter = new Filter();
        this.paginationParam = new PaginationParam(1);
        this.sortingParams = new SortingParams();
    }



    public class Filter {
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

    public class FilterParams {
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

    public class PaginationParam {
        @SerializedName("pageNumber")
        int pageNumber;

        @SerializedName("pageSize")
        int pageSize;

        public PaginationParam(int pageNumber) {
            this.pageNumber = 1;
            this.pageSize = 10;
        }
    }

    public class SortingParams {
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

    public class EnumValue {
        @SerializedName("value")
        String value;

        public EnumValue() {
            this.value = "PREVENTATIVE";
        }
    }

    public class TextValue {
        @SerializedName("value")
        String value;

        public TextValue() {
            this.value = "JOBDONE";
        }
    }


     */
}
