package auth;

import dao.AuthDao;
import dto.AuthDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import request.ParsedRequest;

import java.util.ArrayList;
import java.util.List;

// DONE
public class AuthFilterTests {

    @Test(singleThreaded = true)
    public void testNotLoggedIn(){
        AuthDao mockAuthDao = Mockito.mock(AuthDao.class);
        AuthDao.setInstanceSupplier(() -> mockAuthDao);
        String hash = DigestUtils.sha256Hex(String.valueOf(Math.random()));
        Mockito.when(mockAuthDao.query("hash", hash)).thenReturn(new ArrayList());
        var request = new ParsedRequest();
        request.setCookieValue("auth", hash);
        AuthFilter.AuthResult result = AuthFilter.doFilter(request);
        Assert.assertFalse(result.isLoggedIn);
    }

    @Test(singleThreaded = true)
    public void loggedIn(){
        var request = new ParsedRequest();
        String hash = DigestUtils.sha256Hex(String.valueOf(Math.random()));
        String userName = String.valueOf(Math.random());
        request.setCookieValue("auth", hash);

        var authEntry = new AuthDto();
        authEntry.setHash(hash);
        authEntry.setUserName(userName);
        AuthDao mockAuthDao = Mockito.mock(AuthDao.class);
        AuthDao.setInstanceSupplier(() -> mockAuthDao);
        Mockito.when(mockAuthDao.query("hash", hash))
                .thenReturn(List.of(authEntry));
        AuthFilter.AuthResult result = AuthFilter.doFilter(request);
        Assert.assertTrue(result.isLoggedIn);
        Assert.assertEquals(result.userName, userName);
    }
}
