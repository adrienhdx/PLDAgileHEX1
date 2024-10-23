package source.model;

public class Delivery {
    private Vertex pickUpPt;
    private Vertex deliveryPt;
    private int pickUpTime;
    private int deliveryTime;
    private DeliveryState state;

    public Delivery(Vertex pickUpPt, Vertex deliveryPt, int pickUpTime, int deliveryTime, DeliveryState state) {
        this.pickUpPt = pickUpPt;
        this.deliveryPt = deliveryPt;
        this.pickUpTime = pickUpTime;
        this.deliveryTime = deliveryTime;
        this.state = state;
    }

    // Getters et Setters

    public Vertex getPickUpPt() { return pickUpPt; }
    public void setPickUpPt(Vertex pickUpPt) { this.pickUpPt = pickUpPt; }

    public Vertex getDeliveryPt() { return deliveryPt; }
    public void setDeliveryPt(Vertex deliveryPt) { this.deliveryPt = deliveryPt; }

    public int getPickUpTime() { return pickUpTime; }
    public void setPickUpTime(int pickUpTime) { this.pickUpTime = pickUpTime; }

    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }

    public DeliveryState getState() { return state; }
    public void setState(DeliveryState state) { this.state = state; }


    @Override
    public String toString() {
        return "Delivery{pickUpPt=" + pickUpPt + ", deliveryPt=" + deliveryPt + ", pickUpTime=" + pickUpTime + ", deliveryTime=" + deliveryTime + ", state=" + state+ '}';
    }
}
