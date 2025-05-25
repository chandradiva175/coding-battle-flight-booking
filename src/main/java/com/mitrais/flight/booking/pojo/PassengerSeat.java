package com.mitrais.flight.booking.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PassengerSeat {

    private String passengerName;
    private int seat;
}
