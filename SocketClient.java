import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

public class SocketClient {
	InetAddress host;
	Socket socket = null;
	DataInputStream input = null;
	PrintStream out = null;
	String IP_Addr;
	int Port;
	boolean firstflag = false;

	public SocketClient(String IP_Addr, int Port) {
		this.IP_Addr = IP_Addr;
		this.Port = Port;
	}

	public boolean connectToDevice() {
		int timeout = 6000;
		try {
			if (socket != null) {
				if (socket.isConnected()) {
					return true;
				}
			}
			InetSocketAddress ISA = new InetSocketAddress(IP_Addr, Port);
			socket = new Socket();
			socket.connect(ISA, timeout);
			System.out.println("Connected");
			// takes input from terminal

			input = new DataInputStream(socket.getInputStream());
			// sends output to the socket
			out = new PrintStream(socket.getOutputStream());
			return true;
		} catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}
		return false;

	}

	// sends packet data to rpi to be relayed or processed
	public int sendPacket(String packetData) {
		try {
			System.out.println("Sending packetData...");
			System.out.println(packetData);
			out.print(packetData);
			System.out.println("Packet sent.");
			out.flush();
			return 0;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Sending Error: " + e);
			e.printStackTrace();
			while (socket.isClosed()) {
				System.out.println("socket is not connected... reconnecting..");
				connectToDevice();
			}
			System.out.println("resending packet.");
			sendPacket(packetData);

		}
		return 0;
	}

	public String receivePacket(boolean resentflag, String Data) {
		String instruction = null;
		try {

			// need to rethink this.
			do {

				long timestart = System.currentTimeMillis();
				while (input.available() == 0) {
					Thread.sleep(10);
				}
				instruction = input.readLine();
			} while (instruction == null || instruction.equalsIgnoreCase(""));
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Receiving Error: " + e);
			e.printStackTrace();
			connectToDevice();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instruction;
	}

	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}