package com.jiacheng.cassandra.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelAccount {

	@PrimaryKey
	private UUID id;
	private String name;
	private UUID personId;
	private String accountConfig;
	private Integer channelType;
	private Long createTime;
	private Long updateTime;

}
