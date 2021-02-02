
// import required packages
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

// inherit from JComponent, the base class for all Swing components
// use this the visualize and create the simulator for various tasks
public class Visualization extends JComponent {
	private static final long serialVersionUID = 1L;
	public RobotInterface robot;

	// return the robot
	public RobotInterface getRobot() {
		return robot;
	}

	// specify the robot
	public void setRobot(RobotInterface robot) {
		this.robot = robot;
	}

	// default constructor
	public Visualization() {
	}

	// paramterized constructor
	public Visualization(RobotInterface robot) {
		this.robot = robot;
	}

	// method to draw the components and perform visualization
	protected void paintComponent(Graphics g) {
		robot.map.paintMap(g);
		robot.paintRobot(g);
		PathDrawer.drawGrid(g);
		PathDrawer.drawPath(g);
		super.paintComponent(g);
	}
}