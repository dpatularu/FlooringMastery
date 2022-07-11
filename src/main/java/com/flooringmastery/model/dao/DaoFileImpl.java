package com.flooringmastery.model.dao;

import com.flooringmastery.model.dto.Order;
import com.flooringmastery.model.dto.Product;
import com.flooringmastery.model.dto.State;
import com.flooringmastery.service.OrderDoesNotExistException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Component
public class DaoFileImpl implements Dao {


    private final String DELIMITER = ",";
    private final String ORDER_HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";

    private String ROOT_STORAGE_DIRECTORY = System.getProperty("user.dir") + "\\src\\main\\java\\com\\flooringmastery\\storage";
    public String STATES_FILE = ROOT_STORAGE_DIRECTORY + "\\Data\\Taxes.txt";
    public String PRODUCTS_FILE = ROOT_STORAGE_DIRECTORY + "\\Data\\Products.txt";
    public String ORDERS_DIRECTORY = ROOT_STORAGE_DIRECTORY + "\\Data\\Orders\\";
    public String EXPORT_DIRECTORY = ROOT_STORAGE_DIRECTORY +"\\Backup\\";

    private final HashMap<String, State> states = new HashMap<>();
    private final HashMap<String, Product> products = new HashMap<>();
    private final HashMap<String, List<Order>> ordersByDate = new HashMap<>();

    public int largestOrderId = 0;

    public DaoFileImpl(String ROOT_STORAGE_DIRECTORY) throws PersistenceException {
        this.ROOT_STORAGE_DIRECTORY = ROOT_STORAGE_DIRECTORY;
        STATES_FILE = ROOT_STORAGE_DIRECTORY + "\\Data\\Taxes.txt";
        PRODUCTS_FILE = ROOT_STORAGE_DIRECTORY + "\\Data\\Products.txt";
        ORDERS_DIRECTORY = ROOT_STORAGE_DIRECTORY + "\\Data\\Orders\\";
        EXPORT_DIRECTORY = ROOT_STORAGE_DIRECTORY +"\\Backup\\";
        loadStates();
        loadProducts();
        loadAllOrders();
    }

    public DaoFileImpl() throws PersistenceException {
        ROOT_STORAGE_DIRECTORY = System.getProperty("user.dir") + "\\src\\main\\java\\com\\flooringmastery\\storage";
        loadStates();
        loadProducts();
        loadAllOrders();
    }

    @Override
    public Order addOrder(String orderDate, String customerName, String stateAbbrev, String productName, BigDecimal area) throws PersistenceException {
        State state = states.get(stateAbbrev);
        String capitalizedProductName = productName.substring(0, 1).toUpperCase() + productName.toLowerCase().substring(1);
        Product product = products.get(capitalizedProductName);
        Order newOrder = new Order(++largestOrderId, customerName, state, product, area);

        if (!ordersByDate.containsKey(orderDate)) {
            ordersByDate.put(orderDate, new ArrayList<>());
        }
        ordersByDate.get(orderDate).add(newOrder);

        writeOrders();
        return newOrder;
    }

    @Override
    public Order removeOrder(String date, int orderId) throws DateHasNoOrdersException, OrderDoesNotExistException, PersistenceException {
        if (!ordersByDate.containsKey(date)) {
            throw new DateHasNoOrdersException("Date has no orders.");
        }
        List<Order> orders = ordersByDate.get(date);
        Order targetOrder;
        try {
            targetOrder = orders.stream().filter(order -> order.getId() == orderId).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new OrderDoesNotExistException("Order does not exist.");
        }

        orders.remove(targetOrder);
        if (orders.size() == 0) {
            deleteOrderDateFile(date);
        }
        writeOrders();
        return targetOrder;
    }

    @Override
    public Order editOrder(String date, int orderId, String customerName, String stateAbbrev, String productName, String area) throws DateHasNoOrdersException, OrderDoesNotExistException, PersistenceException {
        if (!ordersByDate.containsKey(date)) {
            throw new DateHasNoOrdersException("Date has no orders.");
        }
        List<Order> orders = ordersByDate.get(date);
        Order targetOrder;
        try {
            targetOrder = orders.stream().filter(order -> order.getId() == orderId).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new OrderDoesNotExistException("Order does not exist.");
        }
        if (!customerName.equals("")) {
            targetOrder.setCustomerName(customerName);
        }
        if (!productName.equals("")) {
            String capitalizedProductName = productName.substring(0, 1).toUpperCase() + productName.toLowerCase().substring(1);
            targetOrder.setProduct(products.get(capitalizedProductName));
        }
        if (!stateAbbrev.equals("")) {
            targetOrder.setState(states.get(stateAbbrev));
        }
        if (!area.equals("")) {
            targetOrder.setArea(area);
        }

        writeOrders();
        return targetOrder;
    }

