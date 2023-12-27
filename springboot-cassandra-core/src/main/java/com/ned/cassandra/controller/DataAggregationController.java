package com.ned.cassandra.controller;

import com.ned.cassandra.entity.bigdata.BizIdAggregationModel;
import com.ned.cassandra.entity.bigdata.ClientIpAggregationModel;
import com.ned.cassandra.repository.bigdata.BizIdAggregationRepository;
import com.ned.cassandra.repository.bigdata.ClientIpAggregationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/big-data")
public class DataAggregationController {

	private final ClientIpAggregationRepository clientIpAggregationRepository;

	private final BizIdAggregationRepository bizIdAggregationRepository;

	public DataAggregationController(ClientIpAggregationRepository clientIpAggregationRepository,
			BizIdAggregationRepository bizIdAggregationRepository) {
		this.clientIpAggregationRepository = clientIpAggregationRepository;
		this.bizIdAggregationRepository = bizIdAggregationRepository;
	}

	@GetMapping("/client-ip-aggregation/search")
	public ResponseEntity<List<ClientIpAggregationModel>> searchClientIpAggregationByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<ClientIpAggregationModel> result = clientIpAggregationRepository
				.findByKeyOperateDayBetween(startDate, endDate);

		return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
	}

	@GetMapping("/biz-id-aggregation/search")
	public ResponseEntity<List<BizIdAggregationModel>> searchBizIdAggregationByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		List<BizIdAggregationModel> result = bizIdAggregationRepository
				.findByKeyOperateDayBetween(startDate, endDate);

		return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
	}

}
