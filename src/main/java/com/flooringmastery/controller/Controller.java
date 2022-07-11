package com.flooringmastery.controller;

import com.flooringmastery.model.dao.DateHasNoOrdersException;
import com.flooringmastery.model.dao.PersistenceException;
import com.flooringmastery.model.dto.Order;
import com.flooringmastery.service.*;
import com.flooringmastery.view.InputMode;
import com.flooringmastery.view.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class Controller {
    private View view;
    private ServiceLayer service;

    @Autowired
    public Controller(View view, ServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() {
        boolean running = true;
        while (running) {
            view.displayMenu();
            MenuSelection menuSelection = getMenuSelection();
            switch (menuSelection) {
                case DISPLAY_ORDERS:
                    displayOrders();
                    break;
                case ADD_ORDER:
                    addOrder();
                    break;
                case EDIT_ORDER:
                    editOrder();
                    break;
                case REMOVE_ORDER:
                    removeOrder();
                    break;
                case EXPORT_DATA:
                    exportData();
                    break;
                case QUIT:
                    quit();
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    private MenuSelection getMenuSelection() {
        int menuSelection = view.getMenuSelection();
        return MenuSelection.values()[menuSelection - 1];
    }

    private void displayOrders() {
        view.displayDisplayOrdersBanner();
        boolean invalidInput = true;
        do {
            try {
                String dateInput = view.getDateInput();
                List<Order> orders = service.getOrdersByDate(dateInput);
                view.displayOrders(orders, dateInput);
            } catch (DateHasNoOrdersException e) {
                view.displayErrorMessage(e.getMessage());
            } catch (InvalidDateException e) {
                view.displayErrorMessage(e.getMessage());
                continue;
            }
            invalidInput = false;
        } while (invalidInput);
    }

    private void addOrder() {
        String date = view.getNewOrderDateInput();
        String customerName = view.getCustomerName();
        String state = view.getState(service.getStateAbbreviationsList());
        String product = view.getProduct(service.getProductNamesList());
        BigDecimal area = view.getArea();

        try{
            service.addOrder(date, customerName, state, product, area);
        }catch(PersistenceException e){
            view.displayErrorMessage(e.getMessage());
        }

    }

    private void editOrder() {
        try{
            String date = view.getDateInput();
            int orderId = view.getOrderId();
            String customerName = view.getCustomerName(InputMode.OPTIONAL);
            String state = view.getState(service.getStateAbbreviationsList(), InputMode.OPTIONAL);
            String product = view.getProduct(service.getProductNamesList(), InputMode.OPTIONAL);

            BigDecimal areaBD = view.getArea(InputMode.OPTIONAL);
            String area = areaBD == null ? "" : areaBD.toString();

            boolean userConfirmsEdit = view.getOrderEditConfirmation(customerName, state, product, area);
            if (userConfirmsEdit) {
                service.editOrder(date, orderId, customerName,  state, product, area);
            }

        } catch (DateHasNoOrdersException | OrderDoesNotExistException | PersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void removeOrder() {
        try{
            String date = view.getDateInput();
            int orderId = view.getOrderId();
            service.removeOrder(date, orderId);
        }catch(OrderDoesNotExistException | DateHasNoOrdersException | PersistenceException e){
            view.displayErrorMessage(e.getMessage());
        }
    }

    private void exportData() {
        boolean wantsToExport = view.getExportConfirmation();
        if(wantsToExport){
            try{
                service.exportData();
                view.displayExportSuccess();
            }catch(PersistenceException e){
                view.displayErrorMessage(e.getMessage());
            }
        }
    }

    private void quit() {
        view.displayQuitMessage();
    }
}