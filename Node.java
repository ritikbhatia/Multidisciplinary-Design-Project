import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable {
    final int x;
    final int y;
    Facing facing;

    boolean isObstacle;
    boolean isVirtualWall;
    int clearance;

    Node pathParent;
    Node up;
    Node down;
    Node left;
    Node right;
    List neighbors = new ArrayList<Node>();

    float costFromStart;
    float estimatedCostToGoal;

    public float getCost() {
        return costFromStart + estimatedCostToGoal;
    }

    // compare the f value and the lower f value put to the front
    public int compareTo(Object other) {
        float thisValue = this.getCost();
        float otherValue = ((Node) other).getCost();

        float v = thisValue - otherValue;
        return (v > 0) ? 1 : (v < 0) ? -1 : 0;
    }

    public Node() {
        x = 0;
        y = 0;
    }

    public Node(int xi, int yi) {
        this.x = xi;
        this.y = yi;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addNeighbors(Node node) {
        neighbors.add(node);
    }

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

    public float getCost(Node node, Node goalNode, boolean isStartNode) {
        return this.costFromStart + getWeight(node, isStartNode);
    }

    public float getEstimatedCost(Node node) {
        Node goal = (Node) node;

        float dx = Math.abs(this.x - goal.x);
        float dy = Math.abs(this.y - goal.y);
        return (dx + dy);
    }

    public List getNeighbors() {
        return neighbors;
    }

    // if
    public int compareX(Node node) {
        return node.x > this.x ? 1 : node.x < this.x ? -1 : 0;
    }

    public int compareY(Node node) {
        return node.y > this.y ? 1 : node.y < this.y ? -1 : 0;
    }

    public float getWeight(Node anode, boolean isStartNode) {
        Node node = (Node) anode;
        setFacing();

        if ((compareX(node) == 1 && facing == Facing.RIGHT || compareX(node) == -1 && facing == Facing.LEFT
                || compareY(node) == 1 && facing == Facing.UP || compareY(node) == -1 && facing == Facing.DOWN)
                && !isStartNode) {
            return 100;
        }

        // Penalize turns by adding edge cost
        return 1500;
    }

    public void setFacing(Facing face) {
        this.facing = face;
    }

    public void setFacing() {
        if (this.pathParent == null) {
            // Set robot's initial orientation
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