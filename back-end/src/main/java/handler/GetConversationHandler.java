package handler;

import auth.AuthFilter;
import dao.MessageDao;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

public class GetConversationHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {

        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        String conversationId = request.getQueryParam("conversationId");
        MessageDao messageDao = MessageDao.getInstance();
        var messages = messageDao.query("conversationId", conversationId);

        var res = new RestApiAppResponse<>(true, messages, null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
