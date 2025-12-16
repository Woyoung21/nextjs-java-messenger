package applogic;

import dto.FriendRequestDto;
import handler.HandlerFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;
import java.util.List;

public class GetFriendRequestsHandlerTests {

    @Test(singleThreaded = true)
    public void getFriendRequestsSuccess() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";

        var authEntry = testUtils.createLogin(userName);

        // Setup pending requests
        var pendingRequest = new FriendRequestDto();
        pendingRequest.setFromUser("user2@test.com");
        pendingRequest.setToUser(userName);
        pendingRequest.setStatus("pending");

        Mockito.when(testUtils.mockFriendRequestDao.findRequestsToUser(userName))
                .thenReturn(List.of(pendingRequest));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriendRequests");
        parsedRequest.setCookieValue("auth", authEntry.getHash());

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("pending"));
        Assert.assertTrue(res.body.contains("user2@test.com"));
    }

    @Test(singleThreaded = true)
    public void getFriendRequestsFiltersPending() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";

        var authEntry = testUtils.createLogin(userName);

        // Setup mixed requests - pending and accepted
        var pendingRequest = new FriendRequestDto();
        pendingRequest.setFromUser("user2@test.com");
        pendingRequest.setToUser(userName);
        pendingRequest.setStatus("pending");

        var acceptedRequest = new FriendRequestDto();
        acceptedRequest.setFromUser("user3@test.com");
        acceptedRequest.setToUser(userName);
        acceptedRequest.setStatus("accepted");

        Mockito.when(testUtils.mockFriendRequestDao.findRequestsToUser(userName))
                .thenReturn(List.of(pendingRequest, acceptedRequest));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriendRequests");
        parsedRequest.setCookieValue("auth", authEntry.getHash());

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        // Should only contain pending, not accepted
        Assert.assertTrue(res.body.contains("user2@test.com"));
    }

    @Test(singleThreaded = true)
    public void getFriendRequestsEmpty() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";

        var authEntry = testUtils.createLogin(userName);

        Mockito.when(testUtils.mockFriendRequestDao.findRequestsToUser(userName))
                .thenReturn(new ArrayList<>());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriendRequests");
        parsedRequest.setCookieValue("auth", authEntry.getHash());

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("\"data\":[]"));
    }

    @Test(singleThreaded = true)
    public void getFriendRequestsUnauthorized() {
        var testUtils = new MockTestUtils();

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriendRequests");
        // No auth cookie

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.UNAUTHORIZED);
    }
}

