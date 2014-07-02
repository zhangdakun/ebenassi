package com.thtf.ldydgz.android.datamanager.update.service;

/**
 * 更新回调接口, 通知更新助手, 当前更新进度
 */
 
interface IUpdateHelperServiceCallback {

    /**
     * 用户对是否立即更新的回复
     * @param statusCode 状态码, 0: 取消立即更新, 1: 立即更新;
     * @param msg 状态信息
     */
    void readyUpdateStatus(int statusCode, String msg);
    
    /**
     * 应用数据更新结果
     * @param statusCode 状态码, 0: 更新失败, 1: 更新成功;
     * @param msg 状态信息
     */
    void updateDataStatus(int statusCode, String msg);
}