package com.digisoft.mma.delegate;

public interface HomeFragmentCallback {
    public boolean hasUserId();
    public void update_ARRP_To_ACK(String serviceId, int position);
}
