package tk.com.sharemusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.adapter.FragmentAdapter;

public class FriendsFragment extends Fragment {

    @BindView(R.id.pager_item)
    ViewPager pagerItem;
    @BindView(R.id.tab_title)
    TabLayout tabTitle;
    private Unbinder uBind;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    private List<TabLayout.Tab> tabList = new ArrayList<>();
    private String[] titles = new String[]{"好友","关注"};

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_friends, null);
        uBind = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        fragments.add(PartnerFragment.newInstance());
        fragments.add(ConcernFragment.newInstance());
        fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragments);
        pagerItem.setAdapter(fragmentAdapter);
        tabTitle.setupWithViewPager(pagerItem);
        for (int i=0;i<titles.length;i++){
            tabList.add(tabTitle.getTabAt(i));
            tabList.get(i).setText(titles[i]);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uBind.unbind();
    }
}
