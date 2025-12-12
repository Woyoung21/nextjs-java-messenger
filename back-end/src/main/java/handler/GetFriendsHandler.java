package handler;

import auth.AuthFilter;
import dao.FriendRequestDao;
import dto.FriendRequestDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.util.List;

public class GetFriendsHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        FriendRequestDao friendRequestDao = FriendRequestDao.getInstance();
        
        List<FriendRequestDto> friends = friendRequestDao.findAcceptedFriends(authResult.userName);

        var res = new RestApiAppResponse<>(true, friends, null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}

