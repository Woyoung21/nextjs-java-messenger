package handler;

import request.ParsedRequest;
import response.ResponseBuilder;

public interface BaseHandler {

    ResponseBuilder handleRequest(ParsedRequest request);
}
