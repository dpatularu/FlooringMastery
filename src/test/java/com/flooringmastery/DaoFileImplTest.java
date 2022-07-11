package com.flooringmastery;

import com.flooringmastery.model.dao.Dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;


import com.flooringmastery.model.dao.DaoFileImpl;
import com.flooringmastery.model.dao.DateHasNoOrdersException;
import com.flooringmastery.model.dao.PersistenceException;
import com.flooringmastery.model.dto.Order;

import com.flooringmastery.service.OrderDoesNotExistException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;

@Component
public class DaoFileImplTest {

    Dao testDao;
    private final String tomorrow = LocalDate.now().plus(Period.ofDays(1)).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

    @Autowired
    public DaoFileImplTest() throws PersistenceException {
        String ROOT_TEST_STORAGE_DIRECTORY = System.getProperty("user.dir") + "\\src\\test\\java\\com\\flooringmastery\\storage\\";
        testDao = new DaoFileImpl(ROOT_TEST_STORAGE_DIRECTORY);
    }
    @BeforeEach
    void setUp(){
        testDao.deleteAllData();
    }

    @AfterEach
    void tearDown() {
        testDao.deleteAllData();
    }

    @Test
    void addOrderTest() throws PersistenceException {
        Order newOrder = testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));

        assertEquals(newOrder.getCustomerName(), "Daniel");
        assertEquals(newOrder.getState().abbreviation(), "TX");
        assertEquals(newOrder.getProduct().productType(), "Carpet");
        assertEquals(newOrder.getArea(), new BigDecimal("200.00"));
    }

    @Test
    void removeOrderTest() throws PersistenceException, DateHasNoOrdersException, OrderDoesNotExistException {
        Order newOrder = testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        Order removedOrder = testDao.removeOrder(tomorrow, newOrder.getId());

        assertEquals(removedOrder.getCustomerName(), "Daniel");
        assertEquals(removedOrder.getState().abbreviation(), "TX");
        assertEquals(removedOrder.getProduct().productType(), "Carpet");
        assertEquals(removedOrder.getArea(), new BigDecimal("200.00"));

        int numOrders = testDao.getAllOrders().size();
        assertEquals(numOrders, 0);
    }

    @Test
    void editOrderTest() throws PersistenceException, DateHasNoOrdersException, OrderDoesNotExistException {
        Order newOrder = testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        Order editedOrder = testDao.editOrder(tomorrow, newOrder.getId(), "John", "CA", "Tile", "250.00");

        assertEquals(editedOrder.getCustomerName(), "John");
        assertEquals(editedOrder.getState().abbreviation(), "CA");
        assertEquals(editedOrder.getProduct().productType(), "Tile");
        assertEquals(editedOrder.getArea(), new BigDecimal("250.00"));

        newOrder = testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        Order noChangeOrder = testDao.editOrder(tomorrow, newOrder.getId(), "", "", "", "");

        assertEquals(noChangeOrder.getCustomerName(), "Daniel");
        assertEquals(noChangeOrder.getState().abbreviation(), "TX");
        assertEquals(noChangeOrder.getProduct().productType(), "Carpet");
        assertEquals(noChangeOrder.getArea(), new BigDecimal("200.00"));
    }

    @Test
    void deleteAllOrdersTest() throws PersistenceException {
        testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        testDao.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));

        testDao.deleteAllData();
        assertEquals(testDao.getAllOrders().size(), 0);
    }
}