    public HashMap<String, List<Order>> getAllOrders() {
        return ordersByDate;
    }

    public List<String> getStateAbbreviationList() {
        return new ArrayList<>(states.keySet());
    }

    public List<String> getProductNameList() {
        return new ArrayList<>(products.keySet());
    }

    private String[] unmarshallData(String data) {
        return data.split(DELIMITER);
    }

    private String marshallData(String[] data) {
        return String.join(",", data);
    }

    private void loadStates() throws PersistenceException {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(STATES_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load state taxes info into memory.", e);
        }
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            String[] data = unmarshallData(currentLine);
            State currentState = new State(data[0], data[1], data[2]);
            String stateAbbreviationKey = data[0];
            states.put(stateAbbreviationKey, currentState);
        }
        scanner.close();
    }

    private void loadProducts() throws PersistenceException {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCTS_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load product information into memory.", e);
        }
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            String[] data = unmarshallData(currentLine);
            Product currentProduct = new Product(data[0], data[1], data[2]);
            String productNameKey = data[0];
            products.put(productNameKey, currentProduct);
        }
        scanner.close();
    }

    private void loadOrder(String orderDate) throws PersistenceException {
        Scanner scanner;
        String ORDER_FILE = ORDERS_DIRECTORY + orderDate + ".txt";
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(ORDER_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load order data from " + ORDER_FILE, e);
        }
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            //Read and unmarshall order
            String currentLine = scanner.nextLine();
            String[] data = unmarshallData(currentLine);

            //Parse state and product keys
            int orderKey = Integer.parseInt(data[0]);
            String stateKey = data[2];
            String productKey = data[4];

            //Create new order object
            State orderState = states.get(stateKey);
            Product orderProduct = products.get(productKey);
            Order order = new Order(orderKey, data[1], orderState, orderProduct, data[5]);

            //Add order to memory
            if (!ordersByDate.containsKey(orderDate)) {
                ordersByDate.put(orderDate, new ArrayList<>());
            }
            ordersByDate.get(orderDate).add(order);

            if (largestOrderId < orderKey) {
                largestOrderId = orderKey;
            }
        }
        scanner.close();
    }

    private void loadAllOrders() throws PersistenceException {
        File[] files = new File(ORDERS_DIRECTORY).listFiles();
        assert files != null;
        for (File file : files) {
            String orderDateWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");
            loadOrder(orderDateWithoutExtension);
        }
    }

    public void writeOrders() throws PersistenceException {
        for (String date : ordersByDate.keySet()) {
            PrintWriter out;
            String ORDER_DATE_FILE_PATH = ORDERS_DIRECTORY + date + ".txt";
            try {
                out = new PrintWriter(new FileWriter(ORDER_DATE_FILE_PATH));
            } catch (IOException e) {
                throw new PersistenceException("Could not save order data of " + date, e);
            }
            List<Order> orders = ordersByDate.get(date);
            out.println(ORDER_HEADER);
            out.flush();
            orders.forEach((data) -> {
                String orderAsText = marshallData(data.toArray());
                out.println(orderAsText);
                out.flush();
            });
            out.close();
        }
    }

    public void deleteOrderDateFile(String orderDate) {
        ordersByDate.remove(orderDate);
        File file = new File(ORDERS_DIRECTORY + orderDate + ".txt");
    }

    public void exportData() throws PersistenceException {
        ArrayList<String> sortedDates = new ArrayList<>(ordersByDate.keySet());
        Collections.sort(sortedDates);

        PrintWriter out;
        String EXPORTED_DATA_FILE_PATH = EXPORT_DIRECTORY + "DataExport.txt";
        try {
            out = new PrintWriter(new FileWriter(EXPORTED_DATA_FILE_PATH));
        } catch (IOException e) {
            throw new PersistenceException("Could not create exported data.", e);
        }
        out.println(ORDER_HEADER + DELIMITER + "OrderDate");
        for (String date : sortedDates) {
            List<Order> orders = ordersByDate.get(date);
            orders.forEach((data) -> {
                String exportedOrderAsText = marshallData(data.toArray()) + DELIMITER + date;
                out.println(exportedOrderAsText);
            });
        }
        out.close();
    }

    public void deleteAllData() {
        for(String date : ordersByDate.keySet()){
            deleteOrderDateFile(date);
        }
    }
}