package com.ned.cassandra.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.apache.spark.launcher.SparkLauncher;

@Slf4j
public class SparkJobScheduler {

	private static final String SPARK_HOME = "/usr/local/Cellar/spark-2.4.4";
	private static final String SPARK_JAR_PATH =
			"/Users/nedonion/Documents/iCollections/Folders/Backend-Learning/springboot-cassandra-crud/scala-data/target/scala-data-1.0-SNAPSHOT.jar";

	private static final String SPARK_MAIN_CLASS = "spark.LogRecordSparkJob";

	private static final String SPARK_CASSANDRA_CONNECTOR_JAR =
			"/Users/nedonion/.m2/repository/com/datastax/spark/spark-cassandra-connector_2.11/2.5.2/spark-cassandra-connector_2.11-2.5.2.jar";

	private static final String CRON_EXPRESSION_EVERY_DAY_1AM = "0 0 1 * * ?";

	private static final String CRON_EXPRESSION_EVERY_1MIN = "0 * * * * ?";

	public static void main(String[] args) throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();

		JobDetail job = JobBuilder.newJob(SparkLauncherJob.class)
				.withIdentity("sparkJob", "group1")
				.build();

		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("sparkTrigger", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule(CRON_EXPRESSION_EVERY_1MIN))  // 每天凌晨1点执行
				.build();

		scheduler.scheduleJob(job, trigger);
	}

	public static class SparkLauncherJob implements Job {

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			try {
				SparkLauncher launcher = new SparkLauncher()
						.setSparkHome(SPARK_HOME)
						.setAppResource(SPARK_JAR_PATH)
						.setMainClass(SPARK_MAIN_CLASS)
						.setMaster("local[*]")
						.setConf(SparkLauncher.DRIVER_MEMORY, "2g");
//						.setConf("spark.jars", SPARK_CASSANDRA_CONNECTOR_JAR);
				Process spark = launcher.launch();

				// print logs
				InputStreamReader input = new InputStreamReader(spark.getInputStream());
				BufferedReader reader = new BufferedReader(input);
				String line;
				while ((line = reader.readLine()) != null) {
					log.info("Spark Output: " + line);
				}
				InputStreamReader errorInput = new InputStreamReader(spark.getErrorStream());
				BufferedReader errorReader = new BufferedReader(errorInput);
				while ((line = errorReader.readLine()) != null) {
					log.error("Spark Error: " + line);
				}


				int exitCode = spark.waitFor();
				log.info("Spark job finished with exit code: " + exitCode);
			} catch (Exception e) {
				e.printStackTrace();
				throw new JobExecutionException(e);
			}
		}
	}

}
