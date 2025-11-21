package applogic;

import dto.AuthDto;
import dto.MessageDto;
import dto.UserDto;
import handler.GsonTool;
import handler.HandlerFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;
import java.util.List;

public class SendMessageTest {

    @Test(singleThreaded = true)
    public void sendMessageTest() {
        var testUtils = new MockTestUtils();
        var user = new UserDto();
        user.setUserName(String.valueOf(Math.random()));
        ArrayList<UserDto> userReturnList = new ArrayList<>();
        userReturnList.add(user);

        String conversationId = String.valueOf(Math.random());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendMessage");

        var auth = testUtils.createLogin(user.getUserName());

        var messageDto = new MessageDto();
        messageDto.setMessage(String.valueOf(Math.random()));
        String toId = String.valueOf(Math.random());
        messageDto.setToId(toId);

        parsedRequest.setBody(GsonTool.GSON.toJson(messageDto));
        parsedRequest.setCookieValue("auth", auth.getHash());
        var handler = HandlerFactory.getHandler(parsedRequest);
        Mockito.when(testUtils.mockUserDao.query("userName", user.getUserName()))
                .thenReturn(userReturnList);
        ArgumentCaptor<AuthDto> authCaptor = ArgumentCaptor.forClass(AuthDto.class);

        var user2 = new UserDto();
        user2.setUserName(toId);
        ArrayList<UserDto> userReturnList2 = new ArrayList<>();
        userReturnList2.add(user2);

        Mockito.when(testUtils.mockUserDao.query("userName", user2.getUserName()))
                .thenReturn(userReturnList2);
        List<MessageDto> messageDtos = new ArrayList<>();
        var message = new MessageDto();
        messageDtos.add(message);


        ArgumentCaptor<UserDto> userCaptor = ArgumentCaptor.forClass(UserDto.class);
        ArgumentCaptor<MessageDto> messageCaptor = ArgumentCaptor.forClass(MessageDto.class);

        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();
        Mockito.verify(testUtils.mockAuthDao).query("hash", auth.getHash());

        Assert.assertEquals(res.status, StatusCodes.OK);

        Assert.assertTrue(builder.getBody().status);

        Mockito.verify(testUtils.mockMessageDao).put(messageCaptor.capture());
        Mockito.verify(testUtils.mockUserDao, Mockito.times(2))
                .put(userCaptor.capture());

        Assert.assertEquals(messageCaptor.getAllValues().size(), 1);
        Assert.assertEquals(messageCaptor.getAllValues().getFirst().getMessage(),
                messageDto.getMessage());
        Assert.assertEquals(userCaptor.getAllValues().size(), 2);
        Assert.assertEquals(userCaptor.getAllValues().getFirst().getMessagesRecieved(),
                1);
        Assert.assertEquals(userCaptor.getAllValues().getFirst().getMessagesSent(),
                0);
        Assert.assertEquals(userCaptor.getAllValues().getLast().getMessagesSent(),
                1);
        Assert.assertEquals(userCaptor.getAllValues().getLast().getMessagesRecieved(),
                0);
    }
}
