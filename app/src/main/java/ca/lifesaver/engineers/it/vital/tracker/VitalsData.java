package ca.lifesaver.engineers.it.vital.tracker;


public class VitalsData {
    public int heartRate;
    public int oxygenLevel;
    public float bodyTemp;

    public VitalsData() {
        // Empty constructor required for Firestore
    }

    public VitalsData(int heartRate, int oxygenLevel, float bodyTemp) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bodyTemp = bodyTemp;
    }
}