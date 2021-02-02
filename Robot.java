import java.util.Stack;

public class Robot extends RobotInterface {
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
	// ArrayList<Node> TraverseNodes = new ArrayList();

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

	public void addSensors(Sensor[] sensors) {
		this.Sen = sensors;

		// make sensors "sense" the surrounding
		LookAtSurroundings();
	}

	public void updateSensor() {
		for (int i = 0; i < Sen.length; i++) {
			Sen[i].updateRobotLocation(x, y);
		}
	}

	public void deactivateSensors() {
		Sen = new Sensor[0];
	}

	public void setSpeed(float stepsPerSecond) {
		this.stepsPerSecond = stepsPerSecond;
	}

	public void moveRobot() {
		// System.out.print("moving forward\n");
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

		// make sensors "sense" the surrounding
		LookAtSurroundings();

	}

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

		// make sensors "sense" the surrounding
		LookAtSurroundings();

	}

	public void LookAtSurroundings() {
		boolean sensePlaceHolder;
		boolean sensePlaceHolder1;
		int countF = 0;
		int countR = 0;
		// SenseRobotLocation();
		for (int i = 0; i < Sen.length; i++) {
			sensePlaceHolder = Sen[i].Sense(map, 0, null);
			sensePlaceHolder1 = Sen[i].SenseRight(map, 0, null);
			if ((i <= 1 || i == 3) && sensePlaceHolder) {
				countF++;
				if (countF == 3)
					hitWallFront = true;
				else
					hitWallFront = false;
			}
			// System.out.println("!!!!!!!!!!!!!!!!!!!!!Front Wall hit
			// !!!!!!!!!!!!!!!!!!!!\n");}
			if ((i == 2 || i == 4) && sensePlaceHolder1) {
				countR++;
				if (countR == 2)
					hitWallRight = true;
				else
					hitWallRight = false;
			}
			// System.out.println("?????????????????????Right wall hit
			// ????????????????????\n");
		}
		System.out.println(countR);
		if (hitWallFront && hitWallRight) {
			System.out
					.println(":::::::::::::::::::::::::::::::::::hit both walls::::::::::::::::::::::::::::::::::::\n");
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
		/*
		 * mapArray[y][x] = ExplorationTypes.toInt("EMPTY"); mapArray[y][x-1] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y][x+1] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y-1][x] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y-1][x+1] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y-1][x-1] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y+1][x] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y+1][x-1] =
		 * ExplorationTypes.toInt("EMPTY");; mapArray[y+1][x+1] =
		 * ExplorationTypes.toInt("EMPTY");;
		 */
	}

	public void turnRight() {
		// System.out.print("turn right\n");
		// use this to change direction for robot instead
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
		// make sensors "sense" the surrounding
		LookAtSurroundings();
	}

	public void turnLeft() {
		// System.out.print("turn left\n");
		// use this to change direction for robot instead
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
		// make sensors "sense" the surrounding
		LookAtSurroundings();
	}

	public boolean getFastestInstruction(Stack<Node> fast) {
		byte[] instruction = new byte[100];
		int instcount = 0;
		if (fast == null)
			return true;
		while (!fast.isEmpty()) {
			Node two = (Node) fast.pop();
			try {
				Thread.sleep((long) (1000 / stepsPerSecond));

				// System.out.println("Y" + two.getY());
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
				} else /* if(two.getY() < one.getY()) */ {
					while (facing != Direction.DOWN) {
						turnRight();
					}
					moveRobot();
				}
				viz.repaint();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean doStepFastestPath() {
		// once the instructions are empty, current fastest path is done
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
					// System.out.print("turning left" + x + y + '\n');
					break;
				case Packet.TURNLEFTi:
					turnLeft();
					// System.out.print("turning right" + x + y + '\n');
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
						// else sideCalibrateCount++;
					}
					if (frontCalibrateCount >= FrontCalibrateNum) {
						if (canFront_Calibrate()) {
							System.out.println("Front calibrating\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
							front_Calibrate();
							frontCalibrateCount = 0;
							frontCalibrated = true;
						}
						// else frontCalibrateCount++;
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
	public void addSensors(RealSensor[] sensors) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isObstacleOrWallFront() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getWantToReset() {
		return false;
	}

	@Override
	public void initial_Calibrate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMapDescriptor() {
		// sendWholeMap(map);
		// TODO Auto-generated method stub

	}

	@Override
	public void side_Calibrate() {
		System.out.println("Side calibrating");
		// TODO Auto-generated method stub

	}

	@Override
	public void left_Calibrate() {
		System.out.println("Left Calibrate");
		// TODO Auto-generated method stub
	}

	@Override
	public void front_Calibrate() {
		System.out.println("Front calibrating");
		// TODO Auto-generated method stub

	}
}
