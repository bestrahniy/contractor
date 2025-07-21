package com.contractor.exeption;

public class OrgFormNotFoundException extends RuntimeException {

    public OrgFormNotFoundException(Integer orgForm) {
        super("org form " + orgForm + " not found");
    }

}
