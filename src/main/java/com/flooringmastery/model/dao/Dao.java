package com.flooringmastery.model.dao;

import com.flooringmastery.model.dto.Order;
import com.flooringmastery.service.OrderDoesNotExistException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface Dao {

    Order addOrder(String orderDate, String customerName, String stateAbbrev, String productName, BigDecimal area) throws PersistenceException;

    Order removeOrder(String date, int orderId) throws OrderDoesNotExistException, DateHasNoOrdersException, PersistenceException;

    Order editOrder(String date, int orderId, String customerName, String stateAbbrev, String productName, String area) throws DateHasNoOrdersException, OrderDoesNotExistException, PersistenceException;
    HashMap<String, List<Order>> getAllOrders();

    List<String> getStateAbbreviationList();

    List<String> getProductNameList();

    void exportData() throws PersistenceException;

    void deleteAllData();
}