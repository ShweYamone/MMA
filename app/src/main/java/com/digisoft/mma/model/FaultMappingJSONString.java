package com.digisoft.mma.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FaultMappingJSONString")
public class FaultMappingJSONString {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    int id;

    @ColumnInfo
    String jsonString;


    public FaultMappingJSONString(String jsonString) {
        this.jsonString = jsonString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}
