package com.mitrais.flight.booking.pojo;

import com.mitrais.flight.booking.util.BookingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FlightBooking {

    private String bookingId;
    private boolean isDirectFlight;
    private String passengerName;
    private Destination from;
    private Destination to;
    private List<FlightBookingDetail> details;
    private BookingStatus bookingStatus;
    private int scheduleDay;

}
