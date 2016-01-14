package com.ssthouse.moduo.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.cons.scan.ScanCons;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.event.scan.ScanDeviceEvent;
import com.ssthouse.moduo.bean.event.video.SessionStateEvent;
import com.ssthouse.moduo.bean.event.video.StreamerConnectChangedEvent;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.ScanUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.activity.LoadingActivity;
import com.ssthouse.moduo.main.view.activity.SettingActivity;
import com.ssthouse.moduo.main.view.fragment.AboutModuoFragment;
import com.ssthouse.moduo.main.view.fragment.MainFragment;
import com.ssthouse.moduo.main.view.fragment.ShareDeviceFragment;
import com.ssthouse.moduo.main.view.fragment.UserInfoFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 当前activity不监听设备数据传达的event
 */
public class MainActivity extends AppCompatActivity implements MainView {

    private static final String EXTRA_IS_LOGIN_SUCCESS = "isLoginSuccess";
    /**
     * 点两次退出程序
     */
    private long exitTimeInMils = 0;
    /**
     * 是否是退出到loading activity
     * 决定是否要kill 当前线程
     */
    private boolean isLogOut = false;

    private FragmentManager fragmentManager;
    private ShareDeviceFragment shareDeviceFragment;
    private AboutModuoFragment aboutModuoFragment;
    private UserInfoFragment userInfoFragment;
    private MainFragment mainFragment;

    public enum FragmentState {
        SHARE_DEVICE_FRAGMENT, ABOUT_MODUO_FRAGMENT, USER_INFO_FRAGMENT, MAIN_FRAGMENT;
    }

    public FragmentState currentFragmentState = FragmentState.MAIN_FRAGMENT;

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    @Bind(R.id.id_drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.id_navigation_view)
    NavigationView navigationView;

    private MaterialDialog waitDialog;

