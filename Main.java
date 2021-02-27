import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

enum State {
	IDLE, WAITINGFORCOMMAND, EXPLORATION, FASTESTPATHHOME, FASTESTPATH, DONE, RESETFASTESTPATHHOME,
	SENDINGMAPDESCRIPTOR,
}

enum OperatingSystem {
	Windows, Linux
}

public class Main {

	public static void main(String[] args) {

		// Initialisation of program objects & variables
		String OS = System.getProperty("os.name").toLowerCase();

		OperatingSystem theOS = OperatingSystem.Windows;
		int wayx = 1;
		int wayy = 1;

		// variable to indicate whether exploration for fastest path is done or not
		// boolean explorationForFastestPathDone = false;

		if (OS.indexOf("win") >= 0)
			theOS = OperatingSystem.Windows;
		else if ((OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0))
			theOS = OperatingSystem.Linux;

		State currentState;
		JFrame frame = null;

		if (theOS == OperatingSystem.Windows) {
			frame = new JFrame("MDP Simulator");
			frame.setSize(600, 820);
		}
		Instant starts = null;
		Instant end = null;
		Map map = new Map();

		////////////////////////// simulator variable //////////////////////////
		boolean simulator = false;

		if (simulator) {
			int[][] test = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
					{ 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
					{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

			// int[][] test = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 1, 1, 1, 1 },
			// { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0 },
			// { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 } };

			// Generated to facilitate debugging
			MapIterator.printExploredResultsToFile(test, "test.txt");
			MapIterator.ArraytoHex((test));
			// map.setMapArray(test);

		}

		//////////////////////////////////////// comment out below and uncomment
		//////////////////////////////////////// statement above to get hardcoded maze
		//////////////////////////////////////// //////////////////////////////

		// remove below statement if exploration but keep if doing fastest path
		// map.setMapArray(MapIterator.IterateTextFile("p1Hex.txt", "p2Hex.txt"));

		// Initialisation of program objects & variables
		RobotInterface theRobot;
		Visualization viz = new Visualization();
		currentState = State.WAITINGFORCOMMAND;
		PacketFactory pf = null;
		Queue<Packet> recvPackets = null;
		Astar as = null;
		Node waypoint = null;

		// Activate rendering frame for simulation
		if (simulator) {
			// Initialise robot simulation
			theRobot = new Robot(1, 18, Direction.RIGHT, map);

			// SENSOR POSITIONS: 3 front, 2 right, 1 (long range) left
			Sensor s1 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 1, theRobot.x, theRobot.y);
			Sensor s2 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);
			Sensor s3 = new Sensor(3, SensorLocation.FACING_DOWN, 1, 0, theRobot.x, theRobot.y);
			Sensor s4 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, -1, theRobot.x, theRobot.y);
			Sensor s5 = new Sensor(3, SensorLocation.FACING_DOWN, -1, 0, theRobot.x, theRobot.y);
			Sensor s6 = new Sensor(5, SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);

			Sensor[] Sensors = { s1, s2, s3, s4, s5, s6 };
			theRobot.addSensors(Sensors);

			viz.setRobot(theRobot);
			theRobot.setViz(viz);
			theRobot.setSpeed(10f);

			if (theOS == OperatingSystem.Windows) {
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
			}
		} else {
			///////////////////////////////////////////////////////////////////
			// // make simulator do exploration first
			// // Initialise robot simulation

			// Robot simRobot = new Robot(1, 18, Direction.RIGHT, map);

			// // SENSOR POSITIONS: 3 front, 2 right, 1 (long range) left
			// Sensor sim1 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 1, simRobot.x,
			// simRobot.y);
			// Sensor sim2 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 0, simRobot.x,
			// simRobot.y);
			// Sensor sim3 = new Sensor(3, SensorLocation.FACING_DOWN, 1, 0, simRobot.x,
			// simRobot.y);
			// Sensor sim4 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, -1, simRobot.x,
			// simRobot.y);
			// Sensor sim5 = new Sensor(3, SensorLocation.FACING_DOWN, -1, 0, simRobot.x,
			// simRobot.y);
			// Sensor sim6 = new Sensor(5, SensorLocation.FACING_TOP, 1, -1, simRobot.x,
			// simRobot.y);

			// Sensor[] simSensors = { sim1, sim2, sim3, sim4, sim5, sim6 };
			// simRobot.addSensors(simSensors);

			// viz.setRobot(simRobot);
			// simRobot.setViz(viz);
			// simRobot.setSpeed(10f);

			// if (theOS == OperatingSystem.Windows) {
			// frame.getContentPane().add(viz);
			// frame.setVisible(true);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame.setResizable(true);
			// }
			// Exploration simexe = new Exploration(null, true, simRobot, viz, map);
			// simexe.initStartPoint(1, 18);
			// simexe.DoSimulatorExploration();

			///////////////////////////////////////////////////////////////////

			// Set up real robot, sensors & communications
			recvPackets = new LinkedList<Packet>();
			pf = new PacketFactory(recvPackets);
			theRobot = new RealRobot(1, 18, Direction.RIGHT, map, pf);

			RealSensor s1 = new RealSensor(4, SensorLocation.FACING_RIGHT, 1, 1, theRobot.x, theRobot.y);
			RealSensor s2 = new RealSensor(4, SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);
			RealSensor s3 = new RealSensor(4, SensorLocation.FACING_DOWN, 1, 1, theRobot.x, theRobot.y);
			RealSensor s4 = new RealSensor(4, SensorLocation.FACING_RIGHT, 1, -1, theRobot.x, theRobot.y);
			RealSensor s5 = new RealSensor(4, SensorLocation.FACING_DOWN, -1, 1, theRobot.x, theRobot.y);
			RealSensor s6 = new RealSensor(5, SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);

			RealSensor[] Sensors = { s1, s2, s3, s4, s5, s6 };
			theRobot.addSensors(Sensors);
			viz.setRobot(theRobot);
			theRobot.setViz(viz);

			if (theOS == OperatingSystem.Windows) {
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
				System.out.print("Waiting for command");
				currentState = State.WAITINGFORCOMMAND;
			}

		}

