package handler;

import request.ParsedRequest;
import response.ResponseBuilder;

public class FallbackHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        return new ResponseBuilder().setStatus("404 Not Found");
    }
}
