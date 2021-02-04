import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable {
    
    // Directional/Positional coordinates 
    final int x;
    final int y;
    Facing facing;

    // Grid variables
    boolean isObstacle;
    boolean isVirtualWall;
    int clearance;

    // Node variables
    Node pathParent;
    Node up;
    Node down;
    Node left;
    Node right;
    List neighbors = new ArrayList<Node>();

    // Cost of path computation
    float costFromStart;
    float estimatedCostToGoal;

    // GET method: Cost 
    public float getCost() {
        return costFromStart + estimatedCostToGoal;
    }

    // Compare node costs 
    public int compareTo(Object other) {
        float thisValue = this.getCost();
        float otherValue = ((Node) other).getCost();

        float v = thisValue - otherValue;
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    // Initialise node coordinates
    public Node() {
        x = 0;
        y = 0;
    }

    // Assign node coordinates
    public Node(int xi, int yi) {
        this.x = xi;
        this.y = yi;
    }

    // GET methods: X & Y coordinates
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addNeighbors(Node node) {
        neighbors.add(node);
    }

    // SET methods: Define directions
    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setUp(Node up) {
        this.up = up;
    }

    public void setDown(Node down) {
        this.down = down;
    }

    // GET methods: Directions
    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Node getUp() {
        return up;
    }

    public Node getDown() {
        return down;
    }

    // Compute path cost 
    public float getCost(Node node, Node goalNode, boolean isStartNode) {
        return this.costFromStart + getWeight(node, isStartNode);
    }

    public List getNeighbors() {
        return neighbors;
    }

    // Compare node coordinates
    public int compareX(Node node) {
        return node.x > this.x ? 1 : node.x < this.x ? -1 : 0;
    }

    public int compareY(Node node) {
        return node.y > this.y ? 1 : node.y < this.y ? -1 : 0;
    }

    // Compute node cost 
    public float getEstimatedCost(Node node) {
        Node goal = (Node) node;

        float dx = Math.abs(this.x - goal.x);
        float dy = Math.abs(this.y - goal.y);
        return (dx + dy);
    }

    // Compute node cost 
    public float getWeight(Node anode, boolean isStartNode) {
        Node node = (Node) anode;
        setFacing();

        if ((compareX(node) == 1 && facing == Facing.RIGHT || compareX(node) == -1 && facing == Facing.LEFT
                || compareY(node) == 1 && facing == Facing.UP || compareY(node) == -1 && facing == Facing.DOWN)
                && !isStartNode) {
            return 100;
        }

        // Add edge cost to penalise turns 
        return 1500;
    }

    // SET method: Robot's orientation 
    public void setFacing(Facing face) {
        this.facing = face;
    }

    // Initialise robot's orientation
    public void setFacing() {

        if (this.pathParent == null) {
            this.facing = Facing.RIGHT;
            return;
        }

        if (compareX((Node) this.pathParent) == 1) {
            this.facing = Facing.LEFT;
        } else if (compareX((Node) this.pathParent) == -1) {
            this.facing = Facing.RIGHT;
        } else if (compareY((Node) this.pathParent) == 1) {
            this.facing = Facing.DOWN;
        } else if (compareY((Node) this.pathParent) == -1) {
            this.facing = Facing.UP;
        }
    }

    // Grid map navigation methods 
    public void setObstacle(boolean val) {
        this.isObstacle = val;
    }

    public void setVirtualWall(boolean val) {
        if (val) {
            this.isVirtualWall = true;
        }
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public void setClearance(int clearance) {
        this.clearance = clearance;
    }

    public int getClearance() {
        return clearance;
    }

}
