package com.contractor.exeption;

public class MissingUserRight extends RuntimeException {

    public MissingUserRight(String login) {
        super("User: " + login + " have not all right for this action");
    }

}
