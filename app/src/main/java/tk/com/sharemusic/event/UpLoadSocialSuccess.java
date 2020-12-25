package tk.com.sharemusic.event;

import tk.com.sharemusic.entity.SocialPublicEntity;

public class UpLoadSocialSuccess {
    public SocialPublicEntity socialPublicEntity;

    public UpLoadSocialSuccess(SocialPublicEntity socialPublicEntity) {
        this.socialPublicEntity = socialPublicEntity;
    }
}
