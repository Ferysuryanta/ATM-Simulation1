package atmApp.ATM;

import com.sun.tools.javac.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Stage1App {
    public static void main(String[] args) {

        /*
        * ATM record Account
         */


        var johnDoe = new Account("John Doe", "012108", 100, "112233");
        var janeDoe = new Account("Jane Doe", "932012", 30, "112244");
        var accounts = new ArrayList<>(Arrays.asList(johnDoe, janeDoe));
        Scanner scanner = new Scanner(System.in);

        while (true){
        System.out.println("====Welcome ATM Screen====");
        System.out.println("Enter your code number : ");
        var accNum = scanner.nextLine();
            System.out.println("Enter Pin : ");
        var pin = scanner.nextLine();

        // validate account
            var authenticaedAcc = validateLogin(accounts, accNum, pin);
            if (!(authenticaedAcc == null)){
                Stage1App.screenTransaction(authenticaedAcc, accounts, scanner);
            }
        }
    }

    public static  Account validateLogin (List<Account> accountsList, String accNum, String pin){
        if (accNum.length() != 6){
            System.out.println("Account number should 6 digits length");
            return null;
        }
        if (!accNum.matches("\\d+")){
            System.out.println("Account number should only contains number [0-9]");
            return null;
        }
        if (pin.length() !=6 ){
            System.out.println("Pin should have 6 digits length");
            return null;
        }
        if (!pin.matches("\\d+")){
            System.out.println("Pin should only contains number [0-9");
            return null;
        }
        if (Stage1App.IsExist(accountsList, accNum, pin)){
            return accountsList.stream().filter(a -> a.getAccountNumber().equals(accNum) && a.getPin().equals(pin))
                    .findAny().get();
        } else {
            System.out.println("Invalid Account Number/Pin");
            return null;
        }
    }

    public static boolean IsExist(List<Account> accountList, String accNum, String pin){
        return accountList.stream().anyMatch(
                ac -> ac.getAccountNumber().equals(accNum) && ac.getPin().equals(pin));
    }

    public static boolean IsExist(List<Account> accountsList, String accNum){
        return accountsList.stream().anyMatch(
                acc -> acc.getAccountNumber().equals(accNum));
    }

    public static void withDrawScreen(Account account, List<Account> accountList, Scanner scanner){
        System.out.println("===== Display Screen =====");
        System.out.println("" +
                "1. $10 \n" +
                "2. $20 \n" +
                "3. $30 \n" +
                "4. $100 \n" +
                "5. Other \n" +
                "6. Back \n" +
                "Please choose option[6]: ");

        String input = scanner.nextLine();
        int amount = 0;
        switch (input){
            case "1" -> amount = 10;
            case "2" -> amount = 20;
            case "3" -> amount = 30;
            case  "4" -> amount = 100;
            case  "5" -> {
                try {
                    System.out.println("Other withdraw amount to withdraw: ");
                    amount = Integer.parseInt(scanner.nextLine());
                    if (amount > 1000){
                        System.out.println("Max amount to withdraw is $1000");
                    }
                    if (amount % 10 !=0) {
                        System.out.println("Invalid amount");
                    }
                } catch (NumberFormatException e){
                    System.out.println("Invalid amount");
                } finally {
                    scanner.nextLine();
                }
            }
            default -> screenTransaction(account, accountList, scanner);
        }

        if (amount > 0) {
            System.out.println("Insufficient balance $ " + amount);
        }
        account.setBalance(account.getBalance() - amount);
        System.out.println("Summary " +
                "\nDate: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")) +
                "\nwithdraw: $" + amount +
                "\nBalance: " + account.getBalance());
        screenTransaction(account, accountList, scanner);
    }

    public static void displayTransfer(Account account, List<Account> accountList, Scanner scanner){
        System.out.println("=== Transfer Display ===");
        System.out.println("Please enter account and press enter to continue or press enter to go back to Transaction: ");
        String destination = scanner.nextLine();
        if (destination.equals("")){
            screenTransaction(account, accountList, scanner);
        }
        System.out.println("Please enter transfer amount then press enter to continue or press enter to go back to Transaction ");
        String amount = scanner.nextLine();
        System.out.println("Reference number : (This is an autogenerated random 6 digits number ) press enter to continue");
        String reference = scanner.nextLine();
        System.out.println("\n Transfer Confirmation " +
                "\n Destination Account: " + destination + "\n Transfer Amount: $ " + amount +
                "\n Reference Number: " + reference + "\n -----------" +
                "\n 1.Confirm Transfer" +
                "\n 2.Cancel Transfer" +
                "\n Choose option [2];");
        String option = scanner.nextLine();
        switch (option){
            case "1" -> processTransfer(account, destination, accountList, amount, reference, scanner);
            case "2" -> account = null;
        }
    }

    public static Account findAcc(List<Account> account, String accNumber){
        return account.stream().filter(a -> a.getAccountNumber().equals(accNumber)).findAny().get();
    }

    public static void processTransfer(Account account, String destination, List<Account> accountList, String amount, String reference, Scanner scanner){
        if (!destination.matches("\\d+") || !Stage1App.IsExist(accountList, destination)) {
            System.out.println("Invalid Account");
            return;
        }
        if (!reference.isEmpty() && !reference.matches("\\d +")){
            System.out.println("Invalid Reference Number");
            return;
        }
        try {
            var dest = findAcc(accountList, destination);
            if (Integer.parseInt(amount) > 1000) {
                System.out.println("Maximum amount to transfer is $1000");
                return;
            }
            if (Integer.parseInt(amount) < 1){
                System.out.println("Maximum amount to transfer is $1");
                return;
            }
            if (account.getBalance() > Integer.parseInt(amount)) {
                account.setBalance(account.getBalance() - Integer.parseInt(amount));
                dest.setBalance(dest.getBalance() + Integer.parseInt(amount));
                displayTransferSummary(account, accountList, destination, amount, reference, scanner);
            }
            System.out.println("Infficient balance $" + amount);
            displayTransfer(account, accountList, scanner);
        } catch (NumberFormatException e){
            System.out.println("Invalid amount");
        }
    }

    private static void displayTransferSummary(Account account, List<Account> accountList, String destination, String amount, String reference, Scanner scanner) {
        System.out.println("Fund transfer summary");
        System.out.println("Destination Account: " + destination + "\n Transfer amount: " + amount + "\n Reference Number: " + reference + "\n Balance: " + account.getBalance());
        System.out.println("1. Transaction");
        System.out.println("2. Exit");
        String input = scanner.nextLine();
        switch (input){
            case "1" -> screenTransaction(account, accountList, scanner);
            case "default" -> account = null;
        }
    }

    public static void screenTransaction(Account account, List<Account> accountList, Scanner scanner) {
        System.out.println("==== Transaction Screen ====");
        System.out.println("" +
                "1. Withdraw \n" +
                "2. Fund Transfer \n" +
                "3. Exit \n" +
                "Please choose option [3]: ");
        String input = scanner.nextLine();
        switch (input){
            case "1" -> withDrawScreen(account, accountList, scanner);
            case "2" -> displayTransfer(account, accountList, scanner);
            default -> account = null;
        }
    }
}
