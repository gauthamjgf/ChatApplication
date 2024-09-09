package application;

import javax.swing.*;
import java.util.Objects;
import java.lang.String;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CaptureView loginDialog = new CaptureView(null, true);
            loginDialog.setVisible(true);

            String ip = loginDialog.GetIP();
            String username = loginDialog.GetUsername();
            String chatroomID = loginDialog.GetRoomID();

            byte[] keyBytes = Security.getKeyBytes(loginDialog.getPassword());

            if(chatroomID.equals("Room ID")){
                System.out.println("Please enter a valid room ID");
                System.exit(0);
            }

            if(username.equals("Name")){
                System.out.println("Please enter your proper name");
                System.exit(0);
            }

            if(ip == null || username == null || chatroomID == null || keyBytes == null) {
                System.exit(0);
            }
            if(ip.isEmpty() || username.isEmpty() || chatroomID.isEmpty()){
                System.exit(0);
            }
            new Client(ip, username, chatroomID, keyBytes).setVisible(true);
        });
    }
}
