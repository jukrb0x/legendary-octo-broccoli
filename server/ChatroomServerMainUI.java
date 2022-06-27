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
    private int mutexBroadcast = 0;
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
        javax.swing.SwingUtilities.invokeLater(() -> createGUI());

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
    public void handleChatroomMsg(String name, String nextPost) throws RemoteException {
        String message = "\n" + name + " [" + new Date(System.currentTimeMillis()) + "]:\n" + nextPost + "\n";
        {
            if (getMutexBroadcast() == 1) { // wait until mutex is free
                while (true) {
                    try {
                        Thread.sleep(100);
                        // awaiting
                        if (getMutexBroadcast() == 0) {
                            setMutexBroadcast(1); // sending lock
                            broadcastMsg(message);
                            setMutexBroadcast(0); // done sending
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else { // mutex is free
                setMutexBroadcast(1); // sending lock
                broadcastMsg(message);
                setMutexBroadcast(0); // done sending
            }
        }
    }

    // receive a new client remote reference
    @Override
    public void handleId(RemoteRef ref) throws RemoteException {
        try {
            System.out.println(divider + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // register a new user
    // send on to register method
    @Override
    public void handleUserRegister(String[] details) throws RemoteException {
        registerOnlineUser(details);
        jta.append(new Date(System.currentTimeMillis()) + "\n");
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
    }

    // register a client user and store the reference
    private void registerOnlineUser(String[] details) {
        try {
            IChatroomClient newClient = (IChatroomClient) Naming.lookup("rmi://" + details[1] + "/" + details[2]);

            onlineUsers.addElement(new OnlineUser(details[0], newClient));

            // todo
            // newcomer.handleServerMsg("\nChatroom Broadcast [" + new Date(System.currentTimeMillis()) + "]\nWelcome " + details[0] + "!\n");

            broadcastMsg("\nChatroom Broadcast [" + new Date(System.currentTimeMillis()) + "]\nWelcome!\n" + details[0] + " has joined.\n");

            updateOnlineUsers();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void updateOnlineUsers() {
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

    // broadcast message
    public void broadcastMsg(String msg) {
        jta.append(msg);
        for (OnlineUser c : onlineUsers) {
            try {
                c.getClient().handleServerMsg(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //remove a client from the list, notify everyone
    @Override
    public void leaveChatroom(String userName) throws RemoteException {

        for (OnlineUser c : onlineUsers) {
            if (c.getName().equals(userName)) {
                jta.append(divider + userName + " left." + "\n");
                System.out.println(divider + userName + " left.");
                jta.append(new Date(System.currentTimeMillis()) + "\n");
                System.out.println(new Date(System.currentTimeMillis()));
                onlineUsers.remove(c);
                break;
            }
        }
        if (!onlineUsers.isEmpty()) {
            updateOnlineUsers();
        }
    }

    public int getMutexBroadcast() {
        return mutexBroadcast;
    }

    public void setMutexBroadcast(int mutexBroadcast) {
        this.mutexBroadcast = mutexBroadcast;
    }
}



