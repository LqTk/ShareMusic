package tk.com.sharemusic.myview.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import tk.com.sharemusic.R;
import tk.com.sharemusic.adapter.FragmentAdapter;
import tk.com.sharemusic.adapter.MenuAdapter;
import tk.com.sharemusic.entity.HeadItem;
import tk.com.sharemusic.myview.CircleImage;
import tk.com.sharemusic.utils.PopWinUtil;

public class PagerMenuGridPicker {

    private LinearLayout simplePanel;
    private Button cancelBtn;
    private Button btnOk;
    private View ok_divider;
    private LinearLayout consolePanel;
    private int current = 0;

    public interface OnOkClickListener {
        void onOkClick();
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }

    public interface OnPagerGridItemClickListener {
        void onPagerGridItemClick(HeadItem item);
    }

    public interface OnConsoleButtonClickLister{
        void onVoiceButtonClick();
        void onContentEditClick();
        void onFacesButtonClick();
        void onImageButtonClick();
    }

    View view;
    PopWinUtil uiHandle;
    AppCompatActivity mContext;
    static final int DEFAULT_NUM_COLUMN = 4;
    boolean asConsole = false;

    private TextView tvTitle;
    ImageView tvSwitch;
    EditText etContent;
    ImageView ivFaces;
    ImageView ivFunctions;
    private ViewPager pager;
    private LinearLayout linearLayout;

    OnOkClickListener onOkClickListener;
    OnCancelClickListener onCancelClickListener;
    OnPagerGridItemClickListener onPagerGridItemClickListener;
    OnConsoleButtonClickLister onConsoleButtonClickLister;

    int consoleWhich = 0;
    List<HeadItem> avatarItems;
    List<List<HeadItem>> pageList;
    int capacity = 8;
    int gridNumColum = 4;

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public View getView() {
        return view;
    }

