package source.controller;

import source.model.*;
import source.model.XmlExtractor;
import source.view.Interface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.FileWriter;
import java.io.IOException;
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
        if (e.getSource() == view.getPendingDeliveryComboBox()) {
            this.displayPendingDelivery();
        }
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
            this.getCourierDeliveries();
        }
        if (e.getSource() == view.getRemoveCourierButton()) {
            this.deleteCourier();
        }
        if (e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileExportWaitingList()) {
            this.exportWaitingArrayList();
        }
        if (e.getActionCommand().equals("ApproveSelection") && e.getSource() == view.getFileExportRoutes()) {
            this.exportRoutes();
        }
        if (e.getActionCommand().equals("comboBoxChanged") && e.getSource() == view.getCourierDeliveryComboBox()){
            this.getCourierDeliveries();
        }
        if(e.getSource() == view.getWaitingListButton()){
            this.addDeliveryInWaitingList();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {
            if (e.getSource() == view.getCourierList()){
                String selectedCourier = view.getCourierList().getSelectedValue();
                System.out.println(view.getCourierList().getSelectedValue());
                this.getCourierInfo(selectedCourier);
            }
            if (e.getSource() == view.getCourierMapList()) {
                this.getCourierSegmentList();
            }
        }
    }

    private void loadMap(){
        String filePath = view.getFileChooserMap().getSelectedFile().getAbsolutePath();
        ArrayList<Object> result = XmlExtractor.extractMap(filePath);
        if (result != null) {
            model.updateMap((ArrayList<Vertex>) result.getFirst(), (ArrayList<Segment>) result.getLast());
        } else {
            model.updateMap(null, null);
        }
    }

    private void loadDeliveries(){
        String filePath = view.getFileChooserDelivery().getSelectedFile().getAbsolutePath();
        if (model.getVertexArrayList() != null) {
            ArrayList<Object> demandArrayList = XmlExtractor.extractDeliveryDemand(filePath,model.getVertexArrayList());
            if (demandArrayList != null) {
                model.updateDeliveryList(
                        (demandArrayList.size() > 0) ? (ArrayList<Delivery>) demandArrayList.getLast() : new ArrayList<Delivery>(),
                        (demandArrayList.size() > 0) ? (Entrepot) demandArrayList.getFirst() : null
                );
            }else {
                model.updateDeliveryList(null, null);
            }
        }
    }

    private void createCourier(){
        String newCourierFirstName = view.getCourierFieldFirstName().getText().trim();
        String newCourierLastName = view.getCourierFieldLastName().getText().trim();
        String newCourierPhoneNumber = view.getCourierFieldPhoneNumber().getText().trim();
        Courier courier = model.createCourier(newCourierFirstName, newCourierLastName, newCourierPhoneNumber);
        if (courier != null) {
            model.addCourier(courier);
        }
    }

    private void assignDeliveryCourier(){
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

    private void deleteCourier(){
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
        model.resetMap();
        if(!view.getCourierMapList().getSelectedValuesList().isEmpty()) {
            ArrayList<String> couriersInfo = (ArrayList<String>) view.getCourierMapList().getSelectedValuesList();
            for (String courierInfo : couriersInfo) {
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
        }
    }

    public void exportWaitingArrayList() {
        try {
            if (view.getWaitingList().getSelectedItem() != null) {
                FileWriter fileWriter = new FileWriter(view.getFileExportWaitingList().getSelectedFile().getAbsolutePath());
                fileWriter.write(XmlExtractor.exportWaitingList(model.getWaitingArrayList()));
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCourierDeliveries() {
        String courierInfo = (String) view.getCourierDeliveryComboBox().getSelectedItem();
        String firstName = "";
        String lastName = "";
        if (courierInfo != null) {
            String[] splitInfo = courierInfo.split(" ");
            firstName = splitInfo[0];
            lastName = splitInfo[1];
            Courier courier = model.getCourier(firstName, lastName);
            model.getCourierDeliveriesDeliveryTab(courier);
        }
    }

    public void exportRoutes() {
        try {
            ArrayList<String> couriersInfo;
            ArrayList<Vertex> vertices = new ArrayList<>();
            ArrayList<Segment> segments = new ArrayList<>();
            if (!view.getCourierMapList().getSelectedValuesList().isEmpty()) {
                FileWriter fileWriter = new FileWriter(view.getFileExportRoutes().getSelectedFile().getAbsolutePath());
                couriersInfo = (ArrayList<String>) view.getCourierMapList().getSelectedValuesList();
                String firstName = "";
                String lastName = "";
                Courier courier = null;
                for (String courierInfo : couriersInfo) {
                    if (courierInfo != null) {
                        String[] splitInfo = courierInfo.split(" ");
                        firstName = splitInfo[0];
                        lastName = splitInfo[1];
                        courier = model.getCourier(firstName, lastName);
                    }
                    for (Vertex vertex : model.getCourierVertexArrayList(courier)) {
                        vertices.add(vertex);
                    }
                    for (Segment segment : model.getCourierSegmentArrayList(courier)) {
                        segments.add(segment);
                    }
                }
                fileWriter.write(XmlExtractor.exportRoutes(vertices, segments));
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCourierInfo(String courierSelected){
        if(courierSelected != null) {
            Courier selectedCourier;
            String firstNameSelected = "";
            String lastNameSelected = "";
            String[] parts = courierSelected.split(" ");
            if (parts.length == 2) {
                firstNameSelected = parts[0];
                lastNameSelected = parts[1];
            }
            selectedCourier = model.getCourier(firstNameSelected, lastNameSelected);
            model.getCourierInfo(selectedCourier);
            model.getCourierDeliveriesCourierTab(selectedCourier);
        }
    }

    private void displayPendingDelivery(){
        String selectedDelivery = (String) view.getPendingDeliveryComboBox() .getSelectedItem();
        Long pickUpPtStr = null;
        Long deliveryPtStr = null;
        if (selectedDelivery != null) {
            int index = selectedDelivery.indexOf('-');
            deliveryPtStr = Long.parseLong(selectedDelivery.substring(index + 1));
            pickUpPtStr = Long.parseLong(selectedDelivery.substring(0, index));
        }
        Delivery delivery = model.getPendingDelivery(pickUpPtStr, deliveryPtStr);
        model.displayDelivery(delivery);
    }

    public void addDeliveryInWaitingList() {
        String selectedDelivery = (String) view.getPendingDeliveryComboBox().getSelectedItem();
        Long pickUpPtStr = null;
        Long deliveryPtStr = null;
        if (selectedDelivery != null) {
            int index = selectedDelivery.indexOf('-');
            deliveryPtStr = Long.parseLong(selectedDelivery.substring(index + 1));
            pickUpPtStr = Long.parseLong(selectedDelivery.substring(0, index));
        }
        Delivery deliveryWaitingList = model.getPendingDelivery(pickUpPtStr, deliveryPtStr);
        model.updateWaitingList(deliveryWaitingList);
    }
}

