package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;


public class Client extends JFrame {
    private String ip;
    private String username;
    private String chatroomID;

    private SecretKey secretKey;

    private JTextField messageField;
    private JButton sendButton;
    private JLabel chatroomTitle;
    private JButton logoutButton;
    private JTextArea messagePanel;
    private JScrollPane jScrollPane1;

    public Timestamp loginTime;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;

    public Client(String ip, String username, String chatroomID, byte[] keyBytes) {
        this.ip = ip;
        this.username = username;
        this.chatroomID = chatroomID;
        this.secretKey= new SecretKeySpec(keyBytes, "AES");
        this.loginTime = new Timestamp(System.currentTimeMillis());
        initializeUI();
        connectToServer();
    }

    private void initializeUI() {
        setTitle("Chat Application");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatroomTitle = new JLabel();
        chatroomTitle.setFont(new Font("Noto Sans", Font.PLAIN, 36));
        chatroomTitle.setForeground(new Color(120, 120, 120));
        chatroomTitle.setText("Chatroom: " + chatroomID);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        messagePanel = new JTextArea();
        messagePanel.setEditable(false);
        messagePanel.setLineWrap(true);
        messagePanel.setWrapStyleWord(true);
        jScrollPane1 = new JScrollPane(messagePanel);

        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                sendButtonActionPerformed(evt);
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(messageField)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendButton, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(chatroomTitle)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                                                .addComponent(logoutButton, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(chatroomTitle)
                                        .addComponent(logoutButton))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(messageField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sendButton))
                                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
        messageField.requestFocusInWindow();
    }

    private void connectToServer() {
        try {
            socket = new Socket(ip, 8080);
            System.out.println("Connected to the server.");

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        ChatMessage message = (ChatMessage) inputStream.readObject();
                        if (message.getChatroomID().equals(chatroomID)) {
                            String decryptedMessage = Security.decrypt(message.getMessage(), secretKey);
//                            if(decryptedMessage.equals("decryption failed, password incorrect")) {
//                                if (loginTime.after(message.getTimestamp())) {
//                                    decryptedMessage = "This message was sent before you joined the chatroom. You cannot decrypt it.";
//                                    JOptionPane.showMessageDialog(null, "Decryption failed. Password incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
//                                    System.exit(1); // Exit the application with error status
//                                }
//                            }
                            messagePanel.append(message.getSender() + ": " + decryptedMessage + "\n");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String userMessage = messageField.getText();
            String encryptedMessage = Security.encrypt(userMessage, secretKey);
            if(userMessage.isEmpty()) return;
            ChatMessage chatMessage = new ChatMessage(username, encryptedMessage, chatroomID, loginTime);
            outputStream.writeObject(chatMessage);
            outputStream.flush();
            messageField.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void logoutButtonActionPerformed(ActionEvent evt) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.dispose();
        System.exit(0);
    }

    private void sendButtonActionPerformed(ActionEvent evt) {
        sendMessage();
    }

    public static void main(String args[]) {
        System.out.println("You are not supposed to run this file. Run Main.java instead.");
        System.exit(0);
    }
}
