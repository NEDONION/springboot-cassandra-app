package com.ned.log.interfaces;


import com.ned.log.model.LogModel;


public interface IOperationLogGetService {

    boolean createLog(LogModel logModel) throws Exception;

}
