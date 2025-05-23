package com.mitrais.flight.booking.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Destination {

    private String name;
    private String code;

    public String getCode() {
        if (name.length() > 3) return name.substring(0, 3).toUpperCase();
        else return name.toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Destination)) return false;
        Destination other = (Destination) obj;
        return name != null && name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
