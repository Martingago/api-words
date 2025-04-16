package com.martingago.words.utils.documentation;

public class ApiErrorExamples {

    public static final String ERROR_400 = """
        {
          "status": false,
          "message": "Example error 400",
          "serverCode": 400,
          "responseObject": null,
          "timeStamp": "2025-04-15T12:00:00Z"
        }
        """;

    public static final String ERROR_401 = """
        {
          "status": false,
          "message": "Example error 401, invalid credentials",
          "serverCode": 401,
          "responseObject": null,
          "timeStamp": "2025-04-15T12:00:00Z"
        }
        """;

    public static final String ERROR_404 = """
        {
          "status": false,
          "message": "Example error 404, entity not found",
          "serverCode": 404,
          "responseObject": null,
          "timeStamp": "2025-04-15T12:00:00Z"
        }
        """;

    public static final String ERROR_500 = """
        {
          "status": false,
          "message": "Example internal error 500",
          "serverCode": 500,
          "responseObject": null,
          "timeStamp": "2025-04-15T12:00:00Z"
        }
        """;
}
