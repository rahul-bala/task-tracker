package com.ey.tasktracker.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Helpers {

    /**
     * This function returns a date time object in format "yyyy/MM/dd HH:mm:ss"
     * */
    public static LocalDateTime getCurrentTime(){
        return getCurrentTime("yyyy/MM/dd HH:mm:ss");
    }

    /**
     * This method returns the local date time in the passed format
     * */
    public static LocalDateTime getCurrentTime(String format){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return(LocalDateTime.now());
    }

    /**
     * This method generates a UUID and timestamp based identifier
     * */
    public static String generateId(){
        return UUID.randomUUID().toString() + System.currentTimeMillis();
    }
}
