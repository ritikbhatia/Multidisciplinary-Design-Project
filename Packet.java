public class Packet {

	int packet_type = 0;
	int x = 0;
	int y = 0;
	Direction Direction = null;
	int[] sensors_data = null;

	// Map instructions together
	final static char ARDUINO = 'A';
	final static char ANDROID = 'B';
	final static char PC = 'P';
	final static char RPI = 'R';

	// Mapping to packet instructions
	public static final int calibrate_instruction = 5;
	final static int click_photo_instruction = 6;
	final static int forward_instruction = 1;
	final static int right_turn_instruction = 2;
	final static int left_turn_instruction = 3;
	final static int reverse_instruction = 4;

	// Define packet_type of packets
	final static int set_way_point_instruction = 5;
	final static int set_robot_position = 6;
	final static int set_maze_obstacle = 7;
	final static int start_exploration = 1;
	final static int start_fastest_path = 2;
	final static int stop_instruction = 3;
	final static int reset_instruction = 4;

	// Exploration instrcutions from Android
	final static String Ok = "ok";
	final static String Cmd = "cmd";
	final static String Set = "set";
	final static String Splitter = ":";
	final static String Stat = "stat";

	// Robot movements
	final static String FORWARD = "forward";
	final static String TURNRIGHT = "right";
	final static String TURNLEFT = "left";
	final static String REVERSE = "reverse";

	final static String side_calibrate = "A:cmd:sc";
	final static String left_calibrate = "A:cmd:lsc";
	final static String front_calibrate = "A:cmd:fc";
	final static String iniital_calibrate = "A:cmd:ic";
	final static String side_turn_calibrate = "A:cmd:frontc";

	final static String StartExplorationTypeOk = "B:ok:start_explore";
	final static String StartExplorationTypeFin = "B:ok:finish_explore"; // After returning back to start point
	final static String StartExplorationType = "explore";
	final static String StartExplorationTypeOkARD = "A:ok:start_explore";

	final static String ACKKNOWLEDGE = "P:cmd:ack";

	// RPi: Click picture command (with direction & X,Y coordinates)
	final static String PhotoPacket = "cam";

	// Android: Stop robot
	final static String Reset = "reset"; // Transmit from Android
	final static String ResetOK = "B:ok:reset";// Transmit to Android

	final static String Stop = "stop";// Transmit from Android
	final static String StopOk = "B:ok:stop";// Transmit to Android
	
	// Fastest Path instrcutions from Android
	final static String StartFastestPathType = "path";// Includes waypoint
	final static String StartFastestPathTypeOk = "start_path"; // Transmit to Android
	final static String StartFastestPathTypeFin = "B:ok:finish_path"; // Transmit to Android
	final static String startExplore = ANDROID + Splitter + Ok + Splitter + StartExplorationTypeOk;

	final static String TURNLEFTCMDANDROID = ANDROID + Splitter + Stat + Splitter + TURNLEFT;
	final static String TURNRIGHTCMDANDROID = ANDROID + Splitter + Stat + Splitter + TURNRIGHT;
	final static String FORWARDCMDANDROID = ANDROID + Splitter + Stat + Splitter + FORWARD;
	final static String REVERSECMDANDROID = ANDROID + Splitter + Stat + Splitter + REVERSE;

	// Process string to configure robot to this X,Y coordinates on map
	final static String SetRobotPos = "startposition";
	final static String SetRobotPosOk = "B:ok:startposition";
	final static String SetWayPoint = "waypoint";
	final static String SetWayPointOK = "ok:waypoint";

	// Map Obstacles
	final static String Map = "map";
	final static String Block = "block";

	final static String TURNLEFTCMD = ARDUINO + Splitter + Cmd + Splitter + TURNLEFT;
	final static String TURNRIGHTCMD = ARDUINO + Splitter + Cmd + Splitter + TURNRIGHT;
	final static String FORWARDCMD = ARDUINO + Splitter + Cmd + Splitter + FORWARD;
	final static String REVERSECMD = ARDUINO + Splitter + Cmd + Splitter + REVERSE;

	final static int GETMAPi = 10;
	final static String GETMAP = "getmap";

	final static String MAPDESCRIPTORCMD = "B:map:set:";
	final static String MAPDESCRIPTORCMDRPI = "D:map:set:";

	final static String StartFastestPathTypeOkANDROID = "A:ok:start_path"; // Transmit to Android
	final static String StartFastestPathTypeOkARDURINO = "B:ok:start_path"; // Transmit to Android

	public Direction getDirection() {
		return Direction;
	}

	public void setDirection(Direction direction) {
		Direction = direction;
	}

	// Handle packets with only one packet_type
	public Packet(int packet_type) {
		this.packet_type = packet_type;
	}

	// Waypoint data
	public Packet(int packet_type, int x, int y) {
		this.packet_type = packet_type;
		this.x = x;
		this.y = y;
	}

	// Navigating Grid Obstacles
	public Packet(int packet_type, int x, int y, Direction direction, int[] sensorData) {
		super();
		this.packet_type = packet_type;
		this.x = x;
		this.y = y;
		Direction = direction;
		sensors_data = sensorData;
	}


	// Robot position and facing direction
	public Packet(int packet_type, int x, int y, Direction direction) {
		this.packet_type = packet_type;
		this.x = x;
		this.y = y;
		Direction = direction;
	}

	public Packet(int setobstacle2, int[] data) {
		this.packet_type = setobstacle2;
		this.sensors_data = data;
	}

	public int[] getSensorData() {
		return sensors_data;
	}

	public void setSensorData(int[] sensorData) {
		sensors_data = sensorData;
	}

	public int getType() {
		return packet_type;
	}

	public void setType(int packet_type) {
		this.packet_type = packet_type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

}
