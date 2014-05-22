package com.thtf.ldydgz.android.update.service;

import java.util.List;
import com.thtf.ldydgz.android.update.service.IUpdateServiceCallback;

/**
 * 更新数据接口
 */
interface IUpdateService {

    void registerCallback(IUpdateServiceCallback cb);
    void unregisterCallback(IUpdateServiceCallback cb);

    /**
     * 获取应用及其对应的数据列表
     * 1. List 中的一个 Item 代表一个 app 的信息;
     * 2. Item 信息格式如下:
     *      {
     *          "app_name" : "datamanager",
     *          "app_package" : "com.thtf.ldydgz.android.datamanager",
     *          "app_version" : "v2.3.4",
     *          "app_version_code" : 17,
     *          "data_version" : "v.2.3.4",
     *          "data_version_code" : 17
     *      }
     */
    String getAppsInfo();
    
    /**
     * 开始更新应用数据, 更新完成后返回更新结果
     *
     * @return msg
     * 成功:
     * {
     *     "update_status_code" : 1,
     *     "update_status_msg" : "Ok"
     * }
     * 
     * 失败:
     * {
     *     "update_status_code" : 0,
     *     "update_status_msg" : "Cancel"
     * }
     *
     */
    String updateAppsData();
    
    /**
     * 是否可直接更新
     * 当存在 UI 线程任务或 数据读写任务时, 不可直接更新
     */
     boolean isDirectUpdate();
     
     /**
      * 通知用户有更新, 让用户选择是立即更新还是继续当前操作
      */
      void readyToUpdate();
}