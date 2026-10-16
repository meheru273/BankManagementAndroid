package com.example.myapplication;

/** KYC details captured at registration (stored under users/$uid in Firebase). */
public class Profile {
    public final String name;
    public final String phone;      // E.164, e.g. +8801700000000
    public final String nid;        // National ID
    public final String dob;        // date of birth, dd MMM yyyy
    public final String address;
    public final String bankAccount;

    public Profile(String name, String phone, String nid, String dob, String address, String bankAccount) {
        this.name = name;
        this.phone = phone;
        this.nid = nid;
        this.dob = dob;
        this.address = address;
        this.bankAccount = bankAccount;
    }
}
