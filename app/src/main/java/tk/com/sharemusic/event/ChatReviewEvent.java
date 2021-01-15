package tk.com.sharemusic.event;

import tk.com.sharemusic.entity.ChatReviewEntity;

public class ChatReviewEvent {
    public ChatReviewEntity chatReviewEntity;
    public int pos;

    public ChatReviewEvent(ChatReviewEntity chatReviewEntity, int pos) {
        this.chatReviewEntity = chatReviewEntity;
        this.pos = pos;
    }
}
