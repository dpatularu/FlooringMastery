package com.flooringmastery.view;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class UserIOConsoleImpl implements  UserIO{

    final private Scanner console = new Scanner(System.in);

    public void println(String msg) {
        System.out.println(msg);
    }

    public void print(String msg) { System.out.print(msg);}

    public String readString(String prompt) {
        System.out.println(prompt);
        return console.nextLine();
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Input error. Please enter an integer value");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        int num = 0;
        do {
            num = readInt(prompt);
        } while (num < min || num > max);
        return num;
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                return new BigDecimal(this.readString(prompt));
            } catch (NumberFormatException e) {
                this.println("Input error. Please try again.");
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt, InputMode inputMode) {
        String input = "";
        while (true) {
            try {
                input = this.readString(prompt);
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                if(inputMode == InputMode.OPTIONAL && input.equals("")){
                    return null;
                }
                this.println("Input error. Please try again.");
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt, BigDecimal min){
        BigDecimal num;
        do {
            num = readBigDecimal(prompt);
        } while(num.compareTo(min) == -1);
        return num;
    }

    public BigDecimal readBigDecimal(String prompt, BigDecimal min, InputMode inputMode){
        BigDecimal num;
        do {
            num = readBigDecimal(prompt, inputMode);
        } while(num.compareTo(min) == -1);
        return num;
    }
}