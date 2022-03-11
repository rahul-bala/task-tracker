package com.ey.tasktracker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpHelpers {

    private static Logger log = LoggerFactory.getLogger(HttpHelpers.class);

    public static void sendResponseMessage(HttpServletResponse response, String message){
        /**
         *
         * Generic function to send the provided message over the response stream to the caller
         * */
        try {
            response.getWriter().println(message);
        } catch (IOException e) {
            log.error("Unable write the error message to the response output stream", e);
        }
    }

    public static void handleInternalServerError(Exception e, HttpServletResponse response, Logger moduleLogger){
        /**
         *
         * Generic function to handle exceptions caught in controller functions
         **/
        moduleLogger.error(e.getMessage(), e);
        HttpHelpers.sendResponseMessage(response, "Internal server error encountered: " + e.getMessage());
        response.setStatus(500);
    }

    public static void handleHttpResponses(String message, HttpServletResponse response, int responseCode){
        /**
         *
         * Generic function to handle string responses to be sent back to the caller from the controller class.
         * The function sets the response code based on the provided input
         * */
        HttpHelpers.sendResponseMessage(response, message);
        response.setStatus(responseCode);
    }
}
