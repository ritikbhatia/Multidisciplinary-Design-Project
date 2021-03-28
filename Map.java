import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Map {

	int explored_maze = 0;
	double percentage_explored;
	final static int map_width = 15;
	final static int map_height = 20;
	final static int sizeofsquare = 30;
	public static Node[][] NodeArray = new Node[map_height][map_width];

	int[][] simulated_map;
	int[][] grid_map_array;
	int[][] map_scores;
	int[][] map_array_2;

	public void setMapArray(int[][] grid_map_array) {
		
		init_neighbours();
		nodes_init();
		this.grid_map_array = grid_map_array;

		for (int y = 0; y < simulated_map.length; y++) {
			for (int x = 0; x < simulated_map[y].length; x++) {
				simulated_map[y][x] = grid_map_array[y][x];
			}
		}
	}

	public void set_map_scores() {
		map_scores = new int[map_height][map_width];

		for (int y = 0; y < map_scores.length; y++) {
			for (int x = 0; x < map_scores[y].length; x++) {
				map_scores[y][x] = 0;

			}
		}

		int confirmed_score = -1000;
		map_scores[17][0] = confirmed_score;
		map_scores[19][0] = confirmed_score;
		map_scores[18][0] = confirmed_score;
		map_scores[17][1] = confirmed_score;
		map_scores[19][1] = confirmed_score;
		map_scores[18][1] = confirmed_score;
		map_scores[17][2] = confirmed_score;
		map_scores[19][2] = confirmed_score;
		map_scores[18][2] = confirmed_score;

		map_scores[0][12] = confirmed_score;
		map_scores[0][13] = confirmed_score;
		map_scores[0][14] = confirmed_score;
		map_scores[1][12] = confirmed_score;
		map_scores[1][13] = confirmed_score;
		map_scores[1][14] = confirmed_score;
		map_scores[2][12] = confirmed_score;
		map_scores[2][13] = confirmed_score;
		map_scores[2][14] = confirmed_score;
		
		simulated_map = new int[map_height][map_width];

	}

	public Map(int[][] mapDisplay) {
		grid_map_array = mapDisplay;
		nodes_init();
		init_neighbours();
		set_map_scores();

	}

	public int[][] get_grid_map_array() {
		return grid_map_array;
	}

	public void setMapScore(int x, int y, int score) {
		map_scores[y][x] += score;
	}

	// Revise probabilities grid map simulation display & real robot
	public void update_map_and_score() {
		for (int y = 0; y < map_scores.length; y++) {
			for (int x = 0; x < map_scores[y].length; x++) {
				if (map_scores[y][x] == 0) {
					grid_map_array[y][x] = ExplorationTypes.exploration_type_to_int("UNEXPLORED_EMPTY");

				} else if (map_scores[y][x] > 0) {
					grid_map_array[y][x] = ExplorationTypes.exploration_type_to_int("OBSTACLE");

				} else if (map_scores[y][x] < 0) {
					grid_map_array[y][x] = ExplorationTypes.exploration_type_to_int("EMPTY");

				}
			}
		}
	}

	public void map_update() {
		compute_clearence();
		nodes_init();
		init_neighbours();	
	}

	public void FPoptimise() {
		for (int i = 0; i < grid_map_array.length; i++) {
			for (int j = 0; j < grid_map_array[i].length; j++) {
				if (map_scores[i][j] == -1)
					map_array_2[i][j] = ExplorationTypes.exploration_type_to_int("UNEXPLORED_EMPTY");
			}
		}
	}

	public Node get_node_with_xy_coordinates(int x, int y) {
		return NodeArray[y][x];
	}

	public void set_map_to_map() {
		System.out.println();
		for (int i = 0; i < map_array_2.length; i++) {
			for (int j = 0; j < map_array_2[i].length; j++) {
				map_array_2[i][j] = grid_map_array[i][j];
				System.out.print(map_array_2[i][j]);
			}
			System.out.println();
		}
	}

	public void nodes_init() {
		for (int r = 0; r < map_height; r++) {
			for (int c = 0; c < map_width; c++) {

				NodeArray[r][c] = new Node(c, r);

				if (grid_map_array[r][c] != 0) {
					NodeArray[r][c].set_maze_obstacle(true);
				} else
					NodeArray[r][c].set_maze_obstacle(false);

			}
		}
		System.out.println();
	}

	public void setEmpty() {
		this.grid_map_array = new int[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

	}

	public void resetMap() {
		this.grid_map_array = new int[][] { { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 } };
		set_map_scores();
		map_update();
	}

	public void init_neighbours() {

		for (int r = 0; r < map_height; r++) {
			for (int c = 0; c < map_width; c++) {
				// Moving left
				if (c > 0) {
					Node left = NodeArray[r][c - 1];
					NodeArray[r][c].add_neighbours(left);
					NodeArray[r][c].set_left(left);
				}
				// Moving up
				if (r > 0) {
					Node up = NodeArray[r - 1][c];
					NodeArray[r][c].add_neighbours(up);
					NodeArray[r][c].setUp(up);
				}
				// Moving right
				if (c < map_width - 1) {
					Node right = NodeArray[r][c + 1];
					NodeArray[r][c].add_neighbours(right);
					NodeArray[r][c].setRight(right);

				}
				// Moving down
				if (r < map_height - 1) {
					Node down = NodeArray[r + 1][c];
					NodeArray[r][c].add_neighbours(down);
					NodeArray[r][c].setDown(down);
				}
			}
		}
	}

	// Calculate clearence distance between robot and obstacle
	public void compute_clearence() {
		Node node;
		for (int r = 0; r < map_height; r++) {
			columnloop: for (int c = 0; c < map_width; c++) {
				node = NodeArray[r][c];
				node.set_clearence(0);

				if (node.is_obstacle()) {
					node.set_clearence(0);
					continue;
				}

				for (int i = -1; i < 2; i++) {
					for (int j = -1; j < 2; j++) {
						if (r + i >= map_height || r + i < 0 || c + j >= map_width || c + j < 0) {
							continue columnloop;
						}
						if (NodeArray[r + i][c + j].is_obstacle())
							continue columnloop;
					}
				}
				node.set_clearence(3);
			}
		}
	}

	public void set_way_point(int x, int y) {
		int confirmed_score = -9999;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (y + i < map_height && x + j < map_width && (y + i) >= 0 && (x + j) >= 0)
					map_scores[y + i][x + j] = confirmed_score;
			}
		}
	}

	public void paint_grid_map(Graphics graphics) {

		// Initialise map variables
		String mapScore = "";
		int distanceY = 0;
		int distanceX = 0;
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < grid_map_array.length; i++) {
			// Paint Y
			distanceX = 0;
			graphics.drawRect(10, 10 + distanceY, sizeofsquare, sizeofsquare);

			for (int j = 0; j < grid_map_array[i].length; j++) {
				// Paint X
				graphics.setColor(Color.WHITE);
				graphics.drawRect(10 + distanceX, 10 + distanceY, sizeofsquare, sizeofsquare);

				if (grid_map_array[i][j] == ExplorationTypes.exploration_type_to_int("OBSTACLE")) {
					// Draw obstacle
					graphics.setColor(Color.BLACK);
					graphics.fillRect(10 + distanceX, 10 + distanceY, sizeofsquare, sizeofsquare);

				} else if ((i == 0 && j == 12) || (i == 0 && j == 13) || (i == 0 && j == 14) || (i == 1 && j == 12)
				|| (i == 1 && j == 13) || (i == 1 && j == 14) || (i == 2 && j == 12) || (i == 2 && j == 13)
				|| (i == 2 && j == 14)) {
					// Draw goal position
					graphics.setColor(Color.GREEN);
					graphics.fillRect(10 + distanceX, 10 + distanceY, sizeofsquare, sizeofsquare);

				} else if ((i == 18 && j == 0) || (i == 18 && j == 1) || (i == 18 && j == 2) || (i == 17 && j == 0)
						|| (i == 17 && j == 1) || (i == 17 && j == 2) || (i == 19 && j == 0) || (i == 19 && j == 1)
						|| (i == 19 && j == 2)) {
					// Draw start position
					graphics.setColor(Color.YELLOW);
					graphics.fillRect(10 + distanceX, 10 + distanceY, sizeofsquare, sizeofsquare);

				} else if (grid_map_array[i][j] == ExplorationTypes.exploration_type_to_int("UNEXPLORED_EMPTY")) {
					graphics.setColor(Color.LIGHT_GRAY);
					graphics.fillRect(10 + distanceX, 10 + distanceY, sizeofsquare, sizeofsquare);

				}

				graphics.setColor(Color.BLACK);
				graphics2d.drawString(Integer.toString(map_scores[i][j]), 20 + distanceX, 30 + distanceY);
				distanceX += sizeofsquare;
			}

			distanceY += sizeofsquare;
		}

	}

	public void generateMapDescriptor() {
		GridMapIterator.print_explored_results_to_file(grid_map_array, "Map.txt");
	}

	public Map() {
		explored_maze = 0;
		this.grid_map_array = new int[][] { { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 } };
		this.map_array_2 = new int[][] { { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 },
				{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 } };
		set_map_scores();
	}

}
