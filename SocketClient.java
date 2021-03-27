
// specify all imports
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

public class SocketClient {

	// define variables to be used like host IP, input stream, socket to use etc
	InetAddress host;
	Socket socket = null;
	DataInputStream input = null;
	PrintStream out = null;
	String IP_Addr;
	int Port;
	boolean firstflag = false;

	// paramterized constructor to initiate socket client using IP and the port
	public SocketClient(String IP_Addr, int Port) {
		this.IP_Addr = IP_Addr;
		this.Port = Port;
	}

	// method to connect to RPi device
	// return true if connection was successful else return false
	public boolean connectToDevice() {

		// specify a timeout
		// if connection not established within this time, return false
		int timeout = 6000;
		try {
			// if socket is already connected, return true
			if (socket != null) {
				if (socket.isConnected()) {
					return true;
				}
			}

			// establish socket connection
			InetSocketAddress ISA = new InetSocketAddress(IP_Addr, Port);
			socket = new Socket();
			socket.connect(ISA, timeout);
			System.out.println("Connected");

			// specify input and output streams
			input = new DataInputStream(socket.getInputStream());
			out = new PrintStream(socket.getOutputStream());
			return true;
		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}
		return false;

	}

	// send packet data to RPi for further processing
	public int sendPacket(String packetData) {
		try {
			System.out.println("Sending packetData...");
			System.out.println(packetData);
			out.print(packetData);
			System.out.println("Packet sent");
			out.flush();
			return 0;
		} catch (Exception e) {
			System.out.println("Sending Error: " + e);
			e.printStackTrace();

			// re-establish connection if socket is not connected
			while (socket.isClosed()) {
				System.out.println("socket is not connected... reconnecting..");
				connectToDevice();
			}

			// attempt to send packet again
			System.out.println("Resending packet...");
			sendPacket(packetData);
		}
		return 0;
	}

	// receive packet
	public String receivePacket(boolean resentflag, String Data) {
		String instruction = null;
		// boolean alreadySent = false;
		try {
			do {
				long timestart = System.currentTimeMillis();
				while (input.available() == 0) {
					// if (System.currentTimeMillis() - timestart >= 10 * 1000 && !alreadySent) {
					// sendPacket("A:req:send_sensor:1$");
					// alreadySent = true;
					// }
					Thread.sleep(10);
				}
				instruction = input.readLine();
			} while (instruction == null || instruction.equalsIgnoreCase(""));
		} catch (IOException e) {
			System.out.println("Receiving Error: " + e);
			e.printStackTrace();
			connectToDevice();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return instruction;
	}

	// close socket connection
	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}