package com.ned.cassandra.model;

import com.ned.cassandra.entity.Person;
import com.ned.cassandra.entity.Tutorial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmailAsyncRequestModel implements Serializable {

	Person person;
	Tutorial tutorial;
}
