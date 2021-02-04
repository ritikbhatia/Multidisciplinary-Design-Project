public enum ExplorationTypes {
    // Exploration ENUMs
    EMPTY(0), OBSTACLE(1), UNEXPLORED_EMPTY(2), UNEXPLORED_OBSTACLE(3);

    // ENUM selector
    // TODO: check for hardcoded value 2
    int type_val = 2;

    // GET method: type_val
    public int getValue() {
        return this.type_val;
    }
 
    public static int toInt(String a) {
        switch (a) {
            case "EMPTY":
                return 0;
            case "OBSTACLE":
                return 1;
            case "UNEXPLORED_EMPTY":
                return 2;
            case "UNEXPLORED_OBSTACLE":
                return 3;
        }
        return -1;
    };

    // Constructor for ENUM
    // Required accessibility to be private
    private ExplorationTypes(int type_val) {
        this.type_val = type_val;
    }
};