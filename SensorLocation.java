public enum SensorLocation {
    FACING_TOP(0), FACING_TOPRIGHT(1), FACING_RIGHT(2), FACING_BOTTOMRIGHT(3), FACING_DOWN(4), FACING_BOTTOMLEFT(5),
    FACING_LEFT(6), FACING_TOPLEFT(7);

    int exploration_type = 0;

    // parameterized constructor to initialize packet_type of sensor (based on enum)
    private SensorLocation(int exploration_type) {
        this.exploration_type = exploration_type;
    }

    // get value of sensor from enum mapping
    public int get_type_value() {
        return this.exploration_type;
    }

    // update value
    public void setValue(int update) {
        exploration_type = update;
    }

};