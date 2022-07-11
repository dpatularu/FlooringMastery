package com.flooringmastery.service;

import com.flooringmastery.model.dao.Dao;
import com.flooringmastery.model.dao.DateHasNoOrdersException;
import com.flooringmastery.model.dao.PersistenceException;
import com.flooringmastery.model.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

@Component
public class ServiceLayerImpl implements ServiceLayer{
    private final Dao dao;

    @Autowired
    public ServiceLayerImpl(Dao dao){
        this.dao = dao;
    }

    @Override
    public List<Order> getOrdersByDate(String dateInput) throws DateHasNoOrdersException, InvalidDateException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        dateFormat.setLenient(false);

        try{
            dateFormat.parse(dateInput);
        }catch(ParseException e){
            throw new InvalidDateException(dateInput + " is not a valid date. Please enter date in mm-dd-yyyy format.");
        }

        HashMap<String, List<Order>> allOrders = dao.getAllOrders();
        List<Order> ordersByDate = allOrders.get(dateInput);
        if(ordersByDate == null){
            throw new DateHasNoOrdersException(dateInput + " has no orders.");
        }
        return allOrders.get(dateInput);
    }

    @Override
    public Order addOrder(String orderDate, String customerName, String stateAbbrev, String productName, BigDecimal area) throws PersistenceException {
        return dao.addOrder(orderDate, customerName, stateAbbrev, productName, area);
    }

    @Override
    public Order removeOrder(String date, int orderId) throws OrderDoesNotExistException, DateHasNoOrdersException, PersistenceException {
        return dao.removeOrder(date, orderId);
    }

    @Override
    public Order editOrder(String date, int orderId, String customerName, String stateAbbrev, String productName, String area) throws DateHasNoOrdersException, OrderDoesNotExistException, PersistenceException {
        return dao.editOrder(date, orderId, customerName, stateAbbrev, productName, area);
    }

    @Override
    public List<String> getStateAbbreviationsList(){
        return dao.getStateAbbreviationList();
    }

    @Override
    public List<String> getProductNamesList(){
        return dao.getProductNameList();
    }

    @Override
    public void exportData() throws PersistenceException {
        dao.exportData();
    }

    @Override
    public void deleteAllData(){
        dao.deleteAllData();
    }
}