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
public class Register {
	
	private String name;
	private String username;
	private String password;
	private String Address;
	private String State;
	private String country;
	private String EmailAddress;
	private String PAN;
	private double contactnumber;
	private String Accounttype;
	 
}
