package com.example.soundfriends.utils;

import java.util.UUID;

public class uuid {

    public String createTransactionID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
