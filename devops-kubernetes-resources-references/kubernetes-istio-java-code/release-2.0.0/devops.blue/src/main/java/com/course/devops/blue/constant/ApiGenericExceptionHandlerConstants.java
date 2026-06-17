package com.course.devops.blue.constant;

public final class ApiGenericExceptionHandlerConstants {

  public static final String CODE_BAD_REQUEST = "bad_request";
  public static final String CODE_API_CHAIN_CALL_ERROR = "chain_call_error";
  public static final String CODE_SQL_EXCEPTION = "sql_exception";
  public static final String CODE_UNREADABLE_REQUEST_BODY = "unreadable_request_body";
  public static final String MESSAGE_BAD_INPUT_FROM_CLIENT = "Bad input from client";
  public static final String MESSAGE_API_CHAIN_CALL_ERROR = "Error on API chain call";
  public static final String MESSAGE_CANNOT_READ_REQUEST = "Cannot read request";
  public static final String MESSAGE_SQL_EXCEPTION = "SQL exception from server";
  public static final String REASON_CANNOT_READ_REQUEST_BODY = "Cannot read request body. Check submmited data (e.g. format, content, ...)";

  private ApiGenericExceptionHandlerConstants() {
    super();
  }

}
