package com.mitrais.flight.booking.service;

import com.mitrais.flight.booking.pojo.Destination;
import com.mitrais.flight.booking.pojo.FlightBookingDetail;
import com.mitrais.flight.booking.pojo.FlightRoute;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PassengerService {

    public List<FlightRoute> searchFlight(List<FlightRoute> flightRoutes, List<FlightBookingDetail> currentBookingDetails, Destination from, Destination to) {
        System.out.println("Searching for flights...");
        if (from == null || to == null) {
            System.out.println("Invalid city name.");
            return null;
        }

        List<FlightRoute> directFlights = flightRoutes.stream()
                .filter(flightRoute -> flightRoute.getDepartureCity().equals(from)
                        && flightRoute.getDestinationCity().equals(to))
                .toList();

        if (directFlights.isEmpty()) {
            System.out.println("No direct flights found.");
        } else {
            for (FlightRoute route : directFlights) {
                System.out.printf("Found direct flight: %s -> %s (Day: %d)\n",
                        route.getDepartureCity().getName(),
                        route.getDestinationCity().getName(),
                        route.getScheduleDay());

                long bookedSeats = 0;
                if (currentBookingDetails != null && !currentBookingDetails.isEmpty()) {
                    bookedSeats = currentBookingDetails.stream()
                            .filter(booked -> booked.getFlightRoute().getDepartureCity().getName().equals(route.getDepartureCity().getName())
                                    && booked.getFlightRoute().getDestinationCity().getName().equals(route.getDestinationCity().getName()))
                            .count();
                }

                int availableSeats = route.getAircraft().getSeatCapacity() - (int) bookedSeats;
                System.out.printf("%d seats available\n", availableSeats);

                if (availableSeats == 0) return null;
            }

            return directFlights;
        }

        Map<Destination, List<FlightRoute>> graph = new HashMap<>();
        for (FlightRoute route : flightRoutes) {
            graph.computeIfAbsent(route.getDepartureCity(), k -> new ArrayList<>()).add(route);
        }

        Queue<List<FlightRoute>> queue = new LinkedList<>();
        Set<Destination> visited = new HashSet<>();
        visited.add(from);

        for (FlightRoute route : graph.getOrDefault(from, new ArrayList<>())) {
            List<FlightRoute> initialPath = new ArrayList<>();
            initialPath.add(route);
            queue.add(initialPath);
        }

        List<FlightRoute> transitFlight = null;
        while (!queue.isEmpty()) {
            List<FlightRoute> path = queue.poll();
            FlightRoute lastLeg = path.get(path.size() - 1);
            Destination current = lastLeg.getDestinationCity();

            if (current.equals(to)) {
                int day = 1;
                System.out.print("Found transit route: ");
                System.out.print(from.getName());
                for (FlightRoute route : path) {
                    System.out.print(" -> " + route.getDestinationCity().getName());
                    day = route.getScheduleDay();
                }
                System.out.printf(" (Day %d)\n", day);

                boolean seatAvailable = true;
                for (FlightRoute route : path) {
                    long bookedSeats = 0;
                    if (currentBookingDetails != null && !currentBookingDetails.isEmpty()) {
                        bookedSeats = currentBookingDetails.stream()
                                .filter(booked -> booked.getFlightRoute().getDepartureCity().getName().equals(route.getDepartureCity().getName())
                                        && booked.getFlightRoute().getDestinationCity().getName().equals(route.getDestinationCity().getName()))
                                .count();
                    }

                    int availableSeats = route.getAircraft().getSeatCapacity() - (int) bookedSeats;
                    if (availableSeats == 0) seatAvailable = false;
                    System.out.printf("%d seats available on %s -> %s\n",
                            availableSeats,
                            route.getDepartureCity().getName(),
                            route.getDestinationCity().getName());

                    if (transitFlight == null) transitFlight = new ArrayList<>();
                    transitFlight.add(route);
                }

                if (!seatAvailable) return null;

                return transitFlight;
            }

            if (!visited.contains(current)) {
                visited.add(current);
                for (FlightRoute next : graph.getOrDefault(current, new ArrayList<>())) {
                    List<FlightRoute> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    queue.add(newPath);
                }
            }
        }

        System.out.println("No transit flights found");

        return null;
    }

}
