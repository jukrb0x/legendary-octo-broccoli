package server;

import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import client.IChatroomClient;

import javax.swing.*;

public class ChatroomServerMainUI extends UnicastRemoteObject implements IChatroomServer {
    String divider = "---------------------------------------------\n";
    private Vector<OnlineUser> onlineUsers;
    private static final long serialVersionUID = 1L;
    protected static JTextArea jta = new JTextArea();

    //Constructor
    public ChatroomServerMainUI() throws RemoteException {
        super();
        onlineUsers = new Vector<OnlineUser>(10, 1);
    }

    //create Server's GUI
    private static void createGUI() {
        JFrame frame = new JFrame("SWE312 Chatroom Server GUI");
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(jta), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jta.setEditable(false);
        frame.setVisible(true);
    }

    //Local Method
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });

        String hostName = "localhost";
        String serviceName = "SWE312Chatroom";
        int portNumber = 1099;

        startRMIRegistry(portNumber);

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            IChatroomServer hello = new ChatroomServerMainUI();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            String msg = "Chatroom server is ready at " + new Date() + "\n";
            jta.append(msg);
            System.out.println(msg);
        } catch (Exception e) {
            String msg = "! --- Chatroom Server Exception --- !";
            jta.append(msg + "\n");
            System.out.println(msg);
            jta.append(String.valueOf(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    // RMI Registry
    public static void startRMIRegistry(int portNumber) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(portNumber);
            String message = "RMI Registry started on port " + portNumber + "\n";
            System.out.println(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // broadcast msg to all clients
    public void updateChat(String name, String nextPost) throws RemoteException {
        String message = "\n" + name + " [" + new Date(System.currentTimeMillis()) + "]:\n" + nextPost + "\n";
        sendToAll(message);
    }

    // receive a new client remote reference
    @Override
    public void passIdentity(RemoteRef ref) throws RemoteException {
        try {
            System.out.println(divider + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Receive a new client and display details to the console
    //send on to register method
    @Override
    public void registerListener(String[] details) throws RemoteException {

        jta.append(String.valueOf(new Date(System.currentTimeMillis())) + "\n");
        System.out.println(new Date(System.currentTimeMillis()));

        String userName = details[0];
        String hostName = details[1];
        String RMI = details[2];
        String msg = divider + userName + " has joined the chat.\n";
        jta.append(msg);
        System.out.println(msg);

        jta.append(" - hostname: " + hostName + "\n");
        System.out.println(" hostname: " + hostName);

        jta.append(" - RMI service: " + RMI + "\n");
        jta.append(divider);
        System.out.println(" - RMI service: " + RMI);
        System.out.println(divider);


        registerChatter(details);
    }

    //register the clients interface and store it in a reference for
    //future messages to be sent to, ie other members messages of the chat session.
    //send a test message for confirmation / test connection
    private void registerChatter(String[] details) {
        try {
            IChatroomClient newClient = (IChatroomClient) Naming.lookup("rmi://" + details[1] + "/" + details[2]);

            onlineUsers.addElement(new OnlineUser(details[0], newClient));

            // todo
            // newClient.handleServerMsg("\nChatroom Broadcast [" + new Date(System.currentTimeMillis()) + "]\nWelcome " + details[0] + "!\n");

            sendToAll("\nChatroom Broadcast [" + new Date(System.currentTimeMillis()) + "]\nWelcome!\n" + details[0] + " has joined.\n");

            updateUserList();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    //Update all clients by remotely invoking their
    //updateUserList RMI method
    private void updateUserList() {
        String[] currentUsers = getUserList();
        for (OnlineUser c : onlineUsers) {
            try {
                c.getClient().updateOnlineUsers(currentUsers);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //generate a String array of current users
    private String[] getUserList() {
        // generate an array of current users
        String[] allUsers = new String[onlineUsers.size()];
        for (int i = 0; i < allUsers.length; i++) {
            allUsers[i] = onlineUsers.elementAt(i).getName();
        }
        return allUsers;
    }

    //Send a message to all users
    public void sendToAll(String newMessage) {
        jta.append(newMessage);
        for (OnlineUser c : onlineUsers) {
            try {
                c.getClient().handleServerMsg(newMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //remove a client from the list, notify everyone
    @Override
    public void leaveChat(String userName) throws RemoteException {

        for (OnlineUser c : onlineUsers) {
            if (c.getName().equals(userName)) {
                jta.append(divider + userName + " left." + "\n");
                System.out.println(divider + userName + " left.");
                jta.append(String.valueOf(new Date(System.currentTimeMillis())) + "\n");
                System.out.println(new Date(System.currentTimeMillis()));
                onlineUsers.remove(c);
                break;
            }
        }
        if (!onlineUsers.isEmpty()) {
            updateUserList();
        }
    }
}



