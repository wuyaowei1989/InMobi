package com.android.funny;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.funny.bean.Constants;
import com.android.funny.component.ApplicationComponent;
import com.android.funny.ui.base.BaseActivity;
import com.kyview.InitConfiguration;
import com.kyview.interfaces.AdViewSpreadListener;
import com.kyview.manager.AdViewBannerManager;
import com.kyview.manager.AdViewInstlManager;
import com.kyview.manager.AdViewNativeManager;
import com.kyview.manager.AdViewSpreadManager;
import com.kyview.manager.AdViewVideoManager;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.spreadlayout)
    RelativeLayout mSpreadlayout;

    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    public static InitConfiguration initConfiguration;
    @Override
    public int getContentLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {

    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        //获取后台配置
        initConfiguration = new InitConfiguration.Builder(
                this).setUpdateMode(InitConfiguration.UpdateMode.EVERYTIME)
                .setBannerCloseble(InitConfiguration.BannerSwitcher.CANCLOSED)
				.setRunMode(InitConfiguration.RunMode.TEST)
                .build();
        //横幅 配置
        AdViewBannerManager.getInstance(this).init(initConfiguration,
                new String[] { Constants.AD_VIEW_KEY });
        //插屏 配置
        AdViewInstlManager.getInstance(this).init(initConfiguration,
                new String[] { Constants.AD_VIEW_KEY });
        //原生 配置
        AdViewNativeManager.getInstance(this).init(initConfiguration,
                new String[] { Constants.AD_VIEW_KEY });
        //开屏 配置
        AdViewSpreadManager.getInstance(this).init(initConfiguration,
                new String[] { Constants.AD_VIEW_KEY });
        //视频 配置
        AdViewVideoManager.getInstance(this).init(initConfiguration,
                new String[] { Constants.AD_VIEW_KEY });

        // 设置开屏下方LOGO，必须调用该方法
        AdViewSpreadManager.getInstance(this).setSpreadLogo(R.drawable.spread_logo);
        // 设置开屏背景颜色，可不设置
        AdViewSpreadManager.getInstance(this).setSpreadBackgroudColor(
                Color.WHITE);

        // 请求开屏广告(null:其他平台传null，广点通可以为null使用广点通默认布局，也可以传自定义跳过布局 (RelativeLayout) findViewById(R.id.skip_view))
        AdViewSpreadManager.getInstance(this).request(this, Constants.AD_VIEW_KEY, new AdViewSpreadListener() {
                    @Override
                    public void onAdDisplay(String s) {

                    }

                    @Override
                    public void onAdClose(String s) {
                        jump();
                    }

                    @Override
                    public void onAdRecieved(String s) {

                    }

                    @Override
                    public void onAdClick(String s) {

                    }

                    @Override
                    public void onAdFailed(String s) {
                        jump();
                    }

                    @Override
                    public void onAdSpreadNotifyCallback(String s, ViewGroup viewGroup, int i, int i1) {

                    }
                },
                (RelativeLayout) findViewById(R.id.spreadlayout), null);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public void initData() {

    }

    @Override
    public void onRetry() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return false;
        return super.onKeyDown(keyCode, event);
    }

    /**
     * onRestart()如果只使用单独竞价开屏，必须注释掉否则引起回调问题。如果和第三方一块来使用，必须打开否则有可能引起跳转问题。
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        // waitingOnRestart 需要自己控制
        //waitingOnRestart = true;
        jumpWhenCanClick();
    }


    public boolean waitingOnRestart = false;

    private void jump() {
        if(!AdViewSpreadManager.hasJumped) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    /*
     * 包含点击的开屏广告时会调用该方法广告
     */
    private void jumpWhenCanClick() {
        if ((this.hasWindowFocus() || waitingOnRestart) && !AdViewSpreadManager.hasJumped) {
            this.startActivity(new Intent(this, MainActivity.class));
            // adSpreadManager.setAdSpreadInterface(null);
            this.finish();
        } else {
            waitingOnRestart = true;
        }

    }

}
