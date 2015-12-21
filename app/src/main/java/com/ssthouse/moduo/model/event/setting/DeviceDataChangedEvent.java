package com.ssthouse.moduo.model.event.setting;

import com.ssthouse.moduo.model.DeviceData;

/**
 * 设备自己推送的数据变化事件
 * Created by ssthouse on 2015/12/21.
 */
public class DeviceDataChangedEvent {
    /**
     * 变化后的设备数据
     */
    private DeviceData changedDeviceData;

    /**
     * 成功的构造方法
     * @param changedDeviceData
     */
    public DeviceDataChangedEvent( DeviceData changedDeviceData) {
        this.changedDeviceData = changedDeviceData;
    }

    public DeviceData getChangedDeviceData() {
        return changedDeviceData;
    }

    public void setChangedDeviceData(DeviceData changedDeviceData) {
        this.changedDeviceData = changedDeviceData;
    }
}
