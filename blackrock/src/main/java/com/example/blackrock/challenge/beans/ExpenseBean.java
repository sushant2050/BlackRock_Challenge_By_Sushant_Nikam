package com.example.blackrock.challenge.beans;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date date;
	private Number amount;
	private Number ceiling;
	private Number remanent;
	private String message;
	private Boolean inKPeriod;
}
