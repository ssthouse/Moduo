/**
 * Project Name:XPGSdkV4AppBase
 * File Name:CmdCenter.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:19
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
package com.ssthouse.moduo.control.setting;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.ssthouse.moduo.model.Constant;
import com.ssthouse.moduo.model.setting.JsonKeys;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDK.XPGWifiConfigureMode;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * ClassName: Class CmdCenter. <br/>
 * 控制指令类
 * <br/>
 * date: 2014-12-15 12:09:02 <br/>
 *
 * @author Lien
 */
public class CmdCenter {
    /**
     * The xpg wifi sdk.
     */
    private static XPGWifiSDK xpgWifiGCC;

    /**
     * The m center.
     */
    private static CmdCenter mCenter;

    /**
     * The m setting manager.
     */
    private SettingManager mSettingManager;

    /**
     * Instantiates a new cmd center.
     *
     * @param c the c
     */
    private CmdCenter(Context c) {
        if (mCenter == null) {
            init(c);
        }
    }

    /**
     * Gets the single instance of CmdCenter.
     *
     * @param c the c
     * @return single instance of CmdCenter
     */
    public static CmdCenter getInstance(Context c) {
        if (mCenter == null) {
            mCenter = new CmdCenter(c);
        }
        return mCenter;
    }

    /**
     * Inits the.
     *
     * @param c the c
     */
    private void init(Context c) {
        mSettingManager = SettingManager.getInstance(c);
        xpgWifiGCC = XPGWifiSDK.sharedInstance();
    }

    /**
     * Gets the XPG wifi sdk.
     *
     * @return the XPG wifi sdk
     */
    public XPGWifiSDK getXPGWifiSDK() {
        return xpgWifiGCC;
    }

    // =================================================================
    //
    // 关于账号的指令
    //
    // =================================================================

    /**
     * 注册账号.
     *
     * @param phone    注册手机号
     * @param code     验证码
     * @param password 注册密码
     */
    public void cRegisterPhoneUser(String phone, String code, String password) {
        xpgWifiGCC.registerUserByPhoneAndCode(phone, password, code);
    }

    /**
     * C register mail user.
     *
     * @param mailAddr the mail addr
     * @param password the password
     */
    public void cRegisterMailUser(String mailAddr, String password) {
        xpgWifiGCC.registerUserByEmail(mailAddr, password);
    }

    /**
     * 匿名登录
     * <p/>
     * 如果一开始不需要直接注册账号，则需要进行匿名登录.
     */
    public void cLoginAnonymousUser() {
        xpgWifiGCC.userLoginAnonymous();
    }

    /**
     * 账号注销.
     */
    public void cLogout() {
        Timber.e("cLogout:uesrid=" + mSettingManager.getUid());
        xpgWifiGCC.userLogout(mSettingManager.getUid());
    }

    /**
     * 账号登陆.
     *
     * @param name 用户名
     * @param psw  密码
     */
    public void cLogin(String name, String psw) {
        xpgWifiGCC.userLoginWithUserName(name, psw);
    }

    /**
     * 忘记密码.
     *
     * @param phone       手机号
     * @param code        验证码
     * @param newPassword the new password
     */
    public void cChangeUserPasswordWithCode(String phone, String code, String newPassword) {
        xpgWifiGCC.changeUserPasswordByCode(phone, code, newPassword);
    }

    /**
     * 修改密码.
     *
     * @param token  令牌
     * @param oldPsw 旧密码
     * @param newPsw 新密码
     */
    public void cChangeUserPassword(String token, String oldPsw, String newPsw) {
        xpgWifiGCC.changeUserPassword(token, oldPsw, newPsw);
    }

    /**
     * 根据邮箱修改密码.
     *
     * @param email 邮箱地址
     */
    public void cChangePassworfByEmail(String email) {
        xpgWifiGCC.changeUserPasswordByEmail(email);
    }

    /**
     * 请求向手机发送验证码.
     *
     * @param phone 手机号
     */
    public void cRequestSendVerifyCode(String phone) {
        xpgWifiGCC.requestSendVerifyCode(phone);
    }

