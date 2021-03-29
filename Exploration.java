import java.util.Stack;

public class Exploration {
	private static final int listOfActionsSize = 4;

	public enum Action {
		NO_ACTION, TURN_LEFT, TURN_RIGHT, TURN_UP, TURN_DOWN, MOVE_FORWARD,
	}

	public enum ExplorationState {
		INITIAL_EXPLORATION, CLEARING_UNKNOWN,
	}

	ExplorationState state;
	//////////////////////////////////////// important
	//////////////////////////////////////// variable!!!////////////////////////////////////////////
	boolean exploreUnexplored = true;
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	Visualization viz;
	SocketClient sc = null;
	boolean simulator;
	RobotInterface robot;
	Map map;

	// Check robot's previous move
	Facing previousFacing;

	// Store list of possible actions for robot
	Action listOfActions[];
	int actionsIterator;

	// Stack to trace back (store previous steps)
	Stack<Action> traceBackSteps;

	// Stack to trace back (store previous steps)
	Stack<Facing> traceBackFacing;

	// Compute number of steps need to back-track
	int stepsToBacktrack;
	boolean backTracking;

	// Count number of sequential moveForward() & tell robot to
	// calibrate when a certain number is reached
	int numTimesMoveForward;
	int timeToSideCalibrate;

	// Robot's Start location
	int startX;
	int startY;

	// Check if robot movement, to prevent end of exploration immediately
	boolean robotMoved;

	// Prevent double front calibration for robot
	boolean hasJustFrontCalibrated;
	// Stack to store unexplored areas of grid, arrays to contain {x,y}
	Stack<int[]> unexploredAreas;

	// A* for next unexplored area
	Astar pathToNextUnexploredArea;

	// get the next unexplored area to go to, coordinates will be {x of grid,y of
	// grid, x of grid that i will go to "look" at target block, y of looking grid}
	int[] nextUnexploredArea;

	// True when robot is going towards an unexplored area in real time
	boolean goingToBlock;

	boolean hasUnexplored = false;

	// Values of the offset for the block
	Stack<int[]> storedOffsetValues;

	// User selected speed (number of steps/sec)
	float stepsPerSecond;
	boolean goingBackToStart = false;

	// % robot has to explore map, before terminating exploration
	float percentageToStop = 100;

	// Time program has to execute, before terminating exploration
	float timeToStop;
	long timeSinceLastUpdate;
	long timeSinceStart;
	long timeLastupdate;

	float minute;
	float second;

	public Exploration(SocketClient sc, boolean simulator, RobotInterface robot, Visualization viz, Map inMap) {
		this.simulator = simulator;
		if (simulator)
			this.sc = sc; // for simulation, can be null
		this.robot = robot;
		this.map = inMap;
		this.viz = viz;

		state = ExplorationState.INITIAL_EXPLORATION;

		// Initialise list to have all "NO.ACTION" items
		listOfActions = new Action[listOfActionsSize];
		for (int i = 0; i < listOfActionsSize; i++)
			listOfActions[i] = Action.NO_ACTION;

		actionsIterator = -1;

		traceBackSteps = new Stack<Action>();
		traceBackFacing = new Stack<Facing>();

		stepsToBacktrack = 0;
		backTracking = false;

		numTimesMoveForward = 0;
		timeToSideCalibrate = 3;

		int timeToTurnRight = 3;
		int timeToTurnLeft = 3;

		// Initialise to False to prevent exploration termination happening immediately
		robotMoved = false;

		// Initialise to False to prevent robot calibration twice
		hasJustFrontCalibrated = false;

		// Initialise stack
		unexploredAreas = new Stack<int[]>();

		// Values stored for checking of offset
		storedOffsetValues = new Stack<int[]>();
		initStoredOffsetValues();

		goingToBlock = false;

		// Variables to control the flow of exploration, mainly for checklist
		stepsPerSecond = 30f;

		// % of map explored before stopping

		// Time to stop simulations
		minute = 5;
		second = 45;

		// timeToStop = minute * 60000 + second * 1000;

		timeSinceLastUpdate = 0;
		timeSinceStart = 0;
		timeLastupdate = System.currentTimeMillis();
	}

