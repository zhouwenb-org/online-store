package com.example.onlinestore.exceptions;

import java.io.Serial;

public class ItemInvalid extends Exception {
    @Serial
    private static final long serialVersionUID = -1010068544071911675L;

    public ItemInvalid(String message) {
        super(message);
    }
}
