package com.digisoft.mma.model;

import java.util.List;

public class PhotoAttachementModel {
    public List<PreMaintenance> preMaintenance;
    public List<PostMaintenance> postMaintenance;

    public List<PreMaintenance> getPreMaintenance() {
        return preMaintenance;
    }

    public void setPreMaintenance(List<PreMaintenance> preMaintenance) {
        this.preMaintenance = preMaintenance;
    }

    public List<PostMaintenance> getPostMaintenance() {
        return postMaintenance;
    }

    public void setPostMaintenance(List<PostMaintenance> postMaintenance) {
        this.postMaintenance = postMaintenance;
    }
}
