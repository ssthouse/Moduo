/**
 * Project Name:XPGSdkV4AppBase
 * File Name:SettingManager.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:24
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ssthouse.moduo.main.control.xpg;

import android.content.Context;
import android.content.SharedPreferences;

import com.ssthouse.moduo.bean.ModuoInfo;
import com.ssthouse.moduo.bean.UserInfo;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;

import timber.log.Timber;

/**
 * SharePreference处理类.
 * <p/>
 * 保存:
 * 当前登陆账户数据
 * 当前绑定设备数据
 *
 * @author Sunny Ding
 */
public class SettingManager {

    private SharedPreferences spf;
    private static SettingManager instance;
    private Context context;

    /**
     * preference文件名
     */
    private final String SHARE_PREFERENCES = "set";

    // 用户名
    private static final String USER_NAME = "username";
    // 密码
    private static final String PASSWORD = "password";
    // 用户名
    private static final String TOKEN = "token";
    // 用户ID
    private static final String UID = "uid";
    // 当前操作设备did
    private static final String DID = "did";
    private static final String PASSCODE = "passcode";
    // 图形密码key
    private static final String KEY_GESTURE_LOCK = "gesture_lock";

    /**
     * 构造方法
     *
     * @param context
     */
    private SettingManager(Context context) {
        this.context = context;
        spf = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingManager(context);
        }
        return instance;
    }

    /**
     * 获取当前moduo Info
     *
     * @return
     */
    public ModuoInfo getCurrentModuoInfo() {
        return new ModuoInfo(getCurrentDid(),
                getPasscode(),
                getCidNumber(),
                getVideoUsername(),
                getVideoPassword());
    }

    /**
     * 设置当前moduo info
     *
     * @param moduoInfo
     */
    public void setCurrentModuoInfo(ModuoInfo moduoInfo) {
        //机智云参数
        setCurrentDid(moduoInfo.getDid());
        setPasscode(moduoInfo.getPasscode());
        //视频参数
        setCidNumber(moduoInfo.getCid());
        setVideoUsername(moduoInfo.getVideoUsername());
        setVideoPassword(moduoInfo.getVideoPassword());
    }

    /**
     * SharePreference clean.
     */
    public void clean() {
        //清除登陆信息
        setUid("");
        setToken("");
        //清除账户信息
        setPassword("");
        setUserName("");
        //清除手势密码
        setGestureLock("");
        //清除设备信息
        setCurrentDid("");
        setPasscode("");
        setVideoUsername("");
        setVideoPassword("");
    }

    /**
     * 是否本地有保存魔哆Info
     *
     * @return
     */
    public boolean hasLocalModuo() {
        if (getCurrentDid() != null
                && getPasscode() != null
                && getCidNumber() != null
                && getVideoUsername() != null
                && getVideoPassword() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否已经登陆
     *
     * @return
     */
    public boolean isLogined() {
        String uid = spf.getString(UID, null);
        String token = spf.getString(TOKEN, null);
        if (uid != null && uid.length() > 0
                && token != null && token.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否为匿名用户
     *
     * @return
     */
    public boolean isAnonymousUser() {
        String username = spf.getString(USER_NAME, null);
        String password = spf.getString(PASSWORD, null);
        if (username != null && username.length() > 0
                && password != null && password.length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置当前userInfo
     *
     * @param userInfo
     */
    public void setCurrentUserInfo(UserInfo userInfo) {
        setUserName(userInfo.getUsername());
        setPassword(userInfo.getPassword());
        setGestureLock(userInfo.getGesturePassword());
    }

    /**
     * 获取手势密码
     *
     * @return
     */
    public String getGestureLock() {
        return spf.getString(KEY_GESTURE_LOCK, null);
    }

    /**
     * 设置图形密码
     *
     * @param gestureLock
     * @return
     */
    public boolean setGestureLock(String gestureLock) {
        if (gestureLock == null) {
            return false;
        }
        spf.edit()
                .putString(KEY_GESTURE_LOCK, gestureLock)
                .commit();
        Timber.e("图形密码:" + gestureLock);
        return true;
    }

    public void setCurrentDid(String did) {
        Timber.e("将当前设备改为:\t" + did);
        spf.edit()
                .putString(DID, did)
                .commit();
    }

    public String getCurrentDid() {
        return spf.getString(DID, null);
    }

    public void setPasscode(String passcode) {
        spf.edit()
                .putString(PASSCODE, passcode)
                .commit();
    }

    public String getPasscode() {
        return spf.getString(PASSCODE, null);
    }

    /**
     * 保存用户登陆数据
     *
     * @param event
     */
    public void setLoginCacheInfo(XPGLoginResultEvent event) {
        setUid(event.getUid());
        setToken(event.getToken());
    }

    public void setUserName(String name) {
        spf.edit().putString(USER_NAME, name).commit();
    }

    public String getUserName() {
        return spf.getString(USER_NAME, "");
    }

    public void setPassword(String psw) {
        spf.edit().putString(PASSWORD, psw).commit();
    }

    public String getPassword() {
        return spf.getString(PASSWORD, "");
    }

    public void setToken(String token) {
        spf.edit().putString(TOKEN, token).commit();
    }

    public String getToken() {
        return spf.getString(TOKEN, "");
    }

    public void setUid(String uid) {
        spf.edit().putString(UID, uid).commit();
    }

    public String getUid() {
        return spf.getString(UID, "");
    }

    /**
     * 视频参数
     */
    private static final String VIDEO_USERNAME = "videoUsername";
    private static final String VIDEO_PASSWORD = "videoPassword";
    private static final String CID_NUMBER = "cidNumber";

    public void setCidNumber(String cidNumber) {
        spf.edit()
                .putString(CID_NUMBER, cidNumber)
                .commit();
    }

    public String getCidNumber() {
        return spf.getString(CID_NUMBER, null);
    }

    public void setVideoUsername(String videoUsername) {
        spf.edit()
                .putString(VIDEO_USERNAME, videoUsername)
                .commit();
    }

    public String getVideoUsername() {
        return spf.getString(VIDEO_USERNAME, null);
    }

    public void setVideoPassword(String videoPassword) {
        spf.edit()
                .putString(VIDEO_PASSWORD, videoPassword)
                .commit();
    }

    public String getVideoPassword() {
        return spf.getString(VIDEO_PASSWORD, null);
    }

}
