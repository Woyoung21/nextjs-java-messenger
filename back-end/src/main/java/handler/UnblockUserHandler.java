package handler;

import auth.AuthFilter;
import dao.BlockDao;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;
import dto.BlockDto;

public class UnblockUserHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        BlockDto body = GsonTool.GSON.fromJson(request.getBody(), BlockDto.class);
        String toUnblock = body.getBlocked();

        if (toUnblock == null || toUnblock.trim().isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "Target username is required");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        BlockDao.getInstance().removeBlock(authResult.userName, toUnblock);

        var res = new RestApiAppResponse<>(true, null, "User unblocked");
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
