/*
The Employee class utilizes singleton design pattern to create an instance of the employee class.
It stores relevant information about an employee.
 */

package com.example.myapplication;

import androidx.annotation.NonNull;

import java.util.Random;

public class Employee {
    private static Employee instance = null;
    private String name;
    private String email;
    private String password;
    private String employeeId;
    private Employee(String name, String email, String password, String employeeId) {
        this.name = name;
        this.email = email;
        this.password = password;
        if(employeeId == null) this.employeeId = generateEmployeeId();
        else this.employeeId = employeeId;
    }

    /***
     * Crates an instance of the Employee class.
     * @param name name of employee
     * @param email email of employee
     * @param password password of employee
     * @param employeeId randomly generated id of employee
     * @return returns the instance of the created or stored employee details.
     */
    public static Employee getInstance(String name, String email, String password, String employeeId) {
        if(instance == null) {
            instance = new Employee(name, email, password, employeeId);
        }
        return instance;
    }

    /***
     * Creates a six-digit random value for employeeId
     * @return randomly generated value
     */
    @NonNull
    private String generateEmployeeId() {
        Random random = new Random();
        int id = random.nextInt(900000) + 100000;
        return String.valueOf(id);
    }

    /***
     * Clears the instance value, which makes it possible to
     * generate a new Employee object for different login or registration
     */
    public void employeeInstanceDestroy() {
        instance = null;
    }

    //Getters for various usage
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getEmployeeId() {
        return employeeId;
    }
}
