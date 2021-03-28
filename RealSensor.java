public class RealSensor extends Sensor {

	// parameterized constructor to initialize real sensor
	public RealSensor(int range, SensorLocation currentDirection, int locationOnRobot_x, int locationOnRobot_y,
			int robot_x, int robot_y) {
		super(range, currentDirection, locationOnRobot_x, locationOnRobot_y, robot_x, robot_y);
	}

	// method to sense location
	public boolean SenseLocation(Map map, int x, int y, int distanceFromRobot, boolean hitWall) {
		int score = 0;

		// conditional statements to assign score
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

		if (x < Map.map_width && y < Map.map_height && x >= 0 && y >= 0) {
			// assign a positive score if wall hit
			// this prevents robot to go there since expensive
			if (hitWall)
				score = -score;
			map.setMapScore(x, y, score);
		}

		return hitWall;
	}

	public boolean Sense(Map map, int data, int[][] mapConfirmed) {
		int nextLocationX = 0;
		int nextLocationY = 0;

		// is true after robot hits a wall to prevent further sensing
		boolean hitWall = false;
		boolean hitWallret = false;

		for (int i = 1; i <= range; i++) {
			// ensure it is within the map
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

			// hitWall will be true when sensor senses a wall
			if (!hitWall) {
				// when the sensor senses wall, then everything after that will be given score 0
				if (i == data) {
					hitWall = true;
					if (SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall) && i == 1)
						hitWallret = true;
				}
				SenseLocation(map, nextLocationX, nextLocationY, i, hitWall);
			} else
				// send a 0 to signify that this is behind a wall
				SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall);
		}
		System.out.println(hitWall);

		// update the map score after "sensing"
		map.update_map_and_score();

		return hitWallret;
	}

	public boolean SenseRight(Map map, int data, int[][] mapConfirmed) {
		int nextLocationX = 0;
		int nextLocationY = 0;

		// is true after robot hits a wall, to prevent it from sensing further
		boolean hitWall = false;
		boolean hitWallret = false;

		for (int i = 1; i <= range; i++) {
			// ensure it is within the map
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

			// hitWall will be true when sensor sensed a wall
			if (!hitWall) {
				// when the sensor senses wall, then everything after that will be given score 0
				if (i == data) {
					hitWall = true;
					if (SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall) && i == 2)
						hitWallret = true;
				}
				SenseLocation(map, nextLocationX, nextLocationY, i, hitWall);
			} else
				// send a 0 to signify that this is behind a wall
				SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall);
		}

		// update the scores on the map after sensing
		map.update_map_and_score();

		return hitWallret;
	}
}