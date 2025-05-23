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
                System.out.print("Found transit route: ");
                System.out.print(from.getName());
                for (FlightRoute leg : path) {
                    System.out.print(" -> " + leg.getDestinationCity().getName());
                }
                System.out.println();
                System.out.println("Aircrafts used:");
                for (FlightRoute leg : path) {
                    System.out.println("- " + leg.getAircraft().getName() + " from " +
                            leg.getDepartureCity().getName() + " to " +
                            leg.getDestinationCity().getName());
                }

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

        System.out.println("No available flight path found from " + from + " to " + to + ".");

        return null;
    }

}
