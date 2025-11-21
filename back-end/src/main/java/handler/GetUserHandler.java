package handler;

import auth.AuthFilter;
import dao.UserDao;
import dto.UserDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.util.List;

public class GetUserHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {

        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        UserDto userDto = UserDao.getInstance().query("userName", authResult.userName)
                .stream()
                .findFirst()
                .orElse(null);

        var res = new RestApiAppResponse<>(true, List.of(userDto), null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
