public class Packet {
	
	// Map instructions together 
	final static char ARDUINO = 'A';
	final static char ANDROID = 'B';
	final static char PC = 'P';
	final static char RPI = 'R';

	// Define type of packets
	final static int StartExploration = 1;
	final static int StartFastestPath = 2;
	final static int StopInstruction = 3;
	final static int ResetInstruction = 4;
	final static int SetWayPointi = 5;
	final static int setRobotPosition = 6;
	final static int setObstacle = 7;
	final static int takePhoto = 8;

	// Mapping to packet instructions
	final static int FORWARDi = 1;
	final static int TURNRIGHTi = 2;
	final static int TURNLEFTi = 3;
	final static int REVERSEi = 4;
	public static final int CALIBRATEi = 5;
	final static int PHOTOi = 6;

	// Robot movements
	final static String FORWARD = "forward";
	final static String TURNRIGHT = "right";
	final static String TURNLEFT = "left";
	final static String REVERSE = "reverse";

	// Exploration instrcutions from Android
	final static String Ok = "ok";
	final static String Cmd = "cmd";
	final static String Set = "set";
	final static String Splitter = ":";
	final static String Stat = "stat";

	final static String StartExplorationType = "explore";
	final static String StartExplorationTypeOkARD = "A:ok:start_explore";
	final static String StartExplorationTypeOk = "B:ok:start_explore";
	final static String StartExplorationTypeFin = "B:ok:finish_explore"; // After returning back to start point

	// Fastest Path instrcutions from Android
	final static String StartFastestPathType = "path";// Includes waypoint
	final static String StartFastestPathTypeOk = "start_path"; // Transmit to Android
	final static String StartFastestPathTypeFin = "finish_path"; // Transmit to Android
	final static String startExplore = ANDROID + Splitter + Ok + Splitter + StartExplorationTypeOk;

	// RPi: Click picture command (with direction & X,Y coordinates)
	final static String PhotoPacket = "cam"; 

	// Android: Stop robot
	final static String Stop = "stop";// Transmit from Android
	final static String StopOk = "B:ok:stop";// Transmit to Android

	final static String Reset = "reset"; // Transmit from Android
	final static String ResetOK = "B:ok:reset";// Transmit to Android

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

	final static String TURNLEFTCMDANDROID = ANDROID + Splitter + Stat + Splitter + TURNLEFT;
	final static String TURNRIGHTCMDANDROID = ANDROID + Splitter + Stat + Splitter + TURNRIGHT;
	final static String FORWARDCMDANDROID = ANDROID + Splitter + Stat + Splitter + FORWARD;
	final static String REVERSECMDANDROID = ANDROID + Splitter + Stat + Splitter + REVERSE;

	final static String SIDECALIBRATE = "A:cmd:sc"; 
	final static String LEFTCALIBRATE = "A:cmd:lsc";
	final static String FRONTCALIBRATE = "A:cmd:fc";
	final static String INITIALCALIBRATE = "A:cmd:ic";
	final static String SIDETURNCALIBRATE = "A:cmd:frontc";

	final static String ACKKNOWLEDGE = "P:cmd:ack";

	final static String MAPDESCRIPTORCMD = "B:map:set:";
	final static String MAPDESCRIPTORCMDRPI = "D:map:set:";

	final static int GETMAPi = 10;

	final static String GETMAP = "getmap";

	final static String StartFastestPathTypeOkANDROID = "A:ok:start_path"; // Transmit to Android
	final static String StartFastestPathTypeOkARDURINO = "B:ok:start_path"; // Transmit to Android

	int type = 0;
	int x = 0;
	int y = 0;
	Direction Direction = null;
	int[] SensorData = null;

	// Handle packets with only one type
	public Packet(int type) {
		this.type = type;
	}

	// Waypoint data
	public Packet(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	// Robot position and facing direction
	public Packet(int type, int x, int y, Direction direction) {
		this.type = type;
		this.x = x;
		this.y = y;
		Direction = direction;
	}

	// Navigating Grid Obstacles 
	public Packet(int type, int x, int y, Direction direction, int[] sensorData) {
		super();
		this.type = type;
		this.x = x;
		this.y = y;
		Direction = direction;
		SensorData = sensorData;
	}

	public Packet(int setobstacle2, int[] data) {
		this.type = setobstacle2;
		this.SensorData = data;
	}

	public int[] getSensorData() {
		return SensorData;
	}

	public void setSensorData(int[] sensorData) {
		SensorData = sensorData;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Direction getDirection() {
		return Direction;
	}

	public void setDirection(Direction direction) {
		Direction = direction;
	}

}
