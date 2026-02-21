package com.example.blackrock.challenge.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Number totalTransactionAmount;
	private Number totalCeiling;
	private List<PeriodBean> savingsByDates;
}
