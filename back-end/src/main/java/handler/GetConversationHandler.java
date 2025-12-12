package handler;

import auth.AuthFilter;
import dao.MessageDao;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class GetConversationHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {

        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        String conversationId = request.getQueryParam("conversationId");
        // Validate presence of conversationId
        if (conversationId == null || conversationId.isEmpty()) {
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST);
        }

        // URL-decode the conversationId so encoded characters (e.g. %40)
        // become the original characters (e.g. @)
        String decodedConversationId = URLDecoder.decode(conversationId, StandardCharsets.UTF_8);

        MessageDao messageDao = MessageDao.getInstance();
        var messages = messageDao.query("conversationId", decodedConversationId);

        var res = new RestApiAppResponse<>(true, messages, null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
