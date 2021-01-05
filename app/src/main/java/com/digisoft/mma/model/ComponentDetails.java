package com.digisoft.mma.model;

import java.io.Serializable;

public class ComponentDetails implements Serializable {
    public String componentType;
    public String status;
    public String assetId;
    public String description;
    public String brand;
    public String model;
    public String serialNo;
    public String supplier;
    public String countryOfOrigin;
    public String manufacture;
    public String purchase;
    public String mtbf;
    public String mttr;
    public String endOfSupport;
    public String endOfLife;
    public String firmwareVersion;
    public String firmwareUpdateDate;

    public ComponentDetails(String componentType, String status, String assetId, String description, String brand, String model, String serialNo, String supplier, String countryOfOrigin, String manufacture, String purchase, String mtbf, String mttr, String endOfSupport, String endOfLife, String firmwareVersion, String firmwareUpdateDate) {
        this.componentType = componentType;
        this.status = status;
        this.assetId = assetId;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.serialNo = serialNo;
        this.supplier = supplier;
        this.countryOfOrigin = countryOfOrigin;
        this.manufacture = manufacture;
        this.purchase = purchase;
        this.mtbf = mtbf;
        this.mttr = mttr;
        this.endOfSupport = endOfSupport;
        this.endOfLife = endOfLife;
        this.firmwareVersion = firmwareVersion;
        this.firmwareUpdateDate = firmwareUpdateDate;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    public String getMtbf() {
        return mtbf;
    }

    public void setMtbf(String mtbf) {
        this.mtbf = mtbf;
    }

    public String getMttr() {
        return mttr;
    }

    public void setMttr(String mttr) {
        this.mttr = mttr;
    }

    public String getEndOfSupport() {
        return endOfSupport;
    }

    public void setEndOfSupport(String endOfSupport) {
        this.endOfSupport = endOfSupport;
    }

    public String getEndOfLife() {
        return endOfLife;
    }

    public void setEndOfLife(String endOfLife) {
        this.endOfLife = endOfLife;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getFirmwareUpdateDate() {
        return firmwareUpdateDate;
    }

    public void setFirmwareUpdateDate(String firmwareUpdateDate) {
        this.firmwareUpdateDate = firmwareUpdateDate;
    }
}
