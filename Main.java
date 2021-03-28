import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

enum OperatingSystem {
	Windows, 
	Linux
}

enum State {
	IDLE, 
	WAITINGFORCOMMAND, 
	EXPLORATION, 
	FASTESTPATHHOME, 
	FASTESTPATH, 
	DONE, 
	RESETFASTESTPATHHOME,
	SENDINGMAPDESCRIPTOR,
}

public class Main {

	public static void main(String[] args) {

		// Initialisation of program objects & variables
		RobotInterface theRobot;
		Visualization viz = new Visualization();
		currentState = State.WAITINGFORCOMMAND;
		PacketFactory pf = null;
		Queue<Packet> recvPackets = null;
		A_star_search as = null;
		Node waypoint = null;

		// Initialisation of program objects & variables
		State currentState;
		JFrame frame = null;

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


		if (theOS == OperatingSystem.Windows) {
			frame = new JFrame("MDP Simulator");
			frame.setSize(600, 820);
		}

		Instant starts = null;
		Instant end = null;
		Map map = new Map();

		////////////////////////// robot_simulator variable //////////////////////////
		boolean robot_simulator = true;

		if (robot_simulator) {
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
			GridMapIterator.print_explored_results_to_file(test, "test.txt");
			GridMapIterator.array_to_hex((test));
			map.setMapArray(GridMapIterator.parse_text_file_for_map_info("p1Hex.txt", "p2Hex.txt"));

		}

		//////////////////////////////////////// comment out below and uncomment
		//////////////////////////////////////// statement above to get hardcoded maze
		//////////////////////////////////////// //////////////////////////////

		// remove below statement if exploration but keep if doing fastest path
		// map.setMapArray(GridMapIterator.parse_text_file_for_map_info("p1Hex.txt", "p2Hex.txt"));


		// Activate rendering frame for simulation
		if (robot_simulator) {
			// Initialise robot simulation
			theRobot = new Robot(1, 18, Direction.RIGHT, map);

			viz.setRobot(theRobot);
			theRobot.setViz(viz);
			theRobot.setSpeed(10f);

			// SENSOR POSITIONS: 3 front, 2 right, 1 (long range) left
			Sensor s1 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 1, theRobot.x, theRobot.y);
			Sensor s2 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);
			Sensor s3 = new Sensor(3, SensorLocation.FACING_DOWN, 1, 0, theRobot.x, theRobot.y);
			Sensor s4 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, -1, theRobot.x, theRobot.y);
			Sensor s5 = new Sensor(3, SensorLocation.FACING_DOWN, -1, 0, theRobot.x, theRobot.y);
			Sensor s6 = new Sensor(5, SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);

			Sensor[] Sensors = { s1, s2, s3, s4, s5, s6 };
			theRobot.addSensors(Sensors);

		
			if (theOS == OperatingSystem.Windows) {
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
			}
		} else {
			///////////////////////////////////////////////////////////////////
			// // make robot_simulator do exploration first
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
			// simexe.initialise_robot_start_position(1, 18);
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

		Exploration exe = new Exploration(null, robot_simulator, theRobot, viz, map);
		exe.initialise_robot_start_position(1, 18);

		while (currentState != State.DONE) {
			switch (currentState) {

			case IDLE:
				break;

			case WAITINGFORCOMMAND:
				System.out.println(
						"\n------------------------------WaitingForCommand Case------------------------------\n");

				// Command line interface for robot_simulator
				if (robot_simulator) {
					Scanner sc = new Scanner(System.in);
					System.out.println("Please enter state:");
					System.out.println("1) Set Waypoint");
					System.out.println("2) Set robot position");
					System.out.println("3) Start Exploration");
					System.out.println("4) Start Fastest Path");
					System.out.println("5) Reset Instruction");
					System.out.println("6) Get Map Descriptor");
					System.out.println("7) Set speed for fastest path, default = 10f");
					System.out.println("8) Set maximum percentage of exploration");
					System.out.println("9) Set maximum time for exploration");

					int scanType = sc.nextInt();

					if (scanType == 1) {
						System.out.println("Please enter x coordinate: ");
						wayx = sc.nextInt();
						System.out.println("Please enter y coordinate: ");
						wayy = sc.nextInt();

						// Determine waypoint
						System.out.println("setting waypoint position at :" + wayx + ", " + wayy);
						waypoint = new Node(wayx, wayy);

						map.set_way_point(wayx, wayy);

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
						// map.setMapArray(GridMapIterator.parse_text_file_for_map_info("p1Hex.txt", "p2Hex.txt"));
						// exe.fastest_path_map_adjustment();
						// map.update_map_and_score();
						// map.set_map_scores();
						// map.map_update();
						// viz.repaint();
						starts = Instant.now();
						currentState = State.FASTESTPATH;

					} else if (scanType == 5) {
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

					} else if (scanType == 6) {
						theRobot.sendMapDescriptor();

					} else if (scanType == 7) {
						System.out.println("Please input intended speed for fastest path\n(1000/robot_steps_per_second): ");
						float robot_steps_per_second = sc.nextInt();
						theRobot.setSpeed(robot_steps_per_second);

					} else if (scanType == 8) {
						System.out.println("Please input maximum percentage of maze exploration: ");
						float maxPercent = sc.nextFloat();
						exe.set_maze_coverage_percentage(maxPercent);

					} else if (scanType == 9) {
						System.out.println("Please input maximum time for maze exploration (time_minutes:time_seconds) ");
						String time = sc.next();
						String[] parts = time.split(":");
						float time_minutes = Float.parseFloat(parts[0]);
						float time_seconds = Float.parseFloat(parts[1]);
						exe.set_exploration_time_limit(time_minutes, time_seconds);

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

					if (pkt.getType() == Packet.set_way_point_instruction) {
						wayx = pkt.getX();
						wayy = 19 - pkt.getY();

						// Assign waypoint position for robot
						System.out.println("setting waypoint position at :" + wayx + ", " + wayy);
						waypoint = new Node(wayx, wayy);
						map.set_way_point(wayx, wayy);

						///////////////////// RITIK - CODE ADDED HERE ///////////////

						// once waypoint received, perform exploration, get fastest path based on it

						// Initialise robot simulation
						Robot simRobot = new Robot(1, 18, Direction.RIGHT, map);

						viz.setRobot(simRobot);
						simRobot.setViz(viz);
						simRobot.setSpeed(10f);

						// SENSOR POSITIONS: 3 front, 2 right, 1 (long range) left
						Sensor sim1 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 1, simRobot.x, simRobot.y);
						Sensor sim2 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, 0, simRobot.x, simRobot.y);
						Sensor sim3 = new Sensor(3, SensorLocation.FACING_DOWN, 1, 0, simRobot.x, simRobot.y);
						Sensor sim4 = new Sensor(3, SensorLocation.FACING_RIGHT, 1, -1, simRobot.x, simRobot.y);
						Sensor sim5 = new Sensor(3, SensorLocation.FACING_DOWN, -1, 0, simRobot.x, simRobot.y);
						Sensor sim6 = new Sensor(5, SensorLocation.FACING_TOP, 1, -1, simRobot.x, simRobot.y);

						Sensor[] simSensors = { sim1, sim2, sim3, sim4, sim5, sim6 };
						simRobot.addSensors(simSensors);

						if (theOS == OperatingSystem.Windows) {
							frame.getContentPane().add(viz);
							frame.setVisible(true);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setResizable(true);
						}

						Exploration simexe = new Exploration(null, true, simRobot, viz, map);
						simexe.initialise_robot_start_position(1, 18);
						simexe.DoSimulatorExploration();

						// calculate P1 and P2
						// send to Android so that they can update their virtual map
						GridMapIterator.print_explored_results_to_file(map.get_grid_map_array(), "theExplored.txt");
						GridMapIterator.print_explored_results_to_hex("ExplorationHex.txt");
						GridMapIterator.print_obstacle_results_to_file(map.get_grid_map_array(), "theObstacle.txt");
						GridMapIterator.print_obstacle_results_to_hex("ObstacleHex.txt");

						// send exploration mdf to android for them to update the map
						pf.sendCMD("B:stat:Exploration mdf:" + GridMapIterator.P1_map_descriptor_hex + "$");
						pf.sendCMD("B:stat:Obstacle mdf:" + GridMapIterator.P2_map_descriptor_hex + "$");

						// send waypoint to android for them to update the map
						pf.sendCMD("B:stat:waypoint:" + String.valueOf(wayx) + ":" + String.valueOf(19 - wayy) + "$");

						// once exploration complete, reset viz to original REAL robot
						viz.setRobot(theRobot);
						viz.repaint();

						///////////////////// RITIK - CODE ENDS HERE ///////////////

						currentState = State.WAITINGFORCOMMAND;

					} else if (pkt.getType() == Packet.start_exploration) {
						starts = Instant.now();
						currentState = State.EXPLORATION;

					} else if (pkt.getType() == Packet.stop_instruction) {
						currentState = State.FASTESTPATHHOME;

					} else if (pkt.getType() == Packet.reset_instruction) {
						currentState = State.RESETFASTESTPATHHOME;
						System.out.println("Resetting Map...");

						map.resetMap();
						theRobot.setface(Direction.RIGHT);
						theRobot.x = 1;
						theRobot.y = 18;
						map.resetMap();
						viz.repaint();

					} else if (pkt.getType() == Packet.start_fastest_path) {
						starts = Instant.now();
						currentState = State.FASTESTPATH;
						
					} else if (pkt.getType() == Packet.GETMAPi)
						theRobot.sendMapDescriptor();
					
					else if (pkt.getType() == Packet.set_robot_position) {
						// Assign robot position
						System.out.println("-----------------Setting robot position--------------");
						theRobot.setRobotPos(pkt.getX(), pkt.getY(), pkt.getDirection());
	
					}

					else {
						System.out.println("Invalid Packet!!");
						continue;
					}
					break;
				}
			
			case FASTESTPATHHOME:

				// Revise nodes and create new A* solution path
				map.map_update();
				A_star_search as1 = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), map.get_node_with_xy_coordinates(1, 18));

				// Transmit instructions to robot
				theRobot.retrieve_fastest_instruction(as1.retrieve_fastest_path());
				System.out.print("Completed fastest path home");

				if (robot_simulator)
					currentState = State.FASTESTPATH;
				else
					currentState = State.WAITINGFORCOMMAND;
				break;

			case EXPLORATION:
				// Initialise algorithmic exploration, invoke start_exploration()
				System.out.println(
						"---------------------------------Exploration case---------------------------------\n");

				if (!robot_simulator)
					theRobot.LookAtSurroundings();

				int DoSimulatorExplorationResult = exe.DoSimulatorExploration();

				if (robot_simulator) {

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
						GridMapIterator.print_explored_results_to_file(map.get_grid_map_array(), "theExplored.txt");
						GridMapIterator.print_explored_results_to_hex("ExplorationHex.txt");
						GridMapIterator.print_obstacle_results_to_file(map.get_grid_map_array(), "theObstacle.txt");
						GridMapIterator.print_obstacle_results_to_hex("ObstacleHex.txt");
						pf.sendCMD("B:stat:Exploration mdf:" + GridMapIterator.P1_map_descriptor_hex + "$");
						pf.sendCMD("B:stat:Obstacle mdf:" + GridMapIterator.P2_map_descriptor_hex + "$");
						pf.sendCMD("B:stat:finish_exe_mdf$");

						// Ritik - added line
						pf.sendCMD("A:cmd:cali_final$");
						//
						currentState = State.WAITINGFORCOMMAND;

						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// RITIK - add during exploration //
						// theRobot.initial_Calibrate();
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

						map.resetMap();

						exe = new Exploration(null, robot_simulator, theRobot, viz, map);
						exe.initialise_robot_start_position(1, 18);
					}
				}

				if (DoSimulatorExplorationResult != 1)
					currentState = State.WAITINGFORCOMMAND;
				break;

			case RESETFASTESTPATHHOME:
				// Revise nodes and create new A* solution path
				map.map_update();
				A_star_search as3 = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), map.get_node_with_xy_coordinates(1, 18));

				// Transmit instructions to robot
				theRobot.retrieve_fastest_instruction(as3.retrieve_fastest_path());
				System.out.print("Completed fastest path home, resetting map");

				map.resetMap();
				theRobot.x = 1;
				theRobot.y = 18;

				currentState = State.WAITINGFORCOMMAND;

				break;

			case SENDINGMAPDESCRIPTOR:
				System.out.println(
						"------------------------------ Sending the map descriptor to files ------------------------------\n");
				System.out.println("doing map descriptor");

				GridMapIterator.print_explored_results_to_file(map.get_grid_map_array(), "theExplored.txt");
				GridMapIterator.print_explored_results_to_hex("ExplorationHex.txt");

				GridMapIterator.print_obstacle_results_to_file(map.get_grid_map_array(), "theObstacle.txt");
				GridMapIterator.print_obstacle_results_to_hex("ObstacleHex.txt");
				if (!robot_simulator) {
					pf.sendCMD("B:stat:Exploration mdf:" + GridMapIterator.P1_map_descriptor_hex + "$");
					pf.sendCMD("B:stat:Obstacle mdf:" + GridMapIterator.P2_map_descriptor_hex + "$");

					// pf.sendCMD("B:stat:finish_exe_mdf$");
				}
				currentState = State.WAITINGFORCOMMAND;

			case FASTESTPATH:
				// Initialise fastest path from start to goal node
				System.out.println(
						"------------------------------------- Fastest Path Case -----------------------------------\n");

				// ///////////////////// RITIK - CODE SEGMENT ADDED HERE!!!
				// ///////////////////// //////////////////////////////

				// // perform robot_simulator exploration before fastest path
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

				if (robot_simulator) {
					theRobot.initial_Calibrate();

					// Revise nodes and create new A* solution path
					map.map_update();
					waypoint = map.get_node_with_xy_coordinates(wayx, wayy);
					A_star_search as31 = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), waypoint);
					A_star_search as2 = new A_star_search(waypoint, map.get_node_with_xy_coordinates(13, 1));
					Stack<Node> as31GFP = as31.retrieve_fastest_path();

					if (as31GFP.isEmpty()) {
						A_star_search as4 = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), map.get_node_with_xy_coordinates(13, 1));
						PathDrawer.update(theRobot.x, theRobot.y, as4.retrieve_fastest_path());
						theRobot.retrieve_fastest_instruction(as4.retrieve_fastest_path());
						PathDrawer.removePath();

					} else {
						PathDrawer.update(theRobot.x, theRobot.y, as31GFP);
						theRobot.retrieve_fastest_instruction(as31.retrieve_fastest_path());
						PathDrawer.update(theRobot.x, theRobot.y, as2.retrieve_fastest_path());
						theRobot.retrieve_fastest_instruction(as2.retrieve_fastest_path());
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

					map.map_update();
					Stack<Node> stack = null;

					if (waypoint == null) {
						System.out.println("NO waypoint.");
						as = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), map.get_node_with_xy_coordinates(13, 1));
						stack = as.retrieve_fastest_path();
						theRobot.retrieve_fastest_instruction(stack);

					} else {
						int x1 = waypoint.getX();
						int y1 = waypoint.getY();
						System.out.println("going to fastest path with waypoint of " + x1 + "," + y1);
						waypoint = map.get_node_with_xy_coordinates(x1, y1);
						as = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), waypoint);
						A_star_search as2 = new A_star_search(waypoint, map.get_node_with_xy_coordinates(13, 1));
						stack = as2.retrieve_fastest_path();
						Stack<Node> stack2 = as.retrieve_fastest_path();

						if (!stack.isEmpty() && !stack2.isEmpty()) {
							System.out.println("going to waypoint...");
							stack.addAll(stack2);
							theRobot.retrieve_fastest_instruction(stack);

						} else {
							System.out.println("failed to go to waypoint");
							System.out.println("going to goal without waypoint");
							as = new A_star_search(map.get_node_with_xy_coordinates(theRobot.x, theRobot.y), map.get_node_with_xy_coordinates(13, 1));
							stack = as.retrieve_fastest_path();
							theRobot.retrieve_fastest_instruction(stack);
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

			}
		}
	}

	// TODO: Configure IP & port 
	SocketClient cs = new SocketClient("192.168.9.9", 8081);
}
