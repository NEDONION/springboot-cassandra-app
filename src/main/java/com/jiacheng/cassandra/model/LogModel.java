package com.jiacheng.cassandra.model;


import java.util.Date;
import lombok.Data;


@Data
public class LogModel {

	/**
	 * 日志唯一ID
	 */
	private String logId;
	/**
	 * 业务ID
	 */
	private String bizId;
	/**
	 * 方法异常信息
	 */
	private String exception;
	/**
	 * 日志操作时间
	 */
	private Date operateDate;
	/**
	 * 方法是否成功
	 */
	private Boolean isSuccess;
	/**
	 * 日志内容
	 */
	private String msg;
	/**
	 * 方法结果
	 */
	private String executeResult;
	/**
	 * 方法执行时间（单位：毫秒）
	 */
	private Long executionTime;
}