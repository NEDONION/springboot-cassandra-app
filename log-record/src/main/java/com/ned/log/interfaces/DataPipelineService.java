package com.ned.log.interfaces;

import com.ned.log.model.LogModel;

/**
 * 消息管道处理接口
 */
public interface DataPipelineService {

    boolean createLog(LogModel logModel);

}
