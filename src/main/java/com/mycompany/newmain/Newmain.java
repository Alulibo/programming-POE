package com.mycompany.newmain;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Login class for PROG5121 POE - Part 1 (Registration and Login feature).
 * Handles registering a user (username, password, SA cell number) and logging
 * that user back in.
 */
public class Newmain {

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final String cellPhoneNumber;
//constuctor

    public Newmain(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    /*
     * Username must contain an underscore and be no more than 5 characters long.
     * @return 
     */
    public boolean checkUserName() {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Password must be at least 8 characters, contain a capital letter, a
     * number, and a special character.
     *
     * @return
     */
    public boolean checkPasswordComplexity() {
        if (password == null) {
            return false;
        }
        boolean longEnough = password.length() >= 8;
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        return longEnough && hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Cell phone number must contain the international country code (e.g. +27)
     * followed by the number, with the number portion no more than 10
     * characters long. Example valid: +27838968976
     *
     * @return
     */
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        // '+' then 1-3 digit country code, then 7-10 digit local number
        String regex = "^\\+[0-9]{1,3}[0-9]{7,10}$";
        return Pattern.matches(regex, cellPhoneNumber);
    }

    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, "
                    + "and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell phone number is incorrectly formatted or does not contain "
                    + "international code; please correct the number and try again.";
        }
        return "Username successfully captured. Password successfully captured. "
                + "Cell number successfully captured. The two above conditions have been met, "
                + "and the user has been registered successfully.";
    }

    /**
     * Verifies that the entered username and password match what was
     * registered.
     *
     * @param enteredUsername
     * @param enteredPassword
     * @return
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (username == null || password == null) {
            return false;
        }
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    /**
     * Returns the appropriate message for a successful or failed login attempt.
     *
     * @param loginSuccessful
     * @return
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // Getters (useful for testing / other parts of the POE)
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public static void main(String[] args) {
    java.util.Scanner in = new java.util.Scanner(System.in);

    System.out.print("First name: ");
    String first = in.nextLine();
    System.out.print("Last name: ");
    String last = in.nextLine();
    System.out.print("Username: ");
    String user = in.nextLine();
    System.out.print("Password: ");
    String pass = in.nextLine();
    System.out.print("Cell number: ");
    String cell = in.nextLine();

    Newmain account = new Newmain(first, last, user, pass, cell);
    System.out.println(account.registerUser());

    if (account.checkUserName() && account.checkPasswordComplexity()
            && account.checkCellPhoneNumber()) {
        System.out.print("Login username: ");
        String u = in.nextLine();
        System.out.print("Login password: ");
        String p = in.nextLine();
boolean ok = account.loginUser(u, p);
            System.out.println(account.returnLoginStatus(ok));
            if (ok) {
                Message.startMessaging(in);
            }
        }
    }
}