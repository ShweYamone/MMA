package com.digisoft.mma.model;

import com.google.gson.annotations.SerializedName;

public class EnumValue{
    @SerializedName("value")
    public String value;

    public EnumValue(String value) {
        this.value = value;
    }
}
