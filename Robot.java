
// specifying imports
import java.util.Stack;

public class Robot extends RobotInterface {

	// declare class variables
	Sensor[] Sen;
	boolean hitWallFront = false;
	boolean hitWallRight = false;
	int sideCalibrateCount = 0;
	int frontCalibrateCount = 0;
	int sideCalibrateNum = 3;
	int FrontCalibrateNum = 3;
	float stepsPerSecond = 1f;
	boolean frontCalibrated = false;
	boolean sideCalibrated = false;

	// parameterized contructor to initialize robot variables
	public Robot(int x, int y, Direction facing, Map map) {
		// starting postition
		super();
		this.x = x;
		this.y = y;
		this.facing = facing;
		this.map = map;
		hitWallFront = false;
		hitWallRight = false;
		instructionsForFastestPath = new Stack<Integer>();
		SenseRobotLocation();
	}

	// add sensors to the dummy robot
	public void addSensors(Sensor[] sensors) {
		this.Sen = sensors;
		// sense the surrounding
		LookAtSurroundings();
	}

	// update robot sensors with the new location
	public void updateSensor() {
		for (int i = 0; i < Sen.length; i++) {
			Sen[i].updateRobotLocation(x, y);
		}
	}

	// deactivate sensors
	public void deactivateSensors() {
		Sen = new Sensor[0];
	}

	// specify robot speed as steps per second
	public void setSpeed(float stepsPerSecond) {
		this.stepsPerSecond = stepsPerSecond;
	}

	// move robot
	public void moveRobot() {
		int movementDistance = 1;
		if (facing == Direction.UP)
			y -= movementDistance;
		else if (facing == Direction.DOWN)
			y += movementDistance;
		else if (facing == Direction.RIGHT)
			x += movementDistance;
		else if (facing == Direction.LEFT)
			x -= movementDistance;

		// update the location for the robot in the sensors
		updateSensor();

		// make robot sense surroundings
		LookAtSurroundings();

	}

	// make robot reverse
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

		// update the location for the robot in the sensors
		updateSensor();

		// make robot sense surrounding
		LookAtSurroundings();

	}

	// method to allow to robot to sense the surrounding
	public void LookAtSurroundings() {
		boolean sensePlaceHolder;
		boolean sensePlaceHolder1;
		int countF = 0;
		int countR = 0;

		for (int i = 0; i < Sen.length; i++) {
			sensePlaceHolder = Sen[i].Sense(map, 0, null);
			sensePlaceHolder1 = Sen[i].SenseRight(map, 0, null);

			// front wall hit
			if ((i <= 1 || i == 3) && sensePlaceHolder) {
				countF++;
				if (countF == 3)
					hitWallFront = true;
				else
					hitWallFront = false;
			}

			// right wall hit
			if ((i == 2 || i == 4) && sensePlaceHolder1) {
				countR++;
				if (countR == 2)
					hitWallRight = true;
				else
					hitWallRight = false;
			}
		}
		System.out.println(countR);
		if (hitWallFront && hitWallRight) {
			System.out.println(
					"************************************ Both walls hit *************************************\n");

			// calibrate both front and side
			front_Calibrate();
			side_Calibrate();
			hitWallFront = false;
			hitWallRight = false;
		}
	}

	public void SenseRobotLocation() {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++)
				map.getMapArray()[y + i][x + j] = ExplorationTypes.toInt("EMPTY");
		}
	}

	// make the robot turn right
	public void turnRight() {
		switch (facing) {
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
		LookAtSurroundings();
	}

	// make the robot turn left
	public void turnLeft() {
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
		}
		// change sensor direction to follow robot
		for (int i = 0; i < Sen.length; i++) {
			Sen[i].ChangeDirectionLeft();
		}
		LookAtSurroundings();
	}

	// get fastest path instructions
	public boolean getFastestInstruction(Stack<Node> fast) {
		byte[] instruction = new byte[100];
		int instcount = 0;
		if (fast == null)
			return true;
		while (!fast.isEmpty()) {
			Node two = (Node) fast.pop();
			try {
				Thread.sleep((long) (1000 / stepsPerSecond));
				if (two.getX() > x) {
					while (facing != Direction.RIGHT) {
						turnRight();
					}
					moveRobot();
				} else if (two.getX() < x) {
					while (facing != Direction.LEFT) {
						turnLeft();
					}
					moveRobot();
				} else if (two.getY() < y) {
					while (facing != Direction.UP) {
						turnLeft();
					}
					moveRobot();
				} else { // if(two.getY() < one.getY())
					while (facing != Direction.DOWN) {
						turnRight();
					}
					moveRobot();
				}
				viz.repaint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	// perform step indicated in the fastest path
	public boolean doStepFastestPath() {

		// if no more instructions, then fastest path is performed
		if (instructionsForFastestPath.isEmpty())
			return true;
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
							System.out.println("Front calibrating\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
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
	public void initial_Calibrate() {
	}

	@Override
	public void sendMapDescriptor() {
		// sendWholeMap(map);
	}

	@Override
	public void side_Calibrate() {
		System.out.println("Side calibrating");

	}

	@Override
	public void left_Calibrate() {
		System.out.println("Left Calibrate");
	}

	@Override
	public void front_Calibrate() {
		System.out.println("Front calibrating");
	}

	@Override
	public void addSensors(RealSensor[] sensors) {
	}

	@Override
	public boolean isObstacleOrWallFront() {
		return false;
	}

	@Override
	public boolean getWantToReset() {
		return false;
	}
}