    /**
     * 启动当前activity
     *
     * @param context
     * @param isLoginSuccess
     */
    public static void start(Context context, boolean isLoginSuccess) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_IS_LOGIN_SUCCESS, isLoginSuccess);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        initView();
        initFragment();

        //初始化视频sdk
        communication = Communication.getInstance(this);
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        mainFragment = new MainFragment();
        userInfoFragment = new UserInfoFragment();
        aboutModuoFragment = new AboutModuoFragment();
        shareDeviceFragment = new ShareDeviceFragment();
        //初始化为MainFragment
        fragmentManager.beginTransaction().add(R.id.id_fragment_container, mainFragment).commit();
        fragmentManager.beginTransaction().add(R.id.id_fragment_container, userInfoFragment).commit();
        fragmentManager.beginTransaction().hide(userInfoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.id_fragment_container, aboutModuoFragment).commit();
        fragmentManager.beginTransaction().hide(aboutModuoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.id_fragment_container, shareDeviceFragment).commit();
        fragmentManager.beginTransaction().hide(shareDeviceFragment).commit();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tb);
        setSupportActionBar(toolbar);

        //初始化抽屉事件
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        //抽屉中的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.id_menu_main:
                        switchFragment(FragmentState.MAIN_FRAGMENT);
                        break;
                    case R.id.id_menu_user_info:
                        switchFragment(FragmentState.USER_INFO_FRAGMENT);
                        break;
                    case R.id.id_menu_about_moduo:
                        switchFragment(FragmentState.ABOUT_MODUO_FRAGMENT);
                        break;
                    case R.id.id_menu_share_device:
                        switchFragment(FragmentState.SHARE_DEVICE_FRAGMENT);
                        break;
                    case R.id.id_menu_setting:
                        SettingActivity.start(MainActivity.this);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        //初始化dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 切换fragment
     *
     * @param newState
     */
    private void switchFragment(FragmentState newState) {
        //隐藏当前fragment
        Fragment currentFragment = null;
        switch (currentFragmentState) {
            case MAIN_FRAGMENT:
                currentFragment = mainFragment;
                break;
            case USER_INFO_FRAGMENT:
                currentFragment = userInfoFragment;
                break;
            case ABOUT_MODUO_FRAGMENT:
                currentFragment = aboutModuoFragment;
                break;
            case SHARE_DEVICE_FRAGMENT:
                currentFragment = shareDeviceFragment;
                break;
        }
        fragmentManager.beginTransaction().hide(currentFragment).commit();
        //显示toFragment
        Fragment toFragment = null;
        switch (newState) {
            case MAIN_FRAGMENT:
                currentFragmentState = FragmentState.MAIN_FRAGMENT;
                toFragment = mainFragment;
                break;
            case USER_INFO_FRAGMENT:
                currentFragmentState = FragmentState.USER_INFO_FRAGMENT;
                toFragment = userInfoFragment;
                break;
            case ABOUT_MODUO_FRAGMENT:
                currentFragmentState = FragmentState.ABOUT_MODUO_FRAGMENT;
                toFragment = aboutModuoFragment;
                break;
            case SHARE_DEVICE_FRAGMENT:
                currentFragmentState = FragmentState.SHARE_DEVICE_FRAGMENT;
                toFragment = shareDeviceFragment;
                break;
        }
        fragmentManager.beginTransaction().show(toFragment).commit();
    }

    @Override
    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public void dismissDialog() {
        waitDialog.dismiss();
    }


    /*
    视频SDK回调
     */

    /**
     * 视频设备连接状态事件
     *
     * @param event
     */
    public void onEventMainThread(SessionStateEvent event) {
        //刷新lv
    }

    /**
     * 视频采集端连接状态回调
     *
     * @param event
     */
    public void onEventMainThread(StreamerConnectChangedEvent event) {
        Timber.e("我接收到视频sdk状态更新");
        //修改设备状态
        for (Device device : XPGController.getDeviceList()) {
            if (device.getCidNumber() == event.getCidNumber()) {
                device.setStreamerPresenceState(event.getState());
                Timber.e("我更新了视频sdk状态");
            }
        }
        //更新界面
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_get_bound_device:
                break;
            case R.id.id_action_log_out:
                SettingManager.getInstance(this).clean();
                PreferenceHelper.getInstance(this).setIsFistIn(true);
                //重新进入loading activity
                LoadingActivity.start(this);
                //结束当前activity
                isLogOut = true;
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //// TODO: 2016/1/10
        communication.destory();//销毁sdk
        //todo---完全退出程序(若不是跳转到登陆界面)
        if (!isLogOut) {
            android.os.Process.killProcess(android.os.Process.myPid());//确保完全退出，释放所有资源
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() < (exitTimeInMils + 1500)) {
            super.onBackPressed();
        } else {
            exitTimeInMils = System.currentTimeMillis();
            ToastHelper.show(this, "再次点击退出");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 获取扫描二维码的结果
         */
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Timber.e("Cancelled scan");
            } else {
                String text = result.getContents();
                //机智云sdk参数
                String product_key = ScanUtil.getParamFromUrl(text, ScanCons.KEY_PRODUCT_KEY);
                String did = ScanUtil.getParamFromUrl(text, ScanCons.KEY_DID);
                String passCode = ScanUtil.getParamFromUrl(text, ScanCons.KEY_PASSCODE);
                //视频sdk参数
                String cidStr = ScanUtil.getParamFromUrl(text, ScanCons.KEY_CID_NUMBER);
                String username = ScanUtil.getParamFromUrl(text, ScanCons.KEY_USER_NAME);
                String password = ScanUtil.getParamFromUrl(text, ScanCons.KEY_PASSWORD);
                //判断二维码扫描数据是否正确
                if (product_key == null
                        || did == null
                        || passCode == null
                        || cidStr == null
                        || username == null
                        || password == null) {
                    ToastHelper.showLong(this, "请扫描正确的二维码");
                    return;
                }
                long cidNumber = Long.parseLong(cidStr);
                Timber.e("机智云参数: " + "product_key:\t" + product_key + "\tdid:\t" + did + "\tpasscode:\t" + passCode);
                Timber.e("视频sdk参数: " + "cidNumber:\t" + cidNumber + "\tusername:\t" + username + "\tpassword:\t" + password);
                //将当前设备数据保存在本地
                PreferenceHelper.getInstance(this).addDevice(did, cidNumber, username, password);
                //抛出扫描
                EventBus.getDefault().post(new ScanDeviceEvent(true, did, passCode));
            }
        } else {
            ToastHelper.show(this, "二维码解析为空");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
