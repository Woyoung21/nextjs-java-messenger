package applogic;

import dao.BlockDao;
import dto.BlockDto;
import dto.FriendRequestDto;
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

public class BlockTests {

    @Test(singleThreaded = true)
    public void blockUserTest() {
        var testUtils = new MockTestUtils();
        var user = new UserDto();
        user.setUserName(String.valueOf(Math.random()));

        var target = new UserDto();
        String targetName = String.valueOf(Math.random());
        target.setUserName(targetName);

        ArrayList<UserDto> targetList = new ArrayList<>();
        targetList.add(target);

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/blockUser");

        var auth = testUtils.createLogin(user.getUserName());

        BlockDto body = new BlockDto();
        body.setBlocked(targetName);

        parsedRequest.setBody(GsonTool.GSON.toJson(body));
        parsedRequest.setCookieValue("auth", auth.getHash());

        Mockito.when(testUtils.mockUserDao.query("userName", targetName))
                .thenReturn(targetList);

        var handler = HandlerFactory.getHandler(parsedRequest);

        ArgumentCaptor<BlockDto> blockCaptor = ArgumentCaptor.forClass(BlockDto.class);

        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertTrue(builder.getBody().status);

        Mockito.verify(testUtils.mockBlockDao).put(blockCaptor.capture());
        Assert.assertEquals(blockCaptor.getValue().getBlocked(), targetName);
    }

    @Test(singleThreaded = true)
    public void sendMessageBlockedTest() {
        var testUtils = new MockTestUtils();
        var user = new UserDto();
        user.setUserName(String.valueOf(Math.random()));
        ArrayList<UserDto> userReturnList = new ArrayList<>();
        userReturnList.add(user);

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendMessage");

        var auth = testUtils.createLogin(user.getUserName());

        var messageDto = new MessageDto();
        messageDto.setMessage(String.valueOf(Math.random()));
        String toId = String.valueOf(Math.random());
        messageDto.setToId(toId);

        parsedRequest.setBody(GsonTool.GSON.toJson(messageDto));
        parsedRequest.setCookieValue("auth", auth.getHash());

        Mockito.when(testUtils.mockUserDao.query("userName", user.getUserName()))
                .thenReturn(userReturnList);

        var user2 = new UserDto();
        user2.setUserName(toId);
        ArrayList<UserDto> userReturnList2 = new ArrayList<>();
        userReturnList2.add(user2);

        Mockito.when(testUtils.mockUserDao.query("userName", user2.getUserName()))
                .thenReturn(userReturnList2);

        // Simulate recipient has blocked sender
        Mockito.when(testUtils.mockBlockDao.isBlocked(toId, user.getUserName())).thenReturn(true);

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.OK);
        Assert.assertFalse(builder.getBody().status);
        Assert.assertEquals(builder.getBody().message, "You are blocked by the recipient");

        Mockito.verify(testUtils.mockMessageDao, Mockito.never()).put(Mockito.any());
    }

    @Test(singleThreaded = true)
    public void sendFriendRequestBlockedTest() {
        var testUtils = new MockTestUtils();
        var user = new UserDto();
        user.setUserName(String.valueOf(Math.random()));

        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setPath("/sendFriendRequest");

        var auth = testUtils.createLogin(user.getUserName());

        var req = new FriendRequestDto();
        String toUser = String.valueOf(Math.random());
        req.setToUser(toUser);

        parsedRequest.setBody(GsonTool.GSON.toJson(req));
        parsedRequest.setCookieValue("auth", auth.getHash());

        ArrayList<UserDto> targetList = new ArrayList<>();
        var target = new UserDto();
        target.setUserName(toUser);
        targetList.add(target);

        Mockito.when(testUtils.mockUserDao.query("userName", toUser)).thenReturn(targetList);

        // Either direction blocked should prevent friend request
        Mockito.when(testUtils.mockBlockDao.isBlocked(Mockito.eq(toUser), Mockito.eq(user.getUserName())))
                .thenReturn(true);

        var handler = HandlerFactory.getHandler(parsedRequest);
        var builder = handler.handleRequest(parsedRequest);
        var res = builder.build();

        Assert.assertEquals(res.status, StatusCodes.BAD_REQUEST);
        Assert.assertFalse(builder.getBody().status);
    }
}
