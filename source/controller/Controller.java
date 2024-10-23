package source.controller;

import source.model.*;
import source.model.XmlExtractor;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Controller implements ActionListener {
    private Model model;
    private Interface view;

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserDelivery()) {
            this.loadDeliveries();
        }
        if(e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileChooserMap()){
            this.loadMap();
        }
        if (e.getSource() == view.getAddCourierButton()) {
            this.createCourier();
        }
        if (e.getSource() == view.getAssignCourierButton()) {
            this.assignDeliveriesCourier();
        }
        if(e.getSource() == view.getRemoveCourierButton()){
            this.deleteCourier();
        }
    }

    public Controller(Model model, Interface view) {
        this.model = model;
        this.view = view;
    }

    public void loadMap(){
        String filePath = view.getFileChooserMap().getSelectedFile().getAbsolutePath();
        ArrayList<Vertex> vertexList = (ArrayList<Vertex>) XmlExtractor.extractPlan(filePath).getFirst();
        ArrayList<Segment> segmentList = (ArrayList<Segment>) XmlExtractor.extractPlan(filePath).getLast();
        model.setSegmentList(segmentList);
        model.setVertexList(vertexList);
        System.out.println(vertexList.size());
        System.out.println(segmentList.size());
    }

    public void loadDeliveries(){
        String filePath = view.getFileChooserDelivery().getSelectedFile().getAbsolutePath();
        Map<String, Vertex> vertexIdMap = new HashMap<>();
        if (model.getVertexList() != null) {
            ArrayList<Delivery> deliveryList = (ArrayList<Delivery>) XmlExtractor.extractDemande(filePath, model.getVertexList());
            model.setPendingDeliveryList(deliveryList);
            System.out.println(deliveryList.size());
            List<Segment> tour = model.computeTour(deliveryList);
            for (Segment s : tour) {
                System.out.println(s);
            }
        }
    }

    public void createCourier(){
        String newCourierFirstName = view.getCourierFieldFirstName().getText().trim();
        String newCourierLastName = view.getCourierFieldLastName().getText().trim();
        String newCourierPhoneNumber = view.getCourierFieldPhoneNumber().getText().trim();
        if(!newCourierFirstName.equals("") & !newCourierLastName.equals("") & !newCourierPhoneNumber.equals("")){
            model.addCourrier(newCourierFirstName, newCourierLastName, newCourierPhoneNumber);
        }
    }

    public void assignDeliveriesCourier(){
        String courierStr = view.getCourierComboBox().getSelectedItem().toString();
        int index = courierStr.indexOf(' '); //trouve l'index de l'espace
        String lastName = courierStr.substring(index+1);
        String firstName = courierStr.substring(0,index);
        Courier selectedCourier = null;
        for (Courier courier : model.getCourierList()) {
            if (courier.getFirstName().equals(firstName) && courier.getLastName().equals(lastName)) {
                selectedCourier = courier;
            }
        }
        if (selectedCourier != null){
            Vector<String> attributedDeliveriesList = view.getAttributedDeliveries();
            ArrayList<Delivery> deliveryArrayList = new ArrayList<>();
            for (String attributedDelivery : attributedDeliveriesList) {
                index = attributedDelivery.indexOf('-'); //trouve l'index de l'espace
                String deliveryPtStr = attributedDelivery.substring(index+1);
                String pickUpPtStr = attributedDelivery.substring(0,index);
                for (Delivery delivery : model.getPendingDeliveryList()) {
                    if (delivery.getPickUpPt().getId().equals(pickUpPtStr) && delivery.getDeliveryPt().getId().equals(deliveryPtStr)) {
                        deliveryArrayList.add(delivery);
                    }
                }
                System.out.println(deliveryArrayList);
                for (Delivery deliveryAtt : deliveryArrayList) {
                    assignDelivery(selectedCourier, deliveryAtt);
                    model.getPendingDeliveryList().remove(deliveryAtt);
                    model.getAssignedDeliveryList().add(deliveryAtt);
                    deliveryAtt.setState(DeliveryState.ASSIGNED);
                }
            }
        }
        System.out.println(selectedCourier);
    }
    public void assignDelivery(Courier courier, Delivery delivery){
        courier.getRoute().getDeliveries().add(delivery);
    }

    public void deleteCourier(){
        String courierInfo = (String) view.getCourierComboBox().getSelectedItem();
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            String firstName = splitInfo[0];
            String lastName = splitInfo[1];
            model.deleteCourier(firstName, lastName);
        }
    }

    public void assignDelivery(){}
    public void withdrawDelivery(){}
}
