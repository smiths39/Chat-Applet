import java.io.*;
import java.net.*;

class ChatServer
{
    static Socket clientSocket = null;               		// client socket for the server
    static ServerSocket serverSocket = null;    	   		// server socket for the server

    static clientThread thread[] = new clientThread[10];  	 // allows up to 10 users in chat room      
    
    public static void main(String [] args) 
	{
		int portNumber = 9999;									 // Cannot choose a port less than 1023
		System.out.println("Using port number = " + portNumber);

        try 
		{
			serverSocket = new ServerSocket(portNumber); 		 // Open a server socket on port port_number
        }													
        catch (IOException e)
	    {
			System.out.println(e);
		}
	

		while(true)
		{
			try 												 // Create a socket object from the ServerSocket
			{			
				clientSocket = serverSocket.accept();			 // Listen and accept connection
				
				for(int i = 0; i < 10; i++)
				{
					if(thread[i] == null)						// if thread equals null, activate
					{
						(thread[i] = new clientThread(clientSocket,thread)).start();
						break;
					}
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
    }
} 

class clientThread extends Thread
{ 
    DataInputStream myDS = null;
    PrintStream myPS = null;
    Socket clientSocket = null;       
    clientThread thread[]; 
    
    public clientThread(Socket clientSocket, clientThread[] thread)
	{
		this.clientSocket = clientSocket;
        this.thread = thread;
    }
    
    public void run() 
    {
		String line;
        String name;
	
		try
		{
			myDS = new DataInputStream(clientSocket.getInputStream());
			myPS = new PrintStream(clientSocket.getOutputStream());
		
			name = myDS.readLine();
		
			for(int i = 0; i < 10; i++)
			{
				if (thread[i] != null && thread[i] != this)  
				{
					thread[i].myPS.println(name + " has entered the chat room..");
				}
			}
		
			while (true) 
			{
				line = myDS.readLine();
				if(line.startsWith("/quit"))	// needed to state that user has left
				{
					break; 
				}
		
				for(int i = 0; i < 10; i++)
				{
					if (thread[i] != null)  
					{
						thread[i].myPS.println("<" + name + "> " + line); 	// prints out conversation
					}
				}
			}
	    
			for(int i = 0; i <	10; i++)
			{
				if (thread[i] != null && thread[i] != this)  			// if an added user has just left the chat room
				{
					thread[i].myPS.println(name + " has left the chat room...");
				}
			}

			for(int i = 0; i < 10; i++)
			{
				if (thread[i] == this) 
				{
					thread[i] = null;  // Set to null the current thread variable such that other client could be accepted by the server
				}
			}
			
			myDS.close();			// close the output stream
			myPS.close();			// close the input stream
			clientSocket.close();	// close the socket
		}
		catch(IOException e){};
	}
}
