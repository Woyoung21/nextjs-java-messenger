package applogic;

import dto.FriendRequestDto;
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

public class SendFriendRequestHandlerTests {

    @Test(singleThreaded = true)
    public void sendFriendRequestSuccess() {
        var testUtils = new MockTestUtils();
        String fromUser = "user1@test.com";
        String toUser = "user2@test.com";

        // Setup login
        var authEntry = testUtils.createLogin(fromUser);

        // Target user exists
        var targetUser = new UserDto();
        targetUser.setUserName(toUser);
        Mockito.when(testUtils.mockUserDao.query("userName", toUser))
                .thenReturn(List.of(targetUser));

        // No existing friend request
        Mockito.when(testUtils.mockFriendRequestDao.findExistingRequest(fromUser, toUser))
                .thenReturn(null);

        Mockito.doNothing().when(testUtils.mockFriendRequestDao).put(Mockito.any());

        // Build request
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        var requestBody = new FriendRequestDto();
        requestBody.setToUser(toUser);
        parsedRequest.setBody(GsonTool.GSON.toJson(requestBody));

        // Execute
        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        // Verify
        Assert.assertEquals(res.status, StatusCodes.OK);
        ArgumentCaptor<FriendRequestDto> captor = ArgumentCaptor.forClass(FriendRequestDto.class);
        Mockito.verify(testUtils.mockFriendRequestDao).put(captor.capture());
        Assert.assertEquals(captor.getValue().getFromUser(), fromUser);
        Assert.assertEquals(captor.getValue().getToUser(), toUser);
        Assert.assertEquals(captor.getValue().getStatus(), "pending");
    }

    @Test(singleThreaded = true)
    public void sendFriendRequestToSelfFails() {
        var testUtils = new MockTestUtils();
        String user = "user1@test.com";

        var authEntry = testUtils.createLogin(user);

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        var requestBody = new FriendRequestDto();
        requestBody.setToUser(user); // Same user
        parsedRequest.setBody(GsonTool.GSON.toJson(requestBody));

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.BAD_REQUEST);
    }

    @Test(singleThreaded = true)
    public void sendFriendRequestUserNotFoundFails() {
        var testUtils = new MockTestUtils();
        String fromUser = "user1@test.com";
        String toUser = "nonexistent@test.com";

        var authEntry = testUtils.createLogin(fromUser);

        // Target user does NOT exist
        Mockito.when(testUtils.mockUserDao.query("userName", toUser))
                .thenReturn(new ArrayList<>());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        var requestBody = new FriendRequestDto();
        requestBody.setToUser(toUser);
        parsedRequest.setBody(GsonTool.GSON.toJson(requestBody));

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.BAD_REQUEST);
    }

    @Test(singleThreaded = true)
    public void sendFriendRequestAlreadyExistsFails() {
        var testUtils = new MockTestUtils();
        String fromUser = "user1@test.com";
        String toUser = "user2@test.com";

        var authEntry = testUtils.createLogin(fromUser);

        var targetUser = new UserDto();
        targetUser.setUserName(toUser);
        Mockito.when(testUtils.mockUserDao.query("userName", toUser))
                .thenReturn(List.of(targetUser));

        // Existing request already exists
        var existingRequest = new FriendRequestDto();
        existingRequest.setStatus("pending");
        Mockito.when(testUtils.mockFriendRequestDao.findExistingRequest(fromUser, toUser))
                .thenReturn(existingRequest);

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        var requestBody = new FriendRequestDto();
        requestBody.setToUser(toUser);
        parsedRequest.setBody(GsonTool.GSON.toJson(requestBody));

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.BAD_REQUEST);
    }

    @Test(singleThreaded = true)
    public void sendFriendRequestUnauthorized() {
        var testUtils = new MockTestUtils();

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendFriendRequest");
        // No auth cookie
        var requestBody = new FriendRequestDto();
        requestBody.setToUser("someone@test.com");
        parsedRequest.setBody(GsonTool.GSON.toJson(requestBody));

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.UNAUTHORIZED);
    }
}

