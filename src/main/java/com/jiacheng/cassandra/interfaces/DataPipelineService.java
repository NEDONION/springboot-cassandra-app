package com.jiacheng.cassandra.interfaces;

import com.jiacheng.cassandra.model.LogModel;

/**
 * 消息管道处理接口
 */
public interface DataPipelineService {

    boolean createLog(LogModel logModel);

}
