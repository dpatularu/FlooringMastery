package com.flooringmastery.model.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class State {
    private String abbreviation;
    private String name;
    private BigDecimal taxRate;

    public State(String abbreviation, String name, BigDecimal taxRate){
        this.abbreviation = abbreviation;
        this.name = name;
        this.taxRate = taxRate;
    }

    public State(String abbreviation, String name, String taxRate){
        this.abbreviation = abbreviation;
        this.name = name;
        this.taxRate = new BigDecimal(taxRate).setScale(2, RoundingMode.HALF_UP);
    }
    public String abbreviation(){
        return abbreviation;
    }

    public String name(){
        return name;
    }

    public BigDecimal taxRate(){
        return taxRate;
    }

    public BigDecimal percentageTaxRate(){
        return taxRate.divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }
}