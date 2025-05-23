package com.mitrais.flight.booking;

import com.mitrais.flight.booking.screen.AdminMenu;
import com.mitrais.flight.booking.screen.PassengerMenu;
import com.mitrais.flight.booking.service.AdminService;
import com.mitrais.flight.booking.service.PassengerService;
import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Getter
@Component
public class RunnerComponent implements CommandLineRunner {

    private static Scanner scanner;
    private static AdminMenu adminMenu;
    private static PassengerMenu passengerMenu;

    private static AdminService adminService;
    private static PassengerService passengerService;

    @Override
    public void run(String... args) {
        scanner = new Scanner(System.in);
        adminService = new AdminService();
        passengerService = new PassengerService();

        adminMenu = new AdminMenu(scanner, adminService);
        passengerMenu = new PassengerMenu(scanner, adminService, passengerService);

        adminService.initializeData();

        showWelcomeScreen();
    }

    public static void showWelcomeScreen() {
        System.out.println("==== SIMPLE FLIGHT BOOKING & RUNNING SYSTEM ====");
        System.out.println();
        System.out.println("Login as:");
        System.out.println("1. Admin");
        System.out.println("2. Passenger");
        System.out.println();
        System.out.print("> ");

        int option = scanner.nextInt();
        if (option == 1) {
            adminMenu.showAdminPanel();
        } else if (option == 2) {
            passengerMenu.showPassengerPanel();
        } else {
            System.out.println();
            System.out.println("Invalid option");
            System.out.println();
            showWelcomeScreen();
        }
    }

}
