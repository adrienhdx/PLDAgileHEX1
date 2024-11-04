package source.view;

import source.controller.Controller;
import source.model.Courier;
import source.model.Delivery;
import source.model.Segment;
import source.model.Vertex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class Interface extends JFrame implements PropertyChangeListener {
    private JTabbedPane tabPan = new JTabbedPane();
    private JPanel mapPanel, deliveryPanel, controlMapPanel, mainPanelMap ;
    private JScrollPane scrollPanelMap;
    private JButton mapButton, deliveryButton, addDeliveryButton, removeDeliveryButton, assignCourierButton, showRoutesButton, addCourierButton, removeCourierButton;
    private JComboBox<String> unassignedDeliveryDropdown, assignedDeliveryDropdown, courierManagementDropdown, courierDeliveryDropdown, courierMapDropdown;
    private DefaultComboBoxModel<String> unassignedModel, assignedModel, courierModel;
    private Vector<String> couriers, attributedDeliveries;
    private MapDisplay map;
    private JFileChooser fileChooserDelivery;
    private JFileChooser fileChooserMap;
    private JTextField courierFieldFirstName, courierFieldLastName, courierFieldPhoneNumber;

    public void addController(Controller controller) {
        fileChooserDelivery.addActionListener(controller);
        fileChooserMap.addActionListener(controller);
        addCourierButton.addActionListener(controller);
        assignCourierButton.addActionListener(controller);
        removeCourierButton.addActionListener(controller);
    }

    public Interface() {
        couriers = new Vector<String>();
        attributedDeliveries = new Vector<String>();
        fileChooserDelivery = new JFileChooser();
        fileChooserDelivery.setCurrentDirectory(new File("."));
        fileChooserMap = new JFileChooser();
        fileChooserMap.setCurrentDirectory(new File("."));
        unassignedModel = new DefaultComboBoxModel<>();
        assignedModel = new DefaultComboBoxModel<>(attributedDeliveries);
        courierModel = new DefaultComboBoxModel<>(couriers);
        unassignedDeliveryDropdown = new JComboBox<>(unassignedModel);
        assignedDeliveryDropdown = new JComboBox<>(assignedModel);
        courierDeliveryDropdown = new JComboBox<>(courierModel);
        addDeliveryButton = new JButton("Assign Delivery");
        removeDeliveryButton = new JButton("Remove Delivery");
        courierManagementDropdown = new JComboBox<>(courierModel);
        assignCourierButton = new JButton("Assign Courier");
        showRoutesButton = new JButton("Compute Route");
        courierMapDropdown = new JComboBox<>(courierModel);
        map = new MapDisplay();
        scrollPanelMap = new JScrollPane(map.getMapViewer());

        setTitle("App Delivery Services");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Onglet "Map"
        setupMapPanel();

        // Onglet "Delivery"
        setupDeliveryPanel();


        // Onglet "Gestion des Courriers"
        setupCourierManagementPanel();

        // Organisation de la fenêtre
        getContentPane().add(tabPan, BorderLayout.CENTER);
        setVisible(true);
        pack();
    }

    private void setupMapPanel() {
        mapPanel = new JPanel();
        mapButton = new JButton("Upload Map");
        mapPanel.add(mapButton);
        tabPan.addTab("Map", mapPanel);

        mapButton.addActionListener(e -> {
            int result = fileChooserMap.showOpenDialog(null);
        });
    }

    private void setupDeliveryPanel() {
        deliveryPanel = new JPanel();  // Utiliser GridLayout pour une meilleure ergonomie
        deliveryButton = new JButton("Load Delivery");
        deliveryPanel.add(deliveryButton);  // Ajout du bouton dans le panel "Delivery"
        tabPan.addTab("Delivery", deliveryPanel);

        deliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooserDelivery.showOpenDialog(null);  // Ouvre le dialogue pour choisir un fichier
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooserDelivery.getSelectedFile();
                    // Supprimer le bouton de chargement
                    deliveryPanel.remove(deliveryButton);
                    // Configurer le panneau de livraison
                    showSettingsDelivery(selectedFile);
                }
            }
        });
    }

    private void showSettingsDelivery(File file){
        // Supprimer le bouton de chargement
        deliveryPanel.remove(deliveryButton);
        deliveryPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Livraisons non attribuées
        gbc.gridx = 0;
        gbc.gridy = 0;
        deliveryPanel.add(new JLabel("Pending Deliveries:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deliveryPanel.add(unassignedDeliveryDropdown, gbc);

        // Livraisons attribuées
        gbc.gridx = 0;
        gbc.gridy = 1;
        deliveryPanel.add(new JLabel("Assigned Deliveries:"), gbc);

        gbc.gridx = 1;
        deliveryPanel.add(assignedDeliveryDropdown, gbc);

        // Bouton pour attribuer une livraison
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deliveryPanel.add(addDeliveryButton, gbc);

        // Bouton pour retirer une livraison
        gbc.gridx = 1;
        deliveryPanel.add(removeDeliveryButton, gbc);

        // Choisir un livreur
        gbc.gridx = 0;
        gbc.gridy = 3;
        deliveryPanel.add(new JLabel("Choose Courier:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deliveryPanel.add(courierDeliveryDropdown, gbc);

        // Bouton pour affecter le livreur
        gbc.gridx = 0;
        gbc.gridy = 4;
        deliveryPanel.add(assignCourierButton, gbc);

        // Bouton pour calculer l'itinéraire
        gbc.gridx = 1;
        gbc.gridy = 4;
        deliveryPanel.add(showRoutesButton, gbc);

        // Actions des boutons
        addDeliveryButton.addActionListener(e -> assignDelivery());
        removeDeliveryButton.addActionListener(e -> removeDelivery());
        assignCourierButton.addActionListener(e -> assignCourier());
        showRoutesButton.addActionListener(e -> calculateRoute());

        //tabPan.addTab("Delivery", deliveryPanel);
        deliveryPanel.revalidate();  // Met à jour le layout
        deliveryPanel.repaint();  // Redessine le panel
    }

    private void assignDelivery() {
        String selectedDelivery = (String) unassignedDeliveryDropdown.getSelectedItem();
        if (selectedDelivery != null) {
            attributedDeliveries.add(selectedDelivery);
            unassignedModel.removeElement(selectedDelivery);
        } else {
            JOptionPane.showMessageDialog(this, "No delivery selected.");
        }
    }

    private void removeDelivery() {
        String selectedDelivery = (String) assignedDeliveryDropdown.getSelectedItem();
        if (selectedDelivery != null) {
            unassignedModel.addElement(selectedDelivery);
            assignedModel.removeElement(selectedDelivery);
        } else {
            JOptionPane.showMessageDialog(this, "No delivery selected.");
        }
    }

    private void assignCourier() {
        String selectedCourier = (String) courierManagementDropdown.getSelectedItem();
        String assignedDelivery = (String) assignedDeliveryDropdown.getSelectedItem();
        if (selectedCourier != null && assignedDelivery != null) {
            JOptionPane.showMessageDialog(this, "Delivery " + assignedDelivery + " managed by " + selectedCourier);
        } else {
            JOptionPane.showMessageDialog(this, "No courier or delivery selected");
        }
    }

    private void calculateRoute() {
        JOptionPane.showMessageDialog(this, "Computing route...");
        // Logique pour calculer l'itinéraire
    }

    private void setupCourierManagementPanel() {
        JPanel managementPanel = new JPanel(new GridBagLayout());  // Utilisation de GridBagLayout pour une disposition flexible et esthétique
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espacement entre les composants

        // Liste déroulante des livreurs existants
        // Utilisation d'un modèle dynamique pour faciliter les modifications
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        managementPanel.add(new JLabel("Couriers :"), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierManagementDropdown, gbc);

        // Champ de texte pour ajouter un nouveau livreur
        courierFieldFirstName = new JTextField(15);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("First Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldFirstName, gbc);

        courierFieldLastName = new JTextField(15);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("Last Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldLastName, gbc);

        courierFieldPhoneNumber = new JTextField(15);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("Phone number :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldPhoneNumber, gbc);

        // Bouton pour ajouter un nouveau livreur
        addCourierButton = new JButton("Add Courier");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        managementPanel.add(addCourierButton, gbc);

        addCourierButton.addActionListener(e ->{
            if(!courierFieldFirstName.getText().trim().equals("") && !courierFieldLastName.getText().trim().equals("") && !courierFieldPhoneNumber.getText().trim().equals("")) {
                courierFieldFirstName.setText("");
                courierFieldLastName.setText("");
                courierFieldPhoneNumber.setText("");
                JOptionPane.showMessageDialog(null, "Courier added");
            }
            else{
                JOptionPane.showMessageDialog(null, "Please fill in all fields");
            }
        });

        // Bouton pour supprimer le livreur sélectionné
        removeCourierButton = new JButton("Remove courier");
        gbc.gridy = 2;
        managementPanel.add(removeCourierButton, gbc);

        tabPan.addTab("Courier Management", managementPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("vertexArrayList")) {
            // remise à jour de l'onglet map
            mapPanel.removeAll();

            // Initialisation du panneau principal
            mainPanelMap = new JPanel(new BorderLayout());

            // Panneau de contrôle amélioré
            controlMapPanel = new JPanel();
            controlMapPanel.setLayout(new BoxLayout(controlMapPanel, BoxLayout.Y_AXIS));
            controlMapPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Ajouter des marges internes

            // Espacement entre les composants
            int componentSpacing = 15;

            // Création des composants
            JLabel select = new JLabel("Select a Courier:");
            select.setFont(new Font("Arial", Font.BOLD, 14)); // Font plus visible
            select.setAlignmentX(Component.CENTER_ALIGNMENT);

            courierMapDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, courierMapDropdown.getPreferredSize().height));
            courierMapDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton selectCourier = new JButton("Select Courier");
            selectCourier.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton exportRoutes = new JButton("Export Routes");
            exportRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton importRoutes = new JButton("Import Routes");
            importRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Ajout des composants avec des espaces entre eux
            controlMapPanel.add(select);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(courierMapDropdown);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(selectCourier);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(exportRoutes);
            controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
            controlMapPanel.add(importRoutes);

            // Ajouter une bordure pour encadrer visuellement le panneau
            controlMapPanel.setBorder(BorderFactory.createTitledBorder("Control Panel"));

            // Ajout de controlMapPanel et scrollPanelMap dans mainPanelMap
            mainPanelMap.add(controlMapPanel, BorderLayout.EAST);
            mainPanelMap.add(scrollPanelMap, BorderLayout.CENTER);

            /*controlMapPanel = new JPanel(new GridLayout(0,1,100,100));
            mainPanelMap = new JPanel(new BorderLayout());

            JLabel select = new JLabel("Select a Courier :");

            JButton selectCourier = new JButton("Select Courier");
            JButton exportRoutes = new JButton("Export Routes");
            JButton importRoutes = new JButton("Import Routes");

            controlMapPanel.add(select);
            controlMapPanel.add(courierMapDropdown);
            controlMapPanel.add(selectCourier);
            controlMapPanel.add(exportRoutes);
            controlMapPanel.add(importRoutes);

            mainPanelMap.add(controlMapPanel, BorderLayout.EAST);
            mainPanelMap.add(scrollPanelMap, BorderLayout.CENTER);*/


            tabPan.setComponentAt(0,mainPanelMap);

            //pour afficher les intersections sur la carte
            ArrayList<Vertex> vertexArrayList = (ArrayList<Vertex>) evt.getNewValue();
            if (!vertexArrayList.isEmpty()){
                map.setCentre(vertexArrayList.getFirst());
                for(Vertex vertex : vertexArrayList){ //Test affichage intersections
                    map.displayVertex(vertex);
                }
            }
        }
        if (evt.getPropertyName().equals("segmentArrayList")) { //Test affichage segments
            ArrayList<Segment> segmentArrayList = (ArrayList<Segment>) evt.getNewValue();
            if (!segmentArrayList.isEmpty()){
                for(Segment segment : segmentArrayList){
                    map.displaySegment(segment);
                }
            }
        }
        if (evt.getPropertyName().equals("addCourierArrayList")) {
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
        }
        if (evt.getPropertyName().equals("deleteCourierArrayList")) {
            courierManagementDropdown.removeAllItems();
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
        }
        if (evt.getPropertyName().equals("pendingDeliveryArrayList")) {
            ArrayList<Delivery> deliveryArrayList = (ArrayList<Delivery>) evt.getNewValue();
            for (Delivery delivery : deliveryArrayList) {
                String deliveryString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
                unassignedModel.addElement(deliveryString);
            }
        }
    }

    private void updateCourierList(ArrayList<Courier> courierList) {
        couriers.clear();
        for (Courier courier : courierList) {
            couriers.add(courier.getFirstName()+ " " + courier.getLastName());
        }
    }

    // Getters
    public JFileChooser getFileChooserDelivery() {
        return fileChooserDelivery;
    }

    public JFileChooser getFileChooserMap() {
        return fileChooserMap;
    }

    public JTextField getCourierFieldFirstName() {
        return courierFieldFirstName;
    }

    public JTextField getCourierFieldLastName() {
        return courierFieldLastName;
    }

    public JTextField getCourierFieldPhoneNumber() {
        return courierFieldPhoneNumber;
    }

    public JButton getAddCourierButton() {
        return addCourierButton;
    }

    public JButton getRemoveCourierButton() {
        return removeCourierButton;
    }

    public JButton getAssignCourierButton(){
        return assignCourierButton;
    }

    public JComboBox<String> getCourierManagementComboBox() {
        return courierManagementDropdown;
    }

    public JComboBox<String> getCourierDeliveryComboBox() {
        return courierDeliveryDropdown;
    }

    public Vector<String> getAttributedDeliveries() {
        return attributedDeliveries;
    }

}

