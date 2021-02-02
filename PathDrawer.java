
// specify required imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Stack;

public final class PathDrawer {

	// declare variables like x and y coordinates of robot, the path etc
	static int robotX;
	static int robotY;
	static Stack<Node> path;
	static Stack<int[]> unexploredAreas;

	// default constructor for the class
	PathDrawer() {
	}

	// update class variables
	public static void update(int robotx, int roboty, Stack<Node> inPath) {
		robotX = robotx;
		robotY = roboty;
		path = inPath;
	}

	// update unexplored areas
	public static void updateUnexploredAreas(Stack<int[]> InUnexploredAreas) {
		unexploredAreas = InUnexploredAreas;
	}

	// remove path after drawing
	public static void removePath() {
		path.removeAllElements();
	}

	// method to draw path based on graphics specifications obtained
	public static void drawPath(Graphics g) {

		// if path is empty, stop further processing and return
		if (path == null || path.empty())
			return;

		// set the stroke size
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(10));

		// set color to cyan
		g.setColor(Color.CYAN);

		// draw the first line from the robot to the first node
		g.drawLine(30 + robotX * Map.sizeofsquare, 30 + robotY * Map.sizeofsquare,
				30 + path.get(path.size() - 1).getX() * Map.sizeofsquare,
				30 + path.get(path.size() - 1).getY() * Map.sizeofsquare);

		// draw the rest of the lines
		for (int i = 0; i < path.size() - 1; i++) {
			g.drawLine(30 + path.get(i).getX() * Map.sizeofsquare, 30 + path.get(i).getY() * Map.sizeofsquare,
					30 + path.get(i + 1).getX() * Map.sizeofsquare, 30 + path.get(i + 1).getY() * Map.sizeofsquare);
		}
	}

	// method to draw the grid for the simulator
	public static void drawGrid(Graphics g) {

		// if no unexplored area, stop further processing and return
		if (unexploredAreas == null || unexploredAreas.empty())
			return;

		// set color to cyan
		g.setColor(Color.RED);

		// draw the the grids
		for (int i = 0; i < unexploredAreas.size(); i++) {
			g.fillRect(10 + (unexploredAreas.get(i)[0]) * Map.sizeofsquare,
					10 + (unexploredAreas.get(i)[1]) * Map.sizeofsquare, 38, 38);
		}
	}
}