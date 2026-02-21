package com.example.blackrock.challenge.beans;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodBean implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Number fixed;
	private Number extra;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date start;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date end;
	private Number amount;
	private Number profit;
	private Number taxBenefit;
	private Number returns;
	
}
