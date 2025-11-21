package handler;

import dao.UserDao;
import dto.UserDto;

import org.apache.commons.codec.digest.DigestUtils;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

// DONE
public class CreateUserHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        UserDto userDto = GsonTool.GSON.fromJson(request.getBody(), dto.UserDto.class);
        UserDao userDao = UserDao.getInstance();
        var existingUser = userDao.query("userName", userDto.getUserName());
        if(!existingUser.isEmpty()){
            var res = new RestApiAppResponse<>(false, null, "Username already exists");
            return new ResponseBuilder().setStatus(StatusCodes.BAD_REQUEST)
                    .setBody(res);
        }
        userDto.setPassword(DigestUtils.sha256Hex(userDto.getPassword()));
        userDao.put(userDto);
        var res = new RestApiAppResponse<>(true, null, null);
        return new ResponseBuilder().setStatus("200 OK").setBody(res);
    }
}
