package handler;

import auth.AuthFilter;
import dao.ConversationDao;
import dao.MessageDao;
import dao.UserDao;
import dto.ConversationDto;
import dto.MessageDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.util.List;

// Done
public class SendMessageHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        MessageDto messageDto = GsonTool.GSON.fromJson(request.getBody(), MessageDto.class);

        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        UserDao userDao = UserDao.getInstance();
        var sendUser = userDao.query("userName", authResult.userName).stream()
                .findFirst()
                .orElse(null);
        if(sendUser == null){
            var res = new RestApiAppResponse<>(false, null, "From id is invalid user");
            return new ResponseBuilder().setStatus("200 OK").setBody(res);
        }
        var toUser = userDao.query("userName", messageDto.getToId()).stream()
                .findFirst()
                .orElse(null);
        if(toUser == null){
            var res = new RestApiAppResponse<>(false, null, "To id is invalid user");
            return new ResponseBuilder().setStatus("200 OK").setBody(res);
        }

        ConversationDao conversationDao = ConversationDao.getInstance();
        String conversationId = ConversationDto.makeUniqueId(sendUser.getUserName(),
                messageDto.getToId());
        var potentialConvos = conversationDao.query("conversationId", conversationId);
        ConversationDto conversationDto = potentialConvos.isEmpty()
                ? null : potentialConvos.getFirst();
        if(conversationDto == null){
            conversationDto = new ConversationDto(
                    sendUser.getUserName(), messageDto.getToId());
        }
        conversationDto.setMessageCount(conversationDto.getMessageCount() + 1);
        conversationDao.put(conversationDto);
        MessageDao messageDao = MessageDao.getInstance();
        messageDto.setConversationId(conversationId);
        messageDto.setFromId(sendUser.getUserName());
        messageDao.put(messageDto);
        toUser.setMessagesRecieved(toUser.getMessagesRecieved() + 1);
        sendUser.setMessagesSent(sendUser.getMessagesSent() + 1);
        userDao.put(toUser);
        userDao.put(sendUser);
        var res = new RestApiAppResponse<>(true, List.of(conversationDto), null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
