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
    ClientMainUI chatGUI;
    private String hostName = "localhost";
    private String serviceName = "SWE312Chatroom";
    private String clientServiceName;
    private String name;
    protected IChatroomServer server;
    protected boolean connectionProblem = false;


    public ChatroomClient(ClientMainUI aChatGUI, String userName) throws RemoteException {
        super();
        this.chatGUI = aChatGUI;
        this.name = userName;
        this.clientServiceName = "Client_" + userName;
    }


    // register the client with the server
    public boolean startConnection() throws RemoteException {
        String[] details = {name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            server = (IChatroomServer) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(
                    chatGUI.frameChatroom, "The server seems to be unavailable\nPlease try later",
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
        chatGUI.chatArea.append(message);
        //make the GUI display the last appended text
        chatGUI.chatArea.setCaretPosition(chatGUI.chatArea.getDocument().getLength());
    }

    // update online user list
    @Override
    public void updateOnlineUsers(String[] currentUsers) throws RemoteException {
        chatGUI.userPanel.remove(chatGUI.clientPanel);
        chatGUI.setClientPanel(currentUsers);
        chatGUI.clientPanel.repaint();
        chatGUI.clientPanel.revalidate();
    }
}












