package com.example.blackrock.challenge.service;

import java.util.List;

import com.example.blackrock.challenge.beans.CalculationBean;
import com.example.blackrock.challenge.beans.ExpenseBean;
import com.example.blackrock.challenge.beans.PeriodBean;
import com.example.blackrock.challenge.beans.TransactionBean;

public interface TransactionService {

	public Number getClosestMultipleOfHundred(Number number);

	public TransactionBean checkAndReturnValidityOfTransaction(List<ExpenseBean> transactions);

	public List<ExpenseBean> parseTransactions(List<ExpenseBean> expenseBeans);

	public List<ExpenseBean> checkTransactionsBasedOnQPeriod(List<ExpenseBean> expenseBeans, List<PeriodBean> qPeriod);

	public List<ExpenseBean> checkTransactionsBasedOnPPeriod(List<ExpenseBean> expenseBeans, List<PeriodBean> pPeriod);

	public TransactionBean calculateRemanentForKPeriods(List<ExpenseBean> expenseBeans, List<PeriodBean> kPeriods,Boolean calcuateAmount);

	public CalculationBean calculateReturns(List<ExpenseBean> expenseBeans, TransactionBean transactionBean, String investmentType);

	public Number calculateTotalTransactionAmount(List<ExpenseBean> expenseBeans);

	public Number calculateTotalCeilingAmount(List<ExpenseBean> expenseBeans);
}
