package handler;

import auth.AuthFilter;
import dao.BlockDao;
import dto.BlockDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.util.List;

public class GetBlockedUsersHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        BlockDao blockDao = BlockDao.getInstance();
        List<BlockDto> blocked = blockDao.findBlockedByUser(authResult.userName);

        var res = new RestApiAppResponse<>(true, blocked, null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