		Exploration exe = new Exploration(null, simulator, theRobot, viz, map);
		exe.initStartPoint(1, 18);

		while (currentState != State.DONE) {
			switch (currentState) {

				case IDLE:
					break;

				case WAITINGFORCOMMAND:
					System.out.println(
							"\n------------------------------WaitingForCommand Case------------------------------\n");

					// Command line interface for simulator
					if (simulator) {
						Scanner sc = new Scanner(System.in);
						System.out.println("Please enter state:");
						System.out.println("1) Set Waypoint");
						System.out.println("2) Set robot position");
						System.out.println("3) Start Exploration");
						System.out.println("4) Start Fastest Path");
						System.out.println("5) Stop Instruction");
						System.out.println("6) Reset Instruction");
						System.out.println("7) Get Map Descriptor");
						System.out.println("8) Set speed for fastest path, default = 10f");
						System.out.println("9) Set maximum percentage of exploration");
						System.out.println("10) Set maximum time for exploration");

						int scanType = sc.nextInt();

						if (scanType == 1) {
							System.out.println("Please enter x coordinate: ");
							wayx = sc.nextInt();
							System.out.println("Please enter y coordinate: ");
							wayy = sc.nextInt();

							// Determine waypoint
							System.out.println("setting waypoint position at :" + wayx + ", " + wayy);
							waypoint = new Node(wayx, wayy);

							map.setWaypointClear(wayx, wayy);

						} else if (scanType == 2) {
							// Determine robot position
							System.out.println("Please enter x coordinate: ");
							int getx = sc.nextInt();
							System.out.println("Please enter y coordinate: ");
							int gety = sc.nextInt();

							// Determine waypoint
							System.out.println("Moving robot to:" + getx + ", " + gety);

							theRobot.setRobotPos(getx, gety, Direction.RIGHT);

						} else if (scanType == 3) {
							starts = Instant.now();
							currentState = State.EXPLORATION;

						} else if (scanType == 4) {
							// map.setMapArray(MapIterator.IterateTextFile("p1Hex.txt", "p2Hex.txt"));
							// exe.adjustMapForFastestPath();
							// map.updateMapWithScore();
							// map.setScoreArray();
							// map.updateMap();
							// viz.repaint();
							starts = Instant.now();
							currentState = State.FASTESTPATH;

						} else if (scanType == 5) {
							// TODO: Implement this case
							// currentState = State.FASTESTPATHHOME;

						} else if (scanType == 6) {
							// TODO: Implement this case
							// TODO: Check hardcoded robot x,y values
							// currentState = State.RESETFASTESTPATHHOME;
							System.out.println("Reseting Map...");
							map.resetMap();
							theRobot.setface(Direction.RIGHT);
							theRobot.x = 1;
							theRobot.y = 18;
							map.resetMap();
							viz.repaint();

						} else if (scanType == 7) {
							theRobot.sendMapDescriptor();
						} else if (scanType == 8) {
							System.out.println("Please input intended speed for fastest path\n(1000/stepsPerSecond): ");
							float stepsPerSecond = sc.nextInt();
							theRobot.setSpeed(stepsPerSecond);
						} else if (scanType == 9) {
							System.out.println("Please input maximum percentage of maze exploration: ");
							float maxPercent = sc.nextFloat();
							exe.setMazeCoverage(maxPercent);
						} else if (scanType == 10) {
							System.out.println("Please input maximum time for maze exploration (minutes:seconds) ");
							String time = sc.next();
							String[] parts = time.split(":");
							float minutes = Float.parseFloat(parts[0]);
							float seconds = Float.parseFloat(parts[1]);
							exe.setMaxExplorationTime(minutes, seconds);
						}

						break;

					} else {
						// RPi commands being sent to real robot
						System.out.print("\nReal Robot Listening for RPi commands\n");

						pf.listen();
						if (recvPackets.isEmpty()) {
							System.out.print("\nreceive packets is empty\n");
							continue;
						}

						System.out.println("recvPackets is not empty");
						Packet pkt = recvPackets.remove();
						System.out.println(pkt.getType());

						if (pkt.getType() == Packet.SetWayPointi) {
							wayx = pkt.getX();
							wayy = 19 - pkt.getY();

							// Assign waypoint position for robot
							System.out.println("setting waypoint position at :" + wayx + ", " + wayy);
							waypoint = new Node(wayx, wayy);
							map.setWaypointClear(wayx, wayy);

							///////////////////// RITIK - CODE ADDED HERE ///////////////

							// once waypoint received, perform exploration, get fastest path based on it

							// Initialise robot simulation
							Robot simRobot = new Robot(1, 18, Direction.RIGHT, map);

							// SENSOR POSITIONS: 3 front, 2 right, 1 (long range) left
							Sensor sim1 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 1, simRobot.x, simRobot.y);
							Sensor sim2 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 0, simRobot.x, simRobot.y);
							Sensor sim3 = new Sensor(3, SensorLocation.FACING_DOWN, 1, 0, simRobot.x, simRobot.y);
							Sensor sim4 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, -1, simRobot.x, simRobot.y);
							Sensor sim5 = new Sensor(3, SensorLocation.FACING_DOWN, -1, 0, simRobot.x, simRobot.y);
							Sensor sim6 = new Sensor(5, SensorLocation.FACING_TOP, 1, -1, simRobot.x, simRobot.y);

							Sensor[] simSensors = { sim1, sim2, sim3, sim4, sim5, sim6 };
							simRobot.addSensors(simSensors);

							viz.setRobot(simRobot);
							simRobot.setViz(viz);
							simRobot.setSpeed(10f);

							if (theOS == OperatingSystem.Windows) {
								frame.getContentPane().add(viz);
								frame.setVisible(true);
								frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
								frame.setResizable(true);
							}

							Exploration simexe = new Exploration(null, true, simRobot, viz, map);
							simexe.initStartPoint(1, 18);
							simexe.DoSimulatorExploration();

							// calculate P1 and P2
							// send to Android so that they can update their virtual map
							MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
							MapIterator.printExploredResultsToHex("ExplorationHex.txt");
							MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
							MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
							pf.sendCMD("B:stat:Exploration mdf:" + MapIterator.mapDescriptorP1Hex + "$");
							pf.sendCMD("B:stat:Obstacle mdf:" + MapIterator.mapDescriptorP2Hex + "$");

							// once exploration complete, reset viz to original REAL robot
							viz.setRobot(theRobot);
							viz.repaint();

							///////////////////// RITIK - CODE ENDS HERE ///////////////

							currentState = State.WAITINGFORCOMMAND;

						} else if (pkt.getType() == Packet.setRobotPosition) {
							// Assign robot position
							System.out.println("-----------------Setting robot position--------------");
							theRobot.setRobotPos(pkt.getX(), pkt.getY(), pkt.getDirection());

						} else if (pkt.getType() == Packet.StartExploration) {
							starts = Instant.now();
							currentState = State.EXPLORATION;

						} else if (pkt.getType() == Packet.StartFastestPath) {
							starts = Instant.now();
							currentState = State.FASTESTPATH;

						} else if (pkt.getType() == Packet.StopInstruction) {
							currentState = State.FASTESTPATHHOME;

						} else if (pkt.getType() == Packet.ResetInstruction) {
							currentState = State.RESETFASTESTPATHHOME;
							System.out.println("Resetting Map...");

							map.resetMap();
							theRobot.setface(Direction.RIGHT);
							theRobot.x = 1;
							theRobot.y = 18;
							map.resetMap();
							viz.repaint();

						} else if (pkt.getType() == Packet.GETMAPi)
							theRobot.sendMapDescriptor();

