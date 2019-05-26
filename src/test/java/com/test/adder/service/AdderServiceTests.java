package com.test.adder.service;

import com.test.adder.web.AdderControllerTests;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.LongStream;

public class AdderServiceTests {

	private static final Logger logger =  LoggerFactory.getLogger(AdderControllerTests.class);

	private AdderService adderService;

	public AdderServiceTests() {
		adderService = new AdderService();
	}

	@Test
	public void givenTwoHundredMillionLongValues_WhenLongValuesAreAddedConcurrentlyAndSumIsRequested_ThenReturnExpectedSumAndReset() {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

		LongStream.generate(() -> 50).limit(200_000_000)
				.mapToObj(this::mapLongToRunnable)
				.forEach(executor::execute);

		while (!executor.getQueue().isEmpty() || executor.getActiveCount() != 0) {
			logger.info("Waiting......");
		}
		Long end = adderService.getSumAndReset();
		Assert.assertEquals("Expected 10B", 10_000_000_000L, end.longValue());
		Assert.assertEquals("Expected 0", 0L, adderService.getSumAndReset().longValue());
	}

	private Runnable mapLongToRunnable(Long number) {
		return () -> adderService.addNumber(number);
	}

}