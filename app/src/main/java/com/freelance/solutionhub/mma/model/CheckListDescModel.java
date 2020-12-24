package com.freelance.solutionhub.mma.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CheckListDescModel")
public class CheckListDescModel {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "keyId")
    String keyId;

    @ColumnInfo(name = "description")
    String description;

    public CheckListDescModel(@NonNull String keyId, String description) {
        this.keyId = keyId;
        this.description = description;
    }

    @NonNull
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(@NonNull String keyId) {
        this.keyId = keyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
