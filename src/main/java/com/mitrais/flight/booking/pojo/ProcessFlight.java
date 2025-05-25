package com.mitrais.flight.booking.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessFlight {

    private boolean isDirectFlight;
    private Destination from;
    private Destination to;
    private List<String> bookingIds;
    private Aircraft aircraft;
    private List<PassengerSeat> passengerSeats;

}
