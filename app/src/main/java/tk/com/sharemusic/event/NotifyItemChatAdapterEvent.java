package tk.com.sharemusic.event;

import tk.com.sharemusic.entity.ChatEntity;

public class NotifyItemChatAdapterEvent {
    public int pos;
    public String des;
    public boolean isSuccess;
    public ChatEntity data;

    public NotifyItemChatAdapterEvent(int pos, String des, boolean isSuccess, ChatEntity data) {
        this.pos = pos;
        this.des = des;
        this.isSuccess = isSuccess;
        this.data = data;
    }
}