	public void setMazeCoverage(float percentMaze) {
		percentageToStop = percentMaze;
	}

	public void setMaxExplorationTime(float mins, float secs) {
		minute = mins;
		second = secs;
	}

	public void initStoredOffsetValues() {

		int[] offset;

		// down side
		offset = new int[] { -1, 1 };
		storedOffsetValues.push(offset);
		offset = new int[] { 0, 1 };
		storedOffsetValues.push(offset);
		offset = new int[] { 1, 1 };
		storedOffsetValues.push(offset);

		// right side
		offset = new int[] { 1, 1 };
		storedOffsetValues.push(offset);
		offset = new int[] { 1, 0 };
		storedOffsetValues.push(offset);
		offset = new int[] { 1, -1 };
		storedOffsetValues.push(offset);

		// up side
		offset = new int[] { 1, -1 };
		storedOffsetValues.push(offset);
		offset = new int[] { 0, -1 };
		storedOffsetValues.push(offset);
		offset = new int[] { -1, -1 };
		storedOffsetValues.push(offset);

		// left side
		offset = new int[] { -1, -1 };
		storedOffsetValues.push(offset);
		offset = new int[] { -1, 0 };
		storedOffsetValues.push(offset);
		offset = new int[] { -1, 1 };
		storedOffsetValues.push(offset);

		// down side
		offset = new int[] { -1, 2 };
		storedOffsetValues.push(offset);
		offset = new int[] { 0, 2 };
		storedOffsetValues.push(offset);
		offset = new int[] { 1, 2 };
		storedOffsetValues.push(offset);

		// right side
		offset = new int[] { 2, 1 };
		storedOffsetValues.push(offset);
		offset = new int[] { 2, 0 };
		storedOffsetValues.push(offset);
		offset = new int[] { 2, -1 };
		storedOffsetValues.push(offset);

		// up side
		offset = new int[] { 1, -2 };
		storedOffsetValues.push(offset);
		offset = new int[] { 0, -2 };
		storedOffsetValues.push(offset);
		offset = new int[] { -1, -2 };
		storedOffsetValues.push(offset);

		// left side
		offset = new int[] { -2, -1 };
		storedOffsetValues.push(offset);
		offset = new int[] { -2, 0 };
		storedOffsetValues.push(offset);
		offset = new int[] { -2, 1 };
		storedOffsetValues.push(offset);

		// down side
		offset = new int[] { -1, 3 };
		storedOffsetValues.push(offset);
		offset = new int[] { 0, 3 };
		storedOffsetValues.push(offset);
		offset = new int[] { 1, 3 };
		storedOffsetValues.push(offset);

		// right side
		offset = new int[] { 3, 1 };
		storedOffsetValues.push(offset);
		offset = new int[] { 3, 0 };
		storedOffsetValues.push(offset);
		offset = new int[] { 3, -1 };
		storedOffsetValues.push(offset);

		// up side
		offset = new int[] { 1, -3 };
		storedOffsetValues.push(offset);
		offset = new int[] { 0, -3 };
		storedOffsetValues.push(offset);
		offset = new int[] { -1, -3 };
		storedOffsetValues.push(offset);

		// left side
		offset = new int[] { -3, -1 };
		storedOffsetValues.push(offset);
		offset = new int[] { -3, 0 };
		storedOffsetValues.push(offset);
		offset = new int[] { -3, 1 };
		storedOffsetValues.push(offset);
	}

	public void initStartPoint(int x, int y) {
		startX = x;
		startY = y;
	}

	int getMapExplored() {
		// Tracks the number of map grids that are explored/DISCOVERED
		int explored = 0;
		for (int i = 0; i < Map.HEIGHT; i++)
			for (int j = 0; j < Map.WIDTH; j++)
				if (map.getMapArray()[i][j] == ExplorationTypes.toInt("EMPTY")
						|| map.getMapArray()[i][j] == ExplorationTypes.toInt("OBSTACLE"))
					explored++;

		return explored;
	}

