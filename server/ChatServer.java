package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import client.ChatClientIF;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF {
    String line = "---------------------------------------------\n";
    private Vector<Chatter> chatters;
    private static final long serialVersionUID = 1L;

    //Constructor
    public ChatServer() throws RemoteException {
        super();
        chatters = new Vector<Chatter>(10, 1);
    }

    //Local Method
    public static void main(String[] args) {
        String hostName = "localhost";
        String serviceName = "GroupChatService";
        int portNumber = 8999;

        startRMIRegistry(portNumber);

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ChatServerIF hello = new ChatServer();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("Group Chat RMI Server is running...");
        } catch (Exception e) {
            System.out.println("Server had problems starting");
        }
    }

    //Start the RMI Registry
    public static void startRMIRegistry(int portNumber) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(portNumber);
            System.out.println("RMI Server ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //Remote Method
    //Send a sentence to all connected clients
    public void updateChat(String name, String nextPost) throws RemoteException {
        String message = name + " : " + nextPost + "\n";
        sendToAll(message);
    }

    //Receive a new client remote reference
    @Override
    public void passIDentity(RemoteRef ref) throws RemoteException {

        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Receive a new client and display details to the console
    //send on to register method
    @Override
    public void registerListener(String[] details) throws RemoteException {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " has joined the chat session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);
        registerChatter(details);
    }

    //register the clients interface and store it in a reference for
    //future messages to be sent to, ie other members messages of the chat session.
    //send a test message for confirmation / test connection
    private void registerChatter(String[] details) {
        try {
            ChatClientIF nextClient = (ChatClientIF) Naming.lookup("rmi://" + details[1] + "/" + details[2]);

            chatters.addElement(new Chatter(details[0], nextClient));

            nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");

            sendToAll("[Server] : " + details[0] + " has joined the group.\n");

            updateUserList();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    //Update all clients by remotely invoking their
    //updateUserList RMI method
    private void updateUserList() {
        String[] currentUsers = getUserList();
        for (Chatter c : chatters) {
            try {
                c.getClient().updateUserList(currentUsers);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //generate a String array of current users
    private String[] getUserList() {
        // generate an array of current users
        String[] allUsers = new String[chatters.size()];
        for (int i = 0; i < allUsers.length; i++) {
            allUsers[i] = chatters.elementAt(i).getName();
        }
        return allUsers;
    }

    //Send a message to all users
    public void sendToAll(String newMessage) {
        for (Chatter c : chatters) {
            try {
                c.getClient().messageFromServer(newMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //remove a client from the list, notify everyone
    @Override
    public void leaveChat(String userName) throws RemoteException {

        for (Chatter c : chatters) {
            if (c.getName().equals(userName)) {
                System.out.println(line + userName + " left the chat session");
                System.out.println(new Date(System.currentTimeMillis()));
                chatters.remove(c);
                break;
            }
        }
        if (!chatters.isEmpty()) {
            updateUserList();
        }
    }
}



