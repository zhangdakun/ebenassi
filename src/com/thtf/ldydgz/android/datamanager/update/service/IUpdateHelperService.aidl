package com.thtf.ldydgz.android.datamanager.update.service;

import java.util.List;
import com.thtf.ldydgz.android.datamanager.update.service.IUpdateHelperServiceCallback;

/**
 * 更新数据接口
 */
interface IUpdateHelperService {

    void registerCallback(IUpdateHelperServiceCallback cb);
    void unregisterCallback(IUpdateHelperServiceCallback cb);

    /**
     * 获取应用及其对应的数据列表
     * 1. List 中的一个 Item 代表一个 app 的信息;
     * 2. Item 信息格式如下:
     *      {
     *          "app_name" : "datamanager",
     *          "app_package_name" : "com.thtf.ldydgz.android.datamanager",
     *          "app_version" : "v2.3.4",
     *          "app_version_code" : 17,
     *          "data_version" : "v.2.3.4",
     *          "data_version_code" : 17
     *      }
     */
    String getAppsInfo();
    
    /**
     * 开始更新应用数据, 更新完成后返回更新结果
     */
    void updateAppsData();
     
     /**
      * 通知用户有更新, 让用户选择是立即更新还是继续当前操作
      */
      void readyToUpdate();
}
