package server;

import client.ChatClientIF;


public class Chatter {

    public String name;
    public ChatClientIF client;

    //constructor
    public Chatter(String name, ChatClientIF client) {
        this.name = name;
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public ChatClientIF getClient() {
        return client;
    }
}
