package com.example.myapplication;

import android.content.Context;

/**
 * Facade over the wallet's financial operations. Screens call these simple
 * methods and get back a {@link Result}; all validation, balance updates and
 * history recording happen here so the UI stays thin.
 */
public class FinancialServicesFacade {

    private final SessionManager session;
    private final TransactionStore store;

    public FinancialServicesFacade(Context context) {
        this.session = new SessionManager(context);
        this.store = new TransactionStore(context);
    }

    public double getBalance() {
        return session.getBalance();
    }

    public double getSavings() {
        return session.getSavings();
    }

    public Result cashIn(double amount) {
        Result invalid = validatePositive(amount);
        if (invalid != null) return invalid;

        double newBalance = session.getBalance() + amount;
        session.setBalance(newBalance);
        record("Cash In", "Added to wallet", amount, true, newBalance);
        return Result.ok("Cashed in " + MoneyUtils.format(amount)
                + ". Balance: " + MoneyUtils.format(newBalance));
    }

    public Result cashOut(double amount) {
        Result invalid = validateOutgoing(amount);
        if (invalid != null) return invalid;

        double newBalance = session.getBalance() - amount;
        session.setBalance(newBalance);
        record("Cash Out", "Withdrawn from wallet", amount, false, newBalance);
        return Result.ok("Cashed out " + MoneyUtils.format(amount)
                + ". Balance: " + MoneyUtils.format(newBalance));
    }

    public Result sendMoney(String recipient, double amount) {
        if (recipient == null || recipient.trim().isEmpty()) {
            return Result.fail("Please enter a recipient");
        }
        Result invalid = validateOutgoing(amount);
        if (invalid != null) return invalid;

        double newBalance = session.getBalance() - amount;
        session.setBalance(newBalance);
        record("Send Money", "To " + recipient.trim(), amount, false, newBalance);
        return Result.ok("Sent " + MoneyUtils.format(amount) + " to " + recipient.trim()
                + ". Balance: " + MoneyUtils.format(newBalance));
    }

    public Result payBill(String biller, double amount) {
        if (biller == null || biller.trim().isEmpty()) {
            return Result.fail("Please enter a biller");
        }
        Result invalid = validateOutgoing(amount);
        if (invalid != null) return invalid;

        double newBalance = session.getBalance() - amount;
        session.setBalance(newBalance);
        record("Pay Bill", biller.trim(), amount, false, newBalance);
        return Result.ok("Paid " + MoneyUtils.format(amount) + " to " + biller.trim()
                + ". Balance: " + MoneyUtils.format(newBalance));
    }

    public Result addToSavings(double amount) {
        Result invalid = validateOutgoing(amount);
        if (invalid != null) return invalid;

        double newBalance = session.getBalance() - amount;
        session.setBalance(newBalance);
        session.setSavings(session.getSavings() + amount);
        record("Savings", "Moved to savings", amount, false, newBalance);
        return Result.ok("Moved " + MoneyUtils.format(amount) + " to savings. Savings: "
                + MoneyUtils.format(session.getSavings()));
    }

    // --- internals -------------------------------------------------------

    private Result validatePositive(double amount) {
        if (amount <= 0) return Result.fail("Amount must be greater than 0");
        return null;
    }

    private Result validateOutgoing(double amount) {
        Result invalid = validatePositive(amount);
        if (invalid != null) return invalid;
        if (amount > session.getBalance()) return Result.fail("Insufficient balance");
        return null;
    }

    private void record(String type, String title, double amount, boolean incoming, double balanceAfter) {
        store.add(new Transaction(type, title, amount, incoming,
                System.currentTimeMillis(), balanceAfter));
    }

    /** Simple success/failure carrier with a user-facing message. */
    public static class Result {
        public final boolean ok;
        public final String message;

        private Result(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        static Result ok(String message) {
            return new Result(true, message);
        }

        static Result fail(String message) {
            return new Result(false, message);
        }
    }
}
