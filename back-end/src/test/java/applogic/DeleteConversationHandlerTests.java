package applogic;

import dao.ConversationDao;
import dao.MessageDao;
import dto.ConversationDto;
import handler.HandlerFactory;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;
import response.StatusCodes;
import util.MockTestUtils;

import java.util.ArrayList;
import java.util.List;

public class DeleteConversationHandlerTests {

    @Test(singleThreaded = true)
    public void testDeleteConversationSuccess() {

        MockTestUtils utils = new MockTestUtils();

        // Mock logged in user
        String userName = "alice";
        var login = utils.createLogin(userName);

        // Prepare conversation data
        String conversationId = "alice_bob";

        ConversationDto conversation = new ConversationDto("alice", "bob");
        conversation.setConversationId(conversationId);

        List<ConversationDto> convos = new ArrayList<>();
        convos.add(conversation);

        // Mock DAO behavior
        Mockito.when(utils.mockConversationDao.query("conversationId", conversationId))
                .thenReturn(convos);

        // Build parsed request
        ParsedRequest request = new ParsedRequest();
        request.setPath("/deleteConversation");
        request.setMethod("POST");
        request.setQueryParam("conversationId", conversationId);
        request.setCookieValue("auth", login.getHash());

        var handler = HandlerFactory.getHandler(request);
        var responseBuilder = handler.handleRequest(request);
        var response = responseBuilder.build();

        // Validate HTTP 200 OK
        Assert.assertEquals(response.status, StatusCodes.OK);

        var body = responseBuilder.getBody();
        Assert.assertTrue(body.status, "Expected successful deletion");

        // Verify deletions occurred
        Mockito.verify(utils.mockMessageDao).deleteByConversationId(conversationId);
        Mockito.verify(utils.mockConversationDao).deleteByConversationId(conversationId);
    }

    @Test(singleThreaded = true)
    public void testDeleteConversationUnauthorized() {

        MockTestUtils utils = new MockTestUtils();

        // No cookie → not logged in
        ParsedRequest request = new ParsedRequest();
        request.setPath("/deleteConversation");
        request.setMethod("POST");
        request.setQueryParam("conversationId", "alice_bob");

        var handler = HandlerFactory.getHandler(request);
        var responseBuilder = handler.handleRequest(request);
        var response = responseBuilder.build();

        Assert.assertEquals(response.status, StatusCodes.UNAUTHORIZED);
        Assert.assertFalse(responseBuilder.getBody().status);
    }
}
