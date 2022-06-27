package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatroomClient extends Remote {

    public void handleServerMsg(String message) throws RemoteException;

    public void updateOnlineUsers(String[] currentUsers) throws RemoteException;

}
