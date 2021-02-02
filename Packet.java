public class Packet {
	// for mapping amount of instructions together

	final static char ARDUINO = 'A';
	final static char ANDROID = 'B';
	final static char PC = 'P';
	final static char RPI = 'R';

	// packet types
	final static int StartExploration = 1;
	final static int StartFastestPath = 2;
	final static int StopInstruction = 3;
	final static int ResetInstruction = 4;
	final static int SetWayPointi = 5;
	final static int setRobotPosition = 6;
	final static int setObstacle = 7;
	final static int takePhoto = 8;

	// instruction to map to packet Instruction.
	final static int FORWARDi = 1;
	final static int TURNRIGHTi = 2;
	final static int TURNLEFTi = 3;
	final static int REVERSEi = 4;
	public static final int CALIBRATEi = 5;
	final static int PHOTOi = 6;

	// for movement of the robot.
	final static String FORWARD = "forward";
	final static String TURNRIGHT = "right";
	final static String TURNLEFT = "left";
	final static String REVERSE = "reverse";

	// start exploration from android
	final static String Ok = "ok";
	final static String Cmd = "cmd";
	final static String Set = "set";
	final static String Splitter = ":";
	final static String Stat = "stat";

	final static String StartExplorationType = "explore";
	final static String StartExplorationTypeOkARD = "A:ok:start_explore";
	final static String StartExplorationTypeOk = "B:ok:start_explore";
	final static String StartExplorationTypeFin = "B:ok:finish_explore"; // after going back to start location

	// start fastestpath from android
	final static String StartFastestPathType = "path";// with waypoint from android
	final static String StartFastestPathTypeOk = "start_path"; // send to android
	final static String StartFastestPathTypeFin = "finish_path"; // send to android
	final static String startExplore = ANDROID + Splitter + Ok + Splitter + StartExplorationTypeOk;

	// Send take photo command to rpi
	final static String PhotoPacket = "cam"; // sends the command to take pictures along with x,y co-ordinates and
												// direction
	/*
	 * final static String sendPhotoCmd = RPI + Splitter + Ok + PhotoPacket +
	 * photoData
	 */

	// stop the robot from android
	final static String Stop = "stop";// send from android
	final static String StopOk = "B:ok:stop";// send to android

	final static String Reset = "reset"; // from android
	final static String ResetOK = "B:ok:reset";// send to android

	// need to process this string to become x y coordinate of robot in map
	final static String SetRobotPos = "startposition";
	final static String SetRobotPosOk = "B:ok:startposition";
	final static String SetWayPoint = "waypoint";
	final static String SetWayPointOK = "ok:waypoint";

	// for map obstacle
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

	final static String SIDECALIBRATE = "A:cmd:sc"; // TODO: @Jarrett removed $
	final static String LEFTCALIBRATE = "A:cmd:lsc";
	final static String FRONTCALIBRATE = "A:cmd:fc";
	final static String INITIALCALIBRATE = "A:cmd:ic";
	final static String SIDETURNCALIBRATE = "A:cmd:frontc";

	final static String ACKKNOWLEDGE = "P:cmd:ack";

	final static String MAPDESCRIPTORCMD = "B:map:set:";
	final static String MAPDESCRIPTORCMDRPI = "D:map:set:";

	final static int GETMAPi = 10;

	final static String GETMAP = "getmap";

	final static String StartFastestPathTypeOkANDROID = "A:ok:start_path"; // send to android
	final static String StartFastestPathTypeOkARDURINO = "B:ok:start_path"; // send to android

	int type = 0;
	int x = 0;
	int y = 0;
	Direction Direction = null;
	int[] SensorData = null;

	public Packet(int type) {
		// for packet with only type
		this.type = type;
	}

	public Packet(int type, int x, int y) {
		// for waypoint packets
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public Packet(int type, int x, int y, Direction direction) {
		// for robot position with direction
		this.type = type;
		this.x = x;
		this.y = y;
		Direction = direction;
	}

	public Packet(int type, int x, int y, Direction direction, int[] sensorData) {
		// for map obstacle data
		super();
		this.type = type;
		this.x = x;
		this.y = y;
		Direction = direction;
		SensorData = sensorData;
	}

	public Packet(int setobstacle2, int[] data) {
		// TODO Auto-generated constructor stub
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