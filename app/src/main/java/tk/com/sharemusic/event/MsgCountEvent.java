package tk.com.sharemusic.event;

public class MsgCountEvent {
    public int pos;
    public int count;

    public MsgCountEvent(int pos, int count) {
        this.pos = pos;
        this.count = count;
    }
}
