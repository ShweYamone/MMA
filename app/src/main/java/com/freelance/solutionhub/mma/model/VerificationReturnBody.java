package com.freelance.solutionhub.mma.model;

import java.io.Serializable;

public class VerificationReturnBody implements Serializable {
    boolean fault_resolved;
    String service_order_id;

    public VerificationReturnBody(boolean fault_resolved, String service_order_id) {
        this.fault_resolved = fault_resolved;
        this.service_order_id = service_order_id;
    }

    public boolean isFault_resolved() {
        return fault_resolved;
    }

    public void setFault_resolved(boolean fault_resolved) {
        this.fault_resolved = fault_resolved;
    }

    public String getService_order_id() {
        return service_order_id;
    }

    public void setService_order_id(String service_order_id) {
        this.service_order_id = service_order_id;
    }
}
