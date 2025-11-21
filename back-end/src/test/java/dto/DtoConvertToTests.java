package dto;

import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Random;

public class DtoConvertToTests {

  @Test
  public void convertUserDto(){
      UserDto userDto = new UserDto();
      userDto.setUserName(String.valueOf(Math.random()));
      userDto.setPassword(String.valueOf(Math.random()));
      userDto.setMessagesSent((int) Math.random());
      userDto.setMessagesRecieved((int) Math.random());
      Document document = userDto.toDocument();
      Assert.assertEquals(document.getString("userName"), userDto.getUserName());
      Assert.assertEquals(document.getString("password"), userDto.getPassword());
      Assert.assertEquals(document.getInteger("messagesReceived"), userDto.getMessagesRecieved());
      Assert.assertEquals(document.getInteger("messagesSent"), userDto.getMessagesSent());
  }

  @Test
  public void convertAuthDto(){
    AuthDto authDto = new AuthDto();
    authDto.setHash(String.valueOf(Math.random()));
    authDto.setUserName(String.valueOf(Math.random()));
    authDto.setExpireTime(new Random().nextLong());

    Document document = authDto.toDocument();
    Assert.assertEquals(document.getString("userName"), authDto.getUserName());
    Assert.assertEquals(document.getString("hash"), authDto.getHash());
    Assert.assertEquals(document.getLong("expireTime"), authDto.getExpireTime());
  }

  @Test
  public void convertMessageDto() {
      MessageDto messageDto = new MessageDto();
      messageDto.setToId(String.valueOf(Math.random()));
      messageDto.setFromId(String.valueOf(Math.random()));
      messageDto.setConversationId(String.valueOf(Math.random()));
      messageDto.setMessage(String.valueOf(Math.random()));
      Document document = messageDto.toDocument();
      Assert.assertEquals(document.getString("toId"), messageDto.getToId());
      Assert.assertEquals(document.getString("fromId"), messageDto.getFromId());
      Assert.assertEquals(document.getString("conversationId"), messageDto.getConversationId());
      Assert.assertEquals(document.getString("message"), messageDto.getMessage());
  }

}
