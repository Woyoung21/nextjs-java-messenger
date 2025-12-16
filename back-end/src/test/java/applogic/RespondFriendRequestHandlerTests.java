package applogic;

import dto.FriendRequestDto;
import handler.HandlerFactory;
import org.bson.types.ObjectId;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;
import java.util.List;

public class RespondFriendRequestHandlerTests {

    @Test(singleThreaded = true)
    public void acceptFriendRequestSuccess() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";
        String requestId = new ObjectId().toHexString();

        var authEntry = testUtils.createLogin(userName);

        // Setup existing pending request
        var pendingRequest = new FriendRequestDto();
        pendingRequest.setUniqueId(requestId);
        pendingRequest.setFromUser("user2@test.com");
        pendingRequest.setToUser(userName);
        pendingRequest.setStatus("pending");

        Mockito.when(testUtils.mockFriendRequestDao.query("_id", new ObjectId(requestId)))
                .thenReturn(List.of(pendingRequest));
        Mockito.doNothing().when(testUtils.mockFriendRequestDao).put(Mockito.any());

        // Build request
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/respondFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        parsedRequest.setBody("{\"requestId\":\"" + requestId + "\",\"accept\":true}");

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("accepted"));

        ArgumentCaptor<FriendRequestDto> captor = ArgumentCaptor.forClass(FriendRequestDto.class);
        Mockito.verify(testUtils.mockFriendRequestDao).put(captor.capture());
        Assert.assertEquals(captor.getValue().getStatus(), "accepted");
    }

    @Test(singleThreaded = true)
    public void declineFriendRequestSuccess() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";
        String requestId = new ObjectId().toHexString();

        var authEntry = testUtils.createLogin(userName);

        var pendingRequest = new FriendRequestDto();
        pendingRequest.setUniqueId(requestId);
        pendingRequest.setFromUser("user2@test.com");
        pendingRequest.setToUser(userName);
        pendingRequest.setStatus("pending");

        Mockito.when(testUtils.mockFriendRequestDao.query("_id", new ObjectId(requestId)))
                .thenReturn(List.of(pendingRequest));
        Mockito.doNothing().when(testUtils.mockFriendRequestDao).put(Mockito.any());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/respondFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        parsedRequest.setBody("{\"requestId\":\"" + requestId + "\",\"accept\":false}");

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("declined"));

        ArgumentCaptor<FriendRequestDto> captor = ArgumentCaptor.forClass(FriendRequestDto.class);
        Mockito.verify(testUtils.mockFriendRequestDao).put(captor.capture());
        Assert.assertEquals(captor.getValue().getStatus(), "declined");
    }

    @Test(singleThreaded = true)
    public void respondToNonExistentRequestFails() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";
        String requestId = new ObjectId().toHexString();

        var authEntry = testUtils.createLogin(userName);

        Mockito.when(testUtils.mockFriendRequestDao.query("_id", new ObjectId(requestId)))
                .thenReturn(new ArrayList<>());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/respondFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        parsedRequest.setBody("{\"requestId\":\"" + requestId + "\",\"accept\":true}");

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.BAD_REQUEST);
    }

    @Test(singleThreaded = true)
    public void respondToOthersRequestFails() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";
        String requestId = new ObjectId().toHexString();

        var authEntry = testUtils.createLogin(userName);

        // Request is for someone else
        var pendingRequest = new FriendRequestDto();
        pendingRequest.setUniqueId(requestId);
        pendingRequest.setFromUser("user2@test.com");
        pendingRequest.setToUser("user3@test.com"); // Different user
        pendingRequest.setStatus("pending");

        Mockito.when(testUtils.mockFriendRequestDao.query("_id", new ObjectId(requestId)))
                .thenReturn(List.of(pendingRequest));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/respondFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        parsedRequest.setBody("{\"requestId\":\"" + requestId + "\",\"accept\":true}");

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.UNAUTHORIZED);
    }

    @Test(singleThreaded = true)
    public void respondToAlreadyRespondedFails() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";
        String requestId = new ObjectId().toHexString();

        var authEntry = testUtils.createLogin(userName);

        // Request already accepted
        var acceptedRequest = new FriendRequestDto();
        acceptedRequest.setUniqueId(requestId);
        acceptedRequest.setFromUser("user2@test.com");
        acceptedRequest.setToUser(userName);
        acceptedRequest.setStatus("accepted");

        Mockito.when(testUtils.mockFriendRequestDao.query("_id", new ObjectId(requestId)))
                .thenReturn(List.of(acceptedRequest));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/respondFriendRequest");
        parsedRequest.setCookieValue("auth", authEntry.getHash());
        parsedRequest.setBody("{\"requestId\":\"" + requestId + "\",\"accept\":true}");

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.BAD_REQUEST);
    }

    @Test(singleThreaded = true)
    public void respondFriendRequestUnauthorized() {
        var testUtils = new MockTestUtils();
        String requestId = new ObjectId().toHexString();

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/respondFriendRequest");
        // No auth cookie
        parsedRequest.setBody("{\"requestId\":\"" + requestId + "\",\"accept\":true}");

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.UNAUTHORIZED);
    }
}

