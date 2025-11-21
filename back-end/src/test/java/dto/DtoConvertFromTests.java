package dto;

import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DtoConvertFromTests {

    @Test
    public void convertUserDto(){
        UserDto userDto = new UserDto();
        userDto.setUserName(String.valueOf(Math.random()));
        userDto.setPassword(String.valueOf(Math.random()));
        userDto.setMessagesSent((int) Math.random());
        userDto.setMessagesRecieved((int) Math.random());
        Document document = userDto.toDocument();
        document.append("userName", userDto.getUserName());
        document.append("password", userDto.getPassword());
        document.append("messagesReceived", userDto.getMessagesRecieved());
        document.append("messagesSent", userDto.getMessagesSent());
        userDto.fromDocument(document);

        Assert.assertEquals(userDto.getUserName(), document.getString("userName"));
        Assert.assertEquals(userDto.getPassword(), document.getString("password"));
        Assert.assertEquals(userDto.getMessagesRecieved(), document.getInteger("messagesReceived"));
        Assert.assertEquals(userDto.getMessagesSent(), document.getInteger("messagesSent"));
    }
}
