package com.mitrais.flight.booking.screen;

import com.mitrais.flight.booking.RunnerComponent;
import com.mitrais.flight.booking.pojo.Destination;
import com.mitrais.flight.booking.pojo.FlightBooking;
import com.mitrais.flight.booking.pojo.FlightBookingDetail;
import com.mitrais.flight.booking.pojo.FlightRoute;
import com.mitrais.flight.booking.service.AdminService;
import com.mitrais.flight.booking.service.FlightBookingService;
import com.mitrais.flight.booking.service.PassengerService;
import com.mitrais.flight.booking.util.BookingStatus;
import com.mitrais.flight.booking.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PassengerMenu {

    private final Scanner scanner;
    private final AdminService adminService;
    private final PassengerService passengerService;
    private final FlightBookingService flightBookingService;

    public PassengerMenu(Scanner scanner, AdminService adminService, PassengerService passengerService, FlightBookingService flightBookingService) {
        this.scanner = scanner;
        this.adminService = adminService;
        this.passengerService = passengerService;
        this.flightBookingService = flightBookingService;
    }

    public void showPassengerPanel() {
        scanner.nextLine();
        System.out.println("==== PASSENGER LOGIN ====");
        System.out.print("Enter passenger name: ");
        String passengerName = scanner.nextLine();
        System.out.println("Welcome, " + passengerName + "!\n");

        showPassengerPanel(passengerName);
    }

    public void showPassengerPanel(String passengerName) {
        System.out.printf("==== PASSENGER PANEL (%s) ====\n", passengerName);
        System.out.printf("Current day: %d\n", adminService.getDay());
        System.out.println("1. Book a Flight");
        System.out.println("2. Cancel a Booking");
        System.out.println("3. Exit");
        System.out.println();
        System.out.print("> ");

        int option = scanner.nextInt();
        switch (option) {
            case 1:
                if (!adminService.isBookingServiceActive()) {
                    System.out.println("\nBooking Service not active\n");
                    showPassengerPanel(passengerName);
                    break;
                }

                showBookFlight(passengerName);
                break;
            case 2:
                break;
            default:
                RunnerComponent.showWelcomeScreen();
                break;
        }
    }

    public void showBookFlight(String passengerName) {
        String availDestination = adminService.getDestinations() == null ? "" : adminService.getDestinations().stream()
                .map(Destination::getName)
                .collect(Collectors.joining(", "));

        scanner.nextLine();
        System.out.println("== BOOK A FLIGHT ==");
        System.out.println("Available destinations: " + availDestination);

        System.out.print("Enter departure city: ");
        String departureCity = scanner.nextLine();
        Destination from = adminService.getMapDestination().get(departureCity);

        System.out.print("Enter destination city: ");
        String destinationCity = scanner.nextLine();
        Destination to = adminService.getMapDestination().get(destinationCity);

        List<FlightBookingDetail> currentBookingDetails = flightBookingService.retrieveAllBookingDetail();
        List<FlightRoute> results = passengerService.searchFlight(adminService.getFlightRoutes(), currentBookingDetails, from, to);
        if (results != null && !results.isEmpty()) {
            System.out.print("Confirm booking? (y/n): ");
            String confirm = scanner.next();
            if (confirm.equalsIgnoreCase("y")) {
                int day = 1;
                int sequence = 0;
                String sequenceStr;

                List<FlightBookingDetail> bookingDetails = new ArrayList<>();
                List<FlightBookingDetail> bookedDetails;
                for (FlightRoute result : results) {
                    FlightBookingDetail detail = FlightBookingDetail.builder()
                            .seat(1)
                            .flightRoute(result)
                            .build();

                    day = result.getScheduleDay();
                    bookedDetails = flightBookingService.findByBookedFlightRoute(result);
                    if (bookedDetails == null) {
                        bookingDetails.add(detail);
                        continue;
                    }

                    sequence = bookedDetails.size();
                    OptionalInt maxSeat = bookedDetails.stream()
                            .mapToInt(FlightBookingDetail::getSeat)
                            .max();

                    detail.setSeat(maxSeat.orElse(1) + 1);
                    bookingDetails.add(detail);
                }

                sequenceStr = StringUtils.paddingZeroString(String.valueOf(sequence + 1), 3);
                String bookingId = from.getCode() + "-" + to.getCode() + "-" + sequenceStr;

                FlightBooking booking = FlightBooking.builder()
                        .bookingId(bookingId)
                        .isDirectFlight(results.size() == 1)
                        .passengerName(passengerName)
                        .from(from)
                        .to(to)
                        .details(bookingDetails)
                        .bookingStatus(BookingStatus.BOOKED)
                        .build();

                flightBookingService.addFlightBooking(booking);

                System.out.printf("Booking confirmed! Booking ID: %s\n", bookingId);
                if (booking.isDirectFlight()) {
                    System.out.printf("Details: %s -> %s on Day %d, Seat #%d\n\n",
                            from.getName(),
                            to.getName(),
                            day,
                            bookingDetails.get(0).getSeat());
                } else {
                    Map<Destination, List<Destination>> graph = new HashMap<>();
                    for (FlightRoute route : results) {
                        graph.computeIfAbsent(route.getDepartureCity(), k -> new ArrayList<>()).add(route.getDestinationCity());
                    }

                    StringBuilder detailRoute = new StringBuilder(from.getName());
                    for (FlightRoute route : results) {
                        detailRoute.append(" -> ").append(route.getDestinationCity().getName());
                        day = route.getScheduleDay();
                    }

                    System.out.printf("Details: %s on Day %d\n", detailRoute, day);
                    for (FlightBookingDetail detail : bookingDetails) {
                        System.out.printf("Seat #%d on %s -> %s\n",
                                detail.getSeat(),
                                detail.getFlightRoute().getDepartureCity().getName(),
                                detail.getFlightRoute().getDestinationCity().getName());
                    }
                }
            } else {
                showPassengerPanel(passengerName);
            }
        }

        showPassengerPanel(passengerName);
    }

}
