package com.freelance.solutionhub.mma.model;

import com.google.gson.annotations.SerializedName;

public class TextValue{
    @SerializedName("value")
    public String value;

    public TextValue(String value) {
        this.value = value;
    }
}
