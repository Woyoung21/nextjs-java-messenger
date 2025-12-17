package handler;

import auth.AuthFilter;
import dao.FriendRequestDao;
import dao.BlockDao;
import dao.UserDao;
import dto.FriendRequestDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.util.List;

public class SendFriendRequestHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        FriendRequestDto requestBody = GsonTool.GSON.fromJson(request.getBody(), FriendRequestDto.class);
        String toUser = requestBody.getToUser();

        if (toUser == null || toUser.trim().isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "Target username is required");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        if (toUser.equals(authResult.userName)) {
            var res = new RestApiAppResponse<>(false, null, "Cannot send friend request to yourself");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        var targetUser = UserDao.getInstance().query("userName", toUser);
        if (targetUser.isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "User not found");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        // Do not allow friend requests if either user has blocked the other
        BlockDao blockDao = BlockDao.getInstance();
        if (blockDao.isBlocked(toUser, authResult.userName) || blockDao.isBlocked(authResult.userName, toUser)) {
            var res = new RestApiAppResponse<>(false, null, "Cannot send friend request due to block status");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        FriendRequestDao friendRequestDao = FriendRequestDao.getInstance();
        FriendRequestDto existing = friendRequestDao.findExistingRequest(authResult.userName, toUser);
        
        if (existing != null) {
            String message = existing.getStatus().equals("accepted") 
                ? "You are already friends with this user"
                : "A friend request already exists";
            var res = new RestApiAppResponse<>(false, null, message);
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        FriendRequestDto newRequest = new FriendRequestDto()
                .setFromUser(authResult.userName)
                .setToUser(toUser)
                .setStatus("pending");

        friendRequestDao.put(newRequest);

        var res = new RestApiAppResponse<>(true, List.of(newRequest), "Friend request sent");
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}

