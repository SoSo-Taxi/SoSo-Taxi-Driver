/**
 * @Author 范承祥
 * @CreateTime 2020/7/18
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.utils;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.EntityListRequest;
import com.baidu.trace.api.entity.FilterCondition;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.TransportMode;
import com.sosotaxi.driver.common.Constant;

import java.util.List;

/**
 * 轨迹帮手类
 */
public class TraceHelper {
    /**
     * 轨迹
     */
    private static Trace sTrace;

    /**
     * 轨迹连接器
     */
    private static LBSTraceClient sTraceClient;

    /**
     * 轨迹查询标签
     */
    private static int tag;

    /**
     * 初始化轨迹
     * @param context 上下文
     * @param entityName 实体名
     * @param gatherInterval 收集间隔时间
     * @param packInterval 打包间隔时间
     * @param onTraceListener 轨迹监听器
     */
    public static void initTrace(Context context, String entityName, int gatherInterval, int packInterval, OnTraceListener onTraceListener){
        // 初始化轨迹服务
        sTrace = new Trace(Constant.SERVICE_ID, entityName, false);
        // 初始化轨迹服务客户端
        sTraceClient = new LBSTraceClient(context);
        // 设置定位和打包周期
        sTraceClient.setInterval(gatherInterval, packInterval);
        // 设置监听器
        sTraceClient.setOnTraceListener(onTraceListener);
        // tag默认为0
        tag=0;
    }

    /**
     * 获取轨迹连接器实例
     * @return 轨迹连接器对象
     */
    public static LBSTraceClient getTraceClient(){
        return sTraceClient;
    }

    /**
     * 开始轨迹记录
     */
    public static void startTrace(){
        sTraceClient.startTrace(sTrace, null);
    }

    /**
     * 开始收集轨迹
     */
    public static void startGather(){
        sTraceClient.startGather(null);
    }

    /**
     * 停止轨迹记录
     */
    public static void stopTrace(){
        sTraceClient.stopTrace(sTrace, null);
    }

    /**
     * 停止收集轨迹
     */
    public static void stopGather(){
        sTraceClient.stopGather(null);
    }

    /**
     * 实体查询
     * @param entityNames 实体名列表
     * @param activeTime 活跃时间
     * @param entityListener 实体监听器
     */
    public static void queryEntity(List<String> entityNames, long activeTime, OnEntityListener entityListener){
        // 创建查询
        EntityListRequest entityListRequest=new EntityListRequest();
        entityListRequest.setServiceId(Constant.SERVICE_ID);
        entityListRequest.setPageSize(100);
        entityListRequest.setPageIndex(1);

        FilterCondition filterCondition=new FilterCondition();
        filterCondition.setEntityNames(entityNames);
        filterCondition.setActiveTime(activeTime);

        entityListRequest.setFilterCondition(filterCondition);
        // 查询
        sTraceClient.queryEntityList(entityListRequest,entityListener);
    }

    /**
     * 查询历史轨迹
     * @param entityName 实体名
     * @param startTime 开始起始
     * @param endTime 截止时间
     * @param onTrackListener 轨迹监听器
     */
    public static void queryHistoryTrack(String entityName, long startTime, long endTime, OnTrackListener onTrackListener){
        HistoryTrackRequest historyTrackRequest=new HistoryTrackRequest(++tag,Constant.SERVICE_ID,entityName);
        // 设置开始时间
        historyTrackRequest.setStartTime(startTime);
        // 设置结束时间
        historyTrackRequest.setEndTime(endTime);
        // 设置需要纠偏
        historyTrackRequest.setProcessed(true);
        // 创建纠偏选项实例
        ProcessOption processOption = new ProcessOption();
        // 设置需要去噪
        processOption.setNeedDenoise(true);
        // 设置需要抽稀
        processOption.setNeedVacuate(true);
        // 设置需要绑路
        processOption.setNeedMapMatch(true);
        // 设置精度过滤值(定位精度大于100米的过滤掉)
        processOption.setRadiusThreshold(100);
        // 设置交通方式为驾车
        processOption.setTransportMode(TransportMode.driving);
        // 设置纠偏选项
        historyTrackRequest.setProcessOption(processOption);
        // 设置里程填充方式为驾车
        historyTrackRequest.setSupplementMode(SupplementMode.driving);
        //查询历史轨迹
        sTraceClient.queryHistoryTrack(historyTrackRequest,onTrackListener);
    }
}
