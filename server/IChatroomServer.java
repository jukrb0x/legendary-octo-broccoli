package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;

public interface IChatroomServer extends Remote {

    public void updateChat(String userName, String chatMessage) throws RemoteException;

    public void passIdentity(RemoteRef ref) throws RemoteException;

    public void registerListener(String[] details) throws RemoteException;

    public void leaveChat(String userName) throws RemoteException;
}


