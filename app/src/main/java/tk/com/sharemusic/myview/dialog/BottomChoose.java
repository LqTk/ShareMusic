package tk.com.sharemusic.myview.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import tk.com.sharemusic.R;
import tk.com.sharemusic.utils.PopWinUtil;

public class BottomChoose {
    View view;
    Context context;
    ClickListener clickListener;
    private PopWinUtil uiHandle;

    public BottomChoose(Context context) {
        this.context = context;
        init();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setUiHandle(PopWinUtil uiHandle) {
        this.uiHandle = uiHandle;
    }

    public View getView() {
        return view;
    }

    private void init() {
        view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_choose,null);
        TextView album = view.findViewById(R.id.tv_album);
        TextView alCamera = view.findViewById(R.id.tv_camera);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle!=null)
                    uiHandle.dismissMenu();
                if (clickListener!=null)
                    clickListener.album();
            }
        });
        alCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle!=null)
                    uiHandle.dismissMenu();
                if (clickListener!=null)
                    clickListener.camera();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiHandle!=null)
                    uiHandle.dismissMenu();
            }
        });
    }

    public interface ClickListener{
        void album();
        void camera();
    }

}
