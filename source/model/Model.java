package source.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class Model {
    private ArrayList<Delivery> assignedDeliveryArrayList;
    private ArrayList<Delivery> postponedDeliveryArrayList;
    private ArrayList<Delivery> pendingDeliveryArrayList;
    private ArrayList<Courier> courierArrayList;
    private PropertyChangeSupport propertyChangeSupport;
    private SolveurTSP solveur;

    public Model(){
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.courierArrayList = new ArrayList<>();
        this.pendingDeliveryArrayList = new ArrayList<>();
        this.assignedDeliveryArrayList = new ArrayList<>();
        this.postponedDeliveryArrayList = new ArrayList<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener ArrayListener){
        propertyChangeSupport.addPropertyChangeListener(ArrayListener);
    }

    public ArrayList<Vertex> getVertexArrayList() { return solveur.getVertexList(); }

    public Courier createCourier (String firstName, String lastName, String phoneNumber){
        if(!firstName.isEmpty() & !lastName.isEmpty() & !phoneNumber.isEmpty()) {
            for (Courier courier : courierArrayList) {
                if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                    propertyChangeSupport.firePropertyChange("errorMessage", null, "This courier already exists");
                    return null;
                }
            }
            return new Courier(firstName, lastName, phoneNumber);
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "Please fill in all fields");
            return null;
        }
    }

    public void addCourier (Courier courier){
        courierArrayList.add(courier);
        propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
    }

    public Courier getCourier(String firstName, String lastName) {
        for (Courier courier : courierArrayList){
            if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                return courier;
            }
        }
        return null;
    }

    public Delivery getPendingDelivery(Long pickUpPtStr, Long deliveryPtStr) {
        for (Delivery delivery : pendingDeliveryArrayList){
            if (delivery.getPickUpPt().getId().equals(pickUpPtStr) && delivery.getDeliveryPt().getId().equals(deliveryPtStr)) {
                return delivery;
            }
        }
        return null;
    }

    public void deleteCourier(Courier courier){
        if (courier != null) {
            courierArrayList.remove(courier);
            propertyChangeSupport.firePropertyChange("courierArrayList", null, courierArrayList);
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    public void assignDelivery(Courier courier, Delivery delivery){
        if (courier != null) {
            if (delivery != null) {
                courier.getRoute().getDeliveries().add(delivery);
                ArrayList<Segment> routeComputed = solveur.ObtenirArrayListeSegmentsTSP(courier.getRoute().getDeliveries());
                if (routeComputed != null) {
                    courier.getRoute().setSegments(routeComputed);
                    pendingDeliveryArrayList.remove(delivery);
                    assignedDeliveryArrayList.add(delivery);
                    delivery.setState(DeliveryState.ASSIGNED);
                    propertyChangeSupport.firePropertyChange("courierRouteDeliveries", null, courier.getRoute().getDeliveries());
                    propertyChangeSupport.firePropertyChange("pendingDeliveryRemoved", null, delivery);
                } else {
                    courier.getRoute().getDeliveries().remove(delivery);
                    propertyChangeSupport.firePropertyChange("errorMessage", null, "No route found : the delivery can't be assigned to this courier");
                }
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No delivery selected");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    public void updateMap(ArrayList<Vertex> vertexList, ArrayList<Segment> segmentList){
        if (segmentList != null && vertexList != null) {
            if (!vertexList.isEmpty()) {
                solveur = new SolveurTSP();
                solveur.setSegmentList(segmentList);
                solveur.setVertexList(vertexList);
                propertyChangeSupport.firePropertyChange("segmentArrayList", null, solveur.getSegmentList());
                propertyChangeSupport.firePropertyChange("map", null, solveur.getVertexList().getFirst());
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "The file doesn't contain any vertex, please select an other file");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
        }
    }

    public void updateDeliveryList(ArrayList<Delivery> deliveryList, Entrepot entrepot){
        if (deliveryList != null) {
            if (entrepot != null){
                pendingDeliveryArrayList = deliveryList;
                propertyChangeSupport.firePropertyChange("pendingDeliveryArrayList", null, pendingDeliveryArrayList);
                solveur.setEntrepot(entrepot);
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "Entrepot location missing");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "The selected file is not an XML file");
        }
    }

    private String isDeliveryPoint(Courier courier, Vertex vertex) {
        if (courier != null && vertex != null) {
            for (Delivery delivery : courier.getRoute().getDeliveries()) {
                if (delivery.getPickUpPt() == vertex) {
                    return "PICK_UP";
                } else if (delivery.getDeliveryPt() == vertex) {
                    return "DELIVERY";
                }
            }
            return null;
        }
        return null;
    }

    public void getCourierDeliveries(Courier courier){
        if (courier != null) {
            propertyChangeSupport.firePropertyChange("deliveryListDeliveryTab", null, courier.getRoute().getDeliveries());
        }
    }

    public Vertex getPickUpFromDelivery(Courier courier, Vertex deliveryVertex) {
        if (courier != null && deliveryVertex != null) {
            for (Delivery delivery : courier.getRoute().getDeliveries()) {
                if (delivery.getDeliveryPt() == deliveryVertex) {
                    return delivery.getPickUpPt();
                }
            }
            return null;
        }
        return null;
    }

    public void getCourierSegmentList(Courier courier){
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                ArrayList<Vector> vertexOrderTypeToDisplay = new ArrayList<>();
                ArrayList<Vertex> vertexDisplayed = new ArrayList<>();
                for (Segment segment : courier.getRoute().getSegments()) {
                    Vertex origin = segment.getOrigine();
                    Vertex destination = segment.getDestination();
                    String originType = this.isDeliveryPoint(courier, origin);
                    String destinationType = this.isDeliveryPoint(courier, destination);
                    if (originType != null && destinationType != null) {
                        System.out.println("test");
                    }
                    if (originType != null && !vertexDisplayed.contains(origin)) {
                        Vector vector = new Vector();
                        vector.add(origin);
                        //if (originType == "DELIVERY" && vertexDisplayed.contains(this.getPickUpFromDelivery(courier, origin))) {
                            vertexDisplayed.add(origin);
                            vector.add(originType);
                            vertexOrderTypeToDisplay.add(vector);
                        //}
                    } else if (destinationType != null && !vertexDisplayed.contains(destination)) {
                        Vector vector = new Vector();
                        vector.add(destination);
                        //if (destinationType == "DELIVERY" && vertexDisplayed.contains(this.getPickUpFromDelivery(courier, destination))) {
                            vertexDisplayed.add(destination);
                            vector.add(destinationType);
                            vertexOrderTypeToDisplay.add(vector);
                        //}
                    }
                }
                propertyChangeSupport.firePropertyChange("displayVertices", null, vertexOrderTypeToDisplay);
                propertyChangeSupport.firePropertyChange("displaySegments", null, courier.getRoute().getSegments());
                propertyChangeSupport.firePropertyChange("displayEntrepot", null, solveur.getEntrepot().getAddress());
            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No route is associated with this courier : you must assign him at least one delivery");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }

    public ArrayList<Delivery> getPendingDeliveryArrayList() { return pendingDeliveryArrayList; }
}
