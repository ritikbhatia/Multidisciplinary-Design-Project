import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Visualization extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RobotInterface robot;

	public RobotInterface getRobot() {
		return robot;
	}

	public void setRobot(RobotInterface robot) {
		this.robot = robot;
	}

	public Visualization() {
	}

	public Visualization(RobotInterface robot) {
		this.robot = robot;
	}

	protected void paintComponent(Graphics g) {
		robot.map.paintMap(g);
		robot.paintRobot(g);
		PathDrawer.drawGrid(g);
		PathDrawer.drawPath(g);
		super.paintComponent(g);

	}

}