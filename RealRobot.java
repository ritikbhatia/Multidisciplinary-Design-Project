
// specify all imports
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class RealRobot extends RobotInterface {
	// declare class variables
	boolean wantToReset = false;
	RealSensor[] Sen;
	PacketFactory pf = null;
	int directionNum = -1;
	int delay = 150;
	int scdelay = 0;
	int sideCalibrateCount = 0;
	int frontCalibrateCount = 0;
	int sideCalibrateNum = 3;
	int FrontCalibrateNum = 3;
	float stepsPerSecond = 10f;
	boolean sideCalibrated = false;
	boolean frontCalibrated = false;
	boolean stepByStep = true;
	boolean fastestcalibrate = false; // needs to calibrate for fastest
	int count = 0;
	int numsteps = 0;
	int[][] mapConfirmed = new int[][] { { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 1, 1 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 1, 1 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 1, 1 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }, { 1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 },
			{ 1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 } };

	// method to set robot spped (steps per second)
	public void setSpeed(float stepsPerSecond) {
		this.stepsPerSecond = stepsPerSecond;
	}

	// parameterized constructor to initialize real robot
	public RealRobot(int x, int y, Direction facing, Map map, PacketFactory pf) {
		super();
		this.pf = pf;
		this.x = x;
		this.y = y;
		this.facing = facing;
		this.map = map;
		SenseRobotLocation();
	}

	// retrieve direction (integer, as enum)
	public int getDirectionNum() {
		switch (facing) {
		case UP:
			return 0;
		case RIGHT:
			return 1;
		case DOWN:
			return 2;
		case LEFT:
			return 3;
		default:
			return -1;
		}
	}

	// add sensors to the robot by initalizing it
	public void addSensors(RealSensor[] sensors) {
		this.Sen = sensors;
	}

	// update robot sensors
	public void updateSensor() {
		for (int i = 0; i < Sen.length; i++) {
			Sen[i].updateRobotLocation(x, y);
		}
	}

	// deactivate robot sensors by re-assigning array
	public void deactivateSensors() {
		Sen = new RealSensor[0];
	}

	// move the robot
	public void moveRobot() {
		numsteps = 1;
		System.out.print("moving robot\n");
		int movementDistance = 1;

		if (facing == Direction.UP) {
			y -= movementDistance;
			mapConfirmed[y + 1][x - 1] = 1;
			mapConfirmed[y + 1][x] = 1;
			mapConfirmed[y + 1][x + 1] = 1;
		} else if (facing == Direction.DOWN) {
			y += movementDistance;
			mapConfirmed[y - 1][x - 1] = 1;
			mapConfirmed[y - 1][x] = 1;
			mapConfirmed[y - 1][x + 1] = 1;
		} else if (facing == Direction.RIGHT) {
			x += movementDistance;
			mapConfirmed[y][x - 1] = 1;
			mapConfirmed[y][x] = 1;
			mapConfirmed[y][x + 1] = 1;
		} else if (facing == Direction.LEFT) {
			x -= movementDistance;
			mapConfirmed[y][x - 1] = 1;
			mapConfirmed[y][x] = 1;
			mapConfirmed[y][x + 1] = 1;
		}

		if (stepByStep) {
			count++;
			if (count % 4 == 0) {
				sendMapDescriptor();
			}
			pf.createOneMovementPacketToArduino(Packet.FORWARDi, x, y, getDirectionNum());
			// update the location for the robot in the sensors
			updateSensor();
			// make sensors sense the surrounding
			LookAtSurroundings();
		}
		// call repaint method as robot has moved
		viz.repaint();
	}

	// make the robot go in reverse direction
	public void reverse() {
		int movementDistance = 1;

		if (facing == Direction.UP)
			y += movementDistance;
		else if (facing == Direction.DOWN)
			y -= movementDistance;
		else if (facing == Direction.RIGHT)
			x -= movementDistance;
		else if (facing == Direction.LEFT)
			x += movementDistance;

		if (stepByStep) {
			pf.createOneMovementPacketToArduino(Packet.REVERSEi, x, y, getDirectionNum());
			// update the location for the robot in the sensors
			updateSensor();
			// make sensors sense the surrounding
			LookAtSurroundings();
		}
		// call repaint method as robot had gone in reverse direction (changed position)
		viz.repaint();
	}

	public boolean getWantToReset() {
		return this.wantToReset;
	}

	// make robot sense surrounding, through packets
	public void LookAtSurroundings() {
		Packet pck = null;
		System.out.println("Waiting for Sensor Packets");
		while (pck == null || pck.type != Packet.setObstacle) {
			pf.listen();
			System.out.println(
					"++++++++++++++++++++++++++++++++++++++ Dequeues buffer ++++++++++++++++++++++++++++++++++++++++++\n");
			pck = pf.getLatestPacket();
			if (pck == null) {
				System.out.println(
						"++++++++++++++++++++++++++++++++++++++ Packet is Null (Need to Reset Instruction) +++++++++++++++++++++++++++++++++++++++++++\n");
				continue;
			}
			System.out.println(pck.getType());
			if (pck.type == Packet.ResetInstruction) {
				this.wantToReset = true;
				this.map.resetMap();
				x = 1;
				y = 18;
				facing = Direction.RIGHT;
				this.viz.repaint();
				return;
			}
		}
		System.out.println(
				"+++++++++++++++++++++++++++++++++++++ Getting Sensor Data +++++++++++++++++++++++++++++++++++++++\n");
		int[] data = pck.getSensorData();
		for (int i = 0; i < Sen.length; i++) {
			Sen[i].Sense(map, data[i], mapConfirmed);
		}
		viz.repaint();
	}

	// make the robot turn right
	public void turnRight() {
		System.out.print("turn right robot\n");
		switch (facing) {
		// turn right
		case RIGHT:
			facing = Direction.DOWN;
			break;
		case LEFT:
			facing = Direction.UP;
			break;
		case UP:
			facing = Direction.RIGHT;
			break;
		case DOWN:
			facing = Direction.LEFT;
			break;
		}

		for (int i = 0; i < Sen.length; i++) {
			Sen[i].ChangeDirectionRight();
		}
		if (stepByStep) {
			pf.createOneMovementPacketToArduino(Packet.TURNRIGHTi, x, y, getDirectionNum());
			// update location for the robot in the sensors
			updateSensor();
			// make sensors sense the surrounding
			LookAtSurroundings();
		}
		// call repaint as robot has moved right
		viz.repaint();
	}

	// make robot turn left
	public void turnLeft() {
		System.out.print("turn left robot\n");

		switch (facing) {
		case RIGHT:
			facing = Direction.UP;
			break;
		case LEFT:
			facing = Direction.DOWN;
			break;
		case UP:
			facing = Direction.LEFT;
			break;
		case DOWN:
			facing = Direction.RIGHT;
			break;
		}
		// change sensor direction to follow robot
		for (int i = 0; i < Sen.length; i++) {
			Sen[i].ChangeDirectionLeft();
		}
		if (stepByStep) {
			pf.createOneMovementPacketToArduino(Packet.TURNLEFTi, x, y, getDirectionNum());
			// update the location for the robot in the sensors
			updateSensor();
			// make sensors "sense" the surrounding
			LookAtSurroundings();
		}
		// call repaint as robot has moved left
		viz.repaint();
	}

	// make robot sense location
	public void SenseRobotLocation() {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++)
				map.getMapArray()[y + i][x + j] = ExplorationTypes.toInt("EMPTY");
		}
	}

	// get fastest path instructions
	public boolean getFastestInstruction(Stack<Node> fast) {
		stepByStep = false;

		///////////// check here whether you need calibration!!! ////////////////////
		int numberofPacketCalibrate = 10;
		int counttocalibrate = 0;
		Queue<Integer> instruction = new LinkedList<Integer>();
		System.out.println("starting Fastest Path");

		if (fast == null) {
			System.out.println("NULL DATA! no fastest path.");
			return false;
		}

		// while there are instructions in the fastest path stack
		while (!fast.isEmpty()) {
			Node two = (Node) fast.pop();
			counttocalibrate++;
			System.out.println("Y" + two.getY());
			if (two.getX() > x) {
				switch (facing) {
				// turn right the fastest way
				case RIGHT:
					break;
				case LEFT:
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					break;
				case UP:
					turnRight();
					instruction.add(Packet.TURNRIGHTi);
					break;
				case DOWN:
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					break;
				}

			} else if (two.getX() < x) {
				switch (facing) {
				// turn right the fastest way
				// to face left
				case RIGHT:
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					break;
				case LEFT:
					break;
				case UP:
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					break;
				case DOWN:
					turnRight();
					instruction.add(Packet.TURNRIGHTi);
					break;
				}

			} else if (two.getY() < y) {
				// facing up
				switch (facing) {
				// to face up
				case RIGHT:
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					break;
				case LEFT:
					turnRight();
					instruction.add(Packet.TURNRIGHTi);
					break;
				case UP:
					break;
				case DOWN:
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					turnLeft();
					instruction.add(Packet.TURNLEFTi);
					break;
				}

			} else if (two.getY() > y) {
				while (facing != Direction.DOWN) {
					switch (facing) {
					// turn right the fastest way
					case RIGHT:
						turnRight();
						instruction.add(Packet.TURNRIGHTi);
						break;
					case LEFT:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case UP:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case DOWN:
						break;
					}
				}

			}

			moveRobot();
			instruction.add(Packet.FORWARDi);

		}
		stepByStep = true;
		pf.createFullMovementPacketToArduino(instruction);// sends data.
		return true;

	}

	public boolean calibrateWallFastestPath() {
		boolean thirdflag = false;
		boolean fourthflag = false;
		boolean fifthflag = false;
		switch (facing) {
		case RIGHT:
			if (isBlocked(x - 1, y - 2))
				thirdflag = true;
			if (isBlocked(x, y - 2))
				fourthflag = true;
			if (isBlocked(x + 1, y - 2))
				fifthflag = true;
			break;
		case LEFT:
			if (isBlocked(x - 1, y + 2))
				thirdflag = true;
			if (isBlocked(x, y + 2))
				fourthflag = true;
			if (isBlocked(x + 1, y + 2))
				fifthflag = true;
			break;
		case UP:
			if (isBlocked(x + 2, y - 1))
				thirdflag = true;
			if (isBlocked(x + 2, y))
				fourthflag = true;
			if (isBlocked(x + 2, y + 1))
				fifthflag = true;
			break;
		case DOWN:
			if (isBlocked(x - 2, y - 1))
				thirdflag = true;
			if (isBlocked(x - 2, y))
				fourthflag = true;
			if (isBlocked(x - 2, y + 1))
				fifthflag = true;
			break;
		}
		if (thirdflag && fourthflag && fifthflag) {
			return true;
		}
		return false;

	}

	public void finalIC() {
		switch (facing) {
		case RIGHT:
			break;
		case LEFT:
			turnLeft();
			turnLeft();
			break;
		case UP:
			turnRight();
			break;
		case DOWN:
			turnLeft();
			break;
		}
		pf.initialCalibrate();
	}

	// perform fastest path step
	public boolean doStepFastestPath() {
		if (instructionsForFastestPath.isEmpty())
			return true;
		// if not empty then continue doing the path
		else {
			frontCalibrated = false;
			sideCalibrated = false;
			int instruction = (Integer) instructionsForFastestPath.remove(0);
			switch (instruction) {
			case Packet.TURNRIGHTi:
				turnRight();
				break;
			case Packet.TURNLEFTi:
				turnLeft();
				break;
			case Packet.FORWARDi:
				if (sideCalibrateCount >= sideCalibrateNum) {
					if (canSide_Calibrate()) {
						System.out.println("Right calibrating\n+++++++++++++++++++++++++++++++++");
						side_Calibrate();
						sideCalibrateCount = 0;
					} else if (canLeft_Calibrate()) {
						System.out.println("Left calibrating\n---------------------------------");
						left_Calibrate();
						sideCalibrateCount = 0;
					}
					sideCalibrated = true;
				}
				if (frontCalibrateCount >= FrontCalibrateNum) {
					if (canFront_Calibrate()) {
						front_Calibrate();
						frontCalibrateCount = 0;
						frontCalibrated = true;
					}
				}
				if (!frontCalibrated)
					frontCalibrateCount++;
				if (!sideCalibrated)
					sideCalibrateCount++;
				moveRobot();
				break;
			}
		}
		return false;
	}

	@Override
	public void addSensors(Sensor[] sensors) {
	}

	@Override
	public void side_Calibrate() {
		pf.sideCalibrate(x, y, (getDirectionNum() + 1) % 4);
		try {
			Thread.sleep((int) (scdelay));
		} catch (Exception e) {
			e.printStackTrace();
		}
		LookAtSurroundings();
	}

	@Override
	public void front_Calibrate() {
		System.out.println("front calibrating");
		pf.frontCalibrate(x, y, (getDirectionNum() + 1) % 4);
		LookAtSurroundings();
	}

	@Override
	public void left_Calibrate() {
		System.out.println("left calibrating");
		pf.leftCalibrate(x, y, (getDirectionNum() - 1) % 4);
		LookAtSurroundings();
	}

	@Override
	public void initial_Calibrate() {
		switch (facing) {
		// ensure the robot is facing the left wall for calibration
		case RIGHT:
			turnLeft();
			turnLeft();
			break;
		case LEFT:
			break;
		case UP:
			turnLeft();
			break;
		case DOWN:
			turnRight();
			break;

		}
		pf.initialCalibrate();
		System.out.println(
				"######################################### Initial Calibration... #########################################");
		facing = Direction.RIGHT;
		// update the android orientation
		String instructionString2 = Packet.TURNLEFTCMDANDROID + Packet.Splitter + "1" + "$";
		pf.sendCMD(instructionString2);
		pf.sendCMD(instructionString2);
		viz.repaint();

	}

	// send whole map
	@Override
	public void sendMapDescriptor() {
		pf.sendWholeMap(map);

		/////////////////// check if you want to add this!! /////////////////////////
		// MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
		// MapIterator.printExploredResultsToHex("ExplorationHex.txt");
		// MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
		// MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
		// pf.sendCMD("B:stat:Exploration mdf:" + MapIterator.mapDescriptorP1Hex + "$");
		// pf.sendCMD("B:stat:Obstacle mdf:" + MapIterator.mapDescriptorP2Hex + "$");
	}

	// send map descriptor to the RPi
	public void sendMapDescriptorRpi() {
		pf.sendWholeMapRpi(map);
	}
}