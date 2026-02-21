package com.example.blackrock.challenge.api;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blackrock.challenge.beans.CalculationBean;
import com.example.blackrock.challenge.beans.ExpenseBean;
import com.example.blackrock.challenge.beans.PeriodBean;
import com.example.blackrock.challenge.beans.TransactionBean;
import com.example.blackrock.challenge.service.TransactionService;

@RestController
@RequestMapping(value = "/blackrock/challenge/v1")
public class TransactionAPIController {

	@Autowired
	TransactionService transactionService;
	
	@PostMapping("/transactions:parse")
	public ResponseEntity<List<ExpenseBean>> transactionsParse(@RequestBody List<ExpenseBean> expenseBeans){
		List<ExpenseBean> result=transactionService.parseTransactions(expenseBeans);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/transactions:validator")
	public ResponseEntity<TransactionBean> transactionsValidator(@RequestBody TransactionBean transactionBean){
		TransactionBean result=new TransactionBean();
		if(transactionBean.getTransactions()!=null && !transactionBean.getTransactions().isEmpty()) {
		result=transactionService.checkAndReturnValidityOfTransaction(transactionBean.getTransactions());
		}
		return ResponseEntity.ok(result);
	}
	@PostMapping(value = "/transactions:filter")
	public ResponseEntity<TransactionBean> filterTransactions(@RequestBody TransactionBean transactionBean){
		
		TransactionBean result=new TransactionBean();
		List<ExpenseBean> expenseBeans=null;
		//determine the amount saved based on the expenses
		expenseBeans=transactionService.parseTransactions(transactionBean.getTransactions());
		transactionBean.setTransactions(expenseBeans);
		
		//update the amount saved based on fixed amounts
		expenseBeans=transactionService.checkTransactionsBasedOnQPeriod(expenseBeans,transactionBean.getQ());
		transactionBean.setTransactions(expenseBeans);
		
		//update the amount saved based on extra increases
		expenseBeans=transactionService.checkTransactionsBasedOnPPeriod(expenseBeans,transactionBean.getP());
		transactionBean.setTransactions(expenseBeans);
		
		//evaluate the amounts saved in periods
		TransactionBean bean=transactionService.calculateRemanentForKPeriods(expenseBeans,transactionBean.getK(),false);
		
		bean=transactionService.checkAndReturnValidityOfTransaction(bean.getTransactions());
		
		return ResponseEntity.ok(bean);
	}
	
	@PostMapping(value={"/returns:nps"})
	public ResponseEntity<CalculationBean> calculateReturnsNPS(@RequestBody TransactionBean transactionBean){
		Number totalTransactionAmount=0d;
		Number totalCeiling=0d;
		List<ExpenseBean> expenseBeans=null;
		TransactionBean bean=transactionService.checkAndReturnValidityOfTransaction(transactionBean.getTransactions());
		expenseBeans=transactionService.parseTransactions(bean.getValid());
		CalculationBean calculationBean=transactionService.calculateReturns(expenseBeans,transactionBean,"NPS");
		totalTransactionAmount=transactionService.calculateTotalTransactionAmount(expenseBeans);
		totalCeiling=transactionService.calculateTotalCeilingAmount(expenseBeans);
		calculationBean.setTotalTransactionAmount(totalTransactionAmount);
		calculationBean.setTotalCeiling(totalCeiling);
		return ResponseEntity.ok(calculationBean);
	}
	@PostMapping(value={"/returns:index"})
	public ResponseEntity<CalculationBean> calculateReturnsIndex(@RequestBody TransactionBean transactionBean){
		Number totalTransactionAmount=0d;
		Number totalCeiling=0d;
		List<ExpenseBean> expenseBeans=null;
		TransactionBean bean=transactionService.checkAndReturnValidityOfTransaction(transactionBean.getTransactions());
		expenseBeans=transactionService.parseTransactions(bean.getValid());
		CalculationBean calculationBean=transactionService.calculateReturns(expenseBeans,transactionBean,"INDEX");
		totalTransactionAmount=transactionService.calculateTotalTransactionAmount(expenseBeans);
		totalCeiling=transactionService.calculateTotalCeilingAmount(expenseBeans);
		calculationBean.setTotalTransactionAmount(totalTransactionAmount);
		calculationBean.setTotalCeiling(totalCeiling);
		return ResponseEntity.ok(calculationBean);
	}
	
	
}
