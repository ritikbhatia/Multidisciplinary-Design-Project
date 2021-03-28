import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable {

    // Node variables
    Node parent_node_in_path;
    List neighbors_list = new ArrayList<Node>();
    Node left;
    Node right;
    Node up;
    Node down;

    // Cost of path computation
    float total_path_cost;
    float cost_estimated_to_goal_node;

    // Grid variables
    boolean is_obstacle;
    int clearance;
    boolean is_virtual_wall;

    // Directional/Positional coordinates 
    final int x;
    final int y;
    Facing facing;

    // Compare node costs 
    public int compareTo(Object other_node) {
        float current_value = this.getCost();
        float other_value = ((Node) other_node).getCost();

        float v = current_value - other_value;
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    // GET method: Cost 
    public float getCost() {
        return total_path_cost + cost_estimated_to_goal_node;
    }

    // Initialise node coordinates
    public Node() {
        x = 0;
        y = 0;
    }

    // Assign node coordinates
    public Node(int x_coordinate, int y_coordinate) {
        this.x = x_coordinate;
        this.y = y_coordinate;
    }

    public void add_neighbours(Node node) {
        neighbors_list.add(node);
    }


    // Compute path cost 
    public float getCost(Node node, Node end_node, boolean is_start_node) {
        return this.total_path_cost + get_node_weight(node, is_start_node);
    }

    public List getNeighbors() {
        return neighbors_list;
    }

    // Compute node cost 
    public float calculate_path_cost(Node node) {
        Node goal = (Node) node;

        float dx = Math.abs(this.x - goal.x);
        float dy = Math.abs(this.y - goal.y);
        return (dx + dy);
    }

    // Compute node cost 
    public float get_node_weight(boolean is_start_node, Node some_node) {
        Node node = (Node) some_node;
        setFacing();

        if ((compareX(node) == 1 && facing == Facing.RIGHT || compareX(node) == -1 && facing == Facing.LEFT
                || compareY(node) == 1 && facing == Facing.UP || compareY(node) == -1 && facing == Facing.DOWN)
                && !is_start_node) {
            return 100;
        }

        // Add edge cost to penalise turns 
        return 1500;
    }

    // SET method: Robot's orientation 
    public void setFacing(Facing face) {
        this.facing = face;
    }

    public void set_clearence(int clearance) {
        this.clearance = clearance;
    }

    // Compare node coordinates
    public int compareX(Node node) {
        return node.x > this.x ? 1 : node.x < this.x ? -1 : 0;
    }

    public int compareY(Node node) {
        return node.y > this.y ? 1 : node.y < this.y ? -1 : 0;
    }

    // Grid map navigation methods 
    public void set_maze_obstacle(boolean val) {
        this.is_obstacle = val;
    }

    public boolean is_obstacle() {
        return is_obstacle;
    }

    // Initialise robot's orientation
    public void setFacing() {

        if (this.parent_node_in_path == null) {
            this.facing = Facing.RIGHT;
            return;
        }

        if (compareX((Node) this.parent_node_in_path) == 1) {
            this.facing = Facing.LEFT;
        } else if (compareX((Node) this.parent_node_in_path) == -1) {
            this.facing = Facing.RIGHT;
        } else if (compareY((Node) this.parent_node_in_path) == 1) {
            this.facing = Facing.DOWN;
        } else if (compareY((Node) this.parent_node_in_path) == -1) {
            this.facing = Facing.UP;
        }
    }

    public int getClearance() {
        return clearance;
    }

    // SET methods: Define directions
    public void set_left(Node left) {
        this.left = left;
    }

    // GET methods: Directions
    public Node getLeft() {
        return left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setUp(Node up) {
        this.up = up;
    }

    public Node getRight() {
        return right;
    }

    public void setDown(Node down) {
        this.down = down;
    }

    public Node getDown() {
        return down;
    }
    
    public Node getUp() {
        return up;
    }

    // GET methods: X & Y coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
