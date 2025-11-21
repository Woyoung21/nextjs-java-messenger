package applogic;

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

public class CreateUserTests {

  @Test(singleThreaded = true)
  public void createUserTest() {
    var testUtils = new MockTestUtils();
    String hash = DigestUtils.sha256Hex(String.valueOf(Math.random()));
    Mockito.when(testUtils.mockUserDao.query("userName", hash)).thenReturn(new ArrayList());
    Mockito.doNothing().when(testUtils.mockUserDao).put(Mockito.any());
    ArgumentCaptor<UserDto> userArgumentCaptor = ArgumentCaptor.forClass(UserDto.class);

    ParsedRequest parsedRequest = new ParsedRequest();
    parsedRequest.setPath("/createUser");
    var user = new UserDto();
    user.setPassword(String.valueOf(Math.random()));
    user.setUserName(String.valueOf(Math.random()));
    parsedRequest.setBody(GsonTool.GSON.toJson(user));
    var handler = HandlerFactory.getHandler(parsedRequest);
    var builder = handler.handleRequest(parsedRequest);
    var res = builder.build();
    Assert.assertEquals(res.status, StatusCodes.OK);
    Mockito.verify(testUtils.mockUserDao).query("userName", user.getUserName());
    Mockito.verify(testUtils.mockUserDao).put(userArgumentCaptor.capture());
    Assert.assertEquals(userArgumentCaptor.getAllValues().size(), 1);
    Assert.assertEquals(userArgumentCaptor.getAllValues().getFirst().getUserName(),
            user.getUserName());
    Assert.assertEquals(userArgumentCaptor.getAllValues().getFirst().getPassword(),
            DigestUtils.sha256Hex(user.getPassword()));
    Assert.assertFalse(res.headers.containsKey("Set-Cookie"));
  }

}
