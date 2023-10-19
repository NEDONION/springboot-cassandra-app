package com.jiacheng.cassandra.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "log-record")
public class LogRecordProperties {

	private ThreadPoolProperties threadPool = new ThreadPoolProperties();

	private String dataPipeline;

	private RetryProperties retry = new RetryProperties();


	@Data
	public static class RetryProperties {

		/**
		 * 日志处理失败重试次数
		 */
		private int retryTimes = 0;
	}

	@Data
	public static class ThreadPoolProperties {

		private int poolSize = 4;

		private boolean enabled = true;
	}

	@Data
	public static class RocketMqProperties {

		private String topic = "logRecord";
		private String tag = "";
		private String namesrvAddr = "localhost:9876";
		private String groupName = "logRecord";
		private int maxMessageSize = 4000000;
		private int sendMsgTimeout = 3000;
		private int retryTimesWhenSendFailed = 2;
	}


}
