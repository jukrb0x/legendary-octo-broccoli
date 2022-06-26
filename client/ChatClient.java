package client;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import server.ChatServerIF;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF {

    private static final long serialVersionUID = 2233L;
    ClientRMIGUI chatGUI;
    private String hostName = "localhost";
    private String serviceName = "GroupChatService";
    private String clientServiceName;
    private String name;
    protected ChatServerIF serverIF;
    protected boolean connectionProblem = false;


    //class constructor,
    //note may also use an overloaded constructor with
    //a port no passed in argument to super
    public ChatClient(ClientRMIGUI aChatGUI, String userName) throws RemoteException {
        super();
        this.chatGUI = aChatGUI;
        this.name = userName;
        this.clientServiceName = "ClientListenService_" + userName;
    }


    //Register our own listening service/interface
    //lookup the server RMI interface, then send our details
    public boolean startClient() throws RemoteException {
        String[] details = {name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            serverIF = (ChatServerIF) Naming.lookup("rmi://" + hostName + "/" + serviceName);
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


    //pass our username, hostname and RMI service name to
    //the server to register out interest in joining the chat
    public void registerWithServer(String[] details) {
        try {
            serverIF.passIDentity(this.ref);
            serverIF.registerListener(details);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Receive a string from the chat server
    //this is the clients RMI method, which will be used by the server
    //to send messages to us
    @Override
    public void messageFromServer(String message) throws RemoteException {
        System.out.println(message);
        chatGUI.chatArea.append(message);
        //make the GUI display the last appended text
        chatGUI.chatArea.setCaretPosition(chatGUI.chatArea.getDocument().getLength());
    }

    //A method to update the display of users
    //currently connected to the server
    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
        chatGUI.userPanel.remove(chatGUI.clientPanel);
        chatGUI.setClientPanel(currentUsers);
        chatGUI.clientPanel.repaint();
        chatGUI.clientPanel.revalidate();
    }
}