    public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
        this.onOkClickListener = onOkClickListener;
    }

    public void setOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public void setOnPagerGridItemClickListener(OnPagerGridItemClickListener onPagerGridItemClickListener) {
        this.onPagerGridItemClickListener = onPagerGridItemClickListener;
    }

    public void setOnConsoleButtonClickLister(OnConsoleButtonClickLister onConsoleButtonClickLister) {
        this.onConsoleButtonClickLister = onConsoleButtonClickLister;
    }

    public void setUiHandle(PopWinUtil uiHandle) {
        this.uiHandle = uiHandle;
    }

    public void setAsConsole(boolean asConsole) {
        this.asConsole = asConsole;
        simplePanel.setVisibility(View.GONE);
        consolePanel.setVisibility(View.VISIBLE);
    }

    public void setConsoleWhich(int consoleWhich) {
        this.consoleWhich = consoleWhich;
        switch (consoleWhich){
            case 0:
                ivFaces.setImageResource(R.drawable.ic_keyborad);
                break;
            case 1:
                ivFunctions.setImageResource(R.drawable.ic_keyborad);
                break;
        }
    }

    public PagerMenuGridPicker(AppCompatActivity mContext, List<HeadItem> avatarItem, int capacity, int gridNumColum) throws Exception {
        this.mContext = mContext;
        this.avatarItems = avatarItem;
        this.capacity = capacity;
        this.gridNumColum = gridNumColum;
        init();
    }

    private void init() throws Exception {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.ui_pager_menu_grid_picker,null);

        simplePanel = (LinearLayout) view.findViewById(R.id.simple);
        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        btnOk = (Button) view.findViewById(R.id.okBtn);
        ok_divider = view.findViewById(R.id.ok_divider);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        consolePanel = (LinearLayout) view.findViewById(R.id.console);
        tvSwitch = (ImageView) view.findViewById(R.id.tv_switch);
        etContent = (EditText) view.findViewById(R.id.et_content);
        ivFaces = (ImageView) view.findViewById(R.id.iv_faces);
        ivFunctions = (ImageView) view.findViewById(R.id.iv_functions);

        pager = (ViewPager) view.findViewById(R.id.pager);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll_round_img);

        if (!asConsole) {
            simplePanel.setVisibility(View.VISIBLE);
            consolePanel.setVisibility(View.GONE);
        }

        if (avatarItems == null || avatarItems.size() == 0 || capacity == 0) {
            throw new Exception("list is null or size is 0, or capacity is 0!");
        }

        pageList = makePageList(avatarItems,capacity);
        for (int i=0;i<pageList.size();i++){
            CircleImage image = new CircleImage(mContext);
            if (i!=current){
                image.setImageResource(R.drawable.round_bg_grey);
            }else {
                image.setImageResource(R.drawable.round_bg_blue);
            }
            linearLayout.addView(image,i);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(pageList);
        pager.setAdapter(viewPagerAdapter);
        pager.setCurrentItem(current);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (current!=position){
                    int childCount = linearLayout.getChildCount();
                    for (int i=0;i<childCount;i++){
                        CircleImage circleImage = (CircleImage) linearLayout.getChildAt(i);
                        if (i!=position){
                            circleImage.setImageResource(R.drawable.round_bg_grey);
                        }else {
                            circleImage.setImageResource(R.drawable.round_bg_blue);
                        }
                    }
                    current = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle != null)
                    uiHandle.dismissMenu();
                if (onOkClickListener != null) {
                    onOkClickListener.onOkClick();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle != null)
                    uiHandle.dismissMenu();
                if (onCancelClickListener != null) {
                    onCancelClickListener.onCancelClick();
                }
            }
        });
        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle != null)
                    uiHandle.dismissMenu();
                if (onConsoleButtonClickLister != null) onConsoleButtonClickLister.onVoiceButtonClick();
            }
        });
        etContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle != null)
                    uiHandle.dismissMenu();
                if (onConsoleButtonClickLister != null) onConsoleButtonClickLister.onContentEditClick();
            }
        });
        ivFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle != null)
                    uiHandle.dismissMenu();
                if (onConsoleButtonClickLister != null) onConsoleButtonClickLister.onFacesButtonClick();
            }
        });
        ivFunctions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle != null)
                    uiHandle.dismissMenu();
                if (onConsoleButtonClickLister != null) onConsoleButtonClickLister.onImageButtonClick();
            }
        });
    }


    private List<List<HeadItem>> makePageList(List<HeadItem> menuItems, int pageCapacity) {
        List<List<HeadItem>> pages = new ArrayList<List<HeadItem>>();

        if (menuItems.size() > pageCapacity) {
            for (int i = 0; i <= menuItems.size() / pageCapacity; i++) {
                List<HeadItem> tmp = new ArrayList<HeadItem>();
                for (int j = 0; j < pageCapacity; j++) {
                    if (i * pageCapacity + j >= menuItems.size()) {
                        continue;
                    } else {
                        tmp.add(menuItems.get(i * pageCapacity + j));
                    }
                }
                if (tmp.size() > 0) pages.add(tmp);
            }
        } else {
            pages.add(menuItems);
        }
        return pages;
    }

    class ViewPagerAdapter extends PagerAdapter{

        private List<List<HeadItem>> lists;

        public ViewPagerAdapter(List<List<HeadItem>> lists) {
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View v = createView(position);
            container.addView(v,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            return v;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
        }
    }

    private View createView(int position){
        View view = mContext.getLayoutInflater().inflate(R.layout.menu_layout,null);
        GridView gridView = view.findViewById(R.id.gv);
        gridView.setNumColumns(gridNumColum);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (uiHandle!=null){
                    uiHandle.dismissMenu();
                }
                HeadItem item = (HeadItem) parent.getItemAtPosition(position);
                if (onPagerGridItemClickListener!=null){
                    onPagerGridItemClickListener.onPagerGridItemClick(item);
                }
            }
        });
        MenuAdapter adapter = new MenuAdapter(pageList.get(position),mContext);
        gridView.setAdapter(adapter);
        return view;
    }

}
