package com.jiacheng.cassandra.interfaces;


import com.jiacheng.cassandra.model.LogModel;


public interface IOperationLogGetService {

    boolean createLog(LogModel logModel) throws Exception;

}
