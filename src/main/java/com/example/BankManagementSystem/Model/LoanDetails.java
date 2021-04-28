package com.example.BankManagementSystem.Model;

import org.jnosql.artemis.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class LoanDetails {
	
	private String LoanType;
	private String LoanAmount;
	private String date;
	private String RateOfInterest;
	private String DurationOfLoan;

}
