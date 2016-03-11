package com.ssthouse.moduo.fragment.moduo.presenter;

import android.content.Context;

import com.ssthouse.moduo.fragment.moduo.bean.event.ModuoScaleChangeEvent;
import com.ssthouse.moduo.fragment.moduo.control.util.DbHelper;
import com.ssthouse.moduo.fragment.moduo.model.ModuoModel;
import com.ssthouse.moduo.fragment.moduo.view.ModuoFragmentView;
import com.ssthouse.moduo.fragment.moduo.view.adapter.MsgBean;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * presenter
 * Created by ssthouse on 2016/2/14.
 */
public class ModuoPresenter {

    //View Model
    private ModuoFragmentView mModuoFragmentView;
    private ModuoModel mModuoModel;

    private Context mContext;

    //构造方法
    public ModuoPresenter(Context context, ModuoFragmentView moduoFragmentView) {
        this.mContext = context;
        this.mModuoFragmentView = moduoFragmentView;
        mModuoModel = new ModuoModel();
        EventBus.getDefault().register(this);
    }

    //加载更多msg
    public void loadMoreMsg() {
        mModuoModel.loadMoreMsg(mModuoFragmentView.getTopMsgBean())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MsgBean>>() {
                    @Override
                    public void call(List<MsgBean> msgBeans) {
                        //刷新UI
                        mModuoFragmentView.loadMoreMsg(msgBeans);
                    }
                });
    }

    //魔哆变大事件回调
    public void onEventMainThread(ModuoScaleChangeEvent event) {
        if (event.isToBig()) {
            mModuoFragmentView.animate2Big();
        } else {
            mModuoFragmentView.animate2Small();
        }
    }

    //添加msgBen回调
    public void onEventMainThread(MsgBean msgBean) {
        if (msgBean == null) {
            return;
        }
        //魔哆变小
        mModuoFragmentView.animate2Small();
        //添加数据到对话框
        mModuoFragmentView.addMsgBean(msgBean);
        //保存到数据库
        DbHelper.saveMsgBean(msgBean);
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

}
