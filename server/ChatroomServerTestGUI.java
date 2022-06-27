package server;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class ChatroomServerTestGUI extends ChatroomServerMainUI {
    public ChatroomServerTestGUI() throws RemoteException {
    }

    void createTestGUI(){
        JFrame testFrame = new JFrame("test");
        btnSend = new JButton("TestSend");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btnSendActionPerformed(evt);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        testFrame.add(btnSend, BorderLayout.SOUTH);
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.pack();
        testFrame.setVisible(true);
    }

}
