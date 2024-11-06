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

    public void getCourierSegmentList(Courier courier){
        if (courier != null) {
            if (!courier.getRoute().getSegments().isEmpty()) {
                solveur.setSegmentList(courier.getRoute().getSegments());
                ArrayList<Vertex> vertexToDisplay = new ArrayList<>();
                vertexToDisplay.add(solveur.getEntrepot().getAddress());
                for (Delivery delivery : courier.getRoute().getDeliveries()) {
                    vertexToDisplay.add(delivery.getPickUpPt());
                    vertexToDisplay.add(delivery.getDeliveryPt());
                }
                propertyChangeSupport.firePropertyChange("displaySegments", null, solveur.getSegmentList());
                propertyChangeSupport.firePropertyChange("displayVertices", null, vertexToDisplay);

            } else {
                propertyChangeSupport.firePropertyChange("errorMessage", null, "No route is associated with this courier : you must assign him at least one delivery");
            }
        } else {
            propertyChangeSupport.firePropertyChange("errorMessage", null, "No courier selected");
        }
    }
}
