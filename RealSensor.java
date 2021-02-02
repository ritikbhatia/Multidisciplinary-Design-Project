//need check data

public class RealSensor extends Sensor {

	public RealSensor(int range, SensorLocation currentDirection, int locationOnRobot_x, int locationOnRobot_y,
			int robot_x, int robot_y) {
		super(range, currentDirection, locationOnRobot_x, locationOnRobot_y, robot_x, robot_y);
		// TODO Auto-generated constructor stub
	}

	public boolean SenseLocation(Map map, int x, int y, int distanceFromRobot, boolean hitWall) {
		int score = 0;

		if (distanceFromRobot == 1)
			score = -34;
		else if (distanceFromRobot == 2)
			score = -21;
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

		// System.out.println("X: "+x+"\tY: "+y+"\tScore: "+score);

		if (x < Map.WIDTH && y < Map.HEIGHT && x >= 0 && y >= 0) {
			// System.out.println("*******X: "+x+"\tY: "+y+"\tScore: "+score);
			// flip the score to positive to indicate that it is a block
			if (hitWall)
				score = -score;

			map.setMapScore(x, y, score);
		}

		return hitWall;
	}

	public boolean Sense(Map map, int data, int[][] mapConfirmed) {
		int nextLocationX = 0;
		int nextLocationY = 0;
		// boolean flag = false;

		// is true after robot hits a wall, to prevent it from sensing further
		boolean hitWall = false;
		boolean hitWallret = false;

		for (int i = 1; i <= range; i++) {

			// make sure it is in the map range and bound.
			if (currentDirection == SensorLocation.FACING_RIGHT) {
				nextLocationX = robot_x + locationOnRobot_x + i;
				nextLocationY = robot_y + locationOnRobot_y;
			}

			else if (currentDirection == SensorLocation.FACING_LEFT) {
				nextLocationX = robot_x + locationOnRobot_x - i;
				nextLocationY = robot_y + locationOnRobot_y;
			} else if (currentDirection == SensorLocation.FACING_TOP) {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y - i;
			} else {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y + i;
			}

			// System.out.println("next X: "+nextLocationX+"\tnext Y: "+nextLocationY +
			// "\ti:" + i + "\tdata:" + data);

			// hitwill will be true when sensor sensed a wall
			if (!hitWall) {
				// when the sensor sensed a wall, then everything after that will be given score
				// 0
				if (i == data) {
					hitWall = true;
					if (SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall) && i == 1)
						hitWallret = true;
					// hitWallret = true;
				}
				// System.out.println("-----------------Call sense location----------------");
				SenseLocation(map, nextLocationX, nextLocationY, i, hitWall);
			}
			// send a 0 to signify that this is behind a wall
			else
				SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall);
		}
		System.out.println(hitWall);

		// update the map score after "sensing"
		map.updateMapWithScore();

		return hitWallret;
	}

	public boolean SenseRight(Map map, int data, int[][] mapConfirmed) {
		int nextLocationX = 0;
		int nextLocationY = 0;
		// boolean flag = false;

		// is true after robot hits a wall, to prevent it from sensing further
		boolean hitWall = false;
		boolean hitWallret = false;

		for (int i = 1; i <= range; i++) {

			// make sure it is in the map range and bound.
			if (currentDirection == SensorLocation.FACING_RIGHT) {
				nextLocationX = robot_x + locationOnRobot_x + i;
				nextLocationY = robot_y + locationOnRobot_y;
			}

			else if (currentDirection == SensorLocation.FACING_LEFT) {
				nextLocationX = robot_x + locationOnRobot_x - i;
				nextLocationY = robot_y + locationOnRobot_y;
			} else if (currentDirection == SensorLocation.FACING_TOP) {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y - i;
			} else {
				nextLocationX = robot_x + locationOnRobot_x;
				nextLocationY = robot_y + locationOnRobot_y + i;
			}

			// hitwill will be true when sensor sensed a wall
			if (!hitWall) {
				// when the sensor sensed a wall, then everything after that will be given score
				// 0
				if (i == data) {
					hitWall = true;
					if (SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall) && i == 2)
						hitWallret = true;
					// hitWallret = true;
				}
				SenseLocation(map, nextLocationX, nextLocationY, i, hitWall);
			}
			// send a 0 to signify that this is behind a wall
			else
				SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall);
		}

		// update the map score after "sensing"
		map.updateMapWithScore();

		return hitWallret;
	}
}