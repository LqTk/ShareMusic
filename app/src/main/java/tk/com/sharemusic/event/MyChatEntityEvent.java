package tk.com.sharemusic.event;

import tk.com.sharemusic.entity.ChatEntity;
import tk.com.sharemusic.entity.MsgEntity;

public class MyChatEntityEvent {
    public ChatEntity chatEntity;
    public MsgEntity partner;

    public MyChatEntityEvent(ChatEntity chatEntity, MsgEntity partner) {
        this.chatEntity = chatEntity;
        this.partner = partner;
    }
}
