package com.flooringmastery;

import com.flooringmastery.controller.Controller;
import com.flooringmastery.model.dao.Dao;
import com.flooringmastery.model.dao.DaoFileImpl;
import com.flooringmastery.model.dao.PersistenceException;
import com.flooringmastery.service.ServiceLayer;
import com.flooringmastery.service.ServiceLayerImpl;
import com.flooringmastery.view.UserIO;
import com.flooringmastery.view.UserIOConsoleImpl;
import com.flooringmastery.view.View;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) throws PersistenceException {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.scan("com.flooringmastery");
        appContext.refresh();

        Controller controller = appContext.getBean("controller", Controller.class);
        controller.run();
    }
}