
// specifying imports
import java.util.HashMap;

public class Sensor {

	// declare class variables
	private static final int WIDTH = 15;
	private static final int HEIGHT = 20;
	int range;
	SensorLocation currentDirection;
	int locationOnRobot_x;
	int locationOnRobot_y;
	int robot_x;
	int robot_y;
	boolean hitWallCheck;
	int[] sensor_XY = new int[2];
	HashMap<int[], int[]> coordinatesLeft;
	HashMap<int[], int[]> coordinatesRight;

	// parameterized constructor to initialize Sensor
	public Sensor(int range, SensorLocation currentDirection, int locationOnRobot_x, int locationOnRobot_y, int robot_x,
			int robot_y) {
		super();
		this.range = range;
		this.currentDirection = currentDirection;
		this.locationOnRobot_x = locationOnRobot_x;
		this.locationOnRobot_y = locationOnRobot_y;
		this.robot_x = robot_x;
		this.robot_y = robot_y;
		this.hitWallCheck = false;
	}

	// set init direction
	public void initDirection() {
		switch (currentDirection) {
			case FACING_TOP:
				locationOnRobot_x = -1;
				locationOnRobot_y = 0;
				break;
			case FACING_TOPRIGHT:
				locationOnRobot_x = -1;
				locationOnRobot_y = 1;
				break;
			case FACING_RIGHT:
				locationOnRobot_x = 1;
				locationOnRobot_y = -1;
				break;
			case FACING_BOTTOMRIGHT:
				locationOnRobot_x = 1;
				locationOnRobot_y = 1;
				break;
			case FACING_DOWN:
				locationOnRobot_x = 1;
				locationOnRobot_y = 0;
				break;
			case FACING_BOTTOMLEFT:
				locationOnRobot_x = 1;
				locationOnRobot_y = -1;
				break;
			case FACING_LEFT:
				locationOnRobot_x = 0;
				locationOnRobot_y = -1;
				break;
			case FACING_TOPLEFT:
				locationOnRobot_x = -1;
				locationOnRobot_y = -1;
				break;
		}
	}

	// change direction to left
	public void ChangeDirectionLeft() {
		switch (currentDirection) {
			case FACING_TOP:
				currentDirection = currentDirection.FACING_LEFT;
				break;
			case FACING_TOPRIGHT:
				currentDirection = currentDirection.FACING_TOPLEFT;
				break;
			case FACING_RIGHT:
				currentDirection = currentDirection.FACING_TOP;
				break;
			case FACING_BOTTOMRIGHT:
				currentDirection = currentDirection.FACING_TOPRIGHT;
				break;
			case FACING_DOWN:
				currentDirection = currentDirection.FACING_RIGHT;
				break;
			case FACING_BOTTOMLEFT:
				currentDirection = currentDirection.FACING_BOTTOMRIGHT;
				break;
			case FACING_LEFT:
				currentDirection = currentDirection.FACING_DOWN;
				break;
			case FACING_TOPLEFT:
				currentDirection = currentDirection.FACING_BOTTOMLEFT;
				break;
		}

		if (locationOnRobot_x == 1 && locationOnRobot_y == -1) {
			locationOnRobot_x = -1;
			locationOnRobot_y = -1;
		} else if (locationOnRobot_x == 1 && locationOnRobot_y == 0) {
			locationOnRobot_x = 0;
			locationOnRobot_y = -1;
		} else if (locationOnRobot_x == 1 && locationOnRobot_y == 1) {
			locationOnRobot_x = 1;
			locationOnRobot_y = -1;
		} else if (locationOnRobot_x == 0 && locationOnRobot_y == 1) {
			locationOnRobot_x = 1;
			locationOnRobot_y = 0;
		} else if (locationOnRobot_x == -1 && locationOnRobot_y == 1) {
			locationOnRobot_x = 1;
			locationOnRobot_y = 1;
		} else if (locationOnRobot_x == -1 && locationOnRobot_y == 0) {
			locationOnRobot_x = 0;
			locationOnRobot_y = 1;
		} else if (locationOnRobot_x == -1 && locationOnRobot_y == -1) {
			locationOnRobot_x = -1;
			locationOnRobot_y = 1;
		} else if (locationOnRobot_x == 0 && locationOnRobot_y == -1) {
			locationOnRobot_x = -1;
			locationOnRobot_y = 0;
		}
	}

