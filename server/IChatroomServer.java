package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface IChatroomServer extends Remote {

    public void updateChatroom(String userName, String chatMessage) throws RemoteException;

    public void handleId(RemoteRef ref) throws RemoteException;

    public void registerUserListener(String[] details) throws RemoteException;

    public void leaveChatroom(String userName) throws RemoteException;
}


