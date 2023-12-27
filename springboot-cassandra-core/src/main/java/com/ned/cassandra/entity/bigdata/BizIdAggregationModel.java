package com.ned.cassandra.entity.bigdata;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table("biz_id_aggregation")
public class BizIdAggregationModel implements Serializable {

	@PrimaryKey
	private BizIdAggregationKey key;

	@Column("count_per_biz_id")
	private Integer countPerBizId;

}
