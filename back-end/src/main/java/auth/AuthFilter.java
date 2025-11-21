package auth;

import dao.AuthDao;
import request.ParsedRequest;

public class AuthFilter {

    public static class AuthResult {
        public boolean isLoggedIn;
        public String userName;
    }

    public static AuthResult doFilter(ParsedRequest parsedRequest) {
        AuthDao authDao = AuthDao.getInstance();
        var result = new AuthResult();
        String hash = parsedRequest.getCookieValue("auth");
        if (hash == null) {
            return result;
        }
        var authRes = authDao.query("hash", hash);
        if (authRes.isEmpty()) {
            result.isLoggedIn = false;
            return result;
        }
        result.isLoggedIn = true;
        result.userName = authRes.getFirst().getUserName();
        return result;
    }
}
