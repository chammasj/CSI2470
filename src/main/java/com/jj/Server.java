package com.jj;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server extends JFrame {
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        // Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("To-Do List Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            jta.append("Server started at " + new Date() + '\n');

            // Listen for a connection request
            Socket socket = serverSocket.accept();

            // Create data input and output streams
            DataInputStream inputFromClient = new DataInputStream(
                    socket.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(
                    socket.getOutputStream());

            ArrayList<String> list = new ArrayList<String>();
            Pattern pattern = Pattern.compile("REMOVE @=(\\d+)", Pattern.CASE_INSENSITIVE);

            while (true) {
                // Receive message from the client
                String message = inputFromClient.readUTF().strip();

                jta.append("Message received from client: " + message + '\n');

                // Check to see if Client is attempting to remove an item from the list
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    // Get capture group output (the number from the message)
                    int index = Integer.parseInt(matcher.group(1));
                    if (index > 0 && index < list.size()) {
                        list.remove(index);
                        jta.append("Removed item at index " + index + '\n');
                    } else {
                        jta.append("Could not remove item at index " + index + '\n');
                    }
                } else {
                    list.add(message);
                    jta.append("Added item to list: " + message + '\n');
                }

                // Send updated list back to client
                outputToClient.writeInt(list.size());
                for (String item : list) {
                    outputToClient.writeUTF(item);
                }
                outputToClient.flush();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
