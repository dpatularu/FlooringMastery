package com.flooringmastery.model.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Order {
    private int orderNumber;
    private String customerName;
    private State state;
    private Product product;
    private BigDecimal area;

    public Order(int orderNumber, String customerName, State state, Product product, String area) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.state = state;
        this.product = product;
        this.area = new BigDecimal(area).setScale(2, RoundingMode.HALF_UP);
    }

    public Order(int orderNumber, String customerName, State state, Product product, BigDecimal area) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.state = state;
        this.product = product;
        this.area = area;
    }

    public int getId(){
        return orderNumber;
    }

    public String getCustomerName(){
        return customerName;
    }

    public State getState(){
        return state;
    }

    public Product getProduct(){
        return product;
    }

    public BigDecimal getArea(){
        return area;
    }

    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }

    public void setArea(String area){
        this.area = new BigDecimal(area);
    }

    public void setState(State state){
        this.state = state;
    }

    public void setProduct(Product product){
        this.product = product;
    }

    public BigDecimal materialCost() {
        return area.multiply(product.costPerSquareFoot()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal laborCost() {
        return area.multiply((product.laborCostPerSquareFoot())).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalTax() {
        BigDecimal laborPlusLabor = laborCost().add(materialCost()).setScale(2, RoundingMode.HALF_UP);
        return laborPlusLabor.multiply(state.percentageTaxRate()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal grandTotal() {
        return materialCost().add(laborCost()).add(totalTax()).setScale(2, RoundingMode.HALF_UP);
    }

    public String[] toArray() {
        String[] array = {
                String.valueOf(orderNumber), customerName,
                state.abbreviation(), state.taxRate().toString(),
                product.productType(), area.toString(), product.costPerSquareFoot().toString(), product.laborCostPerSquareFoot().toString(),
                materialCost().toString(), laborCost().toString(), totalTax().toString(), grandTotal().toString()
        };
        return array;
    }
}