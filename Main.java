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
	// JLabel stepsLabel = new JLabel("No. of Steps to Calibration");
	// JTextField calibrate = new JTextField("");
	// JButton update = new JButton("update");

	public static void main(String[] args) {
		// instantiate objects and variables
		String OS = System.getProperty("os.name").toLowerCase();

		OperatingSystem theOS = OperatingSystem.Windows;
		int wayx = 1;
		int wayy = 1;

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

		////////////////////// IMPORTANT
		////////////////////// VARIABLE///////////////////////////////////////////////////////////////////////
		boolean simulator = true;
		////////////////////// IMPORTANT
		////////////////////// VARIABLE///////////////////////////////////////////////////////////////////////

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
			// for debugging purposes
			MapIterator.printExploredResultsToFile(test, "test.txt");
			MapIterator.ArraytoHex((test));
			map.setMapArray(test);
		}
		RobotInterface theRobot;
		Visualization viz = new Visualization();
		currentState = State.WAITINGFORCOMMAND;
		PacketFactory pf = null;
		Queue<Packet> recvPackets = null;
		Astar as = null;
		Node waypoint = null;

		// the simulator requires the rendering frame to be activated
		if (simulator) {
			// the class and initialisation for the simulated robot
			theRobot = new Robot(1, 18, Direction.RIGHT, map);
			// 3 front, 2 right, 1(Long range) left
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
			// initialize real robot, communications, and sensors
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

		// init the algo classes
		Exploration exe = new Exploration(null, simulator, theRobot, viz, map);
		exe.initStartPoint(1, 18);
		while (currentState != State.DONE) {
			switch (currentState) {

				case IDLE:
					break;

				case WAITINGFORCOMMAND:
					System.out.println(
							"\n------------------------------WaitingForCommand Case------------------------------\n");
					// terminal UI for simulator
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
						int scanType = sc.nextInt();

						// sc.close();
						if (scanType == 1) {
							System.out.println("Please enter x coordinate: ");
							wayx = sc.nextInt();
							System.out.println("Please enter y coordinate: ");
							wayy = sc.nextInt();
							// set robot waypoint
							System.out.println("setting waypoint position at :" + wayx + ", " + wayy);
							waypoint = new Node(wayx, wayy);
							map.setWaypointClear(wayx, wayy);
						} else if (scanType == 2) {
							// set robot robot position
							System.out.println("Please enter x coordinate: ");
							int getx = sc.nextInt();
							System.out.println("Please enter y coordinate: ");
							int gety = sc.nextInt();
							// set robot waypoint
							System.out.println("Moving robot to:" + getx + ", " + gety);
							theRobot.setRobotPos(getx, gety, Direction.RIGHT);
						} else if (scanType == 3) {
							starts = Instant.now();
							currentState = State.EXPLORATION;
						} else if (scanType == 4) {
							starts = Instant.now();
							currentState = State.FASTESTPATH;
						} else if (scanType == 5) {
							// currentState = State.FASTESTPATHHOME;
						} else if (scanType == 6) {
							// currentState = State.RESETFASTESTPATHHOME;
							System.out.println("Reseting Map...");
							map.resetMap();
							theRobot.setface(Direction.RIGHT);
							theRobot.x = 1;
							theRobot.y = 18;
							map.resetMap();
							viz.repaint();
						} else if (scanType == 7)
							theRobot.sendMapDescriptor();
						else if (scanType == 8) {
							System.out.println("Please input intended speed for fastest path\n(1000/stepsPerSecond): ");
							float stepsPerSecond = sc.nextInt();
							theRobot.setSpeed(stepsPerSecond);
						}
						break;
					} else {
						// real robot begin listening to commands from rpi
						System.out.print("\nListening\n");
						// pf.sc.sendPacket("Donald Trump!");
						pf.listen();
						if (recvPackets.isEmpty())
							continue;
						System.out.println("recvPackets is not empty");
						Packet pkt = recvPackets.remove();
						System.out.println(pkt.getType());
						if (pkt.getType() == Packet.SetWayPointi) {
							wayx = pkt.getX();
							wayy = pkt.getY();
							// set robot waypoint
							System.out.println("setting waypoint position at :" + wayx + ", " + wayy);
							waypoint = new Node(wayx, wayy);
							map.setWaypointClear(wayx, wayy);
							currentState = State.WAITINGFORCOMMAND;
						} else if (pkt.getType() == Packet.setRobotPosition) {
							// set robot robot position
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
					// init an explore algo class and call StartExploration()
					System.out.println(
							"---------------------------------Exploration case---------------------------------\n");
					if (!simulator)
						theRobot.LookAtSurroundings();
					int DoSimulatorExplorationResult = exe.DoSimulatorExploration();

					if (simulator) {
						// will return true once the exploration is done(when the robot reaches the
						// starting point again)
						if (DoSimulatorExplorationResult == 1) {
							Scanner sc = new Scanner(System.in);
							theRobot.deactivateSensors();
							System.out.println("Go to fastest path? \n 1=yes \n 2=no");
							int choice = sc.nextInt();
							// sc.close();
							if (choice == 1)
								currentState = State.FASTESTPATH;
							else
								currentState = State.WAITINGFORCOMMAND;
							System.out.println("ending Exploration...");
						} // else
							// currentState = State.WAITINGFORCOMMAND;
					} else {
						// theRobot.LookAtSurroundings();
						// pf.sc.sendPacket(Packet.INITIALCALIBRATE);
						// will return true once the exploration is done(when the robot reaches the
						// starting point again)
						if (DoSimulatorExplorationResult == 1) {
							// send the packet to say that exploration is done
							System.out.println("ending Exploration...");
							// PathDrawer.removePath();
							// theRobot.sendMapDescriptor();
							end = Instant.now();
							System.out.println("Time: " + Duration.between(starts, end));

							((RealRobot) theRobot).sendMapDescriptorRpi();

							pf.sc.sendPacket(Packet.StartExplorationTypeFin + "$");

							// Send map descriptor
							System.out.println(
									"------------------------------Sending this useless descriptor------------------------------\n");
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
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							theRobot.initial_Calibrate();
							pf.setFlag(false);

						} else if (DoSimulatorExplorationResult == -1) {
							System.out.println("JARRETT: Robot wants to reset prematurely. Resetting exe and robot...");
							System.out.println(
									"JARRETT: PLEASE BRING ROBOT BACK TO 1,18 FACING LEFT, THEN SEND IC COMMAND, THEN START EXPLORE (IT SHOULD BE RIGHT FACING AFTER IC)!");

							// viz = new Visualization();
							currentState = State.WAITINGFORCOMMAND;
							// pf = null;
							// recvPackets = null;
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
							// theRobot.setface(Direction.RIGHT);
							// theRobot.x = 1;
							// theRobot.y = 18;
							// REINTIALIZE
							// map = new Map(); // TODO: JARRETT FIX MAP
							exe = new Exploration(null, simulator, theRobot, viz, map);
							exe.initStartPoint(1, 18);
						}
					}
					if (DoSimulatorExplorationResult != 1)
						currentState = State.WAITINGFORCOMMAND;
					break;
				case FASTESTPATHHOME:
					// update the map nodes, then create a new astar path
					map.updateMap();
					// Astar as1 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(5,
					// 10));
					Astar as1 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(1, 18));

					// send it to the robot to handle the instruction
					theRobot.getFastestInstruction(as1.getFastestPath());
					System.out.print("finished fastest path home");

					if (simulator)
						currentState = State.FASTESTPATH;
					else
						currentState = State.WAITINGFORCOMMAND;

					break;
				case RESETFASTESTPATHHOME:
					// update the map nodes, then create a new astar path
					map.updateMap();
					Astar as3 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(1, 18));

					// send it to the robot to handle the instruction
					theRobot.getFastestInstruction(as3.getFastestPath());
					System.out.print("finished fastest path home.. resetting map...");
					map.resetMap();
					theRobot.x = 1;
					theRobot.y = 18;
					// currentState = State.FASTESTPATH;
					currentState = State.WAITINGFORCOMMAND;

					break;
				case FASTESTPATH:
					// init fastest path from startNode to goalNode
					System.out.println(
							"-------------------------------------FastestPath case-----------------------------------\n");
					if (simulator) {
						theRobot.initial_Calibrate();
						// update the map nodes, then create a new astar path
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
							// send it to the robot to handle the instruction
						}
						currentState = State.SENDINGMAPDESCRIPTOR;
						System.out.print("finished fastest path TO GOAL");

					} else {
						// update the map nodes, then create a new astar path
						// testing empty map
						// set empty

						pf.sendCMD(Packet.StartFastestPathTypeOkANDROID + "$"); // B // TODO: @JARRETT see if necessary
																				// or not
						pf.sendCMD(Packet.StartFastestPathTypeOkARDURINO + "$"); // A // TODO: @JARRETT see if necessary
																					// or not
						// NOTE
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

						// create the int[] frm the stack
						// send the whole entire packet to rpi
						viz.repaint();
						end = Instant.now();
						System.out.println("Time : " + Duration.between(starts, end));
						// currentState = State.SENDINGMAPDESCRIPTOR;
						currentState = State.WAITINGFORCOMMAND; // TODO: @JARRETT REMOVED COS OF STUPID BUG WHICH ROBOT
																// DIES BEFORE REACHING END GOAL

					}
					break;

				case SENDINGMAPDESCRIPTOR:
					System.out.println(
							"------------------------------Sending this useless descriptor------------------------------\n");
					System.out.println("doing map descriptor");

					MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
					MapIterator.printExploredResultsToHex("ExplorationHex.txt");

					MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
					MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
					if (!simulator) {
						pf.sendCMD("B:stat:Exploration mdf:" + MapIterator.mapDescriptorP1Hex + "$");
						pf.sendCMD("B:stat:Obstacle mdf:" + MapIterator.mapDescriptorP2Hex + "$");

						pf.sendCMD("B:stat:finish_exe_mdf$");
					}
					currentState = State.WAITINGFORCOMMAND;
			}
		}
	}

	SocketClient cs = new SocketClient("192.168.4.4", 8081);
}