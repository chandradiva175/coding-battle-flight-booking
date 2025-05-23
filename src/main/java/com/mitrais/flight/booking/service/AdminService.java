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
    private List<Aircraft> aircrafts;
    private List<Destination> destinations;
    private List<FlightRoute> flightRoutes;

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

}
