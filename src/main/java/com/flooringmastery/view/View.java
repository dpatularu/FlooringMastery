package com.flooringmastery.view;

import com.flooringmastery.model.dto.Order;
import com.flooringmastery.model.dto.Product;
import com.flooringmastery.model.dto.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;

@Component
public class View {
    private UserIO io;

    @Autowired
    public View(UserIO io) {
        this.io = io;
    }

    public void displayMenu() {
        io.println("<<Main Menu>>");
        io.println("1. Display Orders");
        io.println("2. Add an Order");
        io.println("3. Edit an Order");
        io.println("4. Remove an Order");
        io.println("5. Export All Data");
        io.println("6. Quit");
    }

    public void displayOrders(List<Order> orders, String date) {
        io.println("<<" + date + " orders>>");
        for (Order order : orders) {
            State state = order.getState();
            Product product = order.getProduct();
            io.println(String.format("ID: %d", order.getId()));
            io.println(String.format("Customer: %s | State: %s", order.getCustomerName(), state.name()));
            io.println(String.format("Product: %s | Area Purchased: %ssq ft ", product.productType(), order.getArea()));
            io.println(String.format("Material Cost: $%s | Labor Cost: $%s | Tax: $%s", order.materialCost(), order.laborCost(), order.totalTax()));
            io.println(String.format("Grand Total: $%s", order.grandTotal()));
            io.println("*****************************************************************");
        }
        io.readString("Press any key to continue:");
    }

    public void displayDisplayOrdersBanner() {
        io.println("<<Display Orders>>");
    }

    public int getMenuSelection() {
        return io.readInt("Select an option: ", 1, 6);
    }

    public int getOrderId() {
        return io.readInt("Enter the order ID: ");
    }

    public String getDateInput() {
        String date = null;
        try {
            date = io.readString("Enter a date(mm-dd-yyy): ");
        } catch (DateTimeParseException e) {
            io.println("Invalid date, please try again.");
        }
        return date;
    }

    public String getNewOrderDateInput() {
        boolean invalidInput = true;
        String dateInput = null;

        while (invalidInput) {
            try {
                dateInput = io.readString("When would you like your order (mm-dd-yyyy)?");
                LocalDate present = LocalDate.now();
                LocalDate orderDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                if (present.compareTo(orderDate) >= 0) {
                    io.println("Enter a date in the future.");
                } else {
                    invalidInput = false;
                }
            } catch (DateTimeParseException e) {
                io.println("Invalid date, please try again.");
            }
        }
        return dateInput;
    }

    public String getCustomerName() {
        boolean invalidInput = true;
        String customerName = null;
        while (invalidInput) {
            customerName = io.readString("Enter a customer name: ").trim();
            if (!customerName.matches("^[a-zA-Z0-9\\.\\, ]+$")) {
                io.println("Names can only contain alphanumeric, spaces, commas and periods.");
            } else {
                invalidInput = false;
            }
        }
        return customerName;
    }

    public String getCustomerName(InputMode inputMode) {
        boolean invalidInput = true;
        String customerName = null;
        while (invalidInput) {
            customerName = io.readString("Enter a customer name: ").trim();
            if (inputMode == InputMode.OPTIONAL && customerName.equals("")) {
                return customerName;
            }
            if (!customerName.matches("^[a-zA-Z0-9\\.\\, ]+$")) {
                io.println("Names can only contain alphanumeric, spaces, commas and periods.");
            } else {
                invalidInput = false;
            }
        }
        return customerName;
}

    public String getState(List<String> states) {
        HashSet<String> set = new HashSet<>(states);
        String state = null;
        do {
            io.println("Available states: ");
            states.forEach((stateAbbrev) -> {
                io.print(stateAbbrev + " ");
            });
            state = io.readString("\nEnter the state you wish to order from.").toUpperCase();
            if (!set.contains(state)) {
                io.println("Invalid state, please try again");
            }
        } while (!set.contains(state));
        return state;
    }

    public String getState(List<String> states, InputMode inputMode) {
        HashSet<String> set = new HashSet<>(states);
        String state = null;
        do {
            io.println("Available states: ");
            states.forEach((stateAbbrev) -> {
                io.print(stateAbbrev + " ");
            });
            state = io.readString("\nEnter the state you wish to order from.").toUpperCase();
            if (inputMode == InputMode.OPTIONAL && state.equals("")) {
                return state;
            }
            if (!set.contains(state)) {
                io.println("Invalid state, please try again");
            }
        } while (!set.contains(state));
        return state;
    }

    public String getProduct(List<String> products) {
        HashSet<String> set = new HashSet<>();
        products.forEach((productName) -> {
            set.add(productName.toLowerCase());
        });

        String product = null;
        do {
            io.println("Available products: ");
            products.forEach((productName) -> {
                io.print(productName + " | ");
            });
            product = io.readString("\nEnter the product you wish to order: ").toLowerCase();
            if (!set.contains(product)) {
                io.println("Product does not exist, please try again");
            }
        } while (!set.contains(product));
        return product;
    }

    public String getProduct(List<String> products, InputMode inputMode) {
        HashSet<String> set = new HashSet<>();
        products.forEach((productName) -> {
            set.add(productName.toLowerCase());
        });

        String product = null;
        do {
            io.println("Available products: ");
            products.forEach((productName) -> {
                io.print(productName + " | ");
            });
            product = io.readString("\nEnter the product you wish to order: ").toLowerCase();
            if (inputMode == InputMode.OPTIONAL && product.equals("")) {
                return product;
            }
            if (!set.contains(product)) {
                io.println("Product does not exist, please try again");
            }
        } while (!set.contains(product));
        return product;
    }

    public BigDecimal getArea() {
        return io.readBigDecimal("Enter how much square footage you want (minimum 100): ", new BigDecimal(100));
    }

    public BigDecimal getArea(InputMode inputMode) {
        String strArea = io.readString("Enter how much square footage you want (minimum 100): ");
        if(strArea.equals("")){
            return null;
        }
        return io.readBigDecimal("Enter how much square footage you want (minimum 100): ", new BigDecimal(100));
    }

    public boolean getExportConfirmation() {
        String input = io.readString("Are you sure you want to export data? (Y/N)").toLowerCase();
        return input.equals("y");
    }

    public boolean getOrderEditConfirmation(String customerName, String state, String product, String area) {
        io.println("<<Edit Summary>>");
        io.println("Name: " + (customerName.equals("") ? "Unchanged" : customerName));
        io.println("State: " + (state.equals("") ? "Unchanged" : state));
        io.println("Product: " + (product.equals("") ? "Unchanged" : product));
        io.println("Area: " + (area.equals("") ? "Unchanged" : area));
        String input = io.readString("Are you sure you want to edit with these changes? (Y/N)").toLowerCase();
        return input.equals("y");
    }

    public void displayExportSuccess() {
        io.println("Data successfully exported.");
        io.readString("Press any key to continue:");
    }

    public void displayQuitMessage() {
        io.println("Goodbye!");
        io.println("Program ending.");
    }

    public void displayErrorMessage(String errorMsg) {
        io.println("=== ERROR ===");
        io.println(errorMsg);
    }
}