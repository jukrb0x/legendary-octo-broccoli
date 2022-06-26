package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

public class ClientRMIGUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel textPanel, loginPanel;
    private JTextField textField, usernameField;
    private String name, message;
    private Font microsoftYaHeiUi = new Font("Microsoft YaHei UI", Font.PLAIN, 14);
    private Border blankBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);
    private ChatClient chatClient;
    private JList<String> list;
    private DefaultListModel<String> listModel;

    protected JTextArea chatArea;
    protected JFrame frameChatroom, frameLogin;
    protected JButton loginButton, sendButton, exitButton;
    protected JPanel clientPanel, userPanel;

    //Main method to start client GUI
    public static void main(String args[]) {
        //set the look and feel to 'Nimbus'
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        new ClientRMIGUI();
    }

    //GUI Constructor
    public ClientRMIGUI() {

        frameLogin = new JFrame("Login");
        frameChatroom = new JFrame("Client Chat Console");
        //intercept close method, inform server we are leaving
        //then let the system exit.


        frameChatroom.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                if (chatClient != null) {
                    try {
                        sendMessage("Bye all, I am leaving");
                        chatClient.serverIF.leaveChat(name);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        Container loginContainer = getContentPane();
        JPanel loginOuterPanel = new JPanel(new BorderLayout());
        loginOuterPanel.add(getLoginPanel(), BorderLayout.CENTER);
        loginContainer.setLayout(new BorderLayout());
        loginContainer.add(loginOuterPanel, BorderLayout.CENTER);


        frameLogin.setVisible(true);
        frameLogin.pack();
        frameLogin.setAlwaysOnTop(false);
        frameLogin.setLocation(300, 300);
        frameLogin.setSize(300, 200);
        frameLogin.add(loginContainer);
//        textField.requestFocus();

        frameLogin.setDefaultCloseOperation(EXIT_ON_CLOSE);

        frameLogin.setVisible(true);

//        // Chatroom Window
//        // remove window buttons and border frame to force user to exit on a button
//        Container chatroomContainer = getContentPane();
//        JPanel chatroomOuterPanel = new JPanel(new BorderLayout());
//
////        chatroomOuterPanel.add(getInputPanel(), BorderLayout.CENTER);
//        chatroomOuterPanel.add(getTextPanel(), BorderLayout.SOUTH);
//
//        chatroomContainer.setLayout(new BorderLayout());
//        chatroomContainer.add(chatroomOuterPanel, BorderLayout.CENTER);
//        chatroomContainer.add(getUsersPanel(), BorderLayout.EAST);
//
//        frameChatroom.add(chatroomContainer);
//        frameChatroom.pack();
//        frameChatroom.setLocation(150, 150);
//        frameChatroom.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        frameChatroom.setVisible(true);
//
//
    }

    public void initChatroom() {
        // close login window
//        frameLogin.setVisible(false);
//        frameLogin.dispose();
//        frameLogin.remove(loginContainer);
        // Chatroom Window
        // remove window buttons and border frame to force user to exit on a button
        Container chatroomContainer = getContentPane();
        JPanel chatroomOuterPanel = new JPanel(new BorderLayout());

        chatroomOuterPanel.add(getInputPanel(), BorderLayout.CENTER);
        chatroomOuterPanel.add(getTextPanel(), BorderLayout.SOUTH);

        chatroomContainer.setLayout(new BorderLayout());
        chatroomContainer.add(chatroomOuterPanel, BorderLayout.CENTER);
        chatroomContainer.add(getUsersPanel(), BorderLayout.EAST);

        frameChatroom.add(chatroomContainer);
        frameChatroom.pack();
        frameChatroom.setLocation(150, 150);
        frameChatroom.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frameChatroom.setVisible(true);

    }


    ///Method to set up the JPanel to display the chat text
    public JPanel getTextPanel() {
        String welcome = "Welcome enter your name and press Start to begin\n";
        chatArea = new JTextArea(welcome, 14, 34);
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        chatArea.setFont(microsoftYaHeiUi);

        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        textPanel = new JPanel();
        textPanel.add(scrollPane);

        textPanel.setFont(microsoftYaHeiUi);
        return textPanel;
    }

    //Method to build the panel with input field
    public JPanel getInputPanel() {
        loginPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        loginPanel.setBorder(blankBorder);
        textField = new JTextField();
        textField.setFont(microsoftYaHeiUi);
        loginPanel.add(textField);
        return loginPanel;
    }

    public JPanel getLoginPanel() {
        JLabel textLabel = new JLabel("Your username: ");
        loginPanel = new JPanel(new GridLayout(2, 2, 1, 2));
        loginPanel.setBorder(blankBorder);
        usernameField = new JTextField();
        usernameField.setFont(microsoftYaHeiUi);
        loginPanel.add(textLabel);
        loginPanel.add(usernameField);
        exitButton = new JButton("Exit");
        loginButton = new JButton("Login");
        loginPanel.add(exitButton);
        loginPanel.add(loginButton);
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        return loginPanel;

    }

    //Method to build the panel displaying currently connected users with a call to the button panel building method
    public JPanel getUsersPanel() {

        userPanel = new JPanel(new BorderLayout());
        String userStr = " Current Users      ";

        JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
        userPanel.add(userLabel, BorderLayout.NORTH);
//        userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));
        userLabel.setFont(microsoftYaHeiUi);

        String[] noClientsYet = {"No other users"};
        setClientPanel(noClientsYet);

        clientPanel.setFont(microsoftYaHeiUi);
        userPanel.add(makeButtonPanel(), BorderLayout.NORTH);
        userPanel.setBorder(blankBorder);

        return userPanel;
    }

    //Populate current user panel with a selectable list of currently connected users
    public void setClientPanel(String[] currClients) {
        clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();

        for (String s : currClients) {
            listModel.addElement(s);
        }

        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(microsoftYaHeiUi);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }

    //Make the buttons and add the listener
    public JPanel makeButtonPanel() {
        sendButton = new JButton("Send ");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);

        loginButton = new JButton("Start ");
        loginButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(loginButton);
        buttonPanel.add(sendButton);

        return buttonPanel;
    }

    //Action handling on the buttons
    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            //get connected to chat service
            if (e.getSource() == exitButton) {
                // confirm to exit
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    return;
                }
            }
            if (e.getSource() == loginButton) {
                name = usernameField.getText();
                if (name.length() != 0) {
//                    initChatroom();
//                    frameChatroom.setTitle(name + "'s console ");
//                    usernameField.setText("");
//                    chatArea.append("user: " + name + " connecting to chat...\n");
                    getConnected(name);
                    if (!chatClient.connectionProblem) {
                        initChatroom(); // show chatroom
//                        loginButton.setEnabled(false);
                        sendButton.setEnabled(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(frameChatroom, "Enter your name to Start");
                }
            }

            //get text and clear textField
            if (e.getSource() == sendButton) {
                message = textField.getText();
                textField.setText("");
                sendMessage(message);
                System.out.println("Sending message : " + message);
            }
        } catch (RemoteException remoteExc) {
            remoteExc.printStackTrace();
        }

    }

    //Send a message
    private void sendMessage(String chatMessage) throws RemoteException {
        chatClient.serverIF.updateChat(name, chatMessage);
    }

    //Make the connection to the chat server
    private void getConnected(String userName) throws RemoteException {
        //remove whitespace and non word characters to avoid malformed url
        String cleanedUserName = userName.replaceAll("\\s+", "_");
        cleanedUserName = userName.replaceAll("\\W+", "_");
        try {
            chatClient = new ChatClient(this, cleanedUserName);
            chatClient.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}










