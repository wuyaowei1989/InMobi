package com.android.funny.ui.jandan;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.funny.R;
import com.android.funny.bean.Constants;
import com.android.funny.component.ApplicationComponent;
import com.android.funny.ui.base.BaseFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kyview.interfaces.AdViewNativeListener;
import com.kyview.manager.AdViewNativeManager;
import com.kyview.natives.NativeAdInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by 14400 on 2018/3/16.
 */

public class AdFragment extends BaseFragment {

    @BindView(R.id.list)
    ListView listView;

    public static String HTML = "<meta charset='utf-8'><style type='text/css'>* { padding: 0px; margin: 0px;}a:link { text-decoration: none;}</style><div  style='width: 100%; height: 100%;'><img src=\"image_path\" width=\"100%\" height=\"100%\" ></div>";
    private NativeAdAdapter adAdapter;
    private ArrayList<Data> list;

    @Override
    public int getContentLayout() {
        return R.layout.fragment_adnative;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {

        list = new ArrayList<Data>();
        for (int i = 0; i < 10; i++) {
            Data data = new Data();
            data.icon = "http://www.adview.cn/static/images/logo_1.png";
            data.title = "AdView,移动广告交易平台";
            data.descript = "AdView是国内首个“独立第三方”移动广告交易平台（AdExchange）";
            list.add(data);
        }

        adAdapter = new NativeAdAdapter(getContext(), list);

        listView.setAdapter(adAdapter);
        AdViewNativeManager.getInstance(getContext()).requestAd(getContext(),
                Constants.AD_VIEW_KEY, 4, new AdViewNativeListener() {
                    @Override
                    public void onAdFailed(String s) {
                        Log.d("AdView", "error: " +s);
                    }

                    @Override
                    public void onAdRecieved(String s, ArrayList<NativeAdInfo> arrayList) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            Data data = new Data();
                            NativeAdInfo nativeAdInfo = (NativeAdInfo) arrayList.get(i);
                            data.descript = nativeAdInfo.getDescription();
                            data.icon = nativeAdInfo.getIconUrl();
                            data.title = ((NativeAdInfo) arrayList.get(i)).getTitle();
                            data.adInfo = (NativeAdInfo) arrayList.get(i);
                            ((NativeAdInfo) arrayList.get(i)).getIconHeight();
                            data.isAd = true;
                            Log.i("原生信息：", "data.descript: " + data.descript + "\ndata.icon: "
                                    + data.icon + "\ndata.title:" + data.title);
                            list.add(3, data);
                            ((NativeAdInfo) arrayList.get(i)).onDisplay(new View(
                                    getContext()));
                        }
                        adAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAdStatusChanged(String s, int i) {

                    }
                });
    }

    @Override
    public void initData() {

    }

    public static AdFragment newInstance() {
        Bundle args = new Bundle();
        AdFragment fragment = new AdFragment();
        fragment.setArguments(args);
        return fragment;
    }

    class NativeAdAdapter extends BaseAdapter {
        private List<Data> list;
        private Context context;

        public NativeAdAdapter(Context context, List<Data> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (null != convertView) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.native_ad_item, parent,
                        false);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.webView = (WebView) convertView.findViewById(R.id.icon);
                holder.desc = (TextView) convertView.findViewById(R.id.desc);
                holder.logo = (TextView) convertView.findViewById(R.id.logo);
                convertView.setTag(holder);
            }
            holder.webView.setVerticalScrollBarEnabled(false);
            holder.webView.setHorizontalScrollBarEnabled(false);
            holder.webView.loadData((new String(HTML)).replace("image_path",
                    list.get(position).icon), "text/html; charset=UTF-8", null);
            holder.title.setText(list.get(position).title);
            holder.desc.setText(list.get(position).descript);
            if (list.get(position).isAd)
                holder.logo.setVisibility(View.VISIBLE);
            else
                holder.logo.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Data data = list.get(position);
                    if (data.isAd) {
                        data.adInfo.onClick(v);
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            private WebView webView;
            private TextView title;
            private TextView desc;
            private TextView logo;
        }
    }

    class Data {
        private String icon;
        private String title;
        private String descript;
        public boolean isAd;
        public NativeAdInfo adInfo;
    }
}
