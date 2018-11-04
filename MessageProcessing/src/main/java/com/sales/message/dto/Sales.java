package com.sales.message.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sales implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String productName;
	private Double quantity = new Double(0);
	private Double numberOfSales = new Double(1);
	private String adjustmentType;
	private Integer adjustmentQuantity;
	private boolean isPauseRequired = false;
	
	public Sales() {}
	
	public Sales(String productName, Double quantity) {
		this.productName = productName;
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
    public Double getNumberOfSales() {
		return numberOfSales;
	}

	public void setNumberOfSales(Double numberOfSales) {
		this.numberOfSales = numberOfSales;
	}

	public String getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public Integer getAdjustmentQuantity() {
		return adjustmentQuantity;
	}

	public void setAdjustmentQuantity(Integer adjustmentQuantity) {
		this.adjustmentQuantity = adjustmentQuantity;
	}

	public boolean isPauseRequired() {
		return isPauseRequired;
	}

	public void setPauseRequired(boolean isPauseRequired) {
		this.isPauseRequired = isPauseRequired;
	}

	@Override
    public String toString() {
        return String.format("Sales{Product Name=%s, Quantity=%s, NumberOfSales=%s, AdjustmentType=%s, AdjustmentQuantity=%s, IsPauseRequired=%s}", 
        		getProductName(), getQuantity(), getNumberOfSales(), getAdjustmentType(), getAdjustmentQuantity(), isPauseRequired());
    }
}