    /**
     * 发送airlink广播，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifi     wifi名字
     * @param password wifi密码
     */
    public void cSetAirLink(String wifi, String password) {
        xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeAirLink, 60);
    }

    /**
     * softap，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifi     wifi名字
     * @param password wifi密码
     */
    public void cSetSoftAp(String wifi, String password) {
        xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeSoftAP, 30);
    }

    /**
     * 绑定后刷新设备列表，该方法会同时获取本地设备以及远程设备列表.
     *
     * @param uid   用户名
     * @param token 令牌
     */
    public void cGetBoundDevices(String uid, String token) {
        xpgWifiGCC.getBoundDevices(uid, token, Constant.SettingSdkCons.PRODUCT_KEY);
        // xpgWifiSdk.getBoundDevices(uid, token);
    }

    /**
     * 绑定设备.
     *
     * @param uid      用户名
     * @param token    密码
     * @param did      did
     * @param passcode passcode
     * @param remark   备注
     */
    public void cBindDevice(String uid, String token, String did,
                            String passcode, String remark) {

        xpgWifiGCC.bindDevice(uid, token, did, passcode, remark);
    }

    // =================================================================
    //
    // 关于控制设备的指令
    //
    // =================================================================

    /**
     * 发送指令.
     * 抽象出主体逻辑的命令方法
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param key           the key
     * @param value         the value
     */
    public void cWrite(XPGWifiDevice xpgWifiDevice, String key, Object value) {

        try {
            final JSONObject jsonsend = new JSONObject();
            JSONObject jsonparam = new JSONObject();
            jsonsend.put("cmd", 1);
            jsonparam.put(key, value);
            jsonsend.put(JsonKeys.KEY_ACTION, jsonparam);
            Log.i("sendjson", jsonsend.toString());
            xpgWifiDevice.write(jsonsend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取设备状态.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    public void cGetStatus(XPGWifiDevice xpgWifiDevice) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        xpgWifiDevice.write(json.toString());
    }

    /**
     * 断开连接.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    public void cDisconnect(XPGWifiDevice xpgWifiDevice) {
        xpgWifiDevice.disconnect();
    }

    /**
     * 解除绑定.
     *
     * @param uid      the uid
     * @param token    the token
     * @param did      the did
     * @param passCode the pass code
     */
    public void cUnbindDevice(String uid, String token, String did,
                              String passCode) {
        xpgWifiGCC.unbindDevice(uid, token, did, passCode);
    }

    /**
     * 更新备注.
     *
     * @param uid      the uid
     * @param token    the token
     * @param did      the did
     * @param passCode the pass code
     * @param remark   the remark
     */
    public void cUpdateRemark(String uid, String token, String did,
                              String passCode, String remark) {
        xpgWifiGCC.bindDevice(uid, token, did, passCode, remark);
    }

    // =================================================================
    //
    // 设备控制相关指令
    //
    // =================================================================

    /**
     * C switch on.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param isOn          the is on
     */
    public void cSwitchOn(XPGWifiDevice xpgWifiDevice, boolean isOn) {
        cWrite(xpgWifiDevice, JsonKeys.ON_OFF, isOn);
        cGetStatus(xpgWifiDevice);
    }

    /**
     * C color.
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param color
     */
    public void cColor(XPGWifiDevice xpgWifiDevice, int color) {
        try {
            final JSONObject jsonsend = new JSONObject();
            JSONObject jsonparam = new JSONObject();
            jsonsend.put("cmd", 1);
            jsonparam.put(JsonKeys.COLOR_RED, Color.red(color));
            jsonparam.put(JsonKeys.COLOR_GREEN, Color.green(color));
            jsonparam.put(JsonKeys.COLOR_BLUE, Color.blue(color));
            jsonparam.put(JsonKeys.MODE, 0);
            jsonsend.put(JsonKeys.KEY_ACTION, jsonparam);
            Log.i("sendjson", jsonsend.toString());
            xpgWifiDevice.write(jsonsend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cGetStatus(xpgWifiDevice);
    }

    /**
     * C brightness.
     */
    public void cColorTemp(XPGWifiDevice xpgWifiDevice, int num) {
        try {
            final JSONObject jsonsend = new JSONObject();
            JSONObject jsonparam = new JSONObject();
            jsonsend.put("cmd", 1);
            jsonparam.put(JsonKeys.COLOR_TEMPERATURE, num);
            jsonparam.put(JsonKeys.MODE, 1);
            jsonsend.put(JsonKeys.KEY_ACTION, jsonparam);
            Log.i("sendjson", jsonsend.toString());
            xpgWifiDevice.write(jsonsend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cGetStatus(xpgWifiDevice);
    }

    /**
     * C brightness.
     */
    public void cBrightness(XPGWifiDevice xpgWifiDevice, int progress) {
        cWrite(xpgWifiDevice, JsonKeys.BRIGHTNESS, progress);
        cGetStatus(xpgWifiDevice);
    }
}
