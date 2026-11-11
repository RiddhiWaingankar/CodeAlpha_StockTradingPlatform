import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// Stock Class to manage market data
class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public void displayStock() {
        System.out.printf("[%s] %s - $%.2f%n", symbol, name, price);
    }
}

// Transaction Class to record buy/sell history
class Transaction {
    private String type;
    private String symbol;
    private int quantity;
    private double price;

    public Transaction(String type, String symbol, int quantity, double price) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s: %d shares of %s at $%.2f each", type, quantity, symbol, price);
    }
}

// User Portfolio Class to manage cash, holdings, and transactions
class User {
    private String username;
    private double cashBalance;
    private HashMap<String, Integer> portfolio; // symbol -> quantity
    private ArrayList<Transaction> history;

    public User(String username, double initialCash) {
        this.username = username;
        this.cashBalance = initialCash;
        this.portfolio = new HashMap<>();
        this.history = new ArrayList<>();
    }

    public double getCashBalance() { return cashBalance; }

    public void buyStock(Stock stock, int quantity) {
        double totalCost = stock.getPrice() * quantity;
        if (cashBalance >= totalCost) {
            cashBalance -= totalCost;
            portfolio.put(stock.getSymbol(), portfolio.getOrDefault(stock.getSymbol(), 0) + quantity);
            history.add(new Transaction("BUY", stock.getSymbol(), quantity, stock.getPrice()));
            System.out.println("Successfully bought " + quantity + " shares of " + stock.getSymbol());
        } else {
            System.out.println("Error: Insufficient funds to complete purchase.");
        }
    }

    public void sellStock(Stock stock, int quantity) {
        int currentQty = portfolio.getOrDefault(stock.getSymbol(), 0);
        if (currentQty >= quantity) {
            cashBalance += stock.getPrice() * quantity;
            portfolio.put(stock.getSymbol(), currentQty - quantity);
            if (portfolio.get(stock.getSymbol()) == 0) {
                portfolio.remove(stock.getSymbol());
            }
            history.add(new Transaction("SELL", stock.getSymbol(), quantity, stock.getPrice()));
            System.out.println("Successfully sold " + quantity + " shares of " + stock.getSymbol());
        } else {
            System.out.println("Error: You do not own enough shares to sell.");
        }
    }

    public void displayPortfolio(HashMap<String, Stock> market) {
        System.out.println("\n--- Portfolio Performance for " + username + " ---");
        System.out.printf("Cash Balance: $%.2f%n", cashBalance);
        System.out.println("Holdings:");
        if (portfolio.isEmpty()) {
            System.out.println("  No stocks owned currently.");
        } else {
            double totalPortfolioValue = cashBalance;
            for (String symbol : portfolio.keySet()) {
                int qty = portfolio.get(symbol);
                double currentPrice = market.get(symbol).getPrice();
                double stockValue = qty * currentPrice;
                totalPortfolioValue += stockValue;
                System.out.printf("  - %s: %d shares | Current Price: $%.2f | Value: $%.2f%n", symbol, qty, currentPrice, stockValue);
            }
            System.out.printf("Total Portfolio Net Worth: $%.2f%n", totalPortfolioValue);
        }
        
        System.out.println("\nTransaction History:");
        if (history.isEmpty()) {
            System.out.println("  No transactions made yet.");
        } else {
            for (Transaction t : history) {
                System.out.println("  " + t);
            }
        }
    }
}

// Main Execution Class
public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Stock> market = new HashMap<>();

        // Initialize Market Data
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 180.50));
        market.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 140.20));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 210.00));

        User user = new User("Riddhi", 5000.00); // Starting with $5,000 cash

        boolean running = true;
        while (running) {
            System.out.println("\n=== Stock Trading Platform ===");
            System.out.println("1. View Market Data");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio & Performance");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("\n--- Market Data ---");
                    for (Stock stock : market.values()) {
                        stock.displayStock();
                    }
                    break;
                case 2:
                    System.out.print("Enter stock symbol to buy (AAPL, GOOGL, TSLA): ");
                    String buySymbol = scanner.next().toUpperCase();
                    if (market.containsKey(buySymbol)) {
                        System.out.print("Enter quantity: ");
                        int qty = scanner.nextInt();
                        user.buyStock(market.get(buySymbol), qty);
                    } else {
                        System.out.println("Invalid stock symbol.");
                    }
                    break;
                case 3:
                    System.out.print("Enter stock symbol to sell: ");
                    String sellSymbol = scanner.next().toUpperCase();
                    if (market.containsKey(sellSymbol)) {
                        System.out.print("Enter quantity: ");
                        int qty = scanner.nextInt();
                        user.sellStock(market.get(sellSymbol), qty);
                    } else {
                        System.out.println("Invalid stock symbol.");
                    }
                    break;
                case 4:
                    user.displayPortfolio(market);
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting Stock Trading Platform. Good luck with your internship submission!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}