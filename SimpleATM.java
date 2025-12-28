import java.sql.*;
import java.util.Scanner;

public class SimpleATM {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/atmdb", "root", "Neeraj@5569");

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n--- Welcome to SimpleATM ---");
                System.out.println("1. Create New Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                int startChoice = sc.nextInt();

                if (startChoice == 1) {
                    try {
                        // Simple Account Creation
                        System.out.print("Enter name: ");
                        String newName = sc.next();
                        System.out.print("Enter PIN: ");
                        String newPin = sc.next();
                        System.out.print("Initial deposit: ");
                        double deposit = sc.nextDouble();

                        Statement stmt = conn.createStatement();
                        String sql = "INSERT INTO accounts (name, pin, balance) VALUES ('"
                                     + newName + "', '" + newPin + "', " + deposit + ")";
                        stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

                        //   generated account number
                        ResultSet keys = stmt.getGeneratedKeys();
                        if (keys.next()) {
                            int accNo = keys.getInt(1);
                            System.out.println("Account created successfully! Your Account No is: " + accNo);
                        }
                    } catch (SQLIntegrityConstraintViolationException e) {
                        System.out.println("PIN already exists. Please try a different PIN.");
                    } catch (SQLException e) {
                        System.out.println("Could not create account. Please check your input.");
                    }
                } else if (startChoice == 2) {
                    // Login
                    System.out.print("Enter Account No: ");
                    int accNo = sc.nextInt();
                    System.out.print("Enter PIN: ");
                    String pin = sc.next();

                    PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM accounts WHERE acc_no=? AND pin=?");
                    ps.setInt(1, accNo);
                    ps.setString(2, pin);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        System.out.println("Welcome " + rs.getString("name"));

                        // ATM Menu
                        while (true) {
                            System.out.println("\n1. Balance\n2. Deposit\n3. Withdraw\n4. Exit");
                            int choice = sc.nextInt();

                            if (choice == 1) {
                                PreparedStatement bal = conn.prepareStatement(
                                    "SELECT balance FROM accounts WHERE acc_no=?");
                                bal.setInt(1, accNo);
                                ResultSet brs = bal.executeQuery();
                                if (brs.next()) {
                                    System.out.println("Balance: " + brs.getDouble(1));
                                }

                            } else if (choice == 2) {
                                System.out.print("Amount: ");
                                double amt = sc.nextDouble();
                                PreparedStatement dep = conn.prepareStatement(
                                    "UPDATE accounts SET balance=balance+? WHERE acc_no=?");
                                dep.setDouble(1, amt);
                                dep.setInt(2, accNo);
                                dep.executeUpdate();
                                System.out.println("Deposited!");

                            } else if (choice == 3) {
                                System.out.print("Amount: ");
                                double amt = sc.nextDouble();
                                PreparedStatement bal = conn.prepareStatement(
                                    "SELECT balance FROM accounts WHERE acc_no=?");
                                bal.setInt(1, accNo);
                                ResultSet brs = bal.executeQuery();
                                if (brs.next() && brs.getDouble(1) >= amt) {
                                    PreparedStatement wd = conn.prepareStatement(
                                        "UPDATE accounts SET balance=balance-? WHERE acc_no=?");
                                    wd.setDouble(1, amt);
                                    wd.setInt(2, accNo);
                                    wd.executeUpdate();
                                    System.out.println("Withdrawn!");
                                } else {
                                    System.out.println("Not enough balance!");
                                }

                            } else if (choice == 4) {
                                System.out.println("Goodbye!");
                                break;
                            } else {
                                System.out.println("Invalid choice!");
                            }
                        }
                    } else {
                        System.out.println("Invalid login!");
                    }

                } else if (startChoice == 3) {
                    System.out.println("Thank you for using SimpleATM. Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice! Try again.");
                }
            }

            conn.close();
        } catch (Exception e) {
            System.out.println(" Something went wrong. Please try again.");
        }
    }
}