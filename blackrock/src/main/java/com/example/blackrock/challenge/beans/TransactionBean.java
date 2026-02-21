package com.example.blackrock.challenge.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionBean implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PeriodBean> q;
	private List<PeriodBean> p;
	private List<PeriodBean> k;
	private Number age;
	private Number wage;
	private Number inflation;
	private List<ExpenseBean> transactions;
	private List<ExpenseBean> valid;
	private List<ExpenseBean> invalid;
}
