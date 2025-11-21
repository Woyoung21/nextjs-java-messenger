package cookies;

import org.testng.Assert;
import org.testng.annotations.Test;
import request.CustomParser;
import request.ParsedRequest;

// DONE
public class CookieTests {

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cookie
    @Test
    public void basicTest(){
        String randomKey = String.valueOf(Math.random());
        String randomValue = String.valueOf(Math.random());
        String test1 = "GET /hello HTTP/1.1\n"
                + "Cookie: " + randomKey + "=" + randomValue+"; \n";
        ParsedRequest request = CustomParser.parse(test1);
        Assert.assertEquals(request.getCookieValue(randomKey), randomValue);
    }
}
