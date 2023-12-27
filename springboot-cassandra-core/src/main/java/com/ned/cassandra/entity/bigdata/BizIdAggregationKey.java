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
public class BizIdAggregationKey implements Serializable {

	@PrimaryKeyColumn(name = "operate_day", type = PARTITIONED)
	private LocalDate operateDay;

	@PrimaryKeyColumn(name = "biz_id", type = PARTITIONED)
	private String bizId;

}
