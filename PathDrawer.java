
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
	static Stack<int[]> unexplored_areas_stack;

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
	public static void update_unexplored_map_areas(Stack<int[]> InUnexploredAreas) {
		unexplored_areas_stack = InUnexploredAreas;
	}

	// remove path after drawing
	public static void removePath() {
		path.removeAllElements();
	}

	// method to draw path based on graphics specifications obtained
	public static void drawPath(Graphics graphics) {

		// if path is empty, stop further processing and return
		if (path == null || path.empty())
			return;

		// set the stroke size
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setStroke(new BasicStroke(10));

		// set color to cyan
		graphics.setColor(Color.CYAN);

		// draw the first line from the robot to the first node
		graphics.drawLine(30 + robotX * Map.sizeofsquare, 30 + robotY * Map.sizeofsquare,
				30 + path.get(path.size() - 1).getX() * Map.sizeofsquare,
				30 + path.get(path.size() - 1).getY() * Map.sizeofsquare);

		// draw the rest of the lines
		for (int i = 0; i < path.size() - 1; i++) {
			graphics.drawLine(30 + path.get(i).getX() * Map.sizeofsquare, 30 + path.get(i).getY() * Map.sizeofsquare,
					30 + path.get(i + 1).getX() * Map.sizeofsquare, 30 + path.get(i + 1).getY() * Map.sizeofsquare);
		}
	}

	// method to draw the grid for the robot_simulator
	public static void drawGrid(Graphics graphics) {

		// if no unexplored area, stop further processing and return
		if (unexplored_areas_stack == null || unexplored_areas_stack.empty())
			return;

		// set color to cyan
		graphics.setColor(Color.RED);

		// draw the the grids
		for (int i = 0; i < unexplored_areas_stack.size(); i++) {
			graphics.fillRect(10 + (unexplored_areas_stack.get(i)[0]) * Map.sizeofsquare,
					10 + (unexplored_areas_stack.get(i)[1]) * Map.sizeofsquare, 38, 38);
		}
	}
}