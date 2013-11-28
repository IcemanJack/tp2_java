package connect4.client;

import java.io.IOException;

import connect4.server.IMyServer;
import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

/* TODO
 * Make view with console log, so we print server responses directly.
 * Add extends client?
 */
public class ClientController
{
	Client client;
	IMyServer myRemoteObject;
	CallHandler callHandler;
	
	String serverIP;
	int serverPort;
	IModelListener view;
	
	String username;
	
	public ClientController()
	{
		serverIP = "127.0.0.1";
		serverPort = 12345;
		username = "MadJack";
		
		startClient();
		makeCustomView();
	}
	
	public ClientController(String serverIP, int serverPort, String username)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.username = username;
		
		startClient();
		makeCustomView();
	}
	
	public void makeMove(final int row, final int column)
	{
		System.out.println("Starting move from client");
		String serverResponse = myRemoteObject.makeMove(column, row, username);
		System.out.println(serverResponse);
	}
	
	public void updateNotAvailableUsername(String username)
	{
		this.username = username;
	}
	
	public void quitTheGame()
	{
		disconnectClient();
		System.exit(0);
	}
	
	private void makeCustomView()
	{
		view = new View(this);
		try 
		{
			callHandler.registerGlobal(IModelListener.class, view);
		}
		catch (LipeRMIException e) 
		{
			e.printStackTrace();
		}
		
		String serverResponse = myRemoteObject.registerListener(username, view);
		System.out.println(serverResponse);
	}
	
	private void startClient()
	{
		callHandler = new CallHandler();
		try 
		{
			client  = new Client(serverIP, serverPort, callHandler);
			myRemoteObject = (IMyServer) client.getGlobal(IMyServer.class);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.out.println("Client started");
	}
	
	private void disconnectClient()
	{
		myRemoteObject.unregisterListener(username);
		try 
		{
			client.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Client disconnected");
	}
}
