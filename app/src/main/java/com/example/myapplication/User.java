/*
The user class utilizes singleton design pattern to store and work with users.
Users can add different accounts with the help of compositeAccount.
 */

package com.example.myapplication;

import java.util.List;

public class User {
    private static User instance = null;
    private String name;
    private String email;
    private String password;
    private String accountNumber;
    private CompositeAccount accounts;
    private User(String name, String email, String password, String accountNumber, CompositeAccount accounts) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountNumber = accountNumber;
        this.accounts = accounts;
    }

    /***
     * Crates an instance of the User class.
     * @param name name of user
     * @param email email of user
     * @param password password of user
     * @param accountNumber Account number of the user
     * @param accounts accounts list of the user; for new user: value will be null
     * @return returns the instance of the created or stored user details.
     */
    public static User getInstance(String name, String email, String password, String accountNumber, CompositeAccount accounts) {
        if(instance == null) {
            instance = new User(name, email, password, accountNumber, accounts);
        }
        return instance;
    }

    /***
     * Clears the instance value, which makes it possible to
     * generate a new user object for different login or registration
     */
    public void userInstanceDestroy() {
        instance = null;
    }

    /***
     * Allows to add new account under the user
     * @param account The type of account created
     */
    public void addAccount(AccountComponent account) { accounts.add(account); }

    /***
     * Allows deletion of accounts from the users' account list
     * @param account The account to be deleted
     */
    public void removeAccount(AccountComponent account) { accounts.remove(account); }

    /***
     * This function provides information on the list of accounts the user has
     * @return list of accounts
     */
    public List<AccountComponent> getAccounts() { return accounts.getAccounts(); }
    //Getters for various usage
    public String getUserName() {
        return name;
    }
    public String getUserEmail() {
        return email;
    }
    public String getUserPassword() {
        return password;
    }
    public String getUserAccountNumber() { return accountNumber; }
    public CompositeAccount getUserAccounts() { return accounts; }
}