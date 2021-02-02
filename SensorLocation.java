public enum SensorLocation {
    FACING_TOP(0), FACING_TOPRIGHT(1), FACING_RIGHT(2), FACING_BOTTOMRIGHT(3), FACING_DOWN(4), FACING_BOTTOMLEFT(5),
    FACING_LEFT(6), FACING_TOPLEFT(7);

    int type_val = 0;

    private SensorLocation(int type_val) {
        this.type_val = type_val;
    }

    public int getValue() {
        return this.type_val;
    }

    public void setValue(int update) {
        type_val = update;
    }

};