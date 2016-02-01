package com.ssthouse.moduo.main.view.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.model.bean.event.view.SettingAtyStateEvent;
import com.ssthouse.moduo.main.view.activity.AppIntroActivity;
import com.ssthouse.moduo.main.view.activity.SettingActivity;
import com.ssthouse.moduo.main.view.activity.account.GestureLockActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 设置列表fragment
 * Created by ssthouse on 2016/1/26.
 */
public class SettingListFragment extends Fragment {

    //lv数据
    private String lvEntity[] = {"常见问题", "问题反馈", "使用帮助", "使用条款", "图形密码"};

    @Bind(R.id.id_lv_setting)
    ListView lvSetting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_list, container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        lvSetting.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.item_setting, lvEntity));

        lvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //常见问题
                    case 0:
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_COMMON_ISSUE));
                        break;
                    //问题反馈
                    case 1:
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_ISSUE_FEEDBACK));
                        break;
                    //使用帮助
                    case 2:
                        AppIntroActivity.start(getActivity());
                        break;
                    //使用条款
                    case 3:
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_USER_TERM));
                        break;
                    //图形密码
                    case 4:
                        //todo
                        GestureLockActivity.start(getActivity());
                        break;
                }
            }
        });
    }
}