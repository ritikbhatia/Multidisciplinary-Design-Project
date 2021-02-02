public enum ExplorationTypes {
    EMPTY(0), OBSTACLE(1), UNEXPLORED_EMPTY(2), UNEXPLORED_OBSTACLE(3);

    int type_val = 2;

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

    // enum constructor - cannot be public or protected
    private ExplorationTypes(int type_val) {
        this.type_val = type_val;
    }
};