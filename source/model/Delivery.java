package source.model;

public class Delivery {
    private final Vertex pickUpPt;
    private final Vertex deliveryPt;
    private final int pickUpTime;
    private final int deliveryTime;
    private DeliveryState state;

    public Delivery(Vertex pickUpPt, Vertex deliveryPt, int pickUpTime, int deliveryTime, DeliveryState state) {
        this.pickUpPt = pickUpPt;
        this.deliveryPt = deliveryPt;
        this.pickUpTime = pickUpTime;
        this.deliveryTime = deliveryTime;
        this.state = state;
    }

    //Getters et Setters
    public Vertex getPickUpPt() { return pickUpPt; }

    public Vertex getDeliveryPt() { return deliveryPt; }

    public int getPickUpTime() { return pickUpTime; }

    public int getDeliveryTime() { return deliveryTime; }

    public void setState(DeliveryState state) { this.state = state; }

    @Override
    public String toString() {
        return "Delivery{pickUpPt=" + pickUpPt + ", deliveryPt=" + deliveryPt + ", pickUpTime=" + pickUpTime + ", deliveryTime=" + deliveryTime + ", state=" + state+ '}';
    }
}
