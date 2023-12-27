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
@Table("client_ip_aggregation")
public class ClientIpAggregationModel implements Serializable {

	@PrimaryKey
	private ClientIpAggregationKey key;

	@Column("count_per_client_ip")
	private Integer countPerClientIp;

}

