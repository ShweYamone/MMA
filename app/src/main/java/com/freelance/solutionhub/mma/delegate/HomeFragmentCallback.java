package com.freelance.solutionhub.mma.delegate;

import com.freelance.solutionhub.mma.model.PMServiceInfoModel;

public interface HomeFragmentCallback {
    public boolean hasUserId();
    public void update_ARRP_To_ACK(String date, String serviceId, int position);
}
