// Server Chat program

l
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

public class Server
{
  // A running count of how many clients are currently chatting.
  private static int clientcount = 0;

  // A hash table with the list of Client sockets so that
  private static Set<PrintWriter> clients = new HashSet<>();

  // A hash table with all the client names.
  private static Set<String> names = new HashSet<>();

  // A hash table with the client names to let new clients know who they are talking with.
  private static Set<String> clientnames = new HashSet<>();

  // A class to handle the client threads.
  private static class ServerThread implements Runnable
  {
    private Scanner receive;
    private Socket client;
    private PrintWriter send;
    private String name;

    // Request name from client, and creates input and output streams.
    // Broadcasts messages between clients.
    public void run()
    {
      try
      {
        send = new PrintWriter(client.getOutputStream(), true);
        receive = new Scanner(client.getInputStream());

        // Request a name from the client.
        while (true)
        {
          send.println("Enter name");
          name = receive.nextLine();
          if (name == null)
          {
            return;
          }
          synchronized (names)
          {
            if (!name.isEmpty())
            {
              names.add(name);
              ++clientcount;
              System.out.println(name + " has joined the Chat Program.");
              break;
            }
          }
        }

        // Let's clients know that they have successfully joined the chat program.
        send.println("Name" + name);
        send.println("Client" + name + ", you have successfully joined the Chat Program.");

        // Let's the new client know if they are the first client or the name of the other clients connected.
        if (clientcount == 1)
        {
          send.println("Client" + "You are the first to enter the chat.  Waiting for additional clients.");
        }
        else
        {
          send.println("Client" + "You are chatting with:");

          for (String n : clientnames)
          {
            send.println("Client" + n);
          }
        }

        // Add client name to list to be used to let new clients know who is currently connected.
        clientnames.add(name);

        // Let currently connected clients know when a new client has joined the chat.
        for (PrintWriter c : clients)
        {
          c.println("Client" + name + " has joined");
        }

        // Add the new clients address to the clients hashtable.
        clients.add(send);

        // Accept messages from a client and send them to all the other clients.
        while (true)
        {
          String in = receive.nextLine();

          for (PrintWriter client : clients)
          {
            client.println("Client" + name + ": " + in);
          }
        }
      }
      catch (Exception e)
      {

      }
      finally
      {
        // Remove clients who leave the chat program.
        if (send != null)
        {
          clients.remove(send);
          --clientcount;
        }
        if (name != null)
        {
          System.out.println(name + " has left the Chat program.");
          names.remove(name);
          clientnames.remove(name);

          for (PrintWriter c : clients)
          {
            c.println("Client" + name + " has left the Chat Program.");
          }
        }
        try
        {
          client.close();
        }
        catch (IOException e)
        {
          System.out.println("IOException: " + e);
        }
      }
    }

    // Constructor to create a new client thread.
    public ServerThread(Socket client)
    {
      this.client = client;
    }
  }

  // Main program to set up server to listen and accept new clients.
  public static void main(String[] args)
  {
    System.out.println("Server Chat Program initiated.  Waiting for clients.");
    ExecutorService pool = Executors.newFixedThreadPool(200);

    try (ServerSocket listen = new ServerSocket(2345))
    {
      while (true)
      {
        pool.execute(new ServerThread(listen.accept()));
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception: " + e);
    }
  }
}
