package com.sales.message.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.sales.message.dto.Sales;

/**
 * @author Shanmugam
 * Usage of this class to receive sales of any product information and the message might contain the adjustment details
 */
@Component
public class MessageReceiverListener {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private List<Sales> salesInfoList = new ArrayList<Sales>();
	private boolean isAllrecordProcess = false;
	
	/**
	 * @param sales
	 * This JMS listener will look for the message in salesMessage type and will start processing the message
	 */
	@JmsListener(destination = "salesMessage", containerFactory = "salesListenerFactory")
    public void receiveMessage(Sales sales) {

		if(salesInfoList.size() <=50)
			salesInfoList.add(sales);
		
		if(salesInfoList.size() % 10 == 0 && salesInfoList.size() <= 50) {
			
			List<Sales> processingSalesMessage = null;
			
			//We are considering recent 10 message to process to the report. 
			//It can be controlled whether to consider only recent 10 message or all the message received so far.
			//make isAllrecordProcess = true if we want to consider all records
			if(!isAllrecordProcess) {
				processingSalesMessage = salesInfoList.subList(salesInfoList.size()-10, salesInfoList.size());
			} else {
				processingSalesMessage = salesInfoList;
			}
			
			//First do adjustment if message contains adjustment information
			doAdjustmentProcess(processingSalesMessage);
        
			Map<String, Sales> productSalesDetailMap = new HashMap<String, Sales>();
        	
			//Read all the sales message detail and prepare report to show all the product name 
			//and number of sales of each product and its quantity.
        	for(Sales salesDetail : processingSalesMessage) {

        		if(null != salesDetail && null != salesDetail.getProductName() && !salesDetail.getProductName().trim().isEmpty()
        				&& null != salesDetail.getNumberOfSales() && salesDetail.getNumberOfSales() > 0 
        				&& null != salesDetail.getQuantity() && salesDetail.getQuantity() > 0) {
        			if(productSalesDetailMap.containsKey(salesDetail.getProductName().trim().toUpperCase())) {
        				Sales salesOfIndividualProduct = productSalesDetailMap.get(salesDetail.getProductName().trim().toUpperCase());
        				salesOfIndividualProduct.setNumberOfSales(salesDetail.getNumberOfSales()+salesOfIndividualProduct.getNumberOfSales());
        				salesOfIndividualProduct.setQuantity((salesDetail.getNumberOfSales()*salesDetail.getQuantity())+salesOfIndividualProduct.getQuantity());
        				productSalesDetailMap.put(salesDetail.getProductName().trim().toUpperCase(), salesOfIndividualProduct);
        			} else {
        				Sales salesOfIndividualProduct = new Sales();
        				salesOfIndividualProduct.setNumberOfSales(salesDetail.getNumberOfSales());
        				salesOfIndividualProduct.setQuantity(salesDetail.getNumberOfSales()*salesDetail.getQuantity());
        				productSalesDetailMap.put(salesDetail.getProductName().trim().toUpperCase(), salesOfIndividualProduct);
        			}
        		}    			
        	}
        	
        	//Print the report of product and its sales summary
        	for(String productName : productSalesDetailMap.keySet()) {
        		System.out.printf("\nProduct Name : %s \tNumber of sales : %s, \tQuantity : %s", 
        				productName, productSalesDetailMap.get(productName).getNumberOfSales(), productSalesDetailMap.get(productName).getQuantity());
        	}
        } else if(sales.isPauseRequired() || salesInfoList.size() > 50) {
        	//This piece of code will print only adjustment detail after the 50th message received.
        	for(int i=0;i<salesInfoList.size()-1;i++) {
        		Sales salesDetail = salesInfoList.get(i);
        		if(null != salesDetail && null != salesDetail.getProductName() && !salesDetail.getProductName().trim().isEmpty()
        				&& null != salesDetail.getAdjustmentType() && !salesDetail.getAdjustmentType().trim().isEmpty()
        				&& null != salesDetail.getAdjustmentQuantity() && salesDetail.getAdjustmentQuantity()> 0) {
        			System.out.printf("\nAdjustment -> %sED %s Piece of %s in each sale when we received the message at  %s position.", 
        					salesDetail.getAdjustmentType(), salesDetail.getAdjustmentQuantity(), salesDetail.getProductName().trim(), (i+1));
        		}
        	}
        }
    }
	
	/**
	 * @param processingSalesMessage
	 * This method will accept all available sales and will start adjusting the sales report if exist.
	 */
	public void doAdjustmentProcess(List<Sales> processingSalesMessage) {
		List<String> productNameList = new ArrayList<String>();
		
		for(int i=0;i<processingSalesMessage.size();i++) {
			
			Sales salesDetail = processingSalesMessage.get(i);
			
			if(null != salesDetail && null != salesDetail.getProductName() && !salesDetail.getProductName().trim().isEmpty()
    				&& null != salesDetail.getNumberOfSales() && salesDetail.getNumberOfSales() > 0 
    				&& null != salesDetail.getQuantity() && salesDetail.getQuantity() > 0) {
				productNameList.add(salesDetail.getProductName().trim().toUpperCase());
			}
			
			if(null != salesDetail && null != salesDetail.getProductName() && !salesDetail.getProductName().trim().isEmpty()
    				&& null != salesDetail.getAdjustmentType() && !salesDetail.getAdjustmentType().trim().isEmpty()
    				&& null != salesDetail.getAdjustmentQuantity() && salesDetail.getAdjustmentQuantity()> 0) {
				
				if(productNameList.contains(salesDetail.getProductName().trim().toUpperCase())) {
					
					for(int j=0;j<i;j++) {
						Sales salesDetail1 = processingSalesMessage.get(j);
						if(null != salesDetail1 && null != salesDetail1.getProductName() && !salesDetail1.getProductName().trim().isEmpty()
								&& salesDetail1.getProductName().trim().toUpperCase().equals(salesDetail.getProductName().trim().toUpperCase())) {
							String adjustmentType = salesDetail.getAdjustmentType();
							switch(adjustmentType) {
								case "ADD":
									salesDetail1.setQuantity(salesDetail1.getQuantity()+salesDetail.getAdjustmentQuantity());		
									break;
								case "SUBTRACT":
									salesDetail1.setQuantity(salesDetail1.getQuantity()-salesDetail.getAdjustmentQuantity());
								case "MULTIPLY":
									salesDetail1.setQuantity(salesDetail1.getQuantity()*salesDetail.getAdjustmentQuantity());
							}
							
						}
					}
				}
			}
		}
	}
}
