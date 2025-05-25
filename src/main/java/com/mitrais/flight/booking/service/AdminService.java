package com.mitrais.flight.booking.service;

import com.mitrais.flight.booking.pojo.Aircraft;
import com.mitrais.flight.booking.pojo.Destination;
import com.mitrais.flight.booking.pojo.FlightRoute;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class AdminService {

    private int day = 1;
    private boolean bookingServiceActive = false;
    private List<Aircraft> aircrafts;
    private List<Destination> destinations;
    private List<FlightRoute> flightRoutes;

    public void initializeData() {
        Aircraft aircraft = new Aircraft();
        aircraft.setName("Lion Air");
        aircraft.setSeatCapacity(10);
        addAircraft(aircraft);

        aircraft = new Aircraft();
        aircraft.setName("Batik Air");
        aircraft.setSeatCapacity(15);
        addAircraft(aircraft);

        aircraft = new Aircraft();
        aircraft.setName("Garuda");
        aircraft.setSeatCapacity(20);
        addAircraft(aircraft);

        aircraft = new Aircraft();
        aircraft.setName("Citilink");
        aircraft.setSeatCapacity(15);
        addAircraft(aircraft);

        Destination destination = new Destination();
        destination.setName("Jakarta");
        addDestination(destination);

        destination = new Destination();
        destination.setName("Bali");
        addDestination(destination);

        destination = new Destination();
        destination.setName("Surabaya");
        addDestination(destination);

        destination = new Destination();
        destination.setName("Makassar");
        addDestination(destination);

        FlightRoute route = new FlightRoute();
        route.setDepartureCity(getMapDestination().get("Jakarta"));
        route.setDestinationCity(getMapDestination().get("Bali"));
        route.setAircraft(getMapAircraft().get("Lion Air"));
        route.setScheduleDay(3);
        addFlightRoute(route);

        route = new FlightRoute();
        route.setDepartureCity(getMapDestination().get("Jakarta"));
        route.setDestinationCity(getMapDestination().get("Surabaya"));
        route.setAircraft(getMapAircraft().get("Batik Air"));
        route.setScheduleDay(2);
        addFlightRoute(route);

        route = new FlightRoute();
        route.setDepartureCity(getMapDestination().get("Surabaya"));
        route.setDestinationCity(getMapDestination().get("Makassar"));
        route.setAircraft(getMapAircraft().get("Lion Air"));
        route.setScheduleDay(2);
        addFlightRoute(route);

        activateBookingService();

        System.out.println("initialize data done");
    }

    public void incrementDay() {
        day++;
    }

    public void addAircraft(Aircraft newAircraft) {
        if (aircrafts == null) aircrafts = new ArrayList<>();
        aircrafts.add(newAircraft);
    }

    public Map<String, Aircraft> getMapAircraft() {
        return aircrafts == null ? null : aircrafts.stream()
                .collect(Collectors.toMap(Aircraft::getName, Function.identity()));
    }

    public boolean isAircraftExists(String aircraftName) {
        return getMapAircraft() != null && getMapAircraft().get(aircraftName) != null;
    }

    public void addDestination(Destination destination) {
        if (destinations == null) destinations = new ArrayList<>();
        destinations.add(destination);
    }

    public Map<String, Destination> getMapDestination() {
        return destinations == null ? null : destinations.stream()
                .collect(Collectors.toMap(Destination::getName, Function.identity()));
    }

    public boolean isDestinationExists(String destinationName) {
        return getDestinations() != null && getMapDestination().get(destinationName) != null;
    }

    public void addFlightRoute(FlightRoute flightRoute) {
        if (flightRoutes == null) flightRoutes = new ArrayList<>();
        flightRoutes.add(flightRoute);
    }

    public void activateBookingService() {
        bookingServiceActive = true;
    }

}
