package com.contractor.exeption;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(String country) {
        super("country " + country + " not found");
    }

}