	int[] getNearestCoordinateToUnExploredArea(int x, int y) {
		int[] mostEfficientPath = null;
		if (x == 4 && y == 10)
			mostEfficientPath = null;

		for (int i = 0; i < storedOffsetValues.size(); i++) {
			// if the robot can move to that spot, then calculate the cost and store it
			if (robot.canRobotMoveHere(x + storedOffsetValues.get(i)[0], y + storedOffsetValues.get(i)[1])) {
				return new int[] { x, y, x + storedOffsetValues.get(i)[0], y + +storedOffsetValues.get(i)[1] };
			}
		}

		// Return the path, if found
		// if(mostEfficientPath != null)
		// return mostEfficientPath;

		// Convey error by returning {-1,-1}
		// Error signifiance: need to explore other paths before exploring the block
		return new int[] { -1, -1, -1, -1 };
	}

	boolean ismapCoordinateInStack(int[] mapCoordinate) {
		int[] unexplored; // temp variable

		for (int i = 0; i < unexploredAreas.size(); i++) {

			unexplored = unexploredAreas.get(i);

			if (unexplored[0] == mapCoordinate[0] && unexplored[1] == mapCoordinate[1]
					&& unexplored[2] == mapCoordinate[2] && unexplored[3] == mapCoordinate[3])
				return true;
		}
		return false;
	}

