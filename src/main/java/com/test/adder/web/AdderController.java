package com.test.adder.web;

import com.test.adder.service.AdderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AdderController {

	private static final String RESET_COMMAND = "end";
	private static final Logger logger =  LoggerFactory.getLogger(AdderController.class);

	private AdderService adderService;

	@Autowired
	public AdderController(AdderService adderService) {
		this.adderService = adderService;
	}

	@PostMapping(consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
	public ResponseEntity accept(@RequestBody String value) {
		value =  cleanupValue(value);

		if (RESET_COMMAND.equals(value)) {
			return ResponseEntity.ok(String.valueOf(adderService.getSumAndReset()));
		}
		adderService.addNumber(Long.valueOf(value));
		return ResponseEntity.accepted().build();
	}

	private String cleanupValue(String value) {
		//Remove unnecessary '=' sign appended to body when using 'curl' with '-d' param
		return value.replace("=", "");
	}


	@ExceptionHandler(NumberFormatException.class)
	public ResponseEntity handleNumberFormatException(NumberFormatException ex) {
		logger.error("Could not parse request value.", ex);
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleException(Exception ex) {
		logger.error("An internal error occured.", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}


}
