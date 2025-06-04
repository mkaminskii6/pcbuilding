package ru.itschool.pcbuilding.components;

import java.util.Random;

public class Order {
    private String customerName;
    private String[] components;
    private int price;
    private boolean accepted;
    private boolean completed;

    public Order(String customerName, String[] components, int price) {
        this.customerName = customerName;
        this.components = components;
        this.price = price;
        this.accepted = false;
        this.completed = false;
    }

    public static Order generateRandomOrder(Random random) {
        String[] customers = {"John", "Mike", "Sarah", "Alex", "Emma"};
        String[][] possibleComponents = {
            {"CPU: Intel i5", "GPU: GTX 1660", "RAM: 16GB", "SSD: 500GB"},
            {"CPU: AMD Ryzen 7", "GPU: RTX 3070", "RAM: 32GB", "SSD: 1TB"},
            {"CPU: Intel i9", "GPU: RTX 4090", "RAM: 64GB", "SSD: 2TB"}
        };
        // Базовые цены для конфигураций
        int[] basePrices = {800, 1200, 2000};

        int config = random.nextInt(possibleComponents.length);
        String customer = customers[random.nextInt(customers.length)] + "'s PC";

        // Добавляем случайный разброс (+/- 20%)
        int price = basePrices[config];
        int priceVariation = (int)(price * 0.2f);
        price += random.nextInt(priceVariation * 2) - priceVariation;

        return new Order(
            customer,
            possibleComponents[config],
            price
        );
    }


    public String getCustomerName() {
        return customerName;
    }

    public String getComponentsList() {
        return String.join("\n", components);
    }

    public int getPrice() {
        return price;
    }
    public void complete() { this.completed = true; }
    public boolean isCompleted() { return completed; }
    public void accept() { this.accepted = true; }
    public boolean isAccepted() { return accepted; }
}

