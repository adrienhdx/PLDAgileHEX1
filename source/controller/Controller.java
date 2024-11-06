package source.controller;

import source.model.*;
import source.model.XmlExtractor;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;

public class Controller implements ActionListener,ListSelectionListener {
    private final Model model;
    private final Interface view;

    public Controller(Model model, Interface view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
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
            this.assignDeliveryCourier();
        }
        if (e.getSource() == view.getRemoveCourierButton()) {
            this.deleteCourier();
        }
        if (e.getActionCommand().equals("comboBoxChanged") && e.getSource() == view.getCourierMapComboBox()) {
            this.getCourierSegmentList();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {
            //System.out.println(e);
            if (e.getSource() == view.getCourierList()){
                String selectedCourier = view.getCourierList().getSelectedValue();
                System.out.println(view.getCourierList().getSelectedValue());
                this.getCourierInfo(selectedCourier);
            }

        }
    }

    public void loadMap(){
        String filePath = view.getFileChooserMap().getSelectedFile().getAbsolutePath();
        ArrayList<Object> result = XmlExtractor.extractMap(filePath);
        if (result != null) {
            model.updateMap((ArrayList<Vertex>) result.getFirst(), (ArrayList<Segment>) result.getLast());
        } else {
            model.updateMap(null, null);
        }
    }

    public void loadDeliveries(){
        String filePath = view.getFileChooserDelivery().getSelectedFile().getAbsolutePath();
        if (model.getVertexArrayList() != null) {
            ArrayList<Object> demandArrayList = XmlExtractor.extractDeliveryDemand(filePath,model.getVertexArrayList());
            if (demandArrayList != null) {
                model.updateDeliveryList((ArrayList<Delivery>) demandArrayList.getLast(), (Entrepot) demandArrayList.getFirst());
            } else {
                model.updateDeliveryList(null, null);
            }
        }
    }

    public void createCourier(){
        String newCourierFirstName = view.getCourierFieldFirstName().getText().trim();
        String newCourierLastName = view.getCourierFieldLastName().getText().trim();
        String newCourierPhoneNumber = view.getCourierFieldPhoneNumber().getText().trim();
        Courier courier = model.createCourier(newCourierFirstName, newCourierLastName, newCourierPhoneNumber);
        if (courier != null) {
            model.addCourier(courier);
        }
    }

    public void assignDeliveryCourier(){
        String courierStr = (String) view.getCourierDeliveryComboBox().getSelectedItem();
        String firstName = "";
        String lastName = "";
        if (courierStr != null) {
            int index = courierStr.indexOf(' ');
            lastName = courierStr.substring(index+1);
            firstName = courierStr.substring(0,index);
        }
        Courier selectedCourier = model.getCourier(firstName, lastName);
        String attributedDelivery = (String) view.getPendingDeliveryComboBox() .getSelectedItem();
        Long pickUpPtStr = null;
        Long deliveryPtStr = null;
        if (attributedDelivery != null) {
            int index = attributedDelivery.indexOf('-');
            deliveryPtStr = Long.parseLong(attributedDelivery.substring(index + 1));
            pickUpPtStr = Long.parseLong(attributedDelivery.substring(0, index));
        }
        Delivery delivery = model.getPendingDelivery(pickUpPtStr, deliveryPtStr);
        model.assignDelivery(selectedCourier, delivery);
    }

    public void deleteCourier(){
        String courierInfo = view.getCourierList().getSelectedValue();
        String firstName = "";
        String lastName = "";
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            firstName = splitInfo[0];
            lastName = splitInfo[1];
        }
        Courier courier = model.getCourier(firstName, lastName);
        model.deleteCourier(courier);
    }

    public void getCourierSegmentList(){
        String courierInfo = (String) view.getCourierMapComboBox().getSelectedItem();
        String firstName = "";
        String lastName = "";
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            firstName = splitInfo[0];
            lastName = splitInfo[1];
        }
        Courier courier = model.getCourier(firstName, lastName);
        model.getCourierSegmentList(courier);
    }

    public void withdrawDelivery(){}

    public void getCourierInfo(String courierSelected){
        Courier selectedCourier;
        int rang = -1;
        String firstNameSelected = "";
        String lastNameSelected = "";
        String[] parts = courierSelected.split(" ");
        if (parts.length == 2) {
            firstNameSelected = parts[0];
            lastNameSelected = parts[1];
        } else {
            System.out.println("Format de chaîne incorrect");
        }
        selectedCourier = model.getCourier(firstNameSelected, lastNameSelected);
        model.getCourierInfo(selectedCourier);
        //Afficher les résultats
            System.out.println("Prénom : " + selectedCourier.getFirstName());
            System.out.println("Nom : " + selectedCourier.getLastName());
            System.out.println("Num : " + selectedCourier.getPhoneNum());
    }

}

