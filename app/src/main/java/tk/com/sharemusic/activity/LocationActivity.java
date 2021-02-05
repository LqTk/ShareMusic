package tk.com.sharemusic.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import tk.com.sharemusic.R;
import tk.com.sharemusic.ShareApplication;
import tk.com.sharemusic.adapter.SearchPoiAdapter;
import tk.com.sharemusic.entity.SearchLocationEntity;

public class LocationActivity extends CommonActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.rcy_poi)
    RecyclerView rcyPoi;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.iv_search_iv)
    ImageView ivSearchIv;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_delete_search)
    ImageView ivDeleteSearch;
    @BindView(R.id.rcv_location)
    RecyclerView rcvLocation;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_pro)
    LinearLayout llPro;
    @BindView(R.id.srf_load_location)
    SmartRefreshLayout srfLoadLocation;
    @BindView(R.id.srf_load_poi)
    SmartRefreshLayout srfLoadPoi;


    private Unbinder bind;
    private Context context;
    private boolean showLocation = false;
    private List<SearchLocationEntity> locationLists = new ArrayList<>();
    private List<SearchLocationEntity> searchLists = new ArrayList<>();
    private int select = 0;
    private String city;
    private String address;
    private double latitude;
    private double longitude;
    private SearchPoiAdapter locationAdapter;
    private SearchPoiAdapter searchAdapter;
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(lastStr)) {
                startPoiSearch(lastStr, searchPage);
            }
        }
    };

    private PoiSearch poiSearch;
    private PoiSearch.OnPoiSearchListener poiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            ArrayList<PoiItem> pois = poiResult.getPois();
            srfLoadLocation.finishLoadMore();
            srfLoadPoi.finishLoadMore();
            if (llTop.getVisibility() == View.VISIBLE) {
                llPro.setVisibility(View.GONE);
                Log.d("poiResult", poiResult.getPois().size() + ",i=" + i);
                if (pois.size() > 0) {
                    locationAdapter.addData(1, new SearchLocationEntity(pois.get(0).getCityName(), "",
                            pois.get(0).getLatLonPoint().getLatitude(), pois.get(0).getLatLonPoint().getLongitude(), false));
                }
                for (PoiItem poiItem : pois) {
                    String titile = poiItem.getTitle();
                    String ads = poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet();
                    if (!titile.equals(city) || !ads.equals(address)) {
                        city = titile;
                        address = ads;
                        latitude = poiItem.getLatLonPoint().getLatitude();
                        longitude = poiItem.getLatLonPoint().getLongitude();
                        locationAdapter.addData(new SearchLocationEntity(city, address, latitude, longitude, false));
                    }
                }
            } else {
                if (searchPage==1) {
                    searchLists.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                for (PoiItem poiItem : pois) {
                    String titile = poiItem.getTitle();
                    String ads = poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet();
                    searchAdapter.addData(new SearchLocationEntity(titile, ads, poiItem.getLatLonPoint().getLatitude(),
                            poiItem.getLatLonPoint().getLongitude(), false));
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };
    //定位
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    ShareApplication.showLocation = true;
                    //可在其中解析amapLocation获取相应内容。
                    ShareApplication.locationStr = aMapLocation.getProvince()+aMapLocation.getCity()+aMapLocation.getDistrict()+aMapLocation.getStreet();
                    ShareApplication.latitude = aMapLocation.getLatitude();//获取纬度
                    ShareApplication.longitude = aMapLocation.getLongitude();//获取经度
                    ShareApplication.cityCode = aMapLocation.getCityCode();

                    showLocation = true;
                    //可在其中解析amapLocation获取相应内容。
                    city = aMapLocation.getCity();
                    latitude = aMapLocation.getLatitude();//获取纬度
                    longitude = aMapLocation.getLongitude();//获取经度
                    llPro.setVisibility(View.VISIBLE);
                    startPoiSearch("", locationPage);
                    mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private int locationPage = 1;//定位page
    private int searchPage = 1;//搜索page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        bind = ButterKnife.bind(this);
        context = this;
        initView();
        getData();
        initPoi();
    }

    private void initPoi() {
        if (showLocation) {
            llPro.setVisibility(View.VISIBLE);
            startPoiSearch("", locationPage);
        } else {
            initLocation();
        }
    }

    private void startPoiSearch(String key, int page) {
        PoiSearch.Query query = new PoiSearch.Query(key, "", ShareApplication.cityCode);
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);//设置查询页码
        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(poiSearchListener);
        if (TextUtils.isEmpty(key)) {
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude,
                    longitude), 1000));//设置周边搜索的中心点以及半径
        }
        poiSearch.searchPOIAsyn();
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rcvLocation.setLayoutManager(linearLayoutManager);
        locationAdapter = new SearchPoiAdapter(R.layout.layout_poi_search, locationLists);
        rcvLocation.setAdapter(locationAdapter);
        locationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                locationAdapter.getData().get(select).setCheck(false);
                select = position;
                locationAdapter.getData().get(select).setCheck(true);
                SearchLocationEntity searchLocationEntity = locationLists.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", searchLocationEntity.latitude);
                bundle.putDouble("longitude", searchLocationEntity.longitude);
                bundle.putString("city", searchLocationEntity.city);
                bundle.putString("address", searchLocationEntity.address);
                intent.putExtra("data", bundle);
                setResult(Activity.RESULT_OK, intent);
                LocationActivity.this.finish();
            }
        });
        srfLoadLocation.setEnableRefresh(false);
        srfLoadLocation.setEnableLoadMore(true);
        srfLoadLocation.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                locationPage++;
                llPro.setVisibility(View.VISIBLE);
                startPoiSearch("",locationPage);
            }
        });

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);

        rcyPoi.setLayoutManager(linearLayoutManager1);
        searchAdapter = new SearchPoiAdapter(R.layout.layout_poi_search, searchLists);
        rcyPoi.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SearchLocationEntity searchLocationEntity = searchLists.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", searchLocationEntity.latitude);
                bundle.putDouble("longitude", searchLocationEntity.longitude);
                bundle.putString("city", searchLocationEntity.city);
                bundle.putString("address", searchLocationEntity.address);
                intent.putExtra("data", bundle);
                setResult(Activity.RESULT_OK, intent);
                LocationActivity.this.finish();
            }
        });
        srfLoadPoi.setEnableRefresh(false);
        srfLoadPoi.setEnableLoadMore(true);
        srfLoadPoi.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                searchPage++;
                startPoiSearch(lastStr,searchPage);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && !lastStr.equals(s.toString())) {
                    ivDeleteSearch.setVisibility(View.VISIBLE);
                    searchPage = 1;
                    lastStr = s.toString().trim();
                    mHandler.removeCallbacks(runnable);
                    mHandler.postDelayed(runnable, 500);
                }else {
                    ivDeleteSearch.setVisibility(View.GONE);
                    searchLists.clear();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    String lastStr = "";

    private void getData() {
        locationAdapter.addData(new SearchLocationEntity("不显示位置", "", 0d, 0d, true));
        showLocation = getIntent().getBooleanExtra("locationed", false);
        if (showLocation) {
            city = getIntent().getStringExtra("city");
            address = getIntent().getStringExtra("address");
            latitude = getIntent().getDoubleExtra("latitude", 0d);
            longitude = getIntent().getDoubleExtra("longitude", 0d);
//            locationAdapter.addData(new SearchLocationEntity(city,"",latitude,longitude,false));
            locationAdapter.addData(new SearchLocationEntity(city, address, latitude, longitude, true));
            select = 2;
            locationLists.get(0).setCheck(false);
            locationAdapter.notifyItemChanged(0, "state");
        }
    }

    @OnClick({R.id.btn_back, R.id.iv_search, R.id.tv_cancel, R.id.iv_delete_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                setResult(0, null);
                LocationActivity.this.finish();
                break;
            case R.id.iv_search:
                llTop.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel:
                etSearch.setText("");
                llTop.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                break;
            case R.id.iv_delete_search:
                etSearch.setText("");
                searchLists.clear();
                searchAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        if (mLocationClient!=null) {
            mLocationClient.onDestroy();
        }//销毁定位客户端，同时销毁本地定位服务。
    }
}