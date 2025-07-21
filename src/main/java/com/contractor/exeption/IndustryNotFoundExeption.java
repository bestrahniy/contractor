package com.contractor.exeption;

public class IndustryNotFoundExeption extends RuntimeException {

    public IndustryNotFoundExeption(Integer industry) {
        super("industry " + industry + " not found");
    }

}
