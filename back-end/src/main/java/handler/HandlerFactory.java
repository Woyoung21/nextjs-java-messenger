package handler;

import request.ParsedRequest;

public class HandlerFactory {
    // routes based on the path. Add your custom handlers here
    public static BaseHandler getHandler(ParsedRequest request) {
        System.out.println("request: " + GsonTool.GSON.toJson(request));
        switch (request.getPath()) {
            case "/createUser":
                return new CreateUserHandler();
            case "/sendMessage":
                return new SendMessageHandler();
            case "/getConversations":
                return new GetConversationsHandler();
            case "/getConversation":
                return new GetConversationHandler();
            case "/login":
                return new LoginHandler();
            case "/getUser":
                return new GetUserHandler();
            case "/sendFriendRequest":
                return new SendFriendRequestHandler();
            case "/getFriendRequests":
                return new GetFriendRequestsHandler();
            case "/respondFriendRequest":
                return new RespondFriendRequestHandler();
            case "/getFriends":
                return new GetFriendsHandler();
            default:
                return new FallbackHandler();
        }
    }

}
