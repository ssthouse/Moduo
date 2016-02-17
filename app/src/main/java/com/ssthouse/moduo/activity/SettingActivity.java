package com.ssthouse.moduo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.setting.CommonIssueFragment;
import com.ssthouse.moduo.fragment.setting.IssueFeedbackFragment;
import com.ssthouse.moduo.fragment.setting.SettingListFragment;
import com.ssthouse.moduo.fragment.setting.UserTermFragment;
import com.ssthouse.moduo.model.event.view.SettingAtyStateEvent;

import de.greenrobot.event.EventBus;

/**
 * 设置
 * Created by ssthouse on 2016/1/13.
 */
public class SettingActivity extends AppCompatActivity {

    //管理fragment
    private FragmentManager fragmentManager;
    private SettingListFragment settingListFragment;
    private IssueFeedbackFragment issueFeedbackFragment;
    private CommonIssueFragment commonIssueFragment;
    private UserTermFragment userTermFragment;

    //当前状态
    private State currentState;
    //fragment状态
    public enum State {
        //列表
        STATE_SETTING_LIST,
        //常见问题
        STATE_COMMON_ISSUE,
        //问题反馈
        STATE_ISSUE_FEEDBACK,
        //使用条款
        STATE_USER_TERM
    }

    /**
     * 启动当前Activity
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
        //修改切换动画
        Activity currentActivity = (Activity) context;
        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_setting);

        currentState = State.STATE_SETTING_LIST;
        initView();
        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        settingListFragment = new SettingListFragment();
        issueFeedbackFragment = new IssueFeedbackFragment();
        commonIssueFragment = new CommonIssueFragment();
        userTermFragment = new UserTermFragment();

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.id_fragment_container, settingListFragment)
                .add(R.id.id_fragment_container, commonIssueFragment)
                .hide(commonIssueFragment)
                .add(R.id.id_fragment_container, issueFeedbackFragment)
                .hide(issueFeedbackFragment)
                .add(R.id.id_fragment_container, userTermFragment)
                .hide(userTermFragment)
                .commit();
    }

    //切换状态
    public void changeState(State newState) {
        if (currentState == newState) {
            return;
        }
        //隐藏当前fragment
        Fragment currentFragment = null;
        Fragment toFragment = null;
        switch (currentState) {
            case STATE_SETTING_LIST:
                currentFragment = settingListFragment;
                break;
            case STATE_COMMON_ISSUE:
                currentFragment = commonIssueFragment;
                break;
            case STATE_ISSUE_FEEDBACK:
                currentFragment = issueFeedbackFragment;
                break;
            case STATE_USER_TERM:
                currentFragment = userTermFragment;
                break;
        }
        switch (newState) {
            case STATE_SETTING_LIST:
                toFragment = settingListFragment;
                break;
            case STATE_COMMON_ISSUE:
                toFragment = commonIssueFragment;
                break;
            case STATE_ISSUE_FEEDBACK:
                toFragment = issueFeedbackFragment;
                break;
            case STATE_USER_TERM:
                toFragment = userTermFragment;
                break;
        }
        //更新状态常量
        currentState = newState;
        //开始切换
        fragmentManager.beginTransaction()
                .hide(currentFragment)
                .show(toFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        //更新menu
        invalidateOptionsMenu();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("通用设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (currentState) {
            case STATE_SETTING_LIST:
                getMenuInflater().inflate(R.menu.menu_empty, menu);
                getSupportActionBar().setTitle("通用设置");
                break;
            case STATE_COMMON_ISSUE:
                getMenuInflater().inflate(R.menu.menu_empty, menu);
                getSupportActionBar().setTitle("常见问题");
                break;
            case STATE_ISSUE_FEEDBACK:
                getMenuInflater().inflate(R.menu.menu_frg_issue_feedback, menu);
                getSupportActionBar().setTitle("问题反馈");
                break;
            case STATE_USER_TERM:
                getMenuInflater().inflate(R.menu.menu_empty, menu);
                getSupportActionBar().setTitle("使用条款");
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (currentState != State.STATE_SETTING_LIST) {
                    changeState(State.STATE_SETTING_LIST);
                } else {
                    finish();
                }
                break;
            //提交Issue
            case R.id.id_menu_post_issue:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(currentState != State.STATE_SETTING_LIST){
            changeState(State.STATE_SETTING_LIST);
            return;
        }
        super.onBackPressed();
    }

    //settingActivity状态变化事件回调
    public void onEventMainThread(SettingAtyStateEvent event) {
        changeState(event.getToState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
