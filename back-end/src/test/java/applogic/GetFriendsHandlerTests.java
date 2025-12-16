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

public class GetFriendsHandlerTests {

    @Test(singleThreaded = true)
    public void getFriendsSuccess() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";

        var authEntry = testUtils.createLogin(userName);

        // Setup accepted friend
        var friend = new FriendRequestDto();
        friend.setFromUser(userName);
        friend.setToUser("user2@test.com");
        friend.setStatus("accepted");

        Mockito.when(testUtils.mockFriendRequestDao.findAcceptedFriends(userName))
                .thenReturn(List.of(friend));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriends");
        parsedRequest.setCookieValue("auth", authEntry.getHash());

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("user2@test.com"));
        Assert.assertTrue(res.body.contains("accepted"));
    }

    @Test(singleThreaded = true)
    public void getFriendsMultiple() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";

        var authEntry = testUtils.createLogin(userName);

        var friend1 = new FriendRequestDto();
        friend1.setFromUser(userName);
        friend1.setToUser("user2@test.com");
        friend1.setStatus("accepted");

        var friend2 = new FriendRequestDto();
        friend2.setFromUser("user3@test.com");
        friend2.setToUser(userName);
        friend2.setStatus("accepted");

        Mockito.when(testUtils.mockFriendRequestDao.findAcceptedFriends(userName))
                .thenReturn(List.of(friend1, friend2));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriends");
        parsedRequest.setCookieValue("auth", authEntry.getHash());

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("user2@test.com"));
        Assert.assertTrue(res.body.contains("user3@test.com"));
    }

    @Test(singleThreaded = true)
    public void getFriendsEmpty() {
        var testUtils = new MockTestUtils();
        String userName = "user1@test.com";

        var authEntry = testUtils.createLogin(userName);

        Mockito.when(testUtils.mockFriendRequestDao.findAcceptedFriends(userName))
                .thenReturn(new ArrayList<>());

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriends");
        parsedRequest.setCookieValue("auth", authEntry.getHash());

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(res.body.contains("\"data\":[]"));
    }

    @Test(singleThreaded = true)
    public void getFriendsUnauthorized() {
        var testUtils = new MockTestUtils();

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/getFriends");
        // No auth cookie

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.UNAUTHORIZED);
    }
}

