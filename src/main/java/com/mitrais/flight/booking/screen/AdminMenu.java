package com.mitrais.flight.booking.screen;

import com.mitrais.flight.booking.RunnerComponent;
import com.mitrais.flight.booking.pojo.*;
import com.mitrais.flight.booking.service.AdminService;
import com.mitrais.flight.booking.service.FlightBookingService;
import com.mitrais.flight.booking.util.BookingStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AdminMenu {

    private final Scanner scanner;
    private final AdminService adminService;
    private final FlightBookingService flightBookingService;

    public AdminMenu(Scanner scanner, AdminService adminService, FlightBookingService flightBookingService) {
        this.scanner = scanner;
        this.adminService = adminService;
        this.flightBookingService = flightBookingService;
    }

    public void showAdminPanel() {
        System.out.println("==== ADMIN PANEL ====");
        System.out.println();
        System.out.println("1. Register Aircraft");
        System.out.println("2. Add Destination");
        System.out.println("3. Create Flight Route");
        System.out.println("4. Run Booking Service");
        System.out.println("5. Go to Next Day");
        System.out.println("6. Run Flight");
        System.out.println("7. Exit");
        System.out.println();
        System.out.print("> ");

        int option = scanner.nextInt();
        switch (option) {
            case 1:
                showRegisterAircraft();
                break;
            case 2:
                showAddDestination();
                break;
            case 3:
                showCreateFlightRoute();
                break;
            case 4:
                adminService.activateBookingService();
                System.out.println("== RUN BOOKING SERVICE ==");
                System.out.println("Booking service is now running.");
                System.out.println("Passengers can now make bookings\n");

                showAdminPanel();
                break;
            case 5:
                adminService.incrementDay();

                System.out.println("== NEXT DAY ==");
                System.out.println("Advancing to the next day...");
                System.out.printf("Current day is now: %d\n", adminService.getDay());
                showCurrentAndTomorrowBookedFlight();
                break;
            case 6:
                showRunFlight();
                showAdminPanel();
                break;
            default:
                RunnerComponent.showWelcomeScreen();
                break;
        }
    }

    public void showRegisterAircraft() {
        scanner.nextLine();
        System.out.println("== REGISTER AIRCRAFT ==");
        System.out.print("Enter aircraft name: ");
        String name = scanner.nextLine();

        System.out.print("Enter seat capacity: ");
        int capacity = scanner.nextInt();

        Aircraft newAircraft = new Aircraft();
        newAircraft.setName(name);
        newAircraft.setSeatCapacity(capacity);

        boolean isAircraftExist = adminService.isAircraftExists(name);
        if (isAircraftExist) {
            System.out.printf("Aircraft: %s already registered\n\n", name);
            showAdminPanel();
        }

        adminService.addAircraft(newAircraft);
        showSuccessRegisterAircraft(newAircraft);
    }

    public void showSuccessRegisterAircraft(Aircraft aircraft) {
        System.out.printf("%s with %d seats registered successfully!\n\n", aircraft.getName(), aircraft.getSeatCapacity());
        showAdminPanel();
    }

    public void showAddDestination() {
        scanner.nextLine();
        System.out.println("== ADD DESTINATION ==");
        System.out.print("Enter destination name: ");
        String name = scanner.nextLine();

        Destination destination = new Destination();
        destination.setName(name);

        boolean isDestinationExist = adminService.isDestinationExists(name);
        if (isDestinationExist) {
            System.out.printf("Destination: %s already added\n\n", name);
            showAdminPanel();
        }

        adminService.addDestination(destination);
        showSuccessAddDestination(destination);
    }

    public void showSuccessAddDestination(Destination destination) {
        System.out.printf("%s added as destination!\n\n", destination.getName());
        showAdminPanel();
    }

    public void showCreateFlightRoute() {
        String availDestination = adminService.getDestinations() == null ? "" : adminService.getDestinations().stream()
                .map(Destination::getName)
                .collect(Collectors.joining(", "));

        scanner.nextLine();
        System.out.println("== CREATE FLIGHT ROUTE ==");
        System.out.println("Available destinations: " + availDestination);
        System.out.print("Enter departure city: ");
        String departureCity = scanner.nextLine();

        System.out.print("Enter destination city: ");
        String destinationCity = scanner.nextLine();

        System.out.print("Select aircraft: ");
        String aircraft = scanner.nextLine();

        System.out.print("Enter scheduled day: ");
        int scheduleDay = scanner.nextInt();

        FlightRoute flightRoute = new FlightRoute();
        flightRoute.setDepartureCity(adminService.getMapDestination().get(departureCity));
        flightRoute.setDestinationCity(adminService.getMapDestination().get(destinationCity));
        flightRoute.setAircraft(adminService.getMapAircraft().get(aircraft));
        flightRoute.setScheduleDay(scheduleDay);

        adminService.addFlightRoute(flightRoute);
        showSuccessCreateFlightRoute(flightRoute);
    }

    public void showSuccessCreateFlightRoute(FlightRoute flightRoute) {
        System.out.printf("Direct flight route created: %s -> %s (%s, Day %d)\n\n",
                flightRoute.getDepartureCity().getName(),
                flightRoute.getDestinationCity().getName(),
                flightRoute.getAircraft().getName(),
                flightRoute.getScheduleDay());

        showAdminPanel();
    }

    public void showCurrentAndTomorrowBookedFlight() {
        int currentDay = adminService.getDay();
        List<FlightBooking> bookings = flightBookingService.findBookingByTodayAndTomorrow(currentDay);
        if (bookings == null) {
            showAdminPanel();
            return;
        }

        for (FlightBooking booking : bookings) {
            if (booking.isDirectFlight()) {
                System.out.printf("Flight %s -> %s is scheduled for %s.\n",
                        booking.getFrom().getName(),
                        booking.getTo().getName(),
                        booking.getScheduleDay() == currentDay ? "today" : "tomorrow");
            } else {
                StringBuilder detailRoute = new StringBuilder(booking.getFrom().getName());
                for (FlightBookingDetail detail : booking.getDetails()) {
                    detailRoute.append(" -> ").append(detail.getFlightRoute().getDestinationCity().getName());
                }

                System.out.printf("Flight %s is scheduled for %s\n",
                        detailRoute,
                        booking.getScheduleDay() == currentDay ? "today" : "tomorrow");
            }
        }

        showAdminPanel();
    }

    public void showRunFlight() {
        int currentDay = adminService.getDay();

        System.out.println("== RUN FLIGHT ==");
        System.out.printf("Running flights for day %d...\n\n", currentDay);

        List<FlightBooking> bookings = flightBookingService.findBookingByToday(currentDay);
        if (bookings == null) return;

        List<ProcessFlight> processFlights = transform(bookings);
        if (!processFlights.isEmpty()) {
            for (ProcessFlight flight : processFlights) {
                System.out.printf("Processing flight: %s -> %s\n", flight.getFrom().getName(), flight.getTo().getName());
                System.out.printf("Aircraft: %s\n", flight.getAircraft().getName());

                System.out.printf("Passengers boarding: %d\n", flight.getPassengerSeats().size());
                for (PassengerSeat passengerSeat : flight.getPassengerSeats()) {
                    System.out.printf("- %s (Seat #%d)\n", passengerSeat.getPassengerName(), passengerSeat.getSeat());
                }

                System.out.println("Flight status: DEPARTED");
                System.out.println("Flight status: ARRIVED");
                System.out.printf("All passengers have reached %s.\n\n", flight.getTo().getName());
            }
        }

        flightBookingService.finishBooking(currentDay);
    }

    public List<ProcessFlight> transform(List<FlightBooking> flightBookings) {
        Map<String, List<FlightBooking>> grouped = flightBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.BOOKED)
                .collect(Collectors.groupingBy(b -> b.getFrom().getCode() + "-" + b.getTo().getCode()));

        List<ProcessFlight> result = new ArrayList<>();
        for (Map.Entry<String, List<FlightBooking>> entry : grouped.entrySet()) {
            List<FlightBooking> bookings = entry.getValue();

            if (bookings.isEmpty()) continue;

            FlightBooking firstBooking = bookings.get(0);
            Destination from = firstBooking.getFrom();
            Destination to = firstBooking.getTo();
            boolean isDirect = firstBooking.isDirectFlight();

            List<String> bookingIds = bookings.stream()
                    .map(FlightBooking::getBookingId)
                    .collect(Collectors.toList());

            List<PassengerSeat> passengerSeats = bookings.stream()
                    .flatMap(b -> b.getDetails().stream()
                            .map(d -> PassengerSeat.builder()
                                    .passengerName(b.getPassengerName())
                                    .seat(d.getSeat())
                                    .build()))
                    .collect(Collectors.toList());

            Aircraft aircraft = bookings.stream()
                    .flatMap(b -> b.getDetails().stream())
                    .map(d -> d.getFlightRoute().getAircraft())
                    .findFirst()
                    .orElse(null);

            ProcessFlight pf = new ProcessFlight();
            pf.setFrom(from);
            pf.setTo(to);
            pf.setDirectFlight(isDirect);
            pf.setAircraft(aircraft);
            pf.setBookingIds(bookingIds);
            pf.setPassengerSeats(passengerSeats);

            result.add(pf);
        }

        return result;
    }

}