	// change direction to right
	public void ChangeDirectionRight() {
		switch (currentDirection) {
			case FACING_TOP:
				currentDirection = currentDirection.FACING_RIGHT;
				break;
			case FACING_TOPRIGHT:
				currentDirection = currentDirection.FACING_BOTTOMRIGHT;
				break;
			case FACING_RIGHT:
				currentDirection = currentDirection.FACING_DOWN;
				break;
			case FACING_BOTTOMRIGHT:
				currentDirection = currentDirection.FACING_BOTTOMLEFT;
				break;
			case FACING_DOWN:
				currentDirection = currentDirection.FACING_LEFT;
				break;
			case FACING_BOTTOMLEFT:
				currentDirection = currentDirection.FACING_TOPLEFT;
				break;
			case FACING_LEFT:
				currentDirection = currentDirection.FACING_TOP;
				break;
			case FACING_TOPLEFT:
				currentDirection = currentDirection.FACING_TOPRIGHT;
				break;
		}

		if (locationOnRobot_x == 1 && locationOnRobot_y == -1) {
			locationOnRobot_x = 1;
			locationOnRobot_y = 1;
		} else if (locationOnRobot_x == 1 && locationOnRobot_y == 0) {
			locationOnRobot_x = 0;
			locationOnRobot_y = 1;
		} else if (locationOnRobot_x == 1 && locationOnRobot_y == 1) {
			locationOnRobot_x = -1;
			locationOnRobot_y = 1;
		} else if (locationOnRobot_x == 0 && locationOnRobot_y == 1) {
			locationOnRobot_x = -1;
			locationOnRobot_y = 0;
		} else if (locationOnRobot_x == -1 && locationOnRobot_y == 1) {
			locationOnRobot_x = -1;
			locationOnRobot_y = -1;
		} else if (locationOnRobot_x == -1 && locationOnRobot_y == 0) {
			locationOnRobot_x = 0;
			locationOnRobot_y = -1;
		} else if (locationOnRobot_x == -1 && locationOnRobot_y == -1) {
			locationOnRobot_x = 1;
			locationOnRobot_y = -1;
		} else if (locationOnRobot_x == 0 && locationOnRobot_y == -1) {
			locationOnRobot_x = 1;
			locationOnRobot_y = 0;
		}

	}

	// update location of the robot
	public void updateRobotLocation(int x, int y) {
		robot_x = x;
		robot_y = y;
	}

	// make robot sense the location
	public boolean SenseLocation(Map map, int x, int y, int distanceFromRobot) {
		boolean hitWall = false;

		int score = 0;

		if (distanceFromRobot == 1)
			score = -70;
		else if (distanceFromRobot == 2)
			score = -40;
		else if (distanceFromRobot == 3)
			score = -8;
		else if (distanceFromRobot == 4)
			score = -5;
		else if (distanceFromRobot == 5)
			score = -2;
		else if (distanceFromRobot == 6)
			score = -2;
		else if (distanceFromRobot == 7)
			score = -2;
		else if (distanceFromRobot == 8)
			score = -2;
		else if (distanceFromRobot == 9)
			score = -2;

		if (x < WIDTH && y < HEIGHT && x >= 0 && y >= 0) {
			// make the score positive to indicate that it is a block
			if (map.SimulatedmapArray[y][x] == ExplorationTypes.toInt("OBSTACLE")
					|| map.SimulatedmapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {
				score = -score;
				hitWall = true;
				System.out.print(" X = " + x + " Y + " + y + " score \n");
			}
			map.setMapScore(x, y, score);
		} else
			hitWall = true;

		return hitWall;
	}

	public boolean Sense(Map map, int data, int[][] notWorkinghe) {
		// have to make sure does not overshoot boundary of environment
		int nextLocationX = 0;
		int nextLocationY = 0;

		// is true after robot hits a wall, to prevent further sensing
		boolean hitWall = false;
		boolean hitWallret = false;

		for (int i = 1; i < range + 1; i++) {
			// make sure it is in the map range and bound
			if (currentDirection == SensorLocation.FACING_RIGHT) {
				nextLocationX = robot_x + locationOnRobot_x + i;
				nextLocationY = robot_y + locationOnRobot_y;
			} else if (currentDirection == SensorLocation.FACING_LEFT) {
				nextLocationX = robot_x + locationOnRobot_x - i;
				nextLocationY = robot_y + locationOnRobot_y;
			} else if (currentDirection == SensorLocation.FACING_TOP) {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y - i;
			} else {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y + i;
			}

			// hitWall will be true when senselocation returns a true
			// that indicates a wall has been encountered
			if (!hitWall) {
				hitWall = SenseLocation(map, nextLocationX, nextLocationY, i);
				if (SenseLocation(map, nextLocationX, nextLocationY, 0) && i == 1)
					hitWallret = true;
			} else
				// send a 0 to signify that this is behind a wall
				SenseLocation(map, nextLocationX, nextLocationY, 0);
		}

		// update the map score after sensing
		map.updateMapWithScore();
		return hitWallret;
	}

	public boolean SenseRight(Map map, int data, int[][] notWorkinghe) {
		// make sure does not overshoot boundary of environment
		int nextLocationX = 0;
		int nextLocationY = 0;

		// is true after robot hits a wall, to prevent it from sensing further
		boolean hitWall = false;
		boolean hitWallret = false;

		for (int i = 1; i < range + 1; i++) {
			// make sure it is in the map range and bound
			if (currentDirection == SensorLocation.FACING_RIGHT) {
				nextLocationX = robot_x + locationOnRobot_x + i;
				nextLocationY = robot_y + locationOnRobot_y;
			} else if (currentDirection == SensorLocation.FACING_LEFT) {
				nextLocationX = robot_x + locationOnRobot_x - i;
				nextLocationY = robot_y + locationOnRobot_y;
			} else if (currentDirection == SensorLocation.FACING_TOP) {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y - i;
			} else {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y + i;
			}

			// hitWall will be true when senselocation returns a true
			// that is when a wall is encountered
			if (!hitWall) {
				hitWall = SenseLocation(map, nextLocationX, nextLocationY, i);
				if (SenseLocation(map, nextLocationX, nextLocationY, 0) && i == 2)
					hitWallret = true;
			} else
				// send a 0 to signify that this is behind a wall
				SenseLocation(map, nextLocationX, nextLocationY, 0);

		}
		// update the map score after sensing
		map.updateMapWithScore();
		return hitWallret;
	}
}