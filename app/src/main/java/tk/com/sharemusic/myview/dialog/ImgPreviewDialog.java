package tk.com.sharemusic.myview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import tk.com.sharemusic.R;
import tk.com.sharemusic.adapter.PreImgAdapter;
import tk.com.sharemusic.myview.CircleImage;

public class ImgPreviewDialog extends Dialog {
    private PhotoView imageView;
    private PhotoViewClick photoViewClick;
    private SubsamplingScaleImageView longImageView;
    ViewPager viewPagerImg;
    LinearLayout linearLayout;
    PreImgAdapter adapter;
    List<String> pathLists = new ArrayList<>();
    private int current = 0;

    public ImgPreviewDialog(@NonNull Context context, List<String> strings) {
        this(context, R.style.imgPreviewDialog, strings);
    }

    public ImgPreviewDialog(@NonNull Context context, int themeResId, List<String> stringlist) {
        super(context, themeResId);
        init(context, stringlist);
    }

    protected ImgPreviewDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init(Context context, List<String> stringList) {
        setCancelable(true);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_preview_img,null);
        viewPagerImg = v.findViewById(R.id.view_pager_img);
        linearLayout = v.findViewById(R.id.ll_round_img);
        for (int i=0;i<stringList.size();i++){
            CircleImage image = new CircleImage(context);
            if (i!=current){
                image.setImageResource(R.drawable.round_bg_grey);
            }else {
                image.setImageResource(R.drawable.round_bg_blue);
            }
            linearLayout.addView(image,i);
        }

        adapter = new PreImgAdapter(stringList,context);
        viewPagerImg.setAdapter(adapter);
        adapter.setClickListener(new PreImgAdapter.ClickListener() {
            @Override
            public void picClick() {
                if (photoViewClick!=null)
                    photoViewClick.ImgClick();
            }
        });

        viewPagerImg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


        setContentView(v);
    }

    /**
     * 设置显示哪张图片
     * @param pos
     */
    public void setShowPos(int pos){
        if (pos>adapter.getData().size()){
            viewPagerImg.setCurrentItem(0);
            return;
        }
        viewPagerImg.setCurrentItem(pos);
    }

    private void setCircle(int pos){
        if (current!=pos){
            int childCount = linearLayout.getChildCount();
            for (int i=0;i<childCount;i++){
                CircleImage circleImage = (CircleImage) linearLayout.getChildAt(i);
                if (i!=pos){
                    circleImage.setImageResource(R.drawable.round_bg_grey);
                }else {
                    circleImage.setImageResource(R.drawable.round_bg_blue);
                }
            }
            current = pos;
        }
    }

    public void setPhotoViewClick(PhotoViewClick photoViewClick) {
        this.photoViewClick = photoViewClick;
    }

    public interface PhotoViewClick{
        void ImgClick();
    }
}
