package applogic;


import dto.AuthDto;
import dto.UserDto;
import handler.GsonTool;
import handler.HandlerFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginHandlerTests {

  @Test(singleThreaded = true)
  public void loginTest() {
    var testUtils = new MockTestUtils();

    List<UserDto> returnList = new ArrayList<>();
    var user = new UserDto();
    user.setUserName(String.valueOf(Math.random()));
    returnList.add(user);

    Mockito.doReturn(returnList).when(testUtils.mockUserDao)
            .query("userName", user.getUserName());

    ParsedRequest parsedRequest = new ParsedRequest();
    parsedRequest.setPath("/login");

    // when user sends password for login it is not encrypted
    user.setPassword(String.valueOf(Math.random()));
    parsedRequest.setBody(GsonTool.GSON.toJson(user));
    // In the db password must be encrypted
    user.setPassword(DigestUtils.sha256Hex(user.getPassword()));


    var handler = HandlerFactory.getHandler(parsedRequest);
    var builder = handler.handleRequest(parsedRequest);
    var res = builder.build();
    Assert.assertEquals(res.status, StatusCodes.OK);
    ArgumentCaptor<AuthDto> authCaptor = ArgumentCaptor.forClass(AuthDto.class);
    Mockito.verify(testUtils.mockAuthDao).put(authCaptor.capture());
    Assert.assertEquals(authCaptor.getAllValues().size(), 1);

    Assert.assertEquals(authCaptor.getAllValues().getFirst().getUserName(), user.getUserName());
    String hash = authCaptor.getAllValues().get(0).getHash();
    Assert.assertTrue(res.headers.get("Set-Cookie").contains(hash));
  }
}
