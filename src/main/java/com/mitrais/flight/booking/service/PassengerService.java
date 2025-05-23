package com.mitrais.flight.booking.service;

import com.mitrais.flight.booking.pojo.Destination;
import com.mitrais.flight.booking.pojo.FlightRoute;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PassengerService {

    public void searchFlight(List<FlightRoute> flightRoutes, Destination from, Destination to) {
        System.out.println("Searching for flights...");
        if (from == null || to == null) {
            System.out.println("Invalid city name.");
            return;
        }

        List<FlightRoute> directFlights = new ArrayList<>();

        // Collect all direct flights
        for (FlightRoute route : flightRoutes) {
            if (route.getDepartureCity().equals(from) && route.getDestinationCity().equals(to)) {
                directFlights.add(route);
            }
        }

        if (!directFlights.isEmpty()) {
            System.out.println("== DIRECT FLIGHT(S) ==");
            for (FlightRoute route : directFlights) {
                System.out.println("Direct flight: " + route.getDepartureCity().getName() + " -> " +
                        route.getDestinationCity().getName() + " (Day " + route.getScheduleDay() + ")");
                System.out.println("Aircraft: " + route.getAircraft().getName() + ", Seats: " + route.getAircraft().getSeatCapacity());
            }

            return;
        } else {
            System.out.println("No direct flights found.");
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

        while (!queue.isEmpty()) {
            List<FlightRoute> path = queue.poll();
            FlightRoute lastLeg = path.get(path.size() - 1);
            Destination current = lastLeg.getDestinationCity();

            if (current.equals(to)) {
                System.out.println("No direct flight found.");
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
                return;
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
    }

}
