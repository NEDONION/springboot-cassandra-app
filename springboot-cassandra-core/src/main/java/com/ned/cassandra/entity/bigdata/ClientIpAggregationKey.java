package com.ned.cassandra.entity.bigdata;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyClass
public class ClientIpAggregationKey implements Serializable {

	@PrimaryKeyColumn(name = "operate_day", type = PARTITIONED)
	private LocalDate operateDay; // 用驼峰式命名约定来自动推断数据库中的列名

	@PrimaryKeyColumn(name = "client_ip", type = PARTITIONED)
	private String clientIp;

}
