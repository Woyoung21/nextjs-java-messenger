package response;

import java.util.Map;
import java.util.Map.Entry;

public class CustomHttpResponse {
    public final Map<String, String> headers;
    public final String status;
    public final String version;
    public final String body;

    public CustomHttpResponse(Map<String, String> headers, String status, String version,
                              String body) {
        this.headers = headers;
        this.status = status;
        this.version = version;
        this.body = body;
    }

    // TODO fill this out
    public String toString() {
        String headerString = "";
        String lineBreak = "\r\n";
        for (Entry<String, String> header : headers.entrySet()) {
            headerString += header.getKey() + ": " + header.getValue() + lineBreak;
        }
        String res = version + " " + status + lineBreak + headerString;

        if (body != null && !body.isEmpty() && !body.isBlank()) {
            res += lineBreak + body;
        }
        res += lineBreak;
        System.out.println("Raw http response:");
        System.out.println(res);
        return res;
    }
}
