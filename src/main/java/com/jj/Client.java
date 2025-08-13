package com.jj;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
    // Text field for receiving radius
    private JTextField jtf = new JTextField();

    // Text area to display contents
    private JTextArea jta = new JTextArea();

    // IO streams
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        // Panel p to hold the label and text field
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Enter item OR use REMOVE @=INDEX to delete "), BorderLayout.WEST);
        p.add(jtf, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jtf.setHorizontalAlignment(JTextField.RIGHT);

        setLayout(new BorderLayout());
        add(p, BorderLayout.NORTH);
        add(new JScrollPane(jta), BorderLayout.CENTER);

        jtf.addActionListener(new ButtonListener()); // Register listener
        setTitle("To-Do List Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        } catch (ConnectException ex) {
            jta.append(ex.toString() + "\nIs the server running?\n");
        } catch (IOException ex) {
            jta.append(ex.toString() + '\n');
        }
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String message = jtf.getText().trim();

                // Clear the input/output area when user sends a message
                jtf.setText("");
                jta.setText("");

                // Send the message to the server
                toServer.writeUTF(message);
                toServer.flush();

                if (message.isEmpty()) {
                    jta.append("ERROR: Cannot add empty message to list.");
                }

                // Read the list from the server
                int size = fromServer.readInt();

                for (int i = 0; i < size; i++) {
                    String item = fromServer.readUTF();
                    jta.append(i + ": " + item + "\n");
                }

            } catch (IOException ex) {
                jta.append("IO Exception: " + ex + '\n');
            }
        }
    }
}
