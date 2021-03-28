
// specify all imports and JAR files we will use
import com.sun.xml.bind.v2.runtime.reflect.Lister;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.text.Position;

public class PacketFactory implements Runnable {

	// create class variables
	static SocketClient sc = null;
	int delay = 0;
	//// check the value of whatever to toggle!!! - originally - 0 ///////////
	int whatever = 0;
	int waypoint_x = 0, waypoint_y = 0;
	boolean camRun = true;
	// multi-threading with reference to a queue of strings/commands
	Queue<Packet> buffer;
	String ip = "192.168.9.9";
	// String ip = "127.0.0.1";
	String PreviousPacket = null;

	int port = 8081;
	final static int forward_instruction = 1;
	final static int right_turn_instruction = 2;
	final static int left_turn_instruction = 3;
	final static int reverse_instruction = 4;
	public static final int calibrate_instruction = 5;
	final static int click_photo_instruction = 6;
	boolean explorationflag = false;
	final static byte UPDATESENSOR = 0x1;

	// method to reset the previous packet
	public void resetPreviousPacket() {
		PreviousPacket = null;
	}

	// method to set the previous packet
	public void setPreviousPacket(String Prev) {
		PreviousPacket = Prev;
	}

	// default constructor
	public PacketFactory() {
	}

	// parameterized constructor to connect to device and create buffer
	public PacketFactory(Queue<Packet> buffer) {
		sc = new SocketClient(ip, port);
		sc.connectToDevice();
		this.buffer = buffer;

	}

	// method to reconnect to device
	public void reconnectToDevice() {
		sc.closeConnection();
		sc.connectToDevice();
	}

	@Override
	public void run() {
		while (true) {
			listen();
		}

	}

	// start listening for packets from the socket
	public void listen() {
		boolean flag = true;
		String data = null;

		// float startTime = System.currentTimeMillis();
		// boolean alreadyRequestedSensor = false;

		// while no data received, keep probing for the packet
		while (data == null) {
			data = sc.receivePacket(explorationflag, PreviousPacket);

			////////////////////// Ritik - added code ///////////////////////
			// float currTime = System.currentTimeMillis();

			// // if no data received for more than 10 time_seconds, ask to send sensor data
			// if (currTime - startTime >= 5 * 1000 && !alreadyRequestedSensor) {
			// sendCMD("A:req:send_sensor$");
			// alreadyRequestedSensor = true;
			// System.out.println("Sent sensor request");

			// // can also try the following segment of code
			// // basically hardcoding sensor packet to no obstacles
			// // data = "P:map:sensor:[0,0,0,0,0,0]";
			// // break;
			// }
			//////////////////// added code ends here ///////////////////////
		}

		System.out.println("Receiving data: " + data);
		String removeDataString = data.replace("P:", "");
		if (explorationflag == false)
			processPacket(removeDataString);
		else
			recvSensorOrStop(removeDataString);

	}

	// process the packets received
	public void processPacket(String packetString) {
		String[] splitPacket = packetString.split(Packet.Splitter);
		if (splitPacket[1].equalsIgnoreCase(Packet.StartExplorationType)) {
			// send ok:exploration to android when exploration ends and robot is at the
			// start position
			System.out.println("starting exploration...");
			buffer.add(new Packet(Packet.start_exploration));
			System.out.print(
					"******************************************* Received Exploration Packet *********************************************\n");
			sc.sendPacket(Packet.StartExplorationTypeOk + "$");
			sc.sendPacket(Packet.StartExplorationTypeOkARD + ":" + whatever + "$");
			explorationflag = true;
		} else if (splitPacket[1].equalsIgnoreCase(Packet.StartFastestPathType)) {
			// send fastest path instruction stack at once
			// need to get x and y coordinates of the waypoint
			// inform android device that it is ready to move
			System.out.println(
					"***************************************** Received fastest path packet **************************************\n");

			buffer.add(new Packet(Packet.start_fastest_path));
		} else if (splitPacket[1].equalsIgnoreCase(Packet.Stop)) {
			// interrupt exploration, stop robot
			buffer.add(new Packet(Packet.stop_instruction));
			sc.sendPacket(Packet.StopOk);
		} else if (splitPacket[1].equalsIgnoreCase(Packet.Reset)) {
			// carry robot back to starting point.
			// reset map
			buffer.add(new Packet(Packet.reset_instruction));
			sc.sendPacket(Packet.ResetOK);
			System.out.println("sending ok reset");
		} else if (splitPacket[1].equals(Packet.GETMAP)) {
			buffer.add(new Packet(Packet.GETMAPi));
		} else if (splitPacket[0].equals(Packet.Set)) {
			if (splitPacket[1].equalsIgnoreCase(Packet.SetRobotPos)) {
				sc.sendPacket(Packet.SetRobotPosOk);
			} else if (splitPacket[1].equalsIgnoreCase(Packet.SetWayPoint)) {
				// remove bracket, split by comma , set first as x time_second as y
				String[] waypointcoord = splitPacket[2].replace("[", "").replace("]", "").split(",");
				int x = Integer.parseInt(waypointcoord[0]);
				int y = Integer.parseInt(waypointcoord[1]);
				// set robot position waypoint for the fastest path
				// after setting this, send all instructions to RPi
				// allow faster execution when android sends the command to start fastest path
				buffer.add(new Packet(Packet.set_way_point_instruction, x, y));
				sc.sendPacket("A:" + Packet.SetWayPointOK);
			}
		} else {
			System.out.println("Invalid string received...");
		}

	}

