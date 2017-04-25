package election;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class IpServerThread extends Thread {
	String host;
	int portNumber, processNumber, ipServerPort;
	Window gui;
	Socket socket = null;
	Main main;
	public ServerList newServers = new ServerList();
	public boolean work = true;
	public PrintWriter out;

	BufferedReader input = null;
	
	
	public void init(String host, int portNumber, int ipServerPort, int processNumber, Window gui, Main main) {
		this.host = host;
		this.portNumber = portNumber; // assign the port number
		this.ipServerPort = ipServerPort;
		this.processNumber = processNumber; // assign the process number
		this.gui = gui; // assign the frame
		this.main = main;
	}

	public void run() {
		try {
			socket = new Socket(host, ipServerPort);
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			
		} catch (IOException e) {
			
			//e.printStackTrace();
		}
		
		if (socket!=null){
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(socket.getLocalAddress().toString().replace("/", "") + ":" + portNumber + ":" + processNumber);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while (work) {
					try{
						String answer = input.readLine();
						main.lock.lock();
						if (answer.equalsIgnoreCase("Zle ID")) {
							gui.btnStart.setEnabled(false);
							gui.btnStop.setEnabled(false);
							gui.btnRegister.setEnabled(true);
							gui.textArea.append("Takie id jest juz w systemie\n");
							break;
						}
						// JOptionPane.showMessageDialog(null, answer, "InfoBox: ",
						// JOptionPane.WARNING_MESSAGE);
						gui.textArea.append("Aktualizacja listy znanych serwerow: " + answer.replace("/", "\n") + "\n");
						String[] ips = answer.split("/");
						newServers = new ServerList();
						for (String string : ips) {
							String[] host = string.split(":");
							newServers.add(new ServerClass(host[0], host[1], host[2]));
						}
						
						main.newServers = newServers;
						main.serversFlag = true;
						main.lock.unlock();
					} catch (IOException e) {
						//e.printStackTrace();
						work=false;
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					out.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
