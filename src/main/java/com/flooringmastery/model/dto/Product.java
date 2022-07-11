package com.flooringmastery.model.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product {
    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal laborCostPerSquareFoot;

    public Product(String productType, BigDecimal costPerSquareFoot, BigDecimal laborCostPerSquareFoot){
        this.productType = productType;
        this.costPerSquareFoot = costPerSquareFoot.setScale(2, RoundingMode.HALF_UP);
        this.laborCostPerSquareFoot = laborCostPerSquareFoot.setScale(2, RoundingMode.HALF_UP);
    }

    public Product(String productType, String costPerSquareFoot, String laborCostPerSquareFoot){
        this.productType = productType;
        this.costPerSquareFoot = new BigDecimal(costPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
        this.laborCostPerSquareFoot = new BigDecimal(laborCostPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
    }

    public String productType() {
        return productType;
    }

    public BigDecimal costPerSquareFoot() {
        return costPerSquareFoot;
    }

    public BigDecimal laborCostPerSquareFoot() {
        return laborCostPerSquareFoot;
    }
}