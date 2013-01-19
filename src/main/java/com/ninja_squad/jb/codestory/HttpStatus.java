package com.ninja_squad.jb.codestory;

/**
* An HTTP response status
* @author JB
*/
public enum HttpStatus {
    _200_OK(200, "OK"),
    _201_CREATED(201, "Created"),
    _400_BAD_REQUEST(400, "Bad Request"),
    _404_NOT_FOUND(404, "Not Found"),
    _500_INTERNAL_ERROR(500, "Internal Error");

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
