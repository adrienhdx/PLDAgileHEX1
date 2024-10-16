package source;

import java.util.List;

public class Systems {
    private Graph map;
    private List<Delivery> assignedDeliveryList;
    private List<Delivery> postponedDeliveryList;
    private List<Delivery> pendingDeliveryList;
    private List<Courier> courierList;
    private List<Segment> segmentList;
    private List<Vertice> verticeList;

    public Systems(Graph map, List<Delivery> assignedDeliveryList, List<Delivery> postponedDeliveryList,
                  List<Delivery> pendingDeliveryList, List<Courier> courierList,
                  List<Segment> segmentList, List<Vertice> verticeList) {
        this.map = map;
        this.assignedDeliveryList = assignedDeliveryList;
        this.postponedDeliveryList = postponedDeliveryList;
        this.pendingDeliveryList = pendingDeliveryList;
        this.courierList = courierList;
        this.segmentList = segmentList;
        this.verticeList = verticeList;
    }

    // Getters et Setters
    public Graph getMap() { return map; }
    public void setMap(Graph map) { this.map = map; }

    public List<Delivery> getAssignedDeliveryList() { return assignedDeliveryList; }
    public void setAssignedDeliveryList(List<Delivery> assignedDeliveryList) { this.assignedDeliveryList = assignedDeliveryList; }

    public List<Delivery> getPostponedDeliveryList() { return postponedDeliveryList; }
    public void setPostponedDeliveryList(List<Delivery> postponedDeliveryList) { this.postponedDeliveryList = postponedDeliveryList; }

    public List<Delivery> getPendingDeliveryList() { return pendingDeliveryList; }
    public void setPendingDeliveryList(List<Delivery> pendingDeliveryList) { this.pendingDeliveryList = pendingDeliveryList; }

    public List<Courier> getCourierList() { return courierList; }
    public void setCourierList(List<Courier> courierList) { this.courierList = courierList; }

    public List<Segment> getSegmentList() { return segmentList; }
    public void setSegmentList(List<Segment> segmentList) { this.segmentList = segmentList; }

    public List<Vertice> getVerticeList() { return verticeList; }
    public void setVerticeList(List<Vertice> verticeList) { this.verticeList = verticeList; }
}
