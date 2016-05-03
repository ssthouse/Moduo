/**
 * Project Name:XPGSdkV4AppBase
 * File Name:CmdCenter.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:19
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mingko.moduo.control.xpg;

import android.content.Context;

import com.mingko.moduo.model.cons.Constant;
import com.mingko.moduo.model.cons.xpg.XPGCmdCommand;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDK.XPGWifiConfigureMode;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * 单例
 * 机智云相关:
 * 机智云控制中心{
 *     用户注册, 注销
 *     设备绑定, 设备状态获取
 * }
 */
public class CmdCenter {
    /**
     * The m center.
     */
    private static CmdCenter instance;

    /**
     * The xpg wifi sdk.
     */
    private static XPGWifiSDK xpgWifiGCC;

    /**
     * The m setting manager.
     */
    private SettingManager mSettingManager;

    /**
     * Gets the single instance of CmdCenter.
     */
    public static CmdCenter getInstance(Context context) {
        if (instance == null) {
            synchronized (CmdCenter.class){
                if(instance == null){
                    instance = new CmdCenter(context);
                }
            }
        }
        return instance;
    }

    /**
     * Instantiates a new cmd center.
     *
     * @param context the context
     */
    private CmdCenter(Context context) {
        if (instance == null) {
            init(context);
        }
    }

    /**
     * Inits field
     *
     * @param context the context
     */
    private void init(Context context) {
        mSettingManager = SettingManager.getInstance(context);
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
    // 关于账号的注册、登录、注销、修改密码、忘记密码
    //
    // =================================================================

    /**
     * 注册手机用户
     *
     * @param phone    注册手机号
     * @param code     验证码
     * @param password 注册密码
     */
    public void cRegisterPhoneUser(String phone, String code, String password) {
        xpgWifiGCC.registerUserByPhoneAndCode(phone, password, code);
    }

    /**
     * 注册邮箱用户
     *
     * @param mailAddr 邮箱地址
     * @param password 密码
     */
    public void cRegisterMailUser(String mailAddr, String password) {
        xpgWifiGCC.registerUserByEmail(mailAddr, password);
    }

    /**
     * 注册正常用户
     *
     * @param userName 用户名
     * @param password 密码
     */
    public void cRegisterUser(String userName, String password) {
        xpgWifiGCC.registerUser(userName, password);
    }

    /**
     * 匿名用户转正常用户
     *
     * @param token 令牌
     * @param userName  用户名
     * @param password  密码
     */
    public void cTransferToNormalUser(String token, String userName, String password) {
        xpgWifiGCC.transAnonymousUserToNormalUser(token, userName, password);
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
     * 匿名登录
     *
     * 如果最开始不需要直接注册账号，则需要进行匿名登录.
     * 用户调用后直接登录，每次匿名登录获取到的uid是一样的。
     * 该接口原理使用Android ID进行注册和登录
     * 每个Android系统都有一个独立的Android ID，系统刷机后将改变。
     * 因此，系统刷机后匿名注册的用户信息将无法保留。
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
     * 忘记密码.
     *
     * @param phone       手机号
     * @param code        验证码
     * @param newPassword 新密码
     */
    public void cChangeUserPasswordWithCode(String phone, String code, String newPassword) {
        xpgWifiGCC.changeUserPasswordByCode(phone, code, newPassword);
    }

    /**
     * 请求向手机发送验证码.
     *
     * @param phone 手机号
     */
    public void cRequestSendVerifyCode(String phone) {
        xpgWifiGCC.requestSendVerifyCode(phone);
    }

    // =================================================================
    //
    // 配置 wifi 模块，以及获取设备信息
    //
    // =================================================================

    /**
     * 发送airlink广播，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifiSSID wifi名字
     * @param password wifi密码
     */
    public void cSetAirLink(String wifiSSID, String password) {
        xpgWifiGCC.setDeviceWifi(wifiSSID, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeAirLink, 60);
    }

    /**
     * softap，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifiSSID wifi名字
     * @param password wifi密码
     */
    public void cSetSoftAp(String wifiSSID, String password) {
        xpgWifiGCC.setDeviceWifi(wifiSSID, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeSoftAP, 30);
    }

    /**
     * 绑定后刷新设备列表，该方法会同时获取本地设备以及远程设备列表.
     *
     * @param uid   用户名
     * @param token 令牌
     */
    public void cGetBoundDevices(String uid, String token) {
        //仅仅绑定自己的productkey的数据
        xpgWifiGCC.getBoundDevices(uid, token, Constant.SettingSdkCons.PRODUCT_KEY);
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
     * 获取设备状态.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    public void cGetStatus(XPGWifiDevice xpgWifiDevice) {
        if (xpgWifiDevice == null) {
            Timber.e("设备为空");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", XPGCmdCommand.QUERY_STATUE_FROM_DEVICE.getCommand());
        } catch (JSONException e) {
            e.printStackTrace();
            Timber.e("json出错");
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
}
