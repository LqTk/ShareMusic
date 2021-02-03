package tk.com.sharemusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.adapter.PublishTypeAdapter;
import tk.com.sharemusic.entity.SearchLocationEntity;

public class PublishTypeActivity extends CommonActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.rcy_type)
    RecyclerView rcyType;

    private Unbinder bind;
    private List<SearchLocationEntity> dataLists = new ArrayList<>();
    private PublishTypeAdapter publishTypeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_type);
        bind = ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcyType.setLayoutManager(linearLayoutManager);

        publishTypeAdapter = new PublishTypeAdapter(R.layout.layout_publish_type, dataLists);
        rcyType.setAdapter(publishTypeAdapter);
        publishTypeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SearchLocationEntity searchLocationEntity = dataLists.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("type", searchLocationEntity.city);
                intent.putExtra("data", bundle);
                setResult(Activity.RESULT_OK, intent);
                PublishTypeActivity.this.finish();
            }
        });
    }

    private void initData() {
        String type = getIntent().getStringExtra("type");
        dataLists.add(new SearchLocationEntity("公开", "所有人可见", false));
        dataLists.add(new SearchLocationEntity("私密", "仅自己可见", false));
        dataLists.add(new SearchLocationEntity("隐身", "所有人可见，但不显示个人信息", false));
        for (SearchLocationEntity entity : dataLists) {
            if (type.equals(entity.city)) {
                entity.setCheck(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                PublishTypeActivity.this.finish();
                break;
        }
    }
}