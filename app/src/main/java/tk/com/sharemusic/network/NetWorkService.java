package tk.com.sharemusic.network;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import tk.com.sharemusic.entity.GoodsEntity;
import tk.com.sharemusic.entity.SocialPublicEntity;
import tk.com.sharemusic.entity.User;
import tk.com.sharemusic.network.response.AddPartnerVo;
import tk.com.sharemusic.network.response.AddReviewVo;
import tk.com.sharemusic.network.response.ChatListVo;
import tk.com.sharemusic.network.response.ChatReviewVo;
import tk.com.sharemusic.network.response.GetPublicDataShareIdVo;
import tk.com.sharemusic.network.response.GetPublicDataTenVo;
import tk.com.sharemusic.network.response.GoodsResultVo;
import tk.com.sharemusic.network.response.LoginVo;
import tk.com.sharemusic.network.response.PartnerVo;
import tk.com.sharemusic.network.response.PeopleVo;
import tk.com.sharemusic.network.response.PublicMsgVo;
import tk.com.sharemusic.network.response.SendMsgVo;
import tk.com.sharemusic.network.response.UpLoadFileVo;
import tk.com.sharemusic.network.response.UpLoadHeadVo;

public interface NetWorkService {
//    public static String homeUrl = "http://192.168.2.196:8080/";
    public static String homeUrl = "http://192.168.2.199:8080/";
    public static String BaseUrl = homeUrl+"SocialService/";

    //登录
    @POST("user/login")
    Observable<LoginVo> login(@Body Map<String,String> map);

    //注册
    @POST("user/register")
    Observable<BaseResult> register(@Body Map user);

    //更新用户信息
    @POST("user/updataInfo")
    Observable<BaseResult> updataInfo(@Body Map map);

    //重置密码
    @POST("user/resetPassword")
    Observable<BaseResult> resetPassword(@Body Map map);

    //修改密码
    @POST("user/changePassword")
    Observable<BaseResult> changePassword(@Body Map map);

    //上传头像
    @Multipart
    @POST("user/uploadHead")
    Observable<UpLoadHeadVo> uploadHead(@Part MultipartBody.Part part, @Part("userId")RequestBody userId);

    //获取公场数据
    @GET("spublic/getdatas/{page}")
    Observable<GetPublicDataTenVo> getTenDatas(@Path("page") int page);

    //获取发布的单个详情
    @GET("spublic/getByShareId/{shareId}")
    Observable<GetPublicDataShareIdVo> getByShareId(@Path("shareId") String sharedId);

    //发布新分享
    @POST("spublic/publish")
    Observable<GetPublicDataShareIdVo> pulishPublic(@Body SocialPublicEntity socialPublicEntity);

    //删除分享
    @DELETE("spublic/deletePublish/{publishId}")
    Observable<BaseResult> pulishPublic(@Path("publishId") String publishId);

    /**
     * 点赞
     * @param goodsEntity
     * @return
     */
    @PUT("spublic/addGoods")
    Observable<GoodsResultVo> goodsAdd(@Body GoodsEntity goodsEntity);

    @DELETE("spublic/deleteGoods/{goodsId}")
    Observable<BaseResult> goodsCancel(@Path("goodsId")String goodsId);

    /**
     * 评论
     * @param map
     * @return
     */
    @PUT("spublic/addview")
    Observable<AddReviewVo> addReview(@Body HashMap map);

    @DELETE("spublic/deleteView/{reviewId}")
    Observable<BaseResult> deleteReview(@Path("reviewId") String reviewId);

    //评论回复
    @PUT("spublic/addChatReview")
    Observable<ChatReviewVo> addReviewChat(@Body HashMap map);

    @DELETE("spublic/deleteChatReview/{chatId}")
    Observable<BaseResult> deleteReviewChat(@Path("chatId") String chatId);

    //获取用户的分享内容
    @POST("user/getByUserId")
    Observable<GetPublicDataTenVo> getMyPublish(@Body Map<String,Object> map);

    //刷新上传registerID
    @POST("user/updataRegisterId")
    Observable<BaseResult> updataRegisterId(@Body Map<String,Object> map);

    //获取个人资料
    @GET("user/getProfileByUserId")
    Observable<PeopleVo> getPeopleInfo(@QueryMap Map<String, String> map);

    //获取好友列表
    @GET("user/getPartners/{userId}")
    Observable<PartnerVo> getPartnerInfo(@Path("userId")String userId);

    //获取关注列表
    @GET("user/getConcerns/{userId}")
    Observable<PartnerVo> getConcernsInfo(@Path("userId")String userId);

    //关注好友
    @POST("user/addPartner")
    Observable<AddPartnerVo> addPartner(@Body Map<String,Object> map);

    //设置备注
    @POST("user/setNote")
    Observable<BaseResult> setNote(@Body Map<String,Object> map);

    //取消关注好友
    @POST("user/cancelPartner")
    Observable<BaseResult> cancelPartner(@Body Map<String,Object> map);

    //发送消息
    @FormUrlEncoded
    @POST("user/chat/sendMsg")
    Observable<SendMsgVo> sendMsg(@FieldMap Map<String,String> msgEntity);

    @Multipart
    @POST("user/chat/sendMsg")
    Observable<SendMsgVo> sendMsg(@QueryMap Map<String,String> msgEntity, @Part MultipartBody.Part part);

    @Multipart
    @POST("spublic/uploadFile")
    Observable<UpLoadFileVo> upLoadFile(@Part MultipartBody.Part part);

    //获取聊天消息列表
    @GET("user/chat/getAllChat/{userId}")
    Observable<ChatListVo> getAllChat(@Path("userId")String userId);

    //获取单个好友聊天消息列表
    @GET("user/chat/getSelectAllChat")
    Observable<ChatListVo> getSelectAllChat(@QueryMap Map<String, String> map);

    //删除聊天记录的语音和图片
    @POST("user/chat/deleteMsg")
    Observable<BaseResult> deleteMsg(@Body RequestBody body);

    //获取具体个人的聊天列表
    @GET("user/chat/getSelectChat")
    Observable<ChatListVo> getPartnerChat(@QueryMap Map<String, String> map);

    //获取公场的消息
    @GET("spublic/getMsgCount/{userId}")
    Observable<PublicMsgVo> getPublicMsg(@Path("userId") String userId);

    //更新公场的消息阅读状态
    @PUT("spublic/updateReadState/{msgId}")
    Observable<BaseResult> updateReadState(@Path("msgId") String msgId);

    //获取消息回复
    @GET("spublic/getShareMsg/{shareId}/{msgId}")
    Observable<GetPublicDataShareIdVo> getShareMsg(@Path("shareId") String sharedId, @Path("msgId") String msgId);
}
