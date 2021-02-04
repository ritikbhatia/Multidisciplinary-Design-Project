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
	//////////////////////////////////////// import
	//////////////////////////////////////// variable!!!////////////////////////////////////////////
	boolean exploreUnexplored = true;
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	Visualization viz;
	SocketClient sc = null;
	boolean simulator;
	RobotInterface robot;
	Map map;

	// to check whats the previous move that the robot did
	Facing previousFacing;

	// to store a list of actions for the robot to take
	Action listOfActions[];
	int actionsIterator;

	// stack to store the previous steps taking(for trace back)
	Stack<Action> traceBackSteps;

	// stack to store the previous steps taking(for trace back)
	Stack<Facing> traceBackFacing;

	// track num of steps to backtrack
	int stepsToBacktrack;
	boolean backTracking;

	// track the number of consecutive moveForward() and call the robot to calibrate
	// when a certain number is reached
	int numTimesMoveForward;
	int timeToSideCalibrate;

	// the starting position of the robot
	int startX;
	int startY;

	// bool to check weather the robot has moved, to prevent the exploration ending
	// immediately cause since
	// robot starts at the start position and i use it to check if exploration is
	// done
	boolean robotMoved;

	// prevent robot from front calibrating twice
	boolean hasJustFrontCalibrated;
	// the stack to contain the unexplored areas of the map, arrays will contain
	// {x,y}
	Stack<int[]> unexploredAreas;

	// the Astar class to the next unexplored area
	Astar pathToNextUnexploredArea;

	// get the next unexplored area to go to, coordinates will be {x of grid,y of
	// grid, x of grid that i will go to "look" at target block, y of looking grid}
	int[] nextUnexploredArea;

	// true when the robot is currently on its way to an unexplored area
	boolean goingToBlock;

	boolean hasUnexplored = false;

	// the values of the offset for the block
	Stack<int[]> storedOffsetValues;

	// number of steps per second(user selected)
	float stepsPerSecond;
	boolean goingBackToStart = false;

	// the %that map has to be explored before to terminate
	float percentageToStop;

	// the time that the program has to run before terminating
	int timeToStop;
	long timeSinceLastUpdate;
	long timeSinceStart;
	long timeLastupdate;

	int minute;
	int second;

	public Exploration(SocketClient sc, boolean simulator, RobotInterface robot, Visualization viz, Map inMap) {
		this.simulator = simulator;
		if (simulator)
			this.sc = sc; // can be null if simulation
		this.robot = robot;
		this.map = inMap;
		this.viz = viz;

		state = ExplorationState.INITIAL_EXPLORATION;

		// init the list to all have "NO.ACTION"
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
		// init to false to prevent exploration phase ending immediately
		robotMoved = false;

		// init to false to prevent robot from calibrating twice in a row
		hasJustFrontCalibrated = false;

		// init the stack
		unexploredAreas = new Stack<int[]>();

		// the values stored for the checking of offset
		storedOffsetValues = new Stack<int[]>();
		initStoredOffsetValues();

		goingToBlock = false;

		// variables to control the flow of exploration, mainly for checklist

		stepsPerSecond = 30f;

		// % of map explored before stopping

		// time to stop simulationaa
		minute = 20;
		second = 5;

		timeToStop = minute * 60000 + second * 1000;

		timeSinceLastUpdate = 0;
		timeSinceStart = 0;
		timeLastupdate = System.currentTimeMillis();
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
		// counts the number of map that is explored, aka is not UNDISCOVERED
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

		// if a path was found, return the path
		// if(mostEfficientPath != null)
		// return mostEfficientPath;

		// return {-1,-1} to singify an error
		// if {-1,-1} is returned, it means that the block cannot be explored at first,
		// need to explore other path first
		return new int[] { -1, -1, -1, -1 };
	}

	boolean ismapCoordinateInStack(int[] mapCoordinate) {
		// temp variable
		int[] unexplored;
		for (int i = 0; i < unexploredAreas.size(); i++) {

			unexplored = unexploredAreas.get(i);

			if (unexplored[0] == mapCoordinate[0] && unexplored[1] == mapCoordinate[1]
					&& unexplored[2] == mapCoordinate[2] && unexplored[3] == mapCoordinate[3])
				return true;
		}
		return false;
	}

	void inputAllUnexploredAreas() {
		// get the map array to work on
		int[][] mapArray = map.getMapArray();

		int[] mapCoordinate;
		for (int y = 0; y < Map.HEIGHT; y++) {
			for (int x = 0; x < Map.WIDTH; x++) {
				if (mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_EMPTY")
						|| mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {
					// need to input an area that the robot has explored and is next to the
					// unexplored area, and that the robot can "fit/go" there
					mapCoordinate = getNearestCoordinateToUnExploredArea(x, y);

					// if the values are -1, do not push into the stack
					// or if the values are inside the stack already, dont push too
					if (mapCoordinate[0] != -1 && mapCoordinate[1] != -1 && !ismapCoordinateInStack(mapCoordinate))
						unexploredAreas.push(mapCoordinate);
				}
			}
		}

	}

	void updateUnexploredAreas() {
		// get the map array to work on
		int[][] mapArray = map.getMapArray();

		// run thru the whole list to remove those that were explored halfway
		for (int i = 0; i < unexploredAreas.size(); i++) {
			// if the area is now not a "unexplored", means it was explored on the way, then
			// remove it from the stack
			if (mapArray[unexploredAreas.get(i)[1]][unexploredAreas.get(i)[0]] == ExplorationTypes.toInt("EMPTY")
					|| mapArray[unexploredAreas.get(i)[1]][unexploredAreas.get(i)[0]] == ExplorationTypes
							.toInt("OBSTACLE")) {
				// System.out.print("removed" + unexploredAreas.get(i)[0] + " , " +
				// unexploredAreas.get(i)[1] + " with index " + i + "\n");
				unexploredAreas.remove(i);
				i--;
			}
		}

		// input any new areas that becomes available to explore
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
				// go backwards
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

					// if theres a obstacle now, then break out and return the steps
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

		// return the number of steps to take before being next to a wall to continue
		// the algo
		return numSteps;
	}

	void doStoredActions() {
		// robot will do the actions that have been stored here instead of doing
		// computation in StartExploration()
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
		// Check if can front calibrate and can left calibrate(has 3 blocks)
		if (robot.canFront_Calibrate() && robot.canLeft_Calibrate() && !hasJustFrontCalibrated) {
			robot.front_Calibrate();
			hasJustFrontCalibrated = true;
			numTimesMoveForward = 0;
		}
		// Check is can only left calibrate after a certain number of steps
		else if (robot.canLeft_Calibrate() && !hasJustFrontCalibrated) {
			robot.left_Calibrate();
			hasJustFrontCalibrated = true;
			numTimesMoveForward = 0;
		}
		// Turn right after all calibrations are done
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
			// check if the robot side sensors have the blocks next to them to
			if (robot.canSide_Calibrate()) {
				System.out.print("align right\n");
				robot.side_Calibrate();

				// reset the counter
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
			// simply move forward
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

		// check if the front can be calibrated(if theres 3 blocks right infront of the
		// robot
		if (robot.canFront_Calibrate() && robot.canSide_Calibrate() && !hasJustFrontCalibrated) {
			System.out.print("align front\n");
			robot.front_Calibrate();
			hasJustFrontCalibrated = true;
			// reset the counter
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

	// returns 1 for true
	// returns 0 for false
	// returns -1 for reset
	public int DoInitialExploration() {
		// make sure there isnt a stored action before continuing, if have den do stored
		// actions
		if (actionsIterator != -1) {
			System.out.print("doing stored actions\n");
			// System.out.print("doing stored actions\n");
			doStoredActions();
			// return false;
			return 0;
		}

		// when backtracking, do not do other actions
		if (backTracking) {
			DoIEBackTrack();
			// return false;
			return 0;
		}

		switch (robot.facing) {
			case LEFT:
				// if can move up and it was facing left previously, execute stored actions
				if (robot.isAbleToMove(Direction.UP) && previousFacing == Facing.LEFT)
					DoIETurnRight();

				// if above is a wall and left is clear, move left
				else if (!robot.isAbleToMove(Direction.UP) && robot.isAbleToMove(Direction.LEFT))
					DoIEMoveForward(Facing.LEFT);

				// if cannot move up or left, turn left to face down
				else if (!robot.isAbleToMove(Direction.UP) && !robot.isAbleToMove(Direction.LEFT))
					DoIETurnLeft();

				// no wall next to robot, do back track
				else
					DoIEBackTrack();

				break;

			case RIGHT:
				// if can move down and it was facing right previously, execute stored actions
				if (robot.isAbleToMove(Direction.DOWN) && previousFacing == Facing.RIGHT)
					DoIETurnRight();

				// if down is a wall and right is clear, move right
				else if (!robot.isAbleToMove(Direction.DOWN) && robot.isAbleToMove(Direction.RIGHT))
					DoIEMoveForward(Facing.RIGHT);

				// if cannot move down or right, turn left to face up
				else if (!robot.isAbleToMove(Direction.DOWN) && !robot.isAbleToMove(Direction.RIGHT))
					DoIETurnLeft();

				// no wall next to robot, do trace back
				else
					DoIEBackTrack();
				break;

			case UP:
				// if can move right and it was facing up previously, execute stored actions
				if (robot.isAbleToMove(Direction.RIGHT) && previousFacing == Facing.UP)
					DoIETurnRight();

				// if right is a wall and up is clear, move up
				else if (!robot.isAbleToMove(Direction.RIGHT) && robot.isAbleToMove(Direction.UP))
					DoIEMoveForward(Facing.UP);

				// if cannot move right or up, turn left to face left
				else if (!robot.isAbleToMove(Direction.RIGHT) && !robot.isAbleToMove(Direction.UP))
					DoIETurnLeft();

				// no wall next to robot, do trace back
				else
					DoIEBackTrack();

				break;

			case DOWN:
				if (robot.isAbleToMove(Direction.LEFT) && previousFacing == Facing.DOWN)
					DoIETurnRight();

				// if left is a wall and down is clear, move right
				else if (!robot.isAbleToMove(Direction.LEFT) && robot.isAbleToMove(Direction.DOWN))
					DoIEMoveForward(Facing.DOWN);

				else if (!robot.isAbleToMove(Direction.LEFT) && !robot.isAbleToMove(Direction.DOWN))
					DoIETurnLeft();

				// no wall next to robot, do trace back
				else
					DoIEBackTrack();

				break;

			default:
				// return false;
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
		 */

		// TODO: @JARRETT added new method to check if robot has asked for reset
		// If yes, then create new Robot
		if (robot.getWantToReset()) {
			System.out.println("JARRETT: ROBOT WANTS TO RESET");
			return -1;
		}

		// once the robot moves, check if its at the start position to end the
		// exploration
		if (robotMoved && robot.getX() == startX && robot.getY() == startY) {
			System.out.println("finished exploration");
			// return true;
			return 1;
		}
		// return false;
		return 0;
	}

	// optimized to reduce the number of turns the robot takes during clearing
	// unknown.
	public boolean DoClearingUnknown() {
		System.out.println("doing clear unknown");
		// while(true) {
		// if the robot is going to a block, iterate step by step
		if (goingToBlock) {
			System.out.println("goingToBlock");
			// if function returns true, means it has reached its current destination(an
			// unexplored area)

			if (robot.doStepFastestPath()) {
				System.out.println("robot.doStepFastestPath()");
				// make sure the robot is facing the unexplored area before finishing the
				// current path
				// if(robot.isFacingArea(nextUnexploredArea[0], nextUnexploredArea[1]))
				goingToBlock = false;
			}

		} else {
			System.out.println("else");
			// if the array is empty then check if robot is back at start position, if not
			// then go back start
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
				// if the grid is not fully explored, then continue exploring
				else if (getMapExplored() < 290)// != map.HEIGHT*map.WIDTH)
				{
					System.out.print("checking if map got properly explored aka 90% of map");
					inputAllUnexploredAreas();
					goingBackToStart = false;
				} else {
					System.out.print("finished exploring part 2, going back to start");

					pathToNextUnexploredArea = new Astar(map.getNodeXY(robot.x, robot.y),
							map.getNodeXY(startX, startY));

					// send it to the robot to store the instruction
					Stack<Node> fast = pathToNextUnexploredArea.getFastestPath();
					robot.setFastestInstruction(fast, startX, startY);

					// updates the pathdrawer for graphics
					PathDrawer.update(robot.x, robot.y, pathToNextUnexploredArea.getFastestPath());
					goingBackToStart = true;

					goingToBlock = true;
				}
			} else {
				hasUnexplored = true;
				System.out.print("update map nodes for A star\n");
				// update map nodes for A star
				map.updateMap();

				// update the unexploredAreas stack, to clear the blocks that were discovered on
				// the way to other blocks
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

						// if the cost is lesser than the current lowest than update the
						// indexOfClosestArea
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

					// send it to the robot to store the instruction
					Stack<Node> fast = pathToNextUnexploredArea.getFastestPath();
					System.out.print("setting fastest instructions\n++++++++++++++++++++++++++++++");
					robot.setFastestInstruction(fast, nextUnexploredArea[0], nextUnexploredArea[1]);

					// updates the pathdrawer for graphics
					PathDrawer.update(robot.x, robot.y, pathToNextUnexploredArea.getFastestPath());
				}
				goingToBlock = true;
				goingBackToStart = false;
			}
		}
		return false;
		// }
	}

	// returns 1 when robot reaches the start point
	// returns 0 in place of false
	// returns -1 if want to reset
	public int DoSimulatorExploration() {
		try {
			while (true) {

				////////////////////////////////// variables for the control of
				////////////////////////////////// exploration(checklist)///////////////////////////
				/*
				 * timeSinceLastUpdate = System.currentTimeMillis() - timeLastupdate;
				 * timeSinceStart += timeSinceLastUpdate; timeLastupdate =
				 * System.currentTimeMillis(); if(timeSinceStart > timeToStop) return true;
				 * float percentageExplored =
				 * (float)getMapExplored()/(float)(Map.HEIGHT*Map.WIDTH);
				 * System.out.print("Percentage Explored: "+percentageExplored+"\n");
				 * System.out.print("Percentage to stop: "+percentageToStop+"\n");
				 * if(percentageExplored >percentageToStop) return true;
				 */
				////////////////////////////////// end of variables for the control of
				////////////////////////////////// exploration(checklist)///////////////////////////

				// only do sleeps when in simulation
				if (simulator)
					Thread.sleep((long) (1000 / stepsPerSecond));

				switch (state) {
					case INITIAL_EXPLORATION:

						// once it reaches the start point, DoInitialExploration() will return 1.
						int DoInitialExplorationResult = DoInitialExploration();
						if (DoInitialExplorationResult == 1) {
							robot.sendMapDescriptor();
							if (exploreUnexplored) {
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
						// once it finishes clearing the map and returning to the start point, function
						// will return true
						// stepsPerSecond set to 2f for debugging purposes
						stepsPerSecond = 2f;
						if (DoClearingUnknown()) {
							return 1;
						}
						map.updateMap();
						PathDrawer.updateUnexploredAreas(unexploredAreas);

						break;
				}

				// update the graphics after each loop
				viz.repaint();
			}

		} catch (Exception e) {
			System.out.println(e);
			System.out.println("error in exploration");
		}

		// return false by default
		// return false;
		return 0;

	}

	public void adjustMapForFastestPath() {
		// count how many obstacles there are, if there are above 30, remove the ones
		// with the lowest score
		// also change all the unexplored areas to explored with score of 1

		// get the map array to work on
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

				// make the unexplore areas explored
				if (mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_EMPTY")
						|| mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {
					map.mapScoreArray[y][x] = -1;
				}
			}
		}

		int lowestScoreIndex = 0;
		int lowestScore = 999;

		// remove the obstacles if there are more than 30 blocks
		while (obstacleAndScore.size() > 30) {
			lowestScoreIndex = 0;
			lowestScore = 999;
			// iterate the list to find out the one with the lowest score
			for (int i = 0; i < obstacleAndScore.size(); i++) {
				// if the current score is less than the recorded lowestScore, then replace it
				if (obstacleAndScore.get(i)[2] < lowestScore) {
					lowestScoreIndex = i;
					lowestScore = obstacleAndScore.get(i)[2];
				}
			}

			// set the obstacle with the lowest score to a empty block
			map.mapScoreArray[obstacleAndScore.get(lowestScoreIndex)[1]][obstacleAndScore
					.get(lowestScoreIndex)[0]] = -1;
			// remove the obstacle with the lowest score
			obstacleAndScore.remove(lowestScoreIndex);
		}

		// update the score map
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
