package com.mingko.moduo.control.xpg.Slots;

import timber.log.Timber;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class LightSlots extends SlotsEntity {

    private String attrValue;
    private String arrrType;
    private String attr;

    static {
        byte bParam = byteParam;
        mapParam.put("开关", bParam++);
        mapParam.put("颜色", bParam++);
        byte bValue = byteValue;
        mapValue.put("开", bValue++);//#15
        mapValue.put("关", bValue++);
        mapValue.put("红", bValue++);
        mapValue.put("橙", bValue++);
        mapValue.put("黄", bValue++);
        mapValue.put("绿", bValue++);
        mapValue.put("青", bValue++);
        mapValue.put("蓝", bValue++);
        mapValue.put("紫", bValue++);
    }

    @Override
    public void initParamValue() {
        setParamAndValue(attr, attrValue);
    }

}
