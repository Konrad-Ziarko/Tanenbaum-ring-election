package election;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
	public static Main main;
	public final Lock lock = new ReentrantLock();
	// declare all the variables
	public Window gui;
	public int processNumber;
	public int portNumber;
	public int ipServerPort;
	public int coordinator;
	public ElectionThread thread = null;
	public IpServerThread ipThread = null;
	public ServerClass me;
	public ServerList servers = new ServerList();
	public boolean serversFlag = false;
	public ServerList newServers = new ServerList();
	//public boolean ipServerIsAlive = false;
	public ServerSocket servSocket; // declare a socket for server

	public ServerClass getMyNextProc(){
		ServerClass next = null;
		lock.lock();
		if (servers.size()==0&&newServers.size()==0)
			next = me;
		else if (serversFlag && servers.size()==0&&newServers.size()!=0){
			int idx = newServers.objectIdx(me);
			next = newServers.get((idx+1)%newServers.size());
		}
		else if (serversFlag && servers.size()!=0&&newServers.size()!=0){
			ServerClass tmp;
			int idx = servers.objectIdx(me);
			next = servers.get((idx+1)%servers.size());
			tmp = newServers.get((newServers.objectIdx(me)+1)%newServers.size());
			if (next.id ==tmp.id && next.ip.equals(tmp.ip)&&next.port==tmp.port){
				;
			}
			else{
				next = tmp;
			}
			servers = newServers;
			serversFlag = false;
		}else
		{
			next = servers.get((servers.objectIdx(me)+1)%servers.size());
		}
		lock.unlock();
		return next;
	}
	public ServerClass getProcNextProc(ServerClass nxt){
		ServerClass next = null;
		lock.lock();
		if (serversFlag && servers.size()==0&&newServers.size()!=0){
			int idx = newServers.objectIdx(nxt);
			next = newServers.get((idx+1)%newServers.size());
		}
		else if (serversFlag && servers.size()!=0&&newServers.size()!=0){
			ServerClass tmp;
			int idx = servers.objectIdx(nxt);
			next = servers.get((idx+1)%servers.size());
			tmp = newServers.get((newServers.objectIdx(nxt)+1)%newServers.size());
			if (next.id ==tmp.id && next.ip.equals(tmp.ip)&&next.port==tmp.port){
				;
			}
			else{
				next = tmp;
			}
			servers = newServers;
			serversFlag = false;
		}else
		{
			next = servers.get((servers.objectIdx(nxt)+1)%servers.size());
		}
		lock.unlock();
		return next;
	}
	public boolean checkPortAvailability() {
		ServerSocket tempSkt;
		try {
			tempSkt = new ServerSocket(portNumber);
			tempSkt.close();
			gui.textArea.append("Nasluchuje na porcie: " + portNumber + "\n");
			return true;
		} catch (IOException e) {
			gui.textArea.append("Port " + portNumber + "jest zajety!");
			return false;
		}
	}

	public void initiateElection(String token, ServerClass nextProc ) {
		try {
			Socket socket = new Socket(nextProc.ip, nextProc.port);// poprawic
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(token);
			gui.textArea.append("1.Wyslano do P" + nextProc.id + " : " + token + "\n");
			out.flush(); // flush the printwriter output
			out.close(); // close the printwriter
			socket.close(); // close the socket
		} catch (Exception ex) {
			gui.textArea.append("2.Nie udalo sie wyslac do P" + nextProc.id + " : " + token + "\n");
			ServerClass next = getProcNextProc(nextProc);
			initiateElection(token, next); // inform to next next if the next is unavailable
		}
	}

	public String getMyIP() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || iface.isPointToPoint())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					final String ip = addr.getHostAddress();
					if (Inet4Address.class == addr.getClass())
						return ip;
				}
			}
		} catch (SocketException e) {
			// throw new RuntimeException(e);
		}
		return null;
	}

	public void start() {
		gui = new Window();
		gui.textArea.append("Adres IP: " + getMyIP() + "\n");

		gui.btnStop.addActionListener(new ActionListener() {
			//@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// thread.stop();
				// ipThread.stop();
				ipThread.out.println("EXIT");
				thread.nextRun = false;
				ipThread.work = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				try {
					ipThread.socket.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				try {
					servSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				thread = null;
				ipThread = null;
				newServers = servers = new ServerList();
				servSocket = null;
				System.gc();
				
				gui.cNo.setText("brak");
				
				gui.btnRegister.setEnabled(true);
				gui.btnStart.setEnabled(false);
				gui.btnStop.setEnabled(false);
				gui.btnRefresh.setEnabled(false);

			}
		});

		gui.btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String token = "ALIVE";
				ServerClass next = servers.get((servers.objectIdx(me)+1)%servers.size());
				gui.textArea.append(
						"Wyslano do P" + next.ip + ":" + next.port + ":"
								+ next.id + " : " + token + " " +processNumber + "\n");
				sendPing(token + " " + processNumber, next);
			}
		});

		gui.btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkPortAvailability()) {
					// Initial process which starts the manual election
					gui.textArea.append("Elekcja: " + processNumber + "\n");
					String currToken = "ELECTION " + processNumber;
					gui.btnStart.setEnabled(false);
					gui.btnStop.setEnabled(true);
					gui.btnRefresh.setEnabled(true);
					gui.btnRegister.setEnabled(false);
					try {
						servSocket = new ServerSocket(portNumber);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					thread = new ElectionThread(processNumber, gui, servSocket);
					thread.start();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					ServerClass next = getMyNextProc();
					initiateElection(currToken, next);
				}
			}
		});
		gui.btnRegister.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				me = new ServerClass(getMyIP(), gui.portTextArea.getText(), gui.pNo.getText());
				gui.btnRegister.setEnabled(false);
				try {
					ipServerPort = Integer.parseInt(gui.ipSPort.getText());
					processNumber = Integer.parseInt(gui.pNo.getText());
					portNumber = Integer.parseInt(gui.portTextArea.getText());
					ipThread = new IpServerThread();
					ipThread.init(gui.ipS.getText(), portNumber, ipServerPort, processNumber, gui, main);
					ipThread.start();
					gui.btnStart.setEnabled(true);
				} catch (Exception e1) {
					gui.textArea.append("Nie mozna nawiazac polaczenia z baza adresow ip!\n");
					gui.btnRegister.setEnabled(true);
				}
			}
		});
	}

	class ElectionThread extends Thread {
		private int processNumber; // assign the process number
		private Window gui; // assign the frame
		private ServerSocket servSoc; // assign the socket
		private boolean nextRun = true;

		public ElectionThread(int processNumber, Window gui, ServerSocket servSoc) {
			this.processNumber = processNumber; // assign the process number
			this.gui = gui; // assign the frame
			this.servSoc = servSoc; // assign the socket
		}

		public void run() {
			while (nextRun) {
				Socket client = null;
				BufferedReader readBuff;
				String token = null;
				boolean canWork = true;
				
				try {
					client = servSoc.accept();
					readBuff = new BufferedReader(new InputStreamReader(client.getInputStream()));
					token = readBuff.readLine();
				} catch (IOException e) {
					canWork = nextRun = false;
					// e.printStackTrace();
				}
				if (canWork) {
					StringTokenizer stringTokenizer = new StringTokenizer(token);
					String tmp = stringTokenizer.nextToken();
					if (tmp.equalsIgnoreCase("ELECTION")) {
						gui.textArea.append("4.Odebrano wiadomosc: " + token + "\n");
						try {
							if (Integer.parseInt(stringTokenizer.nextToken()) == processNumber) {// message
																									// returned
								List<Integer> processes = new ArrayList<Integer>();
								processes.add(processNumber);
								while (stringTokenizer.hasMoreTokens()) {
									processes.add(Integer.parseInt(stringTokenizer.nextToken()));
								}
								int newCoordinator = -1;
								for (Integer i : processes) {
									if (newCoordinator < i)
										newCoordinator = i;
								}
								if (newCoordinator > -1) {
									coordinator = newCoordinator;
									String newToken = token.replaceAll("ELECTION", "COORDINATOR");
									ServerClass nextProc = getMyNextProc();
									gui.textArea.append("5.Wyslano do P" + nextProc.ip + ":"
											+ nextProc.port + ":" + nextProc.id + " : "
											+ newToken + "\n");
									sendToNextProcess(newToken, nextProc);
								}
							} else {// if not the initiated process add process number to the token
								ServerClass nextProc = getMyNextProc();
								gui.textArea.append("6.Wyslano do P" + nextProc.ip + ":"
										+ nextProc.port + ":" + nextProc.id + " : " + token
										+ " " + processNumber + "\n");
								sendToNextProcess(token + " " + processNumber, nextProc);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else if (tmp.equalsIgnoreCase("COORDINATOR")) {
						gui.textArea.append("7.Odebrano wiadomosc: " + token + "\n");
						ServerClass nextProc = getMyNextProc();
						List<Integer> processes = new ArrayList<Integer>();
						while (stringTokenizer.hasMoreTokens()) {
							processes.add(Integer.parseInt(stringTokenizer.nextToken()));
						}
						int newCoordinator = -1;
						for (Integer i : processes) {
							if (newCoordinator < i)
								newCoordinator = i;
						}
						coordinator = newCoordinator;
						gui.textArea.append("8.Nowy koordynator to: P" + coordinator + "\n");
						gui.cNo.setText(coordinator + "");
						if (processes.get(0) == (processNumber)) {// message
																	// returned
							gui.textArea.append("Zakonczono wybieranie koordynatora\n");
						} else {
							gui.textArea.append(
									"9.Wyslano do P" + nextProc.ip + ":" + nextProc.port + ":"
											+ nextProc.id + " : " + token + "\n");
							sendToNextProcess(token, nextProc);
						}
					} else if (tmp.equalsIgnoreCase("ALIVE")) {
						gui.textArea.append("Odebrano wiadomosc: " + token + "\n");
						ServerClass nextProc = getMyNextProc();
						if (Integer.parseInt(stringTokenizer.nextToken()) == processNumber) {
							gui.textArea.append("Zakonczono Ping\n");
						}else{
							gui.textArea.append(
									"Wyslano do P" + nextProc.ip + ":" + nextProc.port + ":"
											+ nextProc.id + " : " + token + " " +processNumber + "\n");
							sendPing(token + " " + processNumber , nextProc);
						}
						
					}
					if (client != null)
						try {
							client.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				if (this.isInterrupted()) {
					nextRun = false;
				}
				if (client != null)
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		private void sendToNextProcess(String token, ServerClass proc) {
			try {
				Socket socket = new Socket(proc.ip, proc.port);
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				out.println(token);
				out.flush();
				out.close();
				socket.close();
			} catch (Exception ex) {
				gui.textArea.append("11.Nie udalo sie wyslac do" + proc.ip + ":"
						+ proc.port + ":" + proc.id + " : " + token + "\n");
				proc = getProcNextProc(proc);
				gui.textArea.append("12.Wysylam do" + proc.ip + ":"
						+ proc.port + ":"
						+ proc.id + " : " + token + "\n");
				sendToNextProcess(token, proc);
			}
		}
	}
	private void sendPing(String token, ServerClass nextProc) {
		try {
			Socket socket = new Socket(nextProc.ip, nextProc.port);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(token);
			out.flush();
			out.close();
			socket.close();
		} catch (Exception ex) {
			ipThread.out.println("PING");
			gui.textArea.append("11.Nie udalo sie wyslac do" + nextProc.ip + ":"
					+ nextProc.port + ":" + nextProc.id + " : " + token + "\n");
			ServerClass next = getProcNextProc(nextProc);
			initiateElection("ELECTION " + processNumber, next);
		}
	}
	
	public static void main(String[] args) {
		main = new Main();
		main.start();
	}

}