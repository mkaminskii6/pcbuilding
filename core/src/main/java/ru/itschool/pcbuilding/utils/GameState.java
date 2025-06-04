
package ru.itschool.pcbuilding.utils;

import com.badlogic.gdx.Gdx;
import java.util.List;
import java.util.ArrayList;

import java.util.ArrayList;

import ru.itschool.pcbuilding.components.Order;

public class GameState {
    public static int balance = 2000;
    public static ArrayList<String> inventory = new ArrayList<>();

    public static ArrayList<Order> orders = new ArrayList<>();
    public static Order currentOrder = null;
    public static void addMoney(int amount) {
        balance += amount;
    }

    public static List<List<String>> builtPCs = new ArrayList<>();


    public static boolean spendMoney(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public static void addToInventory(String item) {
        if (!inventory.contains(item)) {
            inventory.add(item);
            Gdx.app.log("INVENTORY", "Added: " + item);
        }
    }

    public static void removeFromInventory(String item) {
        inventory.remove(item);
    }

    public static boolean hasItem(String item) {
        return inventory.contains(item);
    }

    public static void printInventory() {
        for (String item : inventory) {
            Gdx.app.log("INVENTORY", item);
        }
    }
    public static boolean isPCComplete() {
        boolean hasCPU = false, hasGPU = false, hasRAM = false;

        for (String item : inventory) {
            if (item.contains("Intel") || item.contains("AMD")) hasCPU = true;
            if (item.contains("RTX") || item.contains("RX")) hasGPU = true;
            if (item.contains("DDR")) hasRAM = true;
        }

        return hasCPU && hasGPU && hasRAM;
    }
}