						else {
							System.out.println("Invalid Packet!!");
							continue;
						}
						break;
					}

				case EXPLORATION:
					// Initialise algorithmic exploration, invoke StartExploration()
					System.out.println(
							"---------------------------------Exploration case---------------------------------\n");

					if (!simulator)
						theRobot.LookAtSurroundings();
					int DoSimulatorExplorationResult = exe.DoSimulatorExploration();

					if (simulator) {

						// Exploration completes, robot returns to start position again and return TRUE
						if (DoSimulatorExplorationResult == 1) {
							Scanner sc = new Scanner(System.in);
							theRobot.deactivateSensors();
							System.out.println("Go to fastest path? \n 1 = Yes \n 2 = No");
							int choice = sc.nextInt();

							if (choice == 1)
								currentState = State.FASTESTPATH;
							else
								currentState = State.WAITINGFORCOMMAND;
							System.out.println("Ending Exploration...");

						}
					} else {

						// Exploration completes, robot returns to start position again and return TRUE
						if (DoSimulatorExplorationResult == 1) {

							// Transmit packet to convey exploration is complete
							System.out.println("Ending Exploration...");

							end = Instant.now();
							System.out.println("Time: " + Duration.between(starts, end));

							((RealRobot) theRobot).sendMapDescriptorRpi();

							pf.sc.sendPacket(Packet.StartExplorationTypeFin + "$");

							// Transmit map descriptor information
							System.out.println(
									"------------------------------ Sending the map descriptor to files ------------------------------\n");
							System.out.println("doing map descriptor");
							MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
							MapIterator.printExploredResultsToHex("ExplorationHex.txt");
							MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
							MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
							pf.sendCMD("B:stat:Exploration mdf:" + MapIterator.mapDescriptorP1Hex + "$");
							pf.sendCMD("B:stat:Obstacle mdf:" + MapIterator.mapDescriptorP2Hex + "$");
							pf.sendCMD("B:stat:finish_exe_mdf$");
							currentState = State.WAITINGFORCOMMAND;

							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							theRobot.initial_Calibrate();
							pf.setFlag(false);

						} else if (DoSimulatorExplorationResult == -1) {
							System.out.println("Robot resetting prematurely.");
							System.out.println(
									"Please bring robot back to (1,18) facing left, send IC command, then start explore with robot facing right after IC");

							currentState = State.WAITINGFORCOMMAND;
							as = null;
							waypoint = null;

							// RobotInterface theRobot;
							theRobot = new RealRobot(1, 18, Direction.RIGHT, map, pf);

							RealSensor s1 = new RealSensor(4, SensorLocation.FACING_RIGHT, 1, 1, theRobot.x,
									theRobot.y);
							RealSensor s2 = new RealSensor(4, SensorLocation.FACING_RIGHT, 1, 0, theRobot.x,
									theRobot.y);
							RealSensor s3 = new RealSensor(4, SensorLocation.FACING_DOWN, 1, 1, theRobot.x, theRobot.y);
							RealSensor s4 = new RealSensor(4, SensorLocation.FACING_RIGHT, 1, -1, theRobot.x,
									theRobot.y);
							RealSensor s5 = new RealSensor(4, SensorLocation.FACING_DOWN, -1, 1, theRobot.x,
									theRobot.y);
							RealSensor s6 = new RealSensor(5, SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);

							RealSensor[] Sensors = { s1, s2, s3, s4, s5, s6 };
							theRobot.addSensors(Sensors);
							viz.setRobot(theRobot);
							theRobot.setViz(viz);

							map.resetMap();

							exe = new Exploration(null, simulator, theRobot, viz, map);
							exe.initStartPoint(1, 18);
						}
					}

					if (DoSimulatorExplorationResult != 1)
						currentState = State.WAITINGFORCOMMAND;
					break;

				case FASTESTPATHHOME:

					// Revise nodes and create new A* solution path
					map.updateMap();
					Astar as1 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(1, 18));

					// Transmit instructions to robot
					theRobot.getFastestInstruction(as1.getFastestPath());
					System.out.print("Completed fastest path home");

					if (simulator)
						currentState = State.FASTESTPATH;
					else
						currentState = State.WAITINGFORCOMMAND;
					break;

				case RESETFASTESTPATHHOME:
					// Revise nodes and create new A* solution path
					map.updateMap();
					Astar as3 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(1, 18));

					// Transmit instructions to robot
					theRobot.getFastestInstruction(as3.getFastestPath());
					System.out.print("Completed fastest path home, resetting map");

					map.resetMap();
					theRobot.x = 1;
					theRobot.y = 18;

					currentState = State.WAITINGFORCOMMAND;

					break;

				case FASTESTPATH:
					// Initialise fastest path from start to goal node
					System.out.println(
							"------------------------------------- Fastest Path Case -----------------------------------\n");

					// ///////////////////// RITIK - CODE SEGMENT ADDED HERE!!!
					// ///////////////////// //////////////////////////////

					// // perform simulator exploration before fastest path
					// if (!explorationForFastestPathDone) {
					// DoSimulatorExplorationResult = exe.DoSimulatorExploration();
					// explorationForFastestPathDone = true;
					// Scanner sc = new Scanner(System.in);
					// System.out.println("Exploration for fastest path done..");
					// System.out.print("Enter 1 to continue to fastest path: ");
					// sc.nextInt();
					// }

					// ///////////////////// RITIK - END OF CODE SEGMENT!!!
					// ///////////////////// //////////////////////////////////

					if (simulator) {
						theRobot.initial_Calibrate();

						// Revise nodes and create new A* solution path
						map.updateMap();
						waypoint = map.getNodeXY(wayx, wayy);
						Astar as31 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), waypoint);
						Astar as2 = new Astar(waypoint, map.getNodeXY(13, 1));
						Stack<Node> as31GFP = as31.getFastestPath();

						if (as31GFP.isEmpty()) {
							Astar as4 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(13, 1));
							PathDrawer.update(theRobot.x, theRobot.y, as4.getFastestPath());
							theRobot.getFastestInstruction(as4.getFastestPath());
							PathDrawer.removePath();

						} else {
							PathDrawer.update(theRobot.x, theRobot.y, as31GFP);
							theRobot.getFastestInstruction(as31.getFastestPath());
							PathDrawer.update(theRobot.x, theRobot.y, as2.getFastestPath());
							theRobot.getFastestInstruction(as2.getFastestPath());
							PathDrawer.removePath();

						}
						// Transmit instructions to robot
						currentState = State.SENDINGMAPDESCRIPTOR;
						System.out.print("finished fastest path TO GOAL");

					} else {
						// Revise nodes and create new A* solution path
						// Test empty map | Assign to empty
						pf.sendCMD(Packet.StartFastestPathTypeOkANDROID + "$"); // TODO: Check if this is required
						pf.sendCMD(Packet.StartFastestPathTypeOkARDURINO + "$"); // TODO: Check if this is required

						map.updateMap();
						Stack<Node> stack = null;

						if (waypoint == null) {
							System.out.println("NO waypoint.");
							as = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(13, 1));
							stack = as.getFastestPath();
							theRobot.getFastestInstruction(stack);

						} else {
							int x1 = waypoint.getX();
							int y1 = waypoint.getY();
							System.out.println("going to fastest path with waypoint of " + x1 + "," + y1);
							waypoint = map.getNodeXY(x1, y1);
							as = new Astar(map.getNodeXY(theRobot.x, theRobot.y), waypoint);
							Astar as2 = new Astar(waypoint, map.getNodeXY(13, 1));
							stack = as2.getFastestPath();
							Stack<Node> stack2 = as.getFastestPath();

							if (!stack.isEmpty() && !stack2.isEmpty()) {
								System.out.println("going to waypoint...");
								stack.addAll(stack2);
								theRobot.getFastestInstruction(stack);

							} else {
								System.out.println("failed to go to waypoint");
								System.out.println("going to goal without waypoint");
								as = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(13, 1));
								stack = as.getFastestPath();
								theRobot.getFastestInstruction(stack);
							}
						}

						// Transmit all data packet to RPi
						viz.repaint();
						end = Instant.now();
						pf.sc.sendPacket(Packet.StartFastestPathTypeFin + "$");
						System.out.println("Time : " + Duration.between(starts, end));
						// currentState = State.SENDINGMAPDESCRIPTOR;
						currentState = State.WAITINGFORCOMMAND; // TODO: Check if this needs to be removed : bug causes
																// robot to die before reaching end goal?

					}
					break;

				case SENDINGMAPDESCRIPTOR:
					System.out.println(
							"------------------------------ Sending the map descriptor to files ------------------------------\n");
					System.out.println("doing map descriptor");

					MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
					MapIterator.printExploredResultsToHex("ExplorationHex.txt");

					MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
					MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
					if (!simulator) {
						pf.sendCMD("B:stat:Exploration mdf:" + MapIterator.mapDescriptorP1Hex + "$");
						pf.sendCMD("B:stat:Obstacle mdf:" + MapIterator.mapDescriptorP2Hex + "$");

						// pf.sendCMD("B:stat:finish_exe_mdf$");
					}
					currentState = State.WAITINGFORCOMMAND;
			}
		}
	}

	// TODO: Configure IP & port ?
	SocketClient cs = new SocketClient("192.168.9.9", 8081);
}
