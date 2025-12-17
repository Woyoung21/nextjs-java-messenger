package util;

import dao.AuthDao;
import dao.BlockDao;
import dao.ConversationDao;
import dao.FriendRequestDao;
import dao.MessageDao;
import dao.UserDao;
import dto.AuthDto;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class MockTestUtils {

    public final UserDao mockUserDao;
    public final MessageDao mockMessageDao;
    public final AuthDao mockAuthDao;
    public final ConversationDao mockConversationDao;
    public final FriendRequestDao mockFriendRequestDao;
    public final BlockDao mockBlockDao;

    public MockTestUtils() {
        this.mockUserDao = Mockito.mock(UserDao.class);
        this.mockMessageDao = Mockito.mock(MessageDao.class);
        this.mockAuthDao =  Mockito.mock(AuthDao.class);
        this.mockConversationDao =  Mockito.mock(ConversationDao.class);
        this.mockFriendRequestDao = Mockito.mock(FriendRequestDao.class);
        this.mockBlockDao = Mockito.mock(BlockDao.class);
        AuthDao.setInstanceSupplier(() -> mockAuthDao);
        MessageDao.setInstanceSupplier(() -> mockMessageDao);
        ConversationDao.setInstanceSupplier(() -> mockConversationDao);
        UserDao.setInstanceSupplier(() -> mockUserDao);
        FriendRequestDao.setInstanceSupplier(() -> mockFriendRequestDao);
        BlockDao.setInstanceSupplier(() -> mockBlockDao);
    }

    public AuthDto createLogin(String userName){
        var authEntry = new AuthDto();
        authEntry.setHash(String.valueOf(Math.random()));
        authEntry.setUserName(userName);
        List<AuthDto> returnList = new ArrayList<>();
        returnList.add(authEntry);
        Mockito.doReturn(returnList).when(mockAuthDao).query("hash", authEntry.getHash());
        return authEntry;
    }
}
