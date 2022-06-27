package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class ClientMainUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel textPanel, loginPanel;
    private JTextField textField, usernameField;
    private String username, message;
    private Font microsoftYaHeiUi = new Font("Microsoft YaHei UI", Font.PLAIN, 14);
    private Border defaultBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);
    private ChatroomClient chatroomClient;
    private JList<String> list;
    private DefaultListModel<String> listModel;

    protected JTextArea chatArea;
    protected JFrame frameChatroom, frameLogin;
    protected JButton loginButton, sendButton, exitButton;
    protected JPanel clientPanel, userPanel;

    public static void main(String[] args) {
        // theme
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Synth".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new ClientMainUI();
    }

    //GUI Constructor
    public ClientMainUI() {
        // initialize the frames
        frameLogin = new JFrame("SWE312 Chatroom Login");
        frameChatroom = new JFrame("SWE312 Chatroom");
        initLogin();
        // window listener
        attachListener();
    }

    public void initLogin() {
        JPanel loginOuterPanel = new JPanel(new BorderLayout());
        loginOuterPanel.add(getLoginPanel(), BorderLayout.CENTER);
        frameLogin.setVisible(true);
        frameLogin.pack();
        frameLogin.setAlwaysOnTop(false);
        frameLogin.setLocation(300, 300);
        frameLogin.setSize(300, 200);
        frameLogin.add(loginOuterPanel);
        usernameField.requestFocus();
        frameLogin.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frameLogin.setVisible(true);
    }

    public void disposeLogin() {
        frameLogin.removeAll();
        frameLogin.setVisible(false);
        frameLogin.dispose();
    }

    public void initChatroom() {
        // Chatroom Window
        Container chatroomContainer = getContentPane();
        JPanel chatroomOuterPanel = new JPanel(new BorderLayout());

        chatroomOuterPanel.add(getMsgPanel(), BorderLayout.CENTER);
        chatroomOuterPanel.add(getChatPanel(), BorderLayout.SOUTH);

        chatroomContainer.setLayout(new BorderLayout());
        chatroomContainer.add(chatroomOuterPanel, BorderLayout.CENTER);
        chatroomContainer.add(getUsersPanel(), BorderLayout.EAST);

        frameChatroom.add(chatroomContainer);
        frameChatroom.pack();
        frameChatroom.setLocation(150, 150);
        frameChatroom.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void attachListener() {
        frameChatroom.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                if (chatroomClient != null) {
                    try {
                        sendMessage(username + " is leaving the chatroom.");
                        chatroomClient.server.leaveChatroom(username);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
    }


    ///Method to set up the JPanel to display the chat text
    public JPanel getMsgPanel() {
        String welcome = "Welcome to SWE312 Chatroom!\n";
        chatArea = new JTextArea(welcome, 15, 30);
//        chatArea.setMargin(new Insets(5, 1, 5, 1));
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
        textPanel = new JPanel(new GridLayout(1, 1, 5, 1));
        textPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 1));
        textField = new JTextField();
        textField.setFont(microsoftYaHeiUi);
        textPanel.add(textField);
        return textPanel;
    }

    public JPanel getChatPanel(){
//        JPanel msgPanel = new JPanel(new GridLayout(1, 1, 5, 1));
        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 0));
        msgPanel.add(getInputPanel(), BorderLayout.CENTER);
        msgPanel.add(getButtonPanel(), BorderLayout.EAST);
        return msgPanel;

    }

    public JPanel getLoginPanel() {
        JLabel textLabel = new JLabel("Your username: ");
        loginPanel = new JPanel(new GridLayout(2, 2, 1, 2));
        loginPanel.setBorder(defaultBorder);
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
        String userStr = "Online";

        JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
        userPanel.add(userLabel, BorderLayout.NORTH);
        userLabel.setFont(microsoftYaHeiUi);

        String[] emptyUserListStr = {"No other users"};
        setClientPanel(emptyUserListStr);

        clientPanel.setFont(microsoftYaHeiUi);
//        userPanel.add(getButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBorder(defaultBorder);

        return userPanel;
    }

    //Populate current user panel with a selectable list of currently connected users
    public void setClientPanel(String[] currClients) {
        clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();

        for (String s : currClients) {
            listModel.addElement(s);
        }

        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(microsoftYaHeiUi);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }

    public JPanel getButtonPanel() {
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(sendButton);

        return buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == exitButton) {
                // confirm to exit
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    return;
                }
            }

            // login
            if (e.getSource() == loginButton) {
                username = usernameField.getText();
                if (username.length() != 0) {
                    connectToServer(username);
                    if (!chatroomClient.connectionProblem) {
                        frameChatroom.setVisible(true);
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
                System.out.println("[Sending] " + message);
            }
        } catch (RemoteException remoteExc) {
            remoteExc.printStackTrace();
        }

    }

    //Send a message
    private void sendMessage(String chatMessage) throws RemoteException {
        chatroomClient.server.handleChatroomMsg(username, chatMessage);
    }

    // connect to chatroom server
    private void connectToServer(String userName) throws RemoteException {
        initChatroom(); // show chatroom
        //remove whitespace and non word characters to avoid malformed url
        String cleanedUserName;
        cleanedUserName = userName.replaceAll("\\W+", "_");
        try {
            chatroomClient = new ChatroomClient(this, cleanedUserName);
            boolean success = chatroomClient.startConnection();
            if (success) disposeLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}










