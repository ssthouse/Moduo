package com.ssthouse.moduo.main.control.util;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.ssthouse.moduo.bean.ModuoInfo;
import com.ssthouse.moduo.bean.UserInfo;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.main.control.xpg.SettingManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * leancloud控制类
 * Created by ssthouse on 2016/1/20.
 */
public class CloudUtil {

    /**
     * 魔哆设备数据表
     */
    public static final String TABLE_MODUO_DEVICE = "ModuoDevice";

    public static final String KEY_DID = "did";
    public static final String KEY_PASSCODE = "passcode";
    public static final String KEY_CID = "cid";
    public static final String KEY_VIDEO_USERNAME = "videoUserName";
    public static final String KEY_VIDEO_PASSWORD = "videoPassword";

    /**
     * 保存设备数据到leancloud
     *
     * @param callback
     */
    public static void saveDeviceToCloud(final Device device, final SaveCallback callback) {
        Observable.just(device.getXpgWifiDevice().getDid())
                .map(new Func1<String, AVObject>() {
                    @Override
                    public AVObject call(String s) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_MODUO_DEVICE);
                        query.whereEqualTo(KEY_DID, s);
                        AVObject moduoDevice = null;
                        try {
                            moduoDevice = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return moduoDevice;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        //云端没有才保存
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_MODUO_DEVICE);
                            moduoDevice.put(KEY_DID, device.getXpgWifiDevice().getDid());
                            moduoDevice.put(KEY_PASSCODE, device.getXpgWifiDevice().getPasscode());
                            moduoDevice.put(KEY_CID, device.getVideoCidNumber());
                            moduoDevice.put(KEY_VIDEO_USERNAME, device.getVideoUsername());
                            moduoDevice.put(KEY_VIDEO_PASSWORD, device.getVideoUsername());
                            moduoDevice.saveInBackground(callback);
                        }
                    }
                });
    }

    /**
     * 保存设备数据到leancloud
     */
    public static void saveDeviceToCloud(final ModuoInfo moduoInfo) {
        Observable.just(moduoInfo)
                .map(new Func1<ModuoInfo, AVObject>() {
                    @Override
                    public AVObject call(ModuoInfo moduo) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_MODUO_DEVICE);
                        query.whereEqualTo(KEY_DID, moduo.getDid());
                        AVObject moduoDevice = null;
                        try {
                            moduoDevice = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return moduoDevice;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        //云端没有才保存
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_MODUO_DEVICE);
                            moduoDevice.put(KEY_DID, moduoInfo.getDid());
                            moduoDevice.put(KEY_PASSCODE, moduoInfo.getPasscode());
                            moduoDevice.put(KEY_CID, moduoInfo.getCid());
                            moduoDevice.put(KEY_VIDEO_USERNAME, moduoInfo.getVideoUsername());
                            moduoDevice.put(KEY_VIDEO_PASSWORD, moduoInfo.getVideoPassword());
                            moduoDevice.saveInBackground();
                        }
                    }
                });
    }

    /**
     * 用户信息数据表
     */
    public final static String TABLE_USER_INFO = "UserInfo";

    public final static String KEY_USERNAME = "username";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_GESTURE_PASSWORD = "gesturePassword";

    /**
     * 保存用户信息
     *
     * @param userInfo
     * @param callback
     */
    public static void saveUserInfoToCloud(final UserInfo userInfo, final SaveCallback callback) {
        Observable.just(userInfo)
                .map(new Func1<UserInfo, AVObject>() {
                    @Override
                    public AVObject call(UserInfo u) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_USER_INFO);
                        query.whereEqualTo(KEY_USERNAME, u.getUsername());
                        AVObject moduoDevice = null;
                        try {
                            moduoDevice = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return moduoDevice;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        //判断是否已有用户
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_USER_INFO);
                            moduoDevice.put(KEY_USERNAME, userInfo.getUsername());
                            moduoDevice.put(KEY_PASSWORD, userInfo.getPassword());
                            moduoDevice.put(KEY_GESTURE_PASSWORD, userInfo.getGesturePassword());
                            moduoDevice.saveInBackground(callback);
                        } else {
                            avObject.put(KEY_USERNAME, userInfo.getUsername());
                            avObject.put(KEY_PASSWORD, userInfo.getPassword());
                            avObject.put(KEY_GESTURE_PASSWORD, userInfo.getGesturePassword());
                            avObject.saveInBackground(callback);
                        }
                    }
                });
    }

    /**
     * 从云端获取用户信息
     *
     * @param username 用户名(机智云端用户名是唯一的)
     * @return
     */
    public static void updateUserInfo(final Context context, final String username) {
        Observable.just(username)
                .map(new Func1<String, UserInfo>() {
                    @Override
                    public UserInfo call(String s) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(CloudUtil.TABLE_USER_INFO);
                        query.whereEqualTo(CloudUtil.KEY_USERNAME, s);
                        AVObject userInfoObject = null;
                        try {
                            userInfoObject = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        if (userInfoObject == null) {
                            return null;
                        }
                        return new UserInfo(userInfoObject.getString(CloudUtil.KEY_USERNAME),
                                userInfoObject.getString(CloudUtil.KEY_PASSWORD),
                                userInfoObject.getString(CloudUtil.KEY_GESTURE_PASSWORD));
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        if(userInfo == null){
                            return;
                        }
                        SettingManager.getInstance(context).setCurrentUserInfo(userInfo);
                        Timber.e(userInfo.toString());
                    }
                });
    }
}
