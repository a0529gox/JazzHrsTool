package com.mysterion.jht2.vo;

import java.io.Serializable;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class WorkItemBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CheckBox selected = null;
    private Integer id = null;
    private String summary = null;
    private Double estimateHrs = null;
    private Double realHrs = null;
    private TextField hrs = null;
    private CheckBox close = null;

	public WorkItemBean() {
		selected = new CheckBox();
		close = new CheckBox();
		
		TextField hrs = new TextField();
		hrs.setText("0");
		this.hrs = hrs;
	}

	public CheckBox getSelected() {
		return selected;
	}

	public void setSelected(CheckBox selected) {
		this.selected = selected;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getEstimateHrs() {
		return estimateHrs;
	}

	public void setEstimateHrs(Double estimateHrs) {
		this.estimateHrs = estimateHrs;
	}

	public Double getRealHrs() {
		return realHrs;
	}

	public void setRealHrs(Double realHrs) {
		this.realHrs = realHrs;
	}

	public TextField getHrs() {
		return hrs;
	}

	public void setHrs(TextField hrs) {
		this.hrs = hrs;
	}
	
	public Double getHrsValue() {
		String sHrs = hrs.getText();
		if (sHrs == null || sHrs.trim().length() == 0) {
			return 0d;
		}
		
		try {
			return Double.parseDouble(sHrs);
		} catch (NumberFormatException e) {
			return -1000d;
		}
	}
	
	public void setHrsValue(Double hrs) {
		String value = hrs == null ? null : String.valueOf(hrs);
		this.hrs.setText(value);
	}

	public CheckBox getClose() {
		return close;
	}

	public void setClose(CheckBox close) {
		this.close = close;
	}

}
