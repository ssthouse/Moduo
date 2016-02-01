package com.ssthouse.moduo.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.fragment.video.VideoFragment;

import timber.log.Timber;

/**
 * 视频对话activity
 * Created by ssthouse on 2015/12/17.
 */
public class VideoActivity extends AppCompatActivity {

    //fragment管理
    private static final String TAG_VIDEO_FRAGMENT = "videoFragment";
    private FragmentManager fragmentManager;
    private VideoFragment videoFragment;

    //是否竖屏
    public static boolean isPortrait = true;

    //等待dialog
    private MaterialDialog waitDialog;

    /**
     * 启动当前activity
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, VideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.e("onCreate");
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //横屏就全屏
        if (!isPortrait) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_video);
        initView();
        initFragment();
    }

    private void initFragment() {
        //初始化fragment
        fragmentManager = getSupportFragmentManager();
        videoFragment = (VideoFragment) fragmentManager.findFragmentByTag(TAG_VIDEO_FRAGMENT);
        if (videoFragment == null) {
            Timber.e("init fragment");
            videoFragment = new VideoFragment();
            //改变界面
            fragmentManager.beginTransaction()
                    .replace(R.id.id_fragment_container, videoFragment, TAG_VIDEO_FRAGMENT)
                    .commit();
        }
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("视频控制");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    //关闭当前视频---及界面
    private void closeVideo() {
        //关闭视频数据点
        XPGController.getInstance(this).getmCenter().cWriteVideo(
                XPGController.getCurrentDevice().getXpgWifiDevice(), 0
        );
        //关闭activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeVideo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeVideo();
    }
}
