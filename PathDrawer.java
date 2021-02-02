import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Stack;

public final class PathDrawer {

	static int robotX;
	static int robotY;
	static Stack<Node> path;

	static Stack<int[]> unexploredAreas;

	PathDrawer() {

	}

	public static void update(int robotx, int roboty, Stack<Node> inPath) {
		robotX = robotx;
		robotY = roboty;
		path = inPath;

	}

	public static void updateUnexploredAreas(Stack<int[]> InUnexploredAreas) {
		unexploredAreas = InUnexploredAreas;
	}

	public static void removePath() {
		// dispose of the path after drawing
		path.removeAllElements();
	}

	public static void drawPath(Graphics g) {
		if (path == null || path.empty())
			return;

		// set the stroke size to a larger width
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(10));

		// set color to cyan
		g.setColor(Color.CYAN);

		// draw the first line from the robot to the first node
		g.drawLine(30 + robotX * Map.sizeofsquare, 30 + robotY * Map.sizeofsquare,
				30 + path.get(path.size() - 1).getX() * Map.sizeofsquare,
				30 + path.get(path.size() - 1).getY() * Map.sizeofsquare);

		// g.drawLine(30+robotX*Map.sizeofsquare,30+robotY*Map.sizeofsquare,
		// 30+path.get(0).getX()*Map.sizeofsquare,30+path.get(0).getY()*Map.sizeofsquare);

		// draw the rest of the lines
		for (int i = 0; i < path.size() - 1; i++) {
			// x_g=10+(x-1)*Map.sizeofsquare
			g.drawLine(30 + path.get(i).getX() * Map.sizeofsquare, 30 + path.get(i).getY() * Map.sizeofsquare,
					30 + path.get(i + 1).getX() * Map.sizeofsquare, 30 + path.get(i + 1).getY() * Map.sizeofsquare);
		}

		// dispose of the path after drawing
		// path.removeAllElements();
	}

	public static void drawGrid(Graphics g) {
		if (unexploredAreas == null || unexploredAreas.empty())
			return;

		// set color to cyan
		g.setColor(Color.RED);

		// draw the the grids
		for (int i = 0; i < unexploredAreas.size(); i++) {
			// g.drawRect(10+(unexploredAreas.get(i)[0]-1)*Map.sizeofsquare,
			// 10+(unexploredAreas.get(i)[1]-1)*Map.sizeofsquare, 40, 40);
			g.fillRect(10 + (unexploredAreas.get(i)[0]) * Map.sizeofsquare,
					10 + (unexploredAreas.get(i)[1]) * Map.sizeofsquare, 38, 38);
		}
	}
}