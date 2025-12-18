package handler;

import auth.AuthFilter;
import dao.ConversationDao;
import dao.MessageDao;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class DeleteConversationHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        // auth
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            var res = new RestApiAppResponse<>(false, null, "Not logged in");
            return new ResponseBuilder()
                    .setStatus(StatusCodes.UNAUTHORIZED)
                    .setBody(res);
        }

        // query param
        String rawConversationId = request.getQueryParam("conversationId");
        if (rawConversationId == null || rawConversationId.isBlank()) {
            var res = new RestApiAppResponse<>(false, null, "conversationId is required");
            return new ResponseBuilder()
                    .setStatus(StatusCodes.BAD_REQUEST)
                    .setBody(res);
        }

// 🔑 decode %40 -> @ etc.
        String conversationId = URLDecoder.decode(rawConversationId, StandardCharsets.UTF_8);

        System.out.println("DeleteConversationHandler: deleting " + conversationId);


        MessageDao messageDao = MessageDao.getInstance();
        ConversationDao conversationDao = ConversationDao.getInstance();

        // DEBUG: so we know this handler actually runs
        System.out.println("DeleteConversationHandler: deleting " + conversationId);

        // just delete, even if nothing is there (idempotent)
        try {
            messageDao.deleteByConversationId(conversationId);
        } catch (Exception e) {
            System.out.println("Error deleting messages: " + e.getMessage());
        }

        try {
            conversationDao.deleteByConversationId(conversationId);
        } catch (Exception e) {
            System.out.println("Error deleting conversation row: " + e.getMessage());
        }

        var res = new RestApiAppResponse<>(true, null, "Conversation deleted");
        return new ResponseBuilder()
                .setStatus(StatusCodes.OK)
                .setBody(res);
    }
}


