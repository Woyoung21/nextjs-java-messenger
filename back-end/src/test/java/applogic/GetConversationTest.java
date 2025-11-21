package applogic;

import dto.ConversationDto;
import dto.MessageDto;
import dto.UserDto;
import handler.GsonTool;
import handler.HandlerFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;
import java.util.List;

public class GetConversationTest {

    @Test(singleThreaded = true)
    public void getConversationTest() {
        var testUtils = new MockTestUtils();
        var user = new UserDto();
        user.setUserName(String.valueOf(Math.random()));
        ArrayList<UserDto> userReturnList = new ArrayList<>();
        userReturnList.add(user);

        String conversationId = String.valueOf(Math.random());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getConversation");
        parsedRequest.setQueryParam("conversationId", conversationId);

        var auth = testUtils.createLogin(user.getUserName());

        parsedRequest.setBody(GsonTool.GSON.toJson(user));
        parsedRequest.setCookieValue("auth", auth.getHash());
        var handler = HandlerFactory.getHandler(parsedRequest);
        Mockito.when(testUtils.mockUserDao.query("userName", user.getUserName()))
                .thenReturn(userReturnList);
        List<MessageDto> messageDtos = new ArrayList<>();
        var message = new MessageDto();
        messageDtos.add(message);
        Mockito.when(testUtils.mockMessageDao.query("conversationId", conversationId))
                .thenReturn(messageDtos);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Mockito.verify(testUtils.mockAuthDao).query("hash", auth.getHash());

        Assert.assertTrue(builder.getBody().status);
        Assert.assertEquals(builder.getBody().data.getFirst(), message);
    }
}
