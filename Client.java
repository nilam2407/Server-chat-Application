// Server Chat program


import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;


public class Client
{
  JFrame window = new JFrame("Server Chat Window - ");
  JTextArea msgArea = new JTextArea(16, 45);
  JTextField message = new JTextField(45);
  String serverPort;
  PrintWriter send;
  Scanner receive;

  /**
  /   Main program.  Choice to enter IP address or default to local host.
  /   Initialize chat window.
  /   Run program.
  */
  public static void main(String[] args)
  {
    try
    {
        Client client = new Client("localhost");
        client.window.setVisible(true);
        client.run();
    }
    catch (Exception e)
    {
      System.out.println("Exception: " + e);
    }
  }

  // Constructor to setup the GUI and Action Listener to send messages to the server.
  public Client(String serverPort)
  {
    this.serverPort = serverPort;

    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    msgArea.setEditable(false);
    message.setEditable(true);
    window.getContentPane().add(BorderLayout.SOUTH, message);
    window.getContentPane().add(BorderLayout.CENTER, new JScrollPane(msgArea));
    window.setLocationRelativeTo(null);
    window.pack();

    // Send on enter then clear to prepare for next message
    message.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        send.println(message.getText());
        message.setText("");
      }
    });
  }

  // Run function to establish socket address, input stream, and output stream.
  private void run()
  {
    try
    {
      Socket address = new Socket(serverPort, 2345);
      receive = new Scanner(address.getInputStream());
      send = new PrintWriter(address.getOutputStream(), true);

      while (receive.hasNextLine())
      {
        String line = receive.nextLine();
        if (line.startsWith("Enter"))
          send.println(getName());
        else if (line.startsWith("Name"))
        {
          this.window.setTitle("Server Chat Window - " + line.substring(4));
        }
        else if (line.startsWith("Client"))
          msgArea.append(line.substring(6) + "\n");
        }
     }
     catch (IOException i)
     {
       System.out.println("IOException: " + i);
     }
     finally
     {
        window.setVisible(false);
        window.dispose();
     }
   }

    // Get function to get client screen name.
    private String getName()
    {
      return JOptionPane.showInputDialog(window, "Enter name:", "", JOptionPane.PLAIN_MESSAGE);
    }
}
