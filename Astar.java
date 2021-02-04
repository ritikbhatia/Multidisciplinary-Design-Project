import java.util.*;

public class Astar {
    // A* variables 
    // Default accessibility: Private
    Node startNode;
    Node goalNode;
    Robot robot;
    int cost;

    // A* constructor to initialise values 
    public Astar(Node start, Node end) {
        startNode = start;
        goalNode = end;
        cost = 0;
    }

    // GET method: Cost
    public int getCost() {
        return cost;
    };

    // Nested class: PriorityList
    public static class PriorityList extends LinkedList {
    
        // Add nodes to Priority List
        public void add(Comparable object) {
            
            // Implement Insertion Sort
            for (int i = 0; i < size(); i++) {
                if (object.compareTo(get(i)) <= 0) {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }

    }

    // Return path of nodes to given goal node as stack
    protected Stack<Node> constructPath(Node node) {
        Stack path = new Stack();
        
        while (node.pathParent != null) {
            path.push(node);
            node = node.pathParent;
            cost++;
        }

        return path;
    }

    // Discover fastest path from start to goal node
    public Stack<Node> findPath(Node startNode, Node goalNode) {

        // TODO: check for hardcoded value 3
        int size = 3;
        boolean isStartNode = true;

        PriorityList openList = new PriorityList();
        LinkedList closedList = new LinkedList();

        // Initialise start node
        startNode.costFromStart = 0;
        startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        openList.add(startNode);

        // Begin exploration of grid map
        while (!openList.isEmpty()) {

            // Get node from head of list
            Node node = (Node) openList.removeFirst();

            // Determine if node is Start node
            if (node == startNode)
                isStartNode = true;
            else
                isStartNode = false;

            ((Node) node).setFacing();

            // Determine if node is goal node
            if (node == goalNode) {
                return constructPath(goalNode);
            }

            // Get list of node neighbours
            List neighbors = node.getNeighbors();

            // Iterate through list of node neighbours
            for (int i = 0; i < neighbors.size(); i++) {

                // Extract neighbour node information
                Node neighborNode = (Node) neighbors.get(i);
                boolean isOpen = openList.contains(neighborNode);
                boolean isClosed = closedList.contains(neighborNode);
                boolean isObstacle = (neighborNode).isObstacle();
                int clearance = neighborNode.getClearance();
                float costFromStart = node.getCost(neighborNode, goalNode, isStartNode) + 1;

                System.out.println("costFromStart: " + costFromStart);

                // Check 1. if node neighbours have not been explored OR 2. if shorter path to neighbour node exists 
                if ((!isOpen && !isClosed) || costFromStart < neighborNode.costFromStart) {
                    neighborNode.pathParent = node;
                    neighborNode.costFromStart = costFromStart;
                    neighborNode.estimatedCostToGoal = neighborNode.getEstimatedCost(goalNode);
                    
                    // Add neighbour node to openList if 1. node not in openList/closedList AND 2. robot can reach
                    if (!isOpen && !isObstacle && size == clearance) {
                        openList.add(neighborNode);
                    }
                }
            }
            closedList.add(node);
        }

        // openList empty; no path found
        System.out.println("NULL DATA! no fastest path.");
        return new Stack<Node>();
    }

    // Calculate fastest path from start to goal node
    public Stack<Node> getFastestPath() {
        return findPath(startNode, goalNode);
    }

    // Find robot direction 
    public Facing getRobotDirection(Node node, Facing facing) {

        Node nodeparent = (Node) node.pathParent;

        if (nodeparent == null) {
            return facing;
        }

        // if nodeA of current node < nodeA of parent, then move to left 
        if (node.compareX(nodeparent) == 1) {
            return Facing.LEFT;
        } else if (node.compareX(nodeparent) == -1) {
            return Facing.RIGHT;
        } else if (node.compareY(nodeparent) == 1) {
            return Facing.DOWN;
        } else if (node.compareY(nodeparent) == -1) {
            return Facing.UP;
        }

        return null;
    }

}
