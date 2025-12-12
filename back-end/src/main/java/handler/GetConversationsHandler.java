package handler;

import auth.AuthFilter;
import dao.ConversationDao;
import dao.UserDao;
import dto.UserDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

// Done
public class GetConversationsHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {

        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        UserDto userDto = UserDao.getInstance().query("userName", authResult.userName)
                .stream()
                .findFirst()
                .orElse(null);

        ConversationDao conversationDao = ConversationDao.getInstance();
        assert userDto != null;
        var convos = conversationDao.query("toId", userDto.getUserName());
        var convos2 = conversationDao.query("fromId", userDto.getUserName());

        // convos may be immutable depending on DAO implementation.
        // Create a new mutable list and add elements from both query
        // results.
        var allConvos = new java.util.ArrayList<>(convos);
        allConvos.addAll(convos2);

        var res = new RestApiAppResponse<>(true, allConvos, null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
