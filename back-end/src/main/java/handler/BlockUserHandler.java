package handler;

import auth.AuthFilter;
import dao.BlockDao;
import dao.UserDao;
import dto.BlockDto;
import dto.UserDto;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

public class BlockUserHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if (!authResult.isLoggedIn) {
            return new ResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        BlockDto body = GsonTool.GSON.fromJson(request.getBody(), BlockDto.class);
        String toBlock = body.getBlocked();

        if (toBlock == null || toBlock.trim().isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "Target username is required");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        if (toBlock.equals(authResult.userName)) {
            var res = new RestApiAppResponse<>(false, null, "Cannot block yourself");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        var target = UserDao.getInstance().query("userName", toBlock);
        if (target.isEmpty()) {
            var res = new RestApiAppResponse<>(false, null, "User not found");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        BlockDao blockDao = BlockDao.getInstance();
        if (blockDao.isBlocked(authResult.userName, toBlock)) {
            var res = new RestApiAppResponse<>(false, null, "User already blocked");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST).setBody(res);
        }

        BlockDto newBlock = new BlockDto().setBlocker(authResult.userName).setBlocked(toBlock);
        blockDao.put(newBlock);

        var res = new RestApiAppResponse<>(true, java.util.List.of(newBlock), "User blocked");
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
