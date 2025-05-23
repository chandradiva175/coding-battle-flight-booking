package com.mitrais.flight.booking.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightRoute {

    private Destination departureCity;
    private Destination destinationCity;
    private Aircraft aircraft;
    private int scheduleDay;

}