	public void recvSensorOrStop(String packetString) {
		System.out.println(
				"************************************* recvSensorOrStop called *********************************************\n");
		String[] commandSplit = packetString.split(Packet.Splitter);
		if (commandSplit[0].equalsIgnoreCase(Packet.Map)) {
			if (commandSplit[1].equalsIgnoreCase("sensor")) {
				int[] data = new int[6];
				String[] sensorData = commandSplit[2].replace(" ", "").replace("[", "").replace("]", "").split(",");
				for (int i = 0; i < sensorData.length; i++) {
					data[i] = Integer.parseInt(sensorData[i]);
				}
				buffer.add(new Packet(Packet.set_maze_obstacle, data));

			}
		} else if (commandSplit[1].equalsIgnoreCase(Packet.Stop)) {
			// interrupt exploration
			sc.sendPacket(Packet.StopOk);
			explorationflag = false;
			buffer.add(new Packet(Packet.stop_instruction));
		} else if (commandSplit[1].equalsIgnoreCase(Packet.Reset)) {
			// stop everything and carry robot back to starting point
			// reset map
			sc.sendPacket(Packet.ResetOK);
			System.out.println("sending ok reset");
			explorationflag = false;
			buffer.add(new Packet(Packet.reset_instruction));
		} else {
			System.out.println("Data received and ignored.");
		}
	}

	// get exploration flag
	public boolean getFlag() {
		return explorationflag;
	}

	// set exploration flag
	public void setFlag(boolean flag) {
		this.explorationflag = flag;
	}

	// get the last packet
	public Packet getLatestPacket() {
		if (buffer.isEmpty())
			return null;
		return buffer.remove();
	}

	public void sideTurnCalibrate() {
		sc.sendPacket(Packet.side_turn_calibrate);
		setPreviousPacket(Packet.side_turn_calibrate);
	}

	// for debugging purposes only
	public void sideCalibrate(int x, int y, int directionNum) {
		System.out.println("debug side calibrate");
		sc.sendPacket(Packet.side_calibrate + "$");
		setPreviousPacket(Packet.side_calibrate);
	}

	// for debugging purposes only
	public void frontCalibrate(int x, int y, int directionNum) {
		System.out.println("debug front calibrate");
		sc.sendPacket(Packet.front_calibrate + "$");
		setPreviousPacket(Packet.front_calibrate);
	}

	// for debugging purposes only
	public void leftCalibrate(int x, int y, int directionNum) {
		System.out.println("debug left calibrate");
		sc.sendPacket(Packet.left_calibrate);
		setPreviousPacket(Packet.left_calibrate);
	}

	public void initialCalibrate() {
		sc.sendPacket(Packet.iniital_calibrate);
	}

