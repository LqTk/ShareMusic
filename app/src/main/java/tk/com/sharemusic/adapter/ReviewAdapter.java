package tk.com.sharemusic.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.entity.ChatReviewEntity;
import tk.com.sharemusic.entity.ReviewEntity;
import tk.com.sharemusic.event.ChatReviewDeleteEvent;
import tk.com.sharemusic.event.ChatReviewEvent;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;

public class ReviewAdapter extends BaseQuickAdapter<ReviewEntity, BaseViewHolder> {

    private NetWorkService service;
    public ReviewAdapter(int layoutResId, @Nullable List<ReviewEntity> data) {
        super(layoutResId, data);
        service = HttpMethod.getInstance().create(NetWorkService.class);
    }

    public ReviewAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ReviewEntity reviewEntity) {
        baseViewHolder.setText(R.id.tv_name,reviewEntity.getPeopleName()+"：");
        baseViewHolder.setText(R.id.tv_review_detail,reviewEntity.getReviewText());
        int pos = getData().indexOf(reviewEntity)+1;
        baseViewHolder.setText(R.id.tv_pos,"#"+pos+"楼");
        initReviewChatView(baseViewHolder,reviewEntity);
        if (getData().size()>1){
            baseViewHolder.setVisible(R.id.v_line,true);
            if (reviewEntity.equals(getData().get(getData().size()-1))){
                baseViewHolder.setGone(R.id.v_line, true);
            }
        }else {
            baseViewHolder.setGone(R.id.v_line,true);
        }
        if (!reviewEntity.getChatReviewList().isEmpty()){

        }
    }

    private void initReviewChatView(BaseViewHolder baseViewHolder, ReviewEntity reviewEntity) {
        List<ChatReviewEntity> chatReviewList = reviewEntity.getChatReviewList();
        RecyclerView recyclerView = baseViewHolder.getView(R.id.rcy_review_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        ReviewChatAdapter adapter = new ReviewChatAdapter(R.layout.layout_chat_review,chatReviewList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ChatReviewEntity chatReviewEntity = chatReviewList.get(position);
                if (chatReviewEntity.getTalkId().equals(ShareApplication.user.getUserId())){
                    EventBus.getDefault().post(new ChatReviewDeleteEvent(chatReviewEntity,getData().indexOf(reviewEntity), position));
                }else {
                    EventBus.getDefault().post(new ChatReviewEvent(chatReviewEntity, getData().indexOf(reviewEntity)));
                }
            }
        });
    }
}
