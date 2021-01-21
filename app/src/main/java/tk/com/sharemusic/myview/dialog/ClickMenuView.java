package tk.com.sharemusic.myview.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import tk.com.sharemusic.R;

public class ClickMenuView {
    private View view;

    private Context context;

    private ItemClickListener clickListener;
    private LinearLayout llDeleteItem;
    private LinearLayout llItemReplay;

    public ClickMenuView(Context context) {
        this.context = context;
        init();
    }

    public View getView() {
        return view;
    }

    private void init() {
        view = LayoutInflater.from(context).inflate(R.layout.layout_click_menu,null);
        LinearLayout llDown = view.findViewById(R.id.ll_down);
        llDeleteItem = view.findViewById(R.id.ll_item_delete);
        llItemReplay = view.findViewById(R.id.ll_item_replay);
        llDeleteItem.setVisibility(View.VISIBLE);
        llItemReplay.setVisibility(View.GONE);
        LinearLayout llReport = view.findViewById(R.id.ll_report);
        LinearLayout llDelete = view.findViewById(R.id.ll_delete);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        llDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.cancel();
            }
        });
        llReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.report();
            }
        });
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.delete();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.cancel();
            }
        });
        llItemReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.replay();
            }
        });
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void showReplay(int isShow){
        if (llItemReplay!=null)
            llItemReplay.setVisibility(isShow);
    }

    public void setShowItemDelete(boolean isShowing){
        if (llDeleteItem!=null)
            if (isShowing) llDeleteItem.setVisibility(View.VISIBLE);
            else llDeleteItem.setVisibility(View.GONE);
    }

    public interface ItemClickListener{
        void cancel();
        void delete();
        void report();
        void replay();
    }
}
