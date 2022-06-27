package server;

import client.IChatroomClient;


public class OnlineUser {

    public String name;
    public IChatroomClient client;

    //constructor
    public OnlineUser(String name, IChatroomClient client) {
        this.name = name;
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public IChatroomClient getClient() {
        return client;
    }
}
