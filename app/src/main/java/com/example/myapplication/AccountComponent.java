package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

// The AccountComponent class holds a simple function that is executable from all the different user account classes.
abstract class AccountComponent {
    abstract String getAccountDetails();
}

// Leaf
class IndividualAccount extends AccountComponent {
    private String accountType;
    private double balance;
    private double interestRate;
    public IndividualAccount(String accountType, double balance, double interestRate) {
        this.accountType = accountType;
        this.balance = balance;
        this.interestRate = interestRate;
    }
    @Override
    String getAccountDetails() {
        return "Account Type: " + accountType + ", Balance: $" + balance + ", Interest Rate: " + interestRate + "%";
    }
}

// Leaf
class RetirementAccount extends AccountComponent {
    private String accountType;
    private double balance;
    private boolean taxAdvantaged;

    public RetirementAccount(String accountType, double balance, boolean taxAdvantaged) {
        this.accountType = accountType;
        this.balance = balance;
        this.taxAdvantaged = taxAdvantaged;
    }

    @Override
    String getAccountDetails() {
        return "Account Type: " + accountType + ", Balance: $" + balance + ", Tax Advantaged: " + taxAdvantaged;
    }
}

// Composite
class CompositeAccount extends AccountComponent {
    private List<AccountComponent> childAccounts = new ArrayList<>();
    public void add(AccountComponent accountComponent) {
        childAccounts.add(accountComponent);
    }
    public void remove(AccountComponent accountComponent) {
        childAccounts.remove(accountComponent);
    }
    @Override
    String getAccountDetails() {
        StringBuilder details = new StringBuilder();
        for (AccountComponent account : childAccounts) {
            details.append(account.getAccountDetails()).append("\n");
        }
        return details.toString();
    }
    public List<AccountComponent> getAccounts() { return childAccounts; }
}

// Concrete classes for different account types
class CheckingAccount extends IndividualAccount {
    private boolean overdraftProtection;
    public CheckingAccount(double balance, boolean overdraftProtection) {
        super("Checking", balance, 0.0); // Checking accounts typically don't have interest
        this.overdraftProtection = overdraftProtection;
    }
    @Override
    String getAccountDetails() {
        return super.getAccountDetails() + ", Overdraft Protection: " + overdraftProtection;
    }
}

class SavingsAccount extends IndividualAccount {
    public SavingsAccount(double balance, double interestRate) {
        super("Savings", balance, interestRate);
    }
}

class MoneyMarketAccount extends IndividualAccount {
    private int transactionLimit;
    public MoneyMarketAccount(double balance, double interestRate, int transactionLimit) {
        super("Money Market", balance, interestRate);
        this.transactionLimit = transactionLimit;
    }
    @Override
    String getAccountDetails() {
        return super.getAccountDetails() + ", Transaction Limit: " + transactionLimit;
    }
}

class CertificateOfDeposit extends IndividualAccount {
    private int termLength; // in months
    public CertificateOfDeposit(double balance, double interestRate, int termLength) {
        super("CD", balance, interestRate);
        this.termLength = termLength;
    }
    @Override
    String getAccountDetails() {
        return super.getAccountDetails() + ", Term Length: " + termLength + " months";
    }
}

class TraditionalIRA extends RetirementAccount {
    public TraditionalIRA(double balance) {
        super("Traditional IRA", balance, true);
    }
}

class RothIRA extends RetirementAccount {
    public RothIRA(double balance) {
        super("Roth IRA", balance, true);
    }
}
