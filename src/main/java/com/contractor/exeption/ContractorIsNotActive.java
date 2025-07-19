package com.contractor.exeption;

public class ContractorIsNotActive extends RuntimeException {

    public ContractorIsNotActive(String contractorId) {
        super("Contractor " + contractorId + " is not active");
    }

}
