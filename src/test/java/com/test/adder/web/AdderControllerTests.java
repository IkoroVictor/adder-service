package com.test.adder.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.LongStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdderControllerTests {

	private static final Logger logger =  LoggerFactory.getLogger(AdderControllerTests.class);

	@Autowired
	private WebApplicationContext context;

	@Test
	public void givenConcurrentNumberAdditionRequests_WhenEndRequested_ThenReturnSumAndReset() throws Exception {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);

		LongStream.generate(() -> 5_000_000)
				.limit(2000)
				.mapToObj(this::mapLongToRunnable)
				.forEach(executor::execute);

		while (!executor.getQueue().isEmpty() || executor.getActiveCount() != 0) {
			logger.info("Waiting......");
		}
		postValue("end")
				.andExpect(status().isOk())
				.andExpect(content().string("10000000000"));
	}

	@Test
	public void givenAnInvalidNumericValue_WhenAddRequested_ThenReturn400FailureResponse() throws Exception {
		postValue("test")
				.andExpect(status().isBadRequest());
	}

	private Runnable mapLongToRunnable(Long number) {
		return () -> {
			try {
				postValue(String.valueOf(number)).andReturn();
			} catch (Exception e) {
				logger.error("Error executing request" , e);
			}
		};
	}


	private ResultActions postValue(String value) throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		return mvc.perform(
				post("/")
						.contentType(MediaType.ALL_VALUE)
						.content(String.valueOf(value)));

	}
}
