package com.jiacheng.cassandra.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessageModel<T> implements Serializable {

	private String topic;
	private T payload;
}
