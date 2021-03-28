public enum ExplorationTypes {
    // Exploration ENUMs
    EMPTY(0), OBSTACLE(1), UNEXPLORED_EMPTY(2), UNEXPLORED_OBSTACLE(3);

    // ENUM selector
    int exploration_type = 2;

    // Constructor for ENUM
    // Required accessibility to be private
    private ExplorationTypes(int exploration_type) {
        this.exploration_type = exploration_type;
    }

    // GET method: exploration_type
    public int get_type_value() {
        return this.exploration_type;
    }
 
    public static int exploration_type_to_int(String a) {
        switch (a) {
            case "OBSTACLE":
                return 1;
            case "EMPTY":
                return 0;
            case "UNEXPLORED_OBSTACLE":
                return 3;
            case "UNEXPLORED_EMPTY":
                return 2;
        }
        return -1;
    };

};