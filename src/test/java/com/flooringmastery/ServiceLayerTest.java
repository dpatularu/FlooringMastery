package com.flooringmastery;

import com.flooringmastery.model.dao.DaoFileImpl;
import com.flooringmastery.model.dao.DateHasNoOrdersException;
import com.flooringmastery.model.dao.PersistenceException;
import com.flooringmastery.model.dto.Order;
import com.flooringmastery.service.InvalidDateException;
import com.flooringmastery.service.OrderDoesNotExistException;
import com.flooringmastery.service.ServiceLayer;
import com.flooringmastery.service.ServiceLayerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
public class ServiceLayerTest {
    ServiceLayer service;
    private final String tomorrow = LocalDate.now().plus(Period.ofDays(1)).format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

    @Autowired
    ServiceLayerTest() throws PersistenceException {
        String ROOT_TEST_STORAGE_DIRECTORY = System.getProperty("user.dir") + "\\src\\test\\java\\com\\flooringmastery\\storage\\";
        service = new ServiceLayerImpl(new DaoFileImpl(ROOT_TEST_STORAGE_DIRECTORY));
    }

    @BeforeEach
    void setUp(){
        service.deleteAllData();
    }

    @AfterEach
    void tearDown() {
        service.deleteAllData();
    }

    @Test
    void serviceAddOrderTest() throws PersistenceException {
        Order newOrder = service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));

        assertEquals(newOrder.getCustomerName(), "Daniel");
        assertEquals(newOrder.getState().abbreviation(), "TX");
        assertEquals(newOrder.getProduct().productType(), "Carpet");
        assertEquals(newOrder.getArea(), new BigDecimal("200.00"));
    }

    @Test
    void serviceRemoveOrderTest() throws PersistenceException, DateHasNoOrdersException, OrderDoesNotExistException, InvalidDateException {
        Order newOrder = service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        Order removedOrder = service.removeOrder(tomorrow, newOrder.getId());

        assertEquals(removedOrder.getCustomerName(), "Daniel");
        assertEquals(removedOrder.getState().abbreviation(), "TX");
        assertEquals(removedOrder.getProduct().productType(), "Carpet");
        assertEquals(removedOrder.getArea(), new BigDecimal("200.00"));

        assertThrows(DateHasNoOrdersException.class, () -> service.getOrdersByDate(tomorrow));
    }

    @Test
    void serviceEditOrderTest() throws PersistenceException, DateHasNoOrdersException, OrderDoesNotExistException {
        Order newOrder = service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        Order editedOrder = service.editOrder(tomorrow, newOrder.getId(), "John", "CA", "Tile", "250.00");

        assertEquals(editedOrder.getCustomerName(), "John");
        assertEquals(editedOrder.getState().abbreviation(), "CA");
        assertEquals(editedOrder.getProduct().productType(), "Tile");
        assertEquals(editedOrder.getArea(), new BigDecimal("250.00"));

        newOrder = service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        Order noChangeOrder = service.editOrder(tomorrow, newOrder.getId(), "", "", "", "");

        assertEquals(noChangeOrder.getCustomerName(), "Daniel");
        assertEquals(noChangeOrder.getState().abbreviation(), "TX");
        assertEquals(noChangeOrder.getProduct().productType(), "Carpet");
        assertEquals(noChangeOrder.getArea(), new BigDecimal("200.00"));
    }

    @Test
    void serviceDeleteAllOrdersTest() throws PersistenceException, DateHasNoOrdersException, InvalidDateException {
        service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));
        service.addOrder(tomorrow, "Daniel", "TX", "Carpet", new BigDecimal("200.00"));

        service.deleteAllData();

        assertThrows(DateHasNoOrdersException.class, () -> service.getOrdersByDate(tomorrow));
    }
}