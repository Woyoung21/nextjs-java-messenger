package applogic;

import dto.ConversationDto;
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

public class UsernameTakenTest {

  @Test(singleThreaded = true)
  public void userTakenTest() {
    var testUtils = new MockTestUtils();
    var user = new UserDto();
    user.setUserName(String.valueOf(Math.random()));
    ArrayList<UserDto> userReturnList = new ArrayList<>();
    userReturnList.add(user);

    ParsedRequest parsedRequest = new ParsedRequest();
    parsedRequest.setPath("/getConversations");

    var auth = testUtils.createLogin(user.getUserName());

    parsedRequest.setBody(GsonTool.GSON.toJson(user));
    parsedRequest.setCookieValue("auth", auth.getHash());
    var handler = HandlerFactory.getHandler(parsedRequest);
    Mockito.when(testUtils.mockUserDao.query("userName", user.getUserName()))
            .thenReturn(userReturnList);
    List<ConversationDto> conversationDtos = new ArrayList<>();
    var conversationDto = new ConversationDto();
    conversationDtos.add(conversationDto);

    List<ConversationDto> conversationDtos2 = new ArrayList<>();
    var conversationDto2 = new ConversationDto();
    conversationDtos2.add(conversationDto2);

    Mockito.when(testUtils.mockConversationDao.query("toId", user.getUserName()))
            .thenReturn(conversationDtos);
    Mockito.when(testUtils.mockConversationDao.query("fromId", user.getUserName()))
            .thenReturn(conversationDtos2);
    var builder = handler.handleRequest(parsedRequest);
    var res = builder.build();

    Assert.assertEquals(res.status, StatusCodes.OK);

    Assert.assertTrue(builder.getBody().status);
    Assert.assertTrue(builder.getBody().data.contains(conversationDto));
    Assert.assertTrue(builder.getBody().data.contains(conversationDto2));
  }
}
