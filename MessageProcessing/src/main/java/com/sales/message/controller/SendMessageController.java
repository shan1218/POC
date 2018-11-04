package com.sales.message.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sales.message.dto.Sales;

/**
 * @author Shanmugam
 * This controller is used to receive any sales message and push it to JMS Queue
 *
 */
@RestController
@RequestMapping("/message")
public class SendMessageController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private int messageCount = 0;
	
	@Autowired 
	private JmsTemplate jmsTemplate;
	
	/**
	 * @param sales
	 * @return
	 * @throws Exception
	 * Rest service will accept sales message in rest service and will notify JMS
	 */
	@RequestMapping(value = "/sendSalesInfo", method = RequestMethod.POST)
    public ResponseEntity<String> sendSalesInfo(@RequestBody Sales sales) throws Exception {
		messageCount++;
		if(messageCount <= 50) {
			jmsTemplate.convertAndSend("salesMessage", sales);
	        return new ResponseEntity<String>("", HttpStatus.OK);
		} else if(messageCount == 51) {
			//When the input request reaches 51, it will just notify jms to print adjustments if any
			Sales sale = new Sales();
			sale.setPauseRequired(true);
			jmsTemplate.convertAndSend("salesMessage", sale);
	        return new ResponseEntity<String>("", HttpStatus.FORBIDDEN);
		} else {
			//After 51 message system will not call the jms. Instead it will send forbidden response back to calling system
			System.out.println("System will not accept any more sales message as it reached maximum threshold.");
			return new ResponseEntity<String>("", HttpStatus.FORBIDDEN);
		}
    }
}
