package tk.com.sharemusic.event;

import tk.com.sharemusic.entity.ChatReviewEntity;

public class ChatReviewDeleteEvent {
    public int itemPos;
    public int chatPos;
    public ChatReviewEntity chatReviewEntity;

    public ChatReviewDeleteEvent(ChatReviewEntity chatReviewEntity, int itemPos, int chatPos) {
        this.chatReviewEntity = chatReviewEntity;
        this.itemPos = itemPos;
        this.chatPos = chatPos;
    }
}
