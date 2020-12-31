package tk.com.sharemusic.network;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.response.LoginVo;
import tk.com.sharemusic.network.response.PartnerVo;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.response.SendMsgVo;
import tk.com.sharemusic.network.response.UpLoadHeadVo;

public interface NetWorkService {
    public static String homeUrl = "http://192.168.2.196:8080/";
    public static String BaseUrl = homeUrl+"SocialService/";

    @POST("user/login")
    Observable<LoginVo> login(@Body Map<String,String> map);

    @Multipart
    @POST("user/uploadHead")
    Observable<UpLoadHeadVo> uploadHead(@Part MultipartBody.Part part, @Part("userId")RequestBody userId);

    @GET("spublic/getdatas")
    Observable<GetPublicDataTenVo> getTenDatas();

    @GET("spublic/getByShareId")
    Observable<GetPublicDataShareIdVo> getByShareId(@Body String sharedId);

    @POST("spublic/publish")
    Observable<GetPublicDataShareIdVo> pulishPublic(@Body SocialPublicEntity socialPublicEntity);

    @POST("user/getByUserId")
    Observable<GetPublicDataTenVo> getMyPublish(@Body Map<String,Object> map);

    @POST("user/updataRegisterId")
    Observable<BaseResult> updataRegisterId(@Body Map<String,Object> map);

    @GET("user/getProfileByUserId/{userId}")
    Observable<PeopleVo> getPeopleInfo(@Path("userId")String userId);

    @GET("user/getPartners/{userId}")
    Observable<PartnerVo> getPartnerInfo(@Path("userId")String userId);

    @POST("user/addPartner")
    Observable<BaseResult> addPartner(@Body Map<String,Object> map);

    @FormUrlEncoded
    @POST("user/chat/sendMsg")
    Observable<SendMsgVo> sendMsg(@FieldMap Map<String,String> msgEntity);

    @Multipart
    @POST("user/chat/sendMsg")
    Observable<SendMsgVo> sendMsg(@QueryMap Map<String,String> msgEntity, @Part MultipartBody.Part part);

    @GET("user/chat/getAllChat/{userId}")
    Observable<ChatListVo> getAllChat(@Path("userId")String userId);

    @GET("user/chat/getSelectChat")
    Observable<ChatListVo> getPartnerChat(@QueryMap Map<String, String> map);
}
