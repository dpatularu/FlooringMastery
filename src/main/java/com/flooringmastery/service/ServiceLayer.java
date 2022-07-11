package com.flooringmastery.service;

import com.flooringmastery.model.dao.DateHasNoOrdersException;
import com.flooringmastery.model.dao.PersistenceException;
import com.flooringmastery.model.dto.Order;

import java.math.BigDecimal;
import java.util.List;

public interface ServiceLayer {
    List<Order> getOrdersByDate(String dateInput) throws DateHasNoOrdersException, InvalidDateException;

    Order addOrder(String orderDate, String customerName, String stateAbbrev, String productName, BigDecimal area) throws PersistenceException;

    Order removeOrder(String date, int orderId) throws OrderDoesNotExistException, DateHasNoOrdersException, PersistenceException;

    Order editOrder(String date, int orderId,  String customerName, String stateAbbrev, String productName, String area) throws DateHasNoOrdersException, OrderDoesNotExistException, PersistenceException;
    List<String> getStateAbbreviationsList();

    List<String> getProductNamesList();
    void exportData() throws PersistenceException;

    void deleteAllData();
}