	void inputAllUnexploredAreas() {
		// Load grid map array to work with
		int[][] mapArray = map.getMapArray();

		int[] mapCoordinate;
		for (int y = 0; y < Map.HEIGHT; y++) {
			for (int x = 0; x < Map.WIDTH; x++) {
				if (mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_EMPTY")
						|| mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {

					// Input areas explored by robot, next to unexplored areas,
					// and feasible for robot to fit/move in
					mapCoordinate = getNearestCoordinateToUnExploredArea(x, y);

					// Don't push onto stack if (values == -1) OR (values already in stack )
					if (mapCoordinate[0] != -1 && mapCoordinate[1] != -1 && !ismapCoordinateInStack(mapCoordinate))
						unexploredAreas.push(mapCoordinate);
				}
			}
		}

	}

	void updateUnexploredAreas() {
		// Load grid map array to work with
		int[][] mapArray = map.getMapArray();

		// Iterate through entire list to remove those that were halfway explored
		for (int i = 0; i < unexploredAreas.size(); i++) {

			// Remove area from stack if area NOT unexplored (i.e. it was explored on the
			// way)
			if (mapArray[unexploredAreas.get(i)[1]][unexploredAreas.get(i)[0]] == ExplorationTypes.toInt("EMPTY")
					|| mapArray[unexploredAreas.get(i)[1]][unexploredAreas.get(i)[0]] == ExplorationTypes
							.toInt("OBSTACLE")) {
				// System.out.print("removed" + unexploredAreas.get(i)[0] + " , " +
				// unexploredAreas.get(i)[1] + " with index " + i + "\n");
				unexploredAreas.remove(i);
				i--;
			}
		}

		// Include all new unexplored areas now available to explore
		inputAllUnexploredAreas();
	}

	public boolean haveObstaclesAroundRobot(int tempX, int tempY) {

		if (robot.isAbleToMove(Direction.UP, tempX, tempY) || robot.isAbleToMove(Direction.DOWN, tempX, tempY)
				|| robot.isAbleToMove(Direction.LEFT, tempX, tempY)
				|| robot.isAbleToMove(Direction.RIGHT, tempX, tempY)) {
			System.out.print("found a block!\n");
			return true;
		}

		return false;
	}

	public int calculateNumBackTrackSteps() {
		int numSteps = 0;

		int iterator = traceBackSteps.size() - 1;

		Action curAction;
		int tempX = robot.x;
		int tempY = robot.y;
		Direction tempFacing = robot.facing;

		while (iterator >= 0) {
			curAction = traceBackSteps.get(iterator);

			switch (curAction) {
			// Move backwards
			case MOVE_FORWARD:
				int movementDistance = 1;

				if (tempFacing == Direction.UP)
					tempY += movementDistance;
				else if (tempFacing == Direction.DOWN)
					tempY -= movementDistance;
				else if (tempFacing == Direction.RIGHT)
					tempX -= movementDistance;
				else if (tempFacing == Direction.LEFT)
					tempX += movementDistance;

				numSteps++;

				// Break & return steps if obstacle encountered
				if (haveObstaclesAroundRobot(tempX, tempY))
					return numSteps;
				break;
			case TURN_LEFT:
				tempFacing = robot.simulateTurnRight(tempFacing);

				numSteps++;
				break;
			case TURN_RIGHT:
				tempFacing = robot.simulateTurnLeft(tempFacing);

				numSteps++;
				break;
			default:
				break;

			}

			iterator--;
		}

		// Return total number of steps to move, before returning beside wall to rerun
		// algo
		return numSteps;
	}

	void doStoredActions() {

		// Instead of calculations in StartExploration(), robot executes actions stored
		// here
		switch (listOfActions[actionsIterator]) {
		case TURN_LEFT:
			// System.out.print("turning left at the stored actions\n");
			robot.turnLeft();
			System.out.print("Robot turn left\n");
			traceBackSteps.push(Action.TURN_LEFT);
			break;

		case TURN_RIGHT:
			robot.turnRight();
			System.out.print("Robot turn right\n");
			traceBackSteps.push(Action.TURN_RIGHT);
			break;

		case MOVE_FORWARD:
			// DoIEMoveForward();
			robot.moveRobot();
			System.out.print("Robot move forward\n");
			traceBackSteps.push(Action.MOVE_FORWARD);
			numTimesMoveForward++;
			break;
		default:
			break;
		}

		actionsIterator -= 1;
	}

	public void DoIETurnRight() {
		System.out.print("Robot turn right\n");
		hasJustFrontCalibrated = false;
		actionsIterator = 0;

		// Check if can front calibrate & left calibrate (has 3 blocks)
		if (robot.canFront_Calibrate() && robot.canLeft_Calibrate() && !hasJustFrontCalibrated) {
			robot.front_Calibrate();
			hasJustFrontCalibrated = true;
			numTimesMoveForward = 0;

		}
		// Check if can only left calibrate after a certain no. of steps
		else if (robot.canLeft_Calibrate() && !hasJustFrontCalibrated) {
			robot.left_Calibrate();
			hasJustFrontCalibrated = true;
			numTimesMoveForward = 0;

		}
		// Turn to right after all calibrations complete
		robot.turnRight();
		System.out.print("Robot turn right\n");
		traceBackSteps.push(Action.TURN_RIGHT);

		// listOfActions[1] = Action.TURN_RIGHT;
		listOfActions[0] = Action.MOVE_FORWARD;

	}

	public void DoIEMoveForward(Facing facing) {
		hasJustFrontCalibrated = false;
		boolean hasCalibrated = false;
		System.out.print("Robot move forward\n");

		// once timeToSideCalibrate
		if (numTimesMoveForward >= timeToSideCalibrate) {

			// Check if blocks next to robot, using side sensors
			if (robot.canSide_Calibrate()) {
				System.out.print("align right\n");
				robot.side_Calibrate();

				// Counter reset
				numTimesMoveForward = 0;
				hasCalibrated = true;

			} else if (robot.canLeft_Calibrate()) {
				System.out.println("align left\n");
				robot.left_Calibrate();
				numTimesMoveForward = 0;
				hasCalibrated = true;
			}
		}

		if (!hasCalibrated) {
			// Just move forward
			robot.moveRobot();
			previousFacing = facing;
			traceBackFacing.push(facing);
			robotMoved = true;
			numTimesMoveForward++;

			traceBackSteps.push(Action.MOVE_FORWARD);
		}

	}

	public void DoIETurnLeft() {
		actionsIterator = 0;
		listOfActions[0] = Action.TURN_LEFT;
		System.out.print("Robot turn left\n");

		// Check if can front calibrate (has 3 blocks right infront of robot)
		if (robot.canFront_Calibrate() && robot.canSide_Calibrate() && !hasJustFrontCalibrated) {
			System.out.print("align front\n");
			robot.front_Calibrate();
			hasJustFrontCalibrated = true;
			// Counter reset
			numTimesMoveForward = 0;

		} else if (robot.canSide_Calibrate() && !hasJustFrontCalibrated) {
			System.out.print("align right\n");
			robot.side_Calibrate();
			hasJustFrontCalibrated = true;
			numTimesMoveForward = 0;
		}

	}

	public void DoIEBackTrack() {
		if (!backTracking) {
			backTracking = true;
			stepsToBacktrack = calculateNumBackTrackSteps();
		}

		System.out.print("doing trace back\n");
		if (traceBackSteps.empty()) {
			System.out.print("trace back step is empty\n");
			return;
		}
		Action prevAction = traceBackSteps.pop();

		switch (prevAction) {
		case TURN_LEFT:
			robot.turnRight();
			break;

		case TURN_RIGHT:
			robot.turnLeft();
			break;

		case MOVE_FORWARD:
			robot.reverse();
			break;
		default:
			System.out.print("back track error\n");
			break;
		}

		stepsToBacktrack--;

		if (stepsToBacktrack <= 0)
			backTracking = false;
	}

	// True: return 1
	// False: return 0
	// Reset: return -1
	public int DoInitialExploration() {

		// Only continue if no stored actions, else execute stored actions
		if (actionsIterator != -1) {
			System.out.print("doing stored actions\n");
			doStoredActions();

			return 0;
		}

		// Do not execute other actions when backtracking
		if (backTracking) {
			DoIEBackTrack();

			return 0;
		}

		switch (robot.facing) {
		case LEFT:

			// Do stored actions, if robot previously facing left wall & can move up
			if (robot.isAbleToMove(Direction.UP) && previousFacing == Facing.LEFT)
				DoIETurnRight();

			// Move left, if left is clear and wall is above
			else if (!robot.isAbleToMove(Direction.UP) && robot.isAbleToMove(Direction.LEFT))
				DoIEMoveForward(Facing.LEFT);

			// Turn left to face down, if can't move left or up
			else if (!robot.isAbleToMove(Direction.UP) && !robot.isAbleToMove(Direction.LEFT))
				DoIETurnLeft();

			// Backtrack if no wall beside robot
			else
				DoIEBackTrack();

			break;

		case RIGHT:
			// if can move down and it was facing right previously, execute stored actions
			if (robot.isAbleToMove(Direction.DOWN) && previousFacing == Facing.RIGHT)
				DoIETurnRight();

			// Move right, if right is clear & down is a wall
			else if (!robot.isAbleToMove(Direction.DOWN) && robot.isAbleToMove(Direction.RIGHT))
				DoIEMoveForward(Facing.RIGHT);

			// Turn left to face up, if can't move right or down
			else if (!robot.isAbleToMove(Direction.DOWN) && !robot.isAbleToMove(Direction.RIGHT))
				DoIETurnLeft();

			// Backtrack if no wall next to robot
			else
				DoIEBackTrack();
			break;

		case UP:
			// Do stored actions, if previously facing up & can move right
			if (robot.isAbleToMove(Direction.RIGHT) && previousFacing == Facing.UP)
				DoIETurnRight();

			// Move up, if up is clear & right is a wall
			else if (!robot.isAbleToMove(Direction.RIGHT) && robot.isAbleToMove(Direction.UP))
				DoIEMoveForward(Facing.UP);

			// Turn left to face left, if can't move up or right
			else if (!robot.isAbleToMove(Direction.RIGHT) && !robot.isAbleToMove(Direction.UP))
				DoIETurnLeft();

			// Backtrack if no wall next to robot
			else
				DoIEBackTrack();

			break;

		case DOWN:
			if (robot.isAbleToMove(Direction.LEFT) && previousFacing == Facing.DOWN)
				DoIETurnRight();

			// Move right, if down is clear & left is a wall
			else if (!robot.isAbleToMove(Direction.LEFT) && robot.isAbleToMove(Direction.DOWN))
				DoIEMoveForward(Facing.DOWN);

			else if (!robot.isAbleToMove(Direction.LEFT) && !robot.isAbleToMove(Direction.DOWN))
				DoIETurnLeft();

			// Backtrack if no wall next to robot
			else
				DoIEBackTrack();

			break;

		default:
			return 0;
		}

		/*
		 * debugging stuff
		 * 
		 * System.out.print(traceBackSteps.size() + "\n");
		 * 
		 * if(traceBackSteps.size() > 75) { //if(map.mapScoreArray[13][12] > 22)
		 * map.turnoffgrid = true; map.turnoffgrid3 = true; map.turnoffgrid2 = true;
		 * map.updateMapWithScore(); }
		 * 
		 */

		// Robot requested reset, request granted and new Robot created
		if (robot.getWantToReset()) {
			System.out.println("ROBOT WANTS TO RESET");
			return -1;
		}

		// When robot moves, check if robot at start location to terminate exploration
		if (robotMoved && robot.getX() == startX && robot.getY() == startY) {
			System.out.println("finished exploration");
			return 1;
		}
		return 0;
	}

	// Optimise to minimise no. of turns taken when robot navigating unknown maze
	public boolean DoClearingUnknown() {
		System.out.println("doing clear unknown");

		// while(true) {

		// Iterate step by step, if robot going to a block
		if (goingToBlock) {
			System.out.println("goingToBlock");

			// Function returns true if robot has reached unexplored area
			if (robot.doStepFastestPath()) {
				System.out.println("robot.doStepFastestPath()");

				// Ensure robot is facing the un-explored area before completing current path
				// if(robot.isFacingArea(nextUnexploredArea[0], nextUnexploredArea[1]))
				goingToBlock = false;
			}

		} else {
			System.out.println("else");

			// If array == empty, check if robot returned to start location, else return to
			// start
			if (unexploredAreas.empty()) {
				System.out.println("unexploredAreas.empty()\n++++++++++++++++++++++++++++++++++");
				System.out.println("unexploredAreas.empty()");
				if (robot.x == startX && robot.y == startY) {
					System.out.println("robot.x == startX && robot.y == startY");
					if (goingBackToStart)
						PathDrawer.removePath();
					adjustMapForFastestPath();

					return true;

				}
				// Continue exploring if grid not fully explored/still unexplored
				else if (getMapExplored() < 290)// != map.HEIGHT*map.WIDTH)
				{
					System.out.print("checking if map got properly explored aka 90% of map");
					inputAllUnexploredAreas();

					goingBackToStart = false;

				} else {
					System.out.print("finished exploring part 2, going back to start");

					pathToNextUnexploredArea = new Astar(map.getNodeXY(robot.x, robot.y),
							map.getNodeXY(startX, startY));

					// Transmit to robot to store instructions
					Stack<Node> fast = pathToNextUnexploredArea.getFastestPath();
					robot.setFastestInstruction(fast, startX, startY);

					// Update pathdrawer for simulation graphics
					PathDrawer.update(robot.x, robot.y, pathToNextUnexploredArea.getFastestPath());
					goingBackToStart = true;

					goingToBlock = true;
				}
			} else {
				hasUnexplored = true;
				System.out.print("update map nodes for A star\n");
				// Update grid nodes for A*
				map.updateMap();

				// Update unexploredAreas<Stack> (to clear blocks encountered on the way to
				// other blocks)
				updateUnexploredAreas();

				int indexOfClosestArea = 0;
				int costOfClosest = 999;

				if (!unexploredAreas.empty()) {
					System.out.print("unexploreaAreas.size = " + unexploredAreas.size() + "\n");
					for (int i = 0; i < unexploredAreas.size(); i++) {
						// put the x and y value of the unexplored area
						// [0]=x value of unexplored area
						// [1]=y value of unexplored area
						// [2]=x value of coordinate of point next to unexplored area that the robot can
						// access
						// [3]=y value of coordinate of point next to unexplored area that the robot can
						// access
						System.out.println("unexploredAreas.get(" + i + ")[0]=" + unexploredAreas.get(i)[0]);
						System.out.println("unexploredAreas.get(" + i + ")[1]=" + unexploredAreas.get(i)[1]);
						System.out.println("unexploredAreas.get(" + i + ")[2]=" + unexploredAreas.get(i)[2]);
						System.out.println("unexploredAreas.get(" + i + ")[3]=" + unexploredAreas.get(i)[3]);
						pathToNextUnexploredArea = new Astar(map.getNodeXY(robot.x, robot.y),
								map.getNodeXY(unexploredAreas.get(i)[2], unexploredAreas.get(i)[3]));
						pathToNextUnexploredArea.getFastestPath();
						int cost = pathToNextUnexploredArea.getCost();
						System.out.println("Costs:" + cost);

						// Update {indexOfClosestArea} if cost < current lowest
						if (cost < costOfClosest) {
							costOfClosest = cost;
							indexOfClosestArea = i;
						}
					}

					pathToNextUnexploredArea = new Astar(map.getNodeXY(robot.x, robot.y), map.getNodeXY(
							unexploredAreas.get(indexOfClosestArea)[2], unexploredAreas.get(indexOfClosestArea)[3]));

					System.out.print("next area to explore " + unexploredAreas.get(indexOfClosestArea)[0] + " and "
							+ unexploredAreas.get(indexOfClosestArea)[1] + " but going to "
							+ unexploredAreas.get(indexOfClosestArea)[2] + " and "
							+ unexploredAreas.get(indexOfClosestArea)[3] + '\n');

					nextUnexploredArea = unexploredAreas.remove(indexOfClosestArea);

					// Transmit to robot to store instructions
					Stack<Node> fast = pathToNextUnexploredArea.getFastestPath();
					System.out.print("setting fastest instructions\n++++++++++++++++++++++++++++++");
					robot.setFastestInstruction(fast, nextUnexploredArea[0], nextUnexploredArea[1]);

					// Update pathdrawer for simulation graphics
					PathDrawer.update(robot.x, robot.y, pathToNextUnexploredArea.getFastestPath());
				}
				goingToBlock = true;
				goingBackToStart = false;
			}
		}
		return false;
		// }
	}

	// Robot reaches start position: return 1
	// False: return 0
	// Reset: return 1
	public int DoSimulatorExploration() {

		// Record time since start of simulation
		long startTime = System.currentTimeMillis();
		timeToStop = minute * 60000 + second * 1000;

		try {
			while (true) {

				////////////////////////////////// variables for the control of
				////////////////////////////////// exploration(checklist)///////////////////////////

				// will have to comment out this section to allow robot to go back to start
				// during exploration

				timeSinceLastUpdate = System.currentTimeMillis() - timeLastupdate;
				timeSinceStart += timeSinceLastUpdate;
				timeSinceStart = System.currentTimeMillis() - startTime;
				timeLastupdate = System.currentTimeMillis();

				if (timeSinceStart >= timeToStop) {
					System.out.println("Time up!");
					return 1;
				}

				float percentageExplored = ((float) getMapExplored() / (float) (Map.HEIGHT * Map.WIDTH)) * 100;
				System.out.print("Percentage Explored: " + percentageExplored + "\n");
				System.out.print("Percentage to stop: " + percentageToStop + "\n");

				// if (percentageExplored >= percentageToStop)
				// return 1;

				////////////////////////////////// end of variables for the control of
				////////////////////////////////// exploration(checklist)///////////////////////////

				// Only do sleeps when in simulation
				if (simulator)
					Thread.sleep((long) (500 / stepsPerSecond));

				switch (state) {
				case INITIAL_EXPLORATION:

					// After robot reaches start location, DoInitialExploration() returns 1
					int DoInitialExplorationResult = DoInitialExploration();

					if (DoInitialExplorationResult == 1) {
						robot.sendMapDescriptor();

						if (System.currentTimeMillis() - startTime < 225 * 1000 && exploreUnexplored) {
							System.out.println("Doing explore Unexplored\n\n\n\n\n");

							state = ExplorationState.CLEARING_UNKNOWN;
							inputAllUnexploredAreas();
							break;

						} else {
							System.out.println("NOT!!! doing explore Unexplored\n\n\n\n\n");
							adjustMapForFastestPath();
							// map.updateMap();
							viz.repaint();
							return 1;
						}

					} else if (DoInitialExplorationResult == -1) {
						System.out.println("Reset ordered by robot!");
						return -1;
					}

					map.updateMap();
					break;

				case CLEARING_UNKNOWN:
					// System.out.println("doing clear unknown");

					// Method returns true (1) when robot completes clearing map & returns to start
					// location
					// For debugging purposes, stepsPerSecond set to 2f
					stepsPerSecond = 2f;
					if (DoClearingUnknown()) {
						return 1;
					}
					map.updateMap();
					PathDrawer.updateUnexploredAreas(unexploredAreas);

					break;
				}

				// Update simulation graphics after every iteration
				viz.repaint();
			}

		} catch (Exception e) {
			System.out.println(e);
			System.out.println("error in exploration");
		}

		// return false by default
		return 0;

	}

	public void adjustMapForFastestPath() {

		// Count total number of obstacles
		// If obstacles > 30, remove those with lowest score
		// Also set all unexplored areas to explored with score of 1

		// Load grid map array
		int[][] mapArray = map.getMapArray();

		Stack<Integer[]> obstacleAndScore = new Stack<Integer[]>();

		for (int y = 0; y < Map.HEIGHT; y++) {
			for (int x = 0; x < Map.WIDTH; x++) {
				if (mapArray[y][x] == ExplorationTypes.toInt("OBSTACLE")) {
					Integer[] obstacle = new Integer[3];
					obstacle[0] = x;
					obstacle[1] = y;
					obstacle[2] = map.mapScoreArray[y][x];
					obstacleAndScore.push(obstacle);
				}

				// Mark unexplored areas as explored
				if (mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_EMPTY")
						|| mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {
					map.mapScoreArray[y][x] = -1;
				}
			}
		}

		int lowestScoreIndex = 0;
		int lowestScore = 999;

		// Remove obstacles if (blocks > 30)
		while (obstacleAndScore.size() > 30) {
			lowestScoreIndex = 0;
			lowestScore = 999;

			// Iterate list to find obstacle with lowest score
			for (int i = 0; i < obstacleAndScore.size(); i++) {

				// if (current_score < recorded lowestScore), then replace it
				if (obstacleAndScore.get(i)[2] < lowestScore) {
					lowestScoreIndex = i;
					lowestScore = obstacleAndScore.get(i)[2];
				}
			}

			// Set obstacle with minimum score to an empty block
			map.mapScoreArray[obstacleAndScore.get(lowestScoreIndex)[1]][obstacleAndScore
					.get(lowestScoreIndex)[0]] = -1;

			// Remove obstacle with minimum score
			obstacleAndScore.remove(lowestScoreIndex);
		}

		// Revise the score map
		map.updateMapWithScore();
		for (int i = 0; i < map.mapArray.length; i++) {
			for (int j = 0; j < map.mapArray[i].length; j++) {
				System.out.print(map.mapArray[i][j]);
			}
			System.out.println();
		}
		map.setMapEqMap();
		map.optimiseFP();
		System.out.println();
		for (int i = 0; i < map.mapArray2.length; i++) {
			for (int j = 0; j < map.mapArray2[i].length; j++) {
				System.out.print(map.mapArray2[i][j]);
			}
			System.out.println();
		}
	}

}
