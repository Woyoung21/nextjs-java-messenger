package handler;

import dao.AuthDao;
import dao.UserDao;
import dto.AuthDto;
import dto.UserDto;
import org.apache.commons.codec.digest.DigestUtils;
import request.ParsedRequest;
import response.ResponseBuilder;
import response.RestApiAppResponse;
import response.StatusCodes;

import java.time.Instant;

// DONE
public class LoginHandler implements BaseHandler {

    @Override
    public ResponseBuilder handleRequest(ParsedRequest request) {
        var res = new ResponseBuilder();
        UserDto userDto = GsonTool.GSON.fromJson(request.getBody(), UserDto.class);
        UserDao userDao = UserDao.getInstance();
        AuthDao authDao = AuthDao.getInstance();
        String passwordHash = DigestUtils.sha256Hex(userDto.getPassword());

        var result = userDao.query("userName", userDto.getUserName()).stream()
                .findFirst()
                .orElse(null);
        if (result == null || !result.getPassword().equals(passwordHash)) {
            res.setStatus(StatusCodes.UNAUTHORIZED);
        } else {
            AuthDto authDto = new AuthDto();
            authDto.setExpireTime(Instant.now().getEpochSecond() + 60000);
            String hash = DigestUtils.sha256Hex(authDto.getUserName() + authDto.getExpireTime());
            authDto.setHash(hash);
            authDto.setUserName(userDto.getUserName());
            authDao.put(authDto);
            res.setStatus(StatusCodes.OK);
            res.setHeader("Set-Cookie", "auth=" + hash );
            res.setBody(new RestApiAppResponse<>(true, null, null));
        }
        return res;
    }
}
