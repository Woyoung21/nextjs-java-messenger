package applogic;

import dto.UserDto;
import handler.HandlerFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;

public class GetUserHandlerTests {

    @Test(singleThreaded = true)
    public void getUserTest() {
        var testUtils = new MockTestUtils();
        var user = new UserDto();
        user.setUserName(String.valueOf(Math.random()));
        ArrayList<UserDto> userReturnList = new ArrayList<>();
        userReturnList.add(user);

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getUser");

        var auth = testUtils.createLogin(user.getUserName());

        parsedRequest.setCookieValue("auth", auth.getHash());
        var handler = HandlerFactory.getHandler(parsedRequest);
        Mockito.when(testUtils.mockUserDao.query("userName", user.getUserName()))
                .thenReturn(userReturnList);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Mockito.verify(testUtils.mockAuthDao).query("hash", auth.getHash());

        Assert.assertTrue(builder.getBody().status);
        Assert.assertEquals(builder.getBody().data.getFirst(), user);
    }
}
