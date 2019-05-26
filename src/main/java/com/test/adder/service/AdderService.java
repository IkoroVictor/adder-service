package com.test.adder.service;

import org.springframework.stereotype.Service;
;
import java.util.concurrent.atomic.LongAdder;

@Service
public class AdderService {

	private LongAdder longAdder;

	public AdderService() {
		this.longAdder = new LongAdder();
	}

	public void addNumber(Long number) {
		longAdder.add(number);
	}

	public Long getSumAndReset() {
		return longAdder.sumThenReset();
	}
}
