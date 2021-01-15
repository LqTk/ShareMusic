package tk.com.sharemusic.event;

import tk.com.sharemusic.entity.MsgEntity;

public class NotifyPartInfoEvent {
    public String partnerId;

    public NotifyPartInfoEvent(String partnerId) {
        this.partnerId = partnerId;
    }
}
