package com.mitrais.flight.booking.service;

import com.mitrais.flight.booking.pojo.FlightBooking;
import com.mitrais.flight.booking.pojo.FlightBookingDetail;
import com.mitrais.flight.booking.pojo.FlightRoute;
import com.mitrais.flight.booking.util.BookingStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FlightBookingService {

    private List<FlightBooking> flightBookings;

    public void addFlightBooking(FlightBooking flightBooking) {
        if (flightBookings == null) flightBookings = new ArrayList<>();
        flightBookings.add(flightBooking);
    }

    public List<FlightBookingDetail> findByBookedFlightRoute(FlightRoute bookedRoute) {
        List<FlightBookingDetail> details = null;

        if (flightBookings == null) flightBookings = new ArrayList<>();
        if (!flightBookings.isEmpty()) {
            flightBookings = flightBookings.stream()
                    .filter(booking -> booking.getBookingStatus().equals(BookingStatus.BOOKED))
                    .collect(Collectors.toList());
        }

        for (FlightBooking booking : flightBookings) {
            if (details == null) details = new ArrayList<>();
            details.addAll(booking.getDetails());
        }

        if (details == null) return null;
        details = details.stream()
                .filter(detail -> detail.getFlightRoute().getDepartureCity().equals(bookedRoute.getDepartureCity())
                        && detail.getFlightRoute().getDestinationCity().equals(bookedRoute.getDestinationCity()))
                .sorted(Comparator.comparing(FlightBookingDetail::getSeat))
                .collect(Collectors.toList());

        return details;
    }

    public List<FlightBookingDetail> retrieveAllBookingDetail() {
        List<FlightBookingDetail> details = null;

        if (flightBookings == null) return null;
        flightBookings = flightBookings.stream()
                .filter(booking -> booking.getBookingStatus().equals(BookingStatus.BOOKED))
                .collect(Collectors.toList());

        for (FlightBooking booking : flightBookings) {
            if (details == null) details = new ArrayList<>();
            details.addAll(booking.getDetails());
        }

        return details;
    }

    public List<FlightBooking> retrieveAllBookingByPassenger(String passengerName) {
        if (flightBookings == null) return null;
        return flightBookings.stream()
                .filter(booking -> booking.getBookingStatus().equals(BookingStatus.BOOKED)
                        && booking.getPassengerName().equals(passengerName))
                .collect(Collectors.toList());
    }

    public FlightBooking retrieveAllBookingByBookingId(String passengerName, String bookingId) {
        if (flightBookings == null) return null;
        return flightBookings.stream()
                .filter(booking -> booking.getBookingStatus().equals(BookingStatus.BOOKED)
                        && booking.getPassengerName().equals(passengerName) && booking.getBookingId().equals(bookingId))
                .findAny().orElse(null);
    }

    public void cancelBooking(String passengerName, String bookingId) {
        if (flightBookings != null) {
            flightBookings.stream()
                    .filter(flightBooking -> flightBooking.getPassengerName().equals(passengerName)
                            && flightBooking.getBookingId().equals(bookingId))
                    .forEach(flightBooking -> flightBooking.setBookingStatus(BookingStatus.CANCELLED));
        }
    }

}
