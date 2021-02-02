import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public abstract class RobotInterface {
	Visualization viz;

	// the x and y position of the robot on the map
	int x;
	int y;

	// the x and y coordinates to draw the robot on screen
	int x_g, y_g;
	// radius of the circle to draw the robot
	final static int radius = 114;

	// the facing direction of the robot
	Direction facing;

	// the map reference
	Map map;
	final int WIDTH = 15;
	final int HEIGHT = 20;

	// fastest path from a point to another point
	Stack<Node> fastestPath = new Stack<Node>();
	// the converted int instructions from the above stack array
	// Stack<Integer> instructionsForFastestPath;
	Stack<Integer> instructionsForFastestPath = new Stack<Integer>();

	public abstract void addSensors(RealSensor[] sensors);

	public abstract void addSensors(Sensor[] sensors);

	public abstract void LookAtSurroundings();

	public abstract void SenseRobotLocation();

	public abstract void moveRobot();// move robot forward

	public abstract void turnLeft();

	public abstract void turnRight();

	public abstract boolean getFastestInstruction(Stack<Node> fast);

	public abstract void deactivateSensors();

	public abstract void reverse();

	public abstract void side_Calibrate();

	public abstract void left_Calibrate();

	public abstract void front_Calibrate();

	public abstract void initial_Calibrate();

	public abstract void sendMapDescriptor();

	public abstract boolean doStepFastestPath();

	public abstract void setSpeed(float stepsPerSecond);

	// set the fastest path for the robot to follow
	public void setFastestInstruction(Stack<Node> fast, int targetX, int targetY) {
		// for the purpose of this function only, to input the instructions into the
		// stack
		Direction tempFacing = facing;
		int tempX = x;
		int tempY = y;
		Node next = null;

		// loop until the stack(fast) is empty
		while (true) {
			// if the stack is empty, means the nodes have fully been converted
			// next step will be to TURN towards the correct direction
			if (fast.empty())
				break;
			else {
				next = (Node) fast.pop();
				if (next.getX() > tempX) {
					if (tempFacing != Direction.RIGHT)
						tempFacing = getShortestTurnInstruction(tempFacing, Direction.RIGHT);

					tempX += 1;
				} else if (next.getX() < tempX) {
					if (tempFacing != Direction.LEFT)
						tempFacing = getShortestTurnInstruction(tempFacing, Direction.LEFT);

					tempX -= 1;
				} else if (next.getY() < tempY) {
					if (tempFacing != Direction.UP)
						tempFacing = getShortestTurnInstruction(tempFacing, Direction.UP);

					tempY -= 1;
				} else if (next.getY() > tempY) {
					if (tempFacing != Direction.DOWN)
						tempFacing = getShortestTurnInstruction(tempFacing, Direction.DOWN);

					tempY += 1;
				}

				instructionsForFastestPath.add(Packet.FORWARDi);
			}
		}
	}

	public Direction simulateTurnRight(Direction tempFacing) {
		switch (tempFacing) {
			case RIGHT:
				tempFacing = Direction.DOWN;
				break;
			case LEFT:
				tempFacing = Direction.UP;
				break;
			case UP:
				tempFacing = Direction.RIGHT;
				break;
			case DOWN:
				tempFacing = Direction.LEFT;
				break;
		}
		return tempFacing;
	}

	public Direction simulateTurnLeft(Direction tempFacing) {
		switch (tempFacing) {
			case RIGHT:
				tempFacing = Direction.UP;
				break;
			case LEFT:
				tempFacing = Direction.DOWN;
				break;
			case UP:
				tempFacing = Direction.LEFT;
				break;
			case DOWN:
				tempFacing = Direction.RIGHT;
		}
		return tempFacing;
	}

	public Direction getShortestTurnInstruction(Direction tempFacing, Direction targetFacing) {
		Stack<Direction> listOfTurnInstructions = new Stack<Direction>();

		if (tempFacing == Direction.RIGHT) {
			if (targetFacing == Direction.UP)
				instructionsForFastestPath.add(Packet.TURNLEFTi);

			else if (targetFacing == Direction.DOWN)
				instructionsForFastestPath.add(Packet.TURNRIGHTi);

			else if (targetFacing == Direction.LEFT) {
				instructionsForFastestPath.add(Packet.TURNLEFTi);
				instructionsForFastestPath.add(Packet.TURNLEFTi);
			}

		} else if (tempFacing == Direction.LEFT) {
			if (targetFacing == Direction.UP)
				instructionsForFastestPath.add(Packet.TURNRIGHTi);

			else if (targetFacing == Direction.DOWN)
				instructionsForFastestPath.add(Packet.TURNLEFTi);

			else if (targetFacing == Direction.RIGHT) {
				instructionsForFastestPath.add(Packet.TURNLEFTi);
				instructionsForFastestPath.add(Packet.TURNLEFTi);
			}

		} else if (tempFacing == Direction.UP) {
			if (targetFacing == Direction.LEFT)
				instructionsForFastestPath.add(Packet.TURNLEFTi);

			else if (targetFacing == Direction.RIGHT)
				instructionsForFastestPath.add(Packet.TURNRIGHTi);

			else if (targetFacing == Direction.DOWN) {
				instructionsForFastestPath.add(Packet.TURNLEFTi);
				instructionsForFastestPath.add(Packet.TURNLEFTi);
			}

		} else if (tempFacing == Direction.DOWN) {
			if (targetFacing == Direction.RIGHT)
				instructionsForFastestPath.add(Packet.TURNLEFTi);

			else if (targetFacing == Direction.LEFT)
				instructionsForFastestPath.add(Packet.TURNRIGHTi);

			else if (targetFacing == Direction.UP) {
				instructionsForFastestPath.add(Packet.TURNLEFTi);
				instructionsForFastestPath.add(Packet.TURNLEFTi);
			}

		}
		// return listOfTurnInstructions;
		return targetFacing;
	}

	public boolean canSide_Calibrate() {
		// returns true if the right side of the robot have blocks to use to calibrate
		if (facing == Direction.LEFT && isBlocked(x - 1, y - 2) && isBlocked(x + 1, y - 2))
			return true;
		else if (facing == Direction.RIGHT && isBlocked(x - 1, y + 2) && isBlocked(x + 1, y + 2))
			return true;
		else if (facing == Direction.DOWN && isBlocked(x - 2, y - 1) && isBlocked(x - 2, y + 1))
			return true;
		else if (facing == Direction.UP && isBlocked(x + 2, y - 1) && isBlocked(x + 2, y + 1))
			return true;

		return false;
	}

	public boolean canFront_Calibrate() {
		// returns true if the front of the robot have blocks to use to calibrate
		if (facing == Direction.LEFT && isBlocked(x - 2, y - 1) && isBlocked(x - 2, y) && isBlocked(x - 2, y + 1))
			return true;
		else if (facing == Direction.RIGHT && isBlocked(x + 2, y - 1) && isBlocked(x + 2, y) && isBlocked(x + 2, y + 1))
			return true;
		else if (facing == Direction.DOWN && isBlocked(x - 1, y + 2) && isBlocked(x, y + 2) && isBlocked(x + 1, y + 2))
			return true;
		else if (facing == Direction.UP && isBlocked(x - 1, y - 2) && isBlocked(x, y - 2) && isBlocked(x + 1, y - 2))
			return true;

		return false;
	}

	public boolean canLeft_Calibrate() {
		// returns true if the right side of the robot have blocks to use to calibrate
		if (facing == Direction.LEFT && isBlocked(x - 1, y + 2) && isBlocked(x + 1, y + 2))
			return true;
		else if (facing == Direction.RIGHT && isBlocked(x - 1, y - 2) && isBlocked(x + 1, y - 2))
			return true;
		else if (facing == Direction.DOWN && isBlocked(x + 2, y - 1) && isBlocked(x + 2, y + 1))
			return true;
		else if (facing == Direction.UP && isBlocked(x - 2, y - 1) && isBlocked(x - 2, y + 1))
			return true;

		return false;
	}

	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	public void setRobotPos(int x, int y, Direction facing) {
		this.x = x;
		this.y = y;
		this.facing = facing;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	public Visualization getViz() {
		return viz;
	}

	public void setface(Direction facing) {
		this.facing = facing;
	}

	public void setViz(Visualization viz) {
		this.viz = viz;
	}

	public boolean isObstacleOrWallFront() {
		// System.out.println("Current Position: " + x +" " + y);
		switch (facing) {
			case UP:
				if (isBlocked(x - 1, y - 2) || isBlocked(x, y - 2) || isBlocked(x + 1, y - 2))
					return true;
				break;
			case DOWN:
				if (isBlocked(x - 1, y + 2) || isBlocked(x, y + 2) || isBlocked(x + 1, y + 2))
					return true;
				break;
			case LEFT:
				if (isBlocked(x - 2, y - 1) || isBlocked(x - 2, y) || isBlocked(x - 2, y + 1))
					return true;
				break;
			case RIGHT:
				if (isBlocked(x + 2, y - 1) || isBlocked(x + 2, y) || isBlocked(x + 2, y + 1)) {
					return true;
				}
				break;

		}
		return false;
	}

	public boolean checkLeftBound(int xi, int yi) {
		if ((xi < 0)) {

			// System.out.println("ERROR: out of left bound");
			return true;
		}
		return false;
	}

	public boolean checkTopBound(int xi, int yi) {
		if ((yi < 0)) {
			// System.out.println("ERROR: out of top bound");
			return true;
		}
		return false;
	}

	public boolean checkBottomBound(int xi, int yi) {
		if (yi > (HEIGHT)) {
			// System.out.println("ERROR: out of bottom bound");
			return true;
		}
		return false;
	}

	public boolean checkRightBound(int xi, int yi) {
		if (xi > (WIDTH)) {
			// System.out.println("ERROR: out of right bound");
			return true;
		}
		return false;
	}

	public boolean checkObstacle(int xi, int yi) {
		if (yi >= HEIGHT || xi >= WIDTH || yi < 0 || xi < 0) {
			return true;
		} else if (map.getMapArray()[yi][xi] == ExplorationTypes.toInt("OBSTACLE")) {
			// System.out.println("grid is obstacle");
			return true;
		}
		return false;
	}

	// input the direction of checking and return true if robot can move that
	// direction
	public boolean isAbleToMove(Direction dir) {

		return isAbleToMove(dir, this.x, this.y);
	}

	public boolean isAbleToMove(Direction dir, int x, int y) {
		boolean canMove = true;
		System.out.println("Direction: " + dir + "\nx coord: " + x + "\ny coord: " + y);
		switch (dir) {
			case LEFT:
				for (int i = -1; i < 2; i++) {
					// if part of the robot is out of bounds or going to hit a wall
					if (checkLeftBound(x - 1, y) || checkObstacle(x - 1 - 1, y + i)) {
						canMove = false;
						break;
					}
				}
				break;

			case RIGHT:
				for (int i = -1; i < 2; i++) {
					// if part of the robot is out of bounds or going to hit a wall
					if (checkRightBound(x + 1, y) || checkObstacle(x + 1 + 1, y + i)) {
						canMove = false;
						break;
					}
				}
				break;

			case UP:
				for (int i = -1; i < 2; i++) {
					// if part of the robot is out of bounds or going to hit a wall
					if (checkTopBound(x, y - 1) || checkObstacle(x + i, y - 1 - 1)) {
						canMove = false;
						break;
					}
				}
				break;

			case DOWN:

				for (int i = -1; i < 2; i++) {
					// if part of the robot is out of bounds or going to hit a wall
					if (checkBottomBound(x, y + 1) || checkObstacle(x + i, y + 1 + 1)) {
						canMove = false;
						break;
					}
				}
				break;
		}
		return canMove;
	}

	public boolean isBlocked(int xi, int yi) {
		// return 1 if obstacle, 0 if no obstacle, -1 if out of bound
		boolean rflag = false, tflag = false, bflag = false, lflag = false, obflag = false;
		lflag = checkLeftBound(xi, yi);
		tflag = checkTopBound(xi, yi);
		rflag = checkRightBound(xi, yi);
		bflag = checkBottomBound(xi, yi);
		obflag = checkObstacle(xi, yi);
		if (lflag || tflag || rflag || bflag || obflag) {
			return true;
		}

		if (map.getMapArray()[yi][xi] == ExplorationTypes.toInt("UNEXPLORED_EMPTY")
				|| map.getMapArray()[yi][xi] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE"))
			return true;

		return false;
	}

	// returns false if cannot move
	public boolean canRobotMoveHere(int x, int y) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (isBlocked(x + i, y + j))
					return false;
			}
		}

		return true;
	}

	public void paintRobot(Graphics g) {
		g.setColor(Color.RED);
		x_g = 10 + (x - 1) * Map.sizeofsquare;
		y_g = 10 + (y - 1) * Map.sizeofsquare;
		g.fillArc(x_g, y_g, radius, radius, 0, 360);

		g.setColor(Color.BLUE);

		int dirOffsetX = 0;
		int dirOffsetY = 0;

		if (facing == Direction.UP)
			dirOffsetY = -45;
		else if (facing == Direction.DOWN)
			dirOffsetY = 45;
		else if (facing == Direction.LEFT)
			dirOffsetX = -45;
		else if (facing == Direction.RIGHT)
			dirOffsetX = 45;

		g.fillArc(x_g + 45 + dirOffsetX, y_g + 45 + dirOffsetY, 20, 20, 0, 360);
	}

	public abstract boolean getWantToReset();
}