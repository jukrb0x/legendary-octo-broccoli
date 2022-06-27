package client;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import server.IChatroomServer;

public class ChatroomClient extends UnicastRemoteObject implements IChatroomClient {

    private static final long serialVersionUID = 2233L;
    ClientMainUI clientMainUI;
    private String hostName = "localhost";
    private String serviceName = "SWE312Chatroom";
    private String clientServiceName;
    private String name;
    protected IChatroomServer server;
    protected boolean connectionProblem = false;


    public ChatroomClient(ClientMainUI aChatGUI, String userName) throws RemoteException {
        super();
        this.clientMainUI = aChatGUI;
        this.name = userName;
        this.clientServiceName = "Client_" + userName;
    }


    // register the client with the server
    public boolean startConnection() throws RemoteException {
        String[] details = {name, hostName, clientServiceName};

        try {
            String bindingName = "rmi://" + hostName + "/" + clientServiceName;
            String serverUrl = "rmi://" + hostName + "/" + serviceName;
            Naming.rebind(bindingName, this);
            server = (IChatroomServer) Naming.lookup(serverUrl);
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(
                    clientMainUI.frameChatroom, "The server seems to be unavailable\nPlease try later",
                    "Connection problem", JOptionPane.ERROR_MESSAGE);
            connectionProblem = true;
            e.printStackTrace();
        } catch (NotBoundException | MalformedURLException me) {
            connectionProblem = true;
            me.printStackTrace();
        }
        if (!connectionProblem) {
            registerWithServer(details);
            System.out.println("Connected to server.\n");
            return true;
        } else {
            return false;
        }
    }


    // register user with {username, hostname, RMI service name}
    public void registerWithServer(String[] details) {
        try {
            server.handleId(this.ref);
            server.handleUserRegister(details);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // receive message from server, display it in the chat window
    @Override
    public void handleServerMsg(String message) throws RemoteException {
        System.out.println(message);
        clientMainUI.chatArea.append(message);
        //make the GUI display the last appended text
        clientMainUI.chatArea.setCaretPosition(clientMainUI.chatArea.getDocument().getLength());
    }

    // update online user list
    @Override
    public void updateOnlineUsers(String[] currentUsers) throws RemoteException {
        clientMainUI.onlineUserPanel.remove(clientMainUI.onlineClientsPanel);
        clientMainUI.setOnlineUsersPanel(currentUsers);
        clientMainUI.onlineClientsPanel.repaint();
        clientMainUI.onlineClientsPanel.revalidate();
    }

    @Override
    public void sendTestMsg(String msg) throws RemoteException {
        System.out.println(msg + " from server");
        clientMainUI.sendMessage(msg);
    }


}












