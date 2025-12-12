package handler;

import auth.AuthFilter;
import dao.FriendRequestDao;
import dto.FriendRequestDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.util.List;

public class RespondFriendRequestHandler implements BaseHandler {

    private static class RequestBody {
        String requestId;
        boolean accept;
    }

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        RequestBody body = GsonTool.GSON.fromJson(request.getBody(), RequestBody.class);

        if (body.requestId == null || body.requestId.trim().isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "Request ID is required");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        FriendRequestDao friendRequestDao = FriendRequestDao.getInstance();
        
        List<FriendRequestDto> requests = friendRequestDao.query("_id", 
                new org.bson.types.ObjectId(body.requestId));
        
        if (requests.isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "Friend request not found");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        FriendRequestDto friendRequest = requests.get(0);

        if (!friendRequest.getToUser().equals(authResult.userName)) {
            var res = new RestApiAppResponse<>(false, null, "Not authorized to respond to this request");
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED).setBody(res);
        }

        if (!"pending".equals(friendRequest.getStatus())) {
            var res = new RestApiAppResponse<>(false, null, "This request has already been responded to");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        friendRequest.setStatus(body.accept ? "accepted" : "declined");
        friendRequestDao.put(friendRequest);

        String message = body.accept ? "Friend request accepted" : "Friend request declined";
        var res = new RestApiAppResponse<>(true, List.of(friendRequest), message);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}

