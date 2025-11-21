package server;

import handler.BaseHandler;
import handler.HandlerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import request.CustomParser;
import request.ParsedRequest;
import response.CustomHttpResponse;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket;
        Socket socket = null;
        serverSocket = new ServerSocket(1299);
        System.out.println("Opened socket " + 1299);
        while (true) {
            // keeps listening for new clients, one at a time
            socket = serverSocket.accept(); // waits for client here

            InputStream stream = socket.getInputStream();
            byte[] b = new byte[1024 * 30];
            stream.read(b);
            String input = new String(b).trim();
            System.out.println(input);

            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            PrintWriter writer = new PrintWriter(out, true);  // char output to the client

            // HTTP Response
            if (!input.isEmpty()) {
                CustomHttpResponse response = processRequest(input);
                writer.println(response);
            } else {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Server: TEST");
                writer.println("Connection: close");
                writer.println("Content-type: text/html");
                writer.println("");
            }
            socket.close();
        }
    }

    // Assume the http server feeds the entire raw http request here
    // Response is a raw http response string
    public static CustomHttpResponse processRequest(String requestString) {
        ParsedRequest request = CustomParser.parse(requestString);
        BaseHandler handler = HandlerFactory.getHandler(request);
        CustomHttpResponse response = handler.handleRequest(request).build();
        if (response.body != null && !response.body.isEmpty()) {
            response.headers.put("Content-Type", "application/json");
        }
        return response;
    }

}