	public boolean createFullMovementPacketToArduino(Queue<Integer> instructions) {
		// create one whole command for multiple movements
		// send to both android and arduino
		String toSend = null;
		int count = 1;
		int temp = 0;
		if (instructions == null || instructions.isEmpty())
			return false;
		System.out.println("Sending instruction");
		int subinstruct = instructions.remove();

		// perform while an instruction exists
		while (!instructions.isEmpty()) {
			temp = instructions.remove();
			if (subinstruct == temp && count < 10 && subinstruct == Packet.forward_instruction) {
				count++;
				if (!instructions.isEmpty()) {
					continue;
				}
			}
			if (subinstruct == forward_instruction) {
				toSend = Packet.FORWARDCMD + Packet.Splitter + count + "$";
			} else if (subinstruct == right_turn_instruction) {
				toSend = Packet.TURNRIGHTCMD + Packet.Splitter + 0 + "$";
			} else if (subinstruct == left_turn_instruction) {
				toSend = Packet.TURNLEFTCMD + Packet.Splitter + 0 + "$";
			}
			sc.sendPacket(toSend);
			count = 1;

			// if all instructions have been processed
			if (instructions.isEmpty() && subinstruct != temp) {
				if (temp == forward_instruction) {
					toSend = Packet.FORWARDCMD + Packet.Splitter + count + "$";
				} else if (temp == right_turn_instruction) {
					toSend = Packet.TURNRIGHTCMD + Packet.Splitter + 0 + "$";
				} else if (temp == left_turn_instruction) {
					toSend = Packet.TURNLEFTCMD + Packet.Splitter + 0 + "$";
					System.out.println("Sending " + toSend + "...");
				}
				sc.sendPacket(toSend);
				break;
			}
			subinstruct = temp;
		}

		// check here, have removed the A:cmd:frontc sent in the end of fastest path
		sideTurnCalibrate();
		return true;
	}

	// get x coordinate of the waypoint
	public int getWaypoint_X() {
		return waypoint_x;
	}

	// get y coordinate of waypoint
	public int getWaypoint_Y() {
		return waypoint_y;
	}

	// send the whole map packet
	public void sendWholeMap(Map mapP) {
		// transpose array
		int[][] map = mapP.get_grid_map_array();
		String mapCmd = Packet.MAPDESCRIPTORCMD + "[";
		int[][] newMapArray = new int[Map.map_width][Map.map_height];
		for (int i = 0; i < Map.map_height; i++) {
			for (int j = 0; j < Map.map_width; j++) {
				newMapArray[j][i] = map[i][j];
			}
		}
		for (int i = 0; i < Map.map_width; i++) {
			mapCmd += Arrays.toString(newMapArray[i]);
			if (i != Map.map_width - 1)
				mapCmd += ",";
		}
		mapCmd += "]$";
		sc.sendPacket(mapCmd);
	}

	// send the map packet to the RPi
	public void sendWholeMapRpi(Map mapP) {
		// transpose array
		int[][] map = mapP.get_grid_map_array();
		String mapCmd = Packet.MAPDESCRIPTORCMDRPI + "[";
		int[][] newMapArray = new int[Map.map_width][Map.map_height];
		for (int i = 0; i < Map.map_height; i++) {
			for (int j = 0; j < Map.map_width; j++) {
				newMapArray[j][i] = map[i][j];
			}
		}
		for (int i = 0; i < Map.map_width; i++) {
			mapCmd += Arrays.toString(newMapArray[i]);
			if (i != Map.map_width - 1)
				mapCmd += ",";
		}
		mapCmd += "]$";
		sc.sendPacket(mapCmd);
	}

	public boolean isFacingWall(int x, int y, int directionNum) {

		if ((y == 1 && directionNum == 0) || // facing top wall
				(y == 18 && directionNum == 2) || // facing bottom wall
				(x == 1 && directionNum == 3) || // facing left wall
				(x == 13 && directionNum == 1)) // facing right wall
			return true;
		else
			return false;

	}

	public boolean createOneMovementPacketToArduino(int instruction, int x, int y, int directionNum) {
		// for one by one exploration, create one packet for a single movement
		// send to both android and arduino
		String instructionString = null;
		String instructionString2 = null;
		if (instruction == forward_instruction) {
			instructionString = Packet.FORWARDCMD;
			instructionString2 = Packet.FORWARDCMDANDROID;
			System.out.println("Sending a forward packet");
		} else if (instruction == right_turn_instruction) {
			instructionString = Packet.TURNRIGHTCMD;
			instructionString2 = Packet.TURNRIGHTCMDANDROID;
			System.out.println("Sending a turn right packet");
		} else if (instruction == left_turn_instruction) {
			instructionString = Packet.TURNLEFTCMD;
			instructionString2 = Packet.TURNLEFTCMDANDROID;
			System.out.println("Sending a turn left packet");
		} else if (instruction == reverse_instruction) {
			instructionString = Packet.REVERSECMD;
			instructionString2 = Packet.REVERSECMDANDROID;

			System.out.println("Sending a reverse packet");
		} else {
			System.out.println("Error: Wrong format");
			return false;
		}

		instructionString = instructionString + Packet.Splitter + "1" + "$";
		sc.sendPacket(instructionString);

		instructionString2 = instructionString2 + Packet.Splitter + "1" + "$";
		sc.sendPacket(instructionString2);

		setPreviousPacket(instructionString);
		return true;
	}

	// send command packet
	public void sendCMD(String cmd) {
		sc.sendPacket(cmd);
	}
}