package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;

@ValueObject
public final class AddressValue {
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;

    public AddressValue(final String street, 
                       final String city, 
                       final String state, 
                       final String zipCode, 
                       final String country) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }
        
        this.street = street.trim();
        this.city = city.trim();
        this.state = state != null ? state.trim() : "";
        this.zipCode = zipCode != null ? zipCode.trim() : "";
        this.country = country.trim();
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getFullAddress() {
        final StringBuilder sb = new StringBuilder();
        sb.append(street);
        if (!city.isEmpty()) {
            sb.append(", ").append(city);
        }
        if (!state.isEmpty()) {
            sb.append(", ").append(state);
        }
        if (!zipCode.isEmpty()) {
            sb.append(" ").append(zipCode);
        }
        if (!country.isEmpty()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AddressValue that = (AddressValue) obj;
        return Objects.equals(street, that.street) &&
               Objects.equals(city, that.city) &&
               Objects.equals(state, that.state) &&
               Objects.equals(zipCode, that.zipCode) &&
               Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zipCode, country);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
} 