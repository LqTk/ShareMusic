package tk.com.sharemusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.network.HttpMethod;
import tk.com.sharemusic.network.NetWorkService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_doctor_name)
    TextView tvDoctorName;
    @BindView(R.id.tv_doctor_title)
    TextView tvDoctorTitle;
    @BindView(R.id.btn_profile)
    TextView btnProfile;
    @BindView(R.id.rcv_chat)
    RecyclerView rcvChat;
    @BindView(R.id.refresh_view)
    SmartRefreshLayout refreshView;
    @BindView(R.id.icon_status)
    ImageView iconStatus;
    @BindView(R.id.ll_recorder_anim)
    LinearLayout llRecorderAnim;
    @BindView(R.id.tv_sec)
    TextView tvSec;
    @BindView(R.id.layout_recording_mask)
    RelativeLayout layoutRecordingMask;
    @BindView(R.id.tv_switch)
    ImageView tvSwitch;
    @BindView(R.id.ll_voice_text)
    LinearLayout llVoiceText;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_faces)
    ImageView ivFaces;
    @BindView(R.id.iv_functions)
    ImageView ivFunctions;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.layout_text)
    LinearLayout layoutText;
    @BindView(R.id.btn_press_to_speak)
    TextView btnPressToSpeak;
    @BindView(R.id.layout_full)
    LinearLayout layoutFull;
    @BindView(R.id.empty)
    TextView empty;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder bind;
    private NetWorkService service;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SocialPublic.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.layout_chat, container, false);
        bind = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        service = HttpMethod.getInstance().create(NetWorkService.class);
        initData();
    }

    private void initData() {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @OnClick({R.id.btn_back, R.id.btn_profile, R.id.refresh_view, R.id.ll_recorder_anim, R.id.ll_voice_text, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                break;
            case R.id.btn_profile:
                break;
            case R.id.refresh_view:
                break;
            case R.id.ll_recorder_anim:
                break;
            case R.id.ll_voice_text:
                break;
            case R.id.btn_send:
                break;
        }
    }
}