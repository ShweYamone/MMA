package com.digisoft.mma.model;

import java.io.Serializable;

public class QRReturnBody implements Serializable {
    public ComponentDetails componentDetails;
    public ComponentLocation componentLocation;
    public String componentId;
    public String assetId;
    public String lastModifiedDate;
    public String lastModifiedBy;
    public String status;
    public String updatedDate;
    public String createdDate;

    public QRReturnBody(ComponentDetails componentDetails, ComponentLocation componentLocation, String componentId, String assetId, String lastModifiedDate, String lastModifiedBy, String status, String updatedDate, String createdDate) {
        this.componentDetails = componentDetails;
        this.componentLocation = componentLocation;
        this.componentId = componentId;
        this.assetId = assetId;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedBy = lastModifiedBy;
        this.status = status;
        this.updatedDate = updatedDate;
        this.createdDate = createdDate;
    }

    public ComponentDetails getComponentDetails() {
        return componentDetails;
    }

    public void setComponentDetails(ComponentDetails componentDetails) {
        this.componentDetails = componentDetails;
    }

    public ComponentLocation getComponentLocation() {
        return componentLocation;
    }

    public void setComponentLocation(ComponentLocation componentLocation) {
        this.componentLocation = componentLocation;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}

