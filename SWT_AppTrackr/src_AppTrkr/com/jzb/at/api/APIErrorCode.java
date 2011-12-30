/**
 * 
 */
package com.jzb.at.api;

/**
 * @author n63636
 * 
 */
public class APIErrorCode {

    private static final String[] error_info = { "Request was successful, nothing was created or accepted for review", "OK", "200", "Request was successful and submission has been instantly posted",
            "CREATED", "201", "Request was successful and submission has been accepted for review", "ACCEPTED", "202",
            "Request was successful, but part or all of the submission already existed. Any unique content has been created", "PARTIALLY_CREATED", "206",
            "Request was successful, but part or all of the submission already existed. Any unique content has been accepted", "PARTIALLY_ACCEPTED", "207",
            "Request was badly formatted or improperly submitted", "BAD_REQUEST", "400", "Request requires a user's authentication, or the provided authentication was bad", "UNAUTHORIZED", "401",
            "User authentication was accepted, but user does not have permission to perform the requested action", "USER_NOT_PERMITTED", "402",
            "The API profile being used does not exist or is not activated.", "FORBIDDEN", "403", "Request was made to an object that does not exist", "OBJECT_NOT_FOUND", "404",
            "The API profile being used does not have permission to call this action", "ACTION_NOT_ALLOWED", "405", "Request's format is either invalid or is not allowed for this type of request",
            "FORMAT_NOT_ACCEPTED", "406", "Submitted data was in some way invalid, and no part of it could be accepted.", "UNACCEPTABLE_DATA", "407",
            "Request's format or requested action is deprecated", "DEPRECATED", "408", "Request referenced an external resource that could not be found", "RESOURCE_NOT_FOUND", "410",
            "Request failed at no fault of the requesting party.", "INTERNAL_SERVER_ERROR", "500", "Request called an action that either does not exist or is not yet available", "NOT_IMPLEMENTED",
            "501", "The API is temporarily unavailable", "SERVICE_UNAVAILABLE", "503", "The request triggered a call to an external resource that has stopped responding", "RESOURCE_TIMEOUT", "504" };

    public static String getErrorInfo(String code) {

        for (int n = 0; n < error_info.length / 3; n++) {
            if (code.equals(error_info[3 * n + 2])) {
                return error_info[3 * n + 1] + "(" + error_info[3 * n + 2] + ") - " + error_info[3 * n];
            }
        }
        return "UNKNOWN_ERROR(" + code + ") - Error code is unknown";
    }
}
