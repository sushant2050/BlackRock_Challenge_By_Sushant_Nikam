package com.example.blackrock.challenge.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.blackrock.challenge.beans.CalculationBean;
import com.example.blackrock.challenge.beans.ExpenseBean;
import com.example.blackrock.challenge.beans.PeriodBean;
import com.example.blackrock.challenge.beans.TaxSlab;
import com.example.blackrock.challenge.beans.TransactionBean;
import com.example.blackrock.challenge.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService{

	private static final List<TaxSlab> NEW_REGIME_SLABS_2025 = List.of(
            new TaxSlab(         0,  7_000_001,  0.0),
            new TaxSlab( 7_000_001, 10_000_001, 10.0),
            new TaxSlab(10_000_001, 12_000_001, 15.0),
            new TaxSlab(12_000_001, 15_000_001, 20.0),
            new TaxSlab(15_000_001, Long.MAX_VALUE, 30.0)
    );
	
	@Override
	public Number getClosestMultipleOfHundred(Number number) {
		Number modulus=number.intValue()/100;
		modulus=modulus.intValue()+1;
		return modulus.intValue()*100;
	}

	@Override
	public TransactionBean checkAndReturnValidityOfTransaction(List<ExpenseBean> transactions) {
		Map<Date,Number> transactionMap=new HashMap();
		TransactionBean bean=new TransactionBean();
		List<ExpenseBean> valid=new ArrayList();
		List<ExpenseBean> inValid=new ArrayList();
		for(ExpenseBean expenseBean:transactions) {
			if(transactionMap.get(expenseBean.getDate())!=null) {
				expenseBean.setMessage("Duplicate transaction");
				expenseBean.setInKPeriod(null);
				expenseBean.setCeiling(null);
				expenseBean.setRemanent(null);
			}else {
				transactionMap.put(expenseBean.getDate(),expenseBean.getAmount());
			}
			if(expenseBean.getAmount()!=null && expenseBean.getAmount().intValue()<0) {
				expenseBean.setMessage("Negative amounts are not allowed");
				expenseBean.setInKPeriod(null);
				expenseBean.setCeiling(null);
				expenseBean.setRemanent(null);
			}
			if(expenseBean.getMessage()!=null) {
				inValid.add(expenseBean);
			}else {
				valid.add(expenseBean);
			}
		}
		bean.setValid(valid);
		bean.setInvalid(inValid);
		return bean;
	}

	@Override
	public List<ExpenseBean> parseTransactions(List<ExpenseBean> expenseBeans) {
		expenseBeans=expenseBeans.stream().map(bean->{
			Number amount=bean.getAmount();
			Number ceiling=getClosestMultipleOfHundred(amount);
			Number remanent=(ceiling.doubleValue()-amount.doubleValue());
			bean.setCeiling(ceiling);
			bean.setRemanent(remanent);
			return bean;
		}).collect(Collectors.toList());
		return expenseBeans;
	}

	@Override
	public List<ExpenseBean> checkTransactionsBasedOnQPeriod(List<ExpenseBean> expenseBeans,
			List<PeriodBean> qPeriod) {
		if(!qPeriod.isEmpty()) {
			PeriodBean  bean=qPeriod.get(0);
			Date start=bean.getStart();
			Date end=bean.getEnd();
			Number fixed=bean.getFixed();
			for(ExpenseBean expenseBean:expenseBeans) {
				if((expenseBean.getDate().after(start) || expenseBean.getDate().equals(start))
						&& ( expenseBean.getDate().before(end) ||  expenseBean.getDate().equals(end))) {
					expenseBean.setRemanent(fixed);
				}
			}
		}
		return expenseBeans;
	}

	@Override
	public List<ExpenseBean> checkTransactionsBasedOnPPeriod(List<ExpenseBean> expenseBeans, List<PeriodBean> pPeriod) {
		if(!pPeriod.isEmpty()) {
			PeriodBean bean=pPeriod.get(0);
			Date start=bean.getStart();
			Date end=bean.getEnd();
			Number extra=bean.getExtra();
			for(ExpenseBean expenseBean:expenseBeans) {
				if(expenseBean.getRemanent()!=null 
						&& expenseBean.getRemanent().doubleValue()!=0.0
						&& (expenseBean.getDate().after(start) || expenseBean.getDate().equals(start))
						&& (expenseBean.getDate().before(end) || expenseBean.getDate().equals(end))) {
					expenseBean.setRemanent(expenseBean.getRemanent().doubleValue()+extra.doubleValue());
				}
			}
		}
		return expenseBeans;
	}

	@Override
	public TransactionBean calculateRemanentForKPeriods(List<ExpenseBean> expenseBeans, List<PeriodBean> kPeriods,Boolean calcuateAmount) {
		TransactionBean transactionBean=new TransactionBean();
		Date kStart=null;
		Date kEnd=null;
		Date date=null;
		
		for(PeriodBean period:kPeriods) {
			kStart=period.getStart();
			kEnd=period.getEnd();
			Double amount=0d;
			for(ExpenseBean expenseBean:expenseBeans) {
				date=expenseBean.getDate();
				if(expenseBean.getRemanent()!=null &&
						expenseBean.getRemanent().doubleValue()!=0.0 &&
						date.after(kStart) && date.before(kEnd)) {
					if(calcuateAmount) {
					amount+=expenseBean.getRemanent().doubleValue();
					}
					expenseBean.setInKPeriod(true);
				}
				
			}
			if(calcuateAmount) {
				period.setAmount(amount);
				amount=0d;
				}
		}
		//filtering out the expenses whose remanents are zero
		expenseBeans=expenseBeans.stream().filter(expense->expense.getRemanent()!=null && expense.getRemanent().doubleValue()!=0).collect(Collectors.toList());
		if(calcuateAmount) {
		transactionBean.setK(kPeriods);
		}
		transactionBean.setTransactions(expenseBeans);
		return transactionBean;
	}

	@Override
	public CalculationBean calculateReturns(List<ExpenseBean> expenseBeans,TransactionBean transactionBean,
			 String investmentType) {
		CalculationBean calculationBean=new CalculationBean();
		//expenseBeans=parseTransactions(expenseBeans);
		expenseBeans=checkTransactionsBasedOnPPeriod(expenseBeans,transactionBean.getP());
		expenseBeans=checkTransactionsBasedOnQPeriod(expenseBeans,transactionBean.getQ());
		TransactionBean bean=calculateRemanentForKPeriods(expenseBeans,transactionBean.getK(),true);
		//calculationBean.setTotalCeiling(calculateTotalCeilingAmount(bean.getTransactions()));
		//calculationBean.setTotalTransactionAmount(calculateTotalTransactionAmount(bean.getTransactions()));
		List<PeriodBean> kPeriods=new ArrayList();
		if("NPS".equals(investmentType)) {
		kPeriods=calculateFinalInvestmentAmountNPS(bean.getK(),transactionBean);
		}else {
			kPeriods=calculateFinalInvestmentAmountIndex(bean.getK(),transactionBean);	
		}
		calculationBean.setSavingsByDates(bean.getK());
		return calculationBean;
	}

	private List<PeriodBean> calculateFinalInvestmentAmountIndex(List<PeriodBean> kPeriod, TransactionBean transactionBean) {
		Number investmentPeriod=60-transactionBean.getAge().intValue();
		Number amount=null;
		Number finalInvestmentAmount=null;
		for(PeriodBean periodBean:kPeriod) {
			amount=periodBean.getAmount();
			finalInvestmentAmount=amount.doubleValue()*Math.pow(1.1449,investmentPeriod.doubleValue());
			Number npsRealValue=finalInvestmentAmount.doubleValue()/Math.pow((1+(transactionBean.getInflation().doubleValue()/100)),investmentPeriod.doubleValue());
			periodBean.setReturns(npsRealValue);
		}
		//NPS deduction= min(amount,10 % of monthly salary,200000)
		return kPeriod;
	}

	private List<PeriodBean> calculateFinalInvestmentAmountNPS(List<PeriodBean> kPeriod, TransactionBean transactionBean) {
		//investment period
		Number investmentPeriod=60-transactionBean.getAge().intValue();
		Number amount=null;
		Number finalInvestmentAmount=null;
		for(PeriodBean periodBean:kPeriod) {
			amount=periodBean.getAmount();
			finalInvestmentAmount=amount.doubleValue()*Math.pow(1.0711,investmentPeriod.doubleValue());
			Number npsDeduction=Math.min(amount.doubleValue(),Math.min(transactionBean.getWage().doubleValue()*0.1,200000));
			Number taxBenefit=calculateTaxBenefit(transactionBean,amount);
			periodBean.setTaxBenefit(taxBenefit);
			Number npsRealValue=finalInvestmentAmount.doubleValue()/Math.pow((1+(transactionBean.getInflation().doubleValue()/100)),investmentPeriod.doubleValue());
			periodBean.setProfit(npsRealValue.doubleValue()-npsDeduction.doubleValue());
		}
		//NPS deduction= min(amount,10 % of monthly salary,200000)
		return kPeriod;
	}

	private Number calculateTaxBenefit(TransactionBean transactionBean, Number amount) {
		//tax for only wage
		Number taxForOnlyWage=NEW_REGIME_SLABS_2025.stream().filter(
				range->range.fromInclusive()>=transactionBean.getWage().longValue()
							&& transactionBean.getWage().longValue()<=range.toExclusive()
				).collect(Collectors.toList()).get(0).ratePercentage();
		
		Number wageMinusAmount=transactionBean.getWage().doubleValue()-amount.doubleValue();
		
		Number taxForWageMinusAmount=NEW_REGIME_SLABS_2025.stream().filter(
				range->range.fromInclusive()>=wageMinusAmount.longValue()
				&& wageMinusAmount.longValue()<=range.toExclusive()
	).collect(Collectors.toList()).get(0).ratePercentage();
		
		return Math.abs(taxForOnlyWage.doubleValue()-taxForWageMinusAmount.doubleValue());
	}

	@Override
	public Number calculateTotalCeilingAmount(List<ExpenseBean> expenseBeans) {
		Double totalCeiling=0d;
		for(ExpenseBean bean:expenseBeans) {
			if(bean.getCeiling()!=null)
				totalCeiling+=bean.getCeiling().doubleValue();
		}
		return totalCeiling;
	}
	@Override
	public Number calculateTotalTransactionAmount(List<ExpenseBean> expenseBeans) {
		Double totalTransactionAmount=0d;
		for(ExpenseBean bean:expenseBeans) {
			if(bean.getAmount()!=null)
			totalTransactionAmount+=bean.getAmount().doubleValue();
		}
		return totalTransactionAmount;
	}
}
