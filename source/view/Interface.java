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
    private JButton mapButton, deliveryButton, assignCourierButton, addCourierButton, removeCourierButton;
    private JComboBox<String> unassignedDeliveryDropdown, courierManagementDropdown, courierDeliveryDropdown, courierMapDropdown;
    private DefaultComboBoxModel<String> unassignedModel, courierModel;
    private Vector<String> couriers;
    private JList<String> courierList, selectedCourierListCourierTab, selectedCourierListDeliveryTab;
    private MapDisplay map;
    private JFileChooser fileChooserDelivery;
    private JFileChooser fileChooserMap;
    private JTextField courierFieldFirstName, courierFieldLastName, courierFieldPhoneNumber;
    private boolean settingsDelivery;

    public void addController(Controller controller) {
        fileChooserDelivery.addActionListener(controller);
        fileChooserMap.addActionListener(controller);
        addCourierButton.addActionListener(controller);
        assignCourierButton.addActionListener(controller);
        removeCourierButton.addActionListener(controller);
        courierList.addListSelectionListener(controller);
    }

    public Interface() {
        couriers = new Vector<String>();
        fileChooserDelivery = new JFileChooser();
        fileChooserDelivery.setCurrentDirectory(new File("."));
        fileChooserMap = new JFileChooser();
        fileChooserMap.setCurrentDirectory(new File("."));
        unassignedModel = new DefaultComboBoxModel<>();
        courierModel = new DefaultComboBoxModel<>(couriers);
        unassignedDeliveryDropdown = new JComboBox<>(unassignedModel);
        courierDeliveryDropdown = new JComboBox<>(courierModel);
        courierManagementDropdown = new JComboBox<>(courierModel);
        assignCourierButton = new JButton("Assign the delivery to this courier");
        courierMapDropdown = new JComboBox<>(courierModel);
        map = new MapDisplay();
        scrollPanelMap = new JScrollPane(map.getMapViewer());
        boolean settingsDelivery = false;
        courierList = new JList<>(courierModel);
        courierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
            fileChooserMap.showOpenDialog(null);
        });
    }

    private void setupDeliveryPanel() {
        deliveryPanel = new JPanel();
        deliveryButton = new JButton("Load Delivery");
        deliveryPanel.add(deliveryButton);
        tabPan.addTab("Delivery", deliveryPanel);

        deliveryButton.addActionListener(e -> {
            fileChooserDelivery.showOpenDialog(null);
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deliveryPanel.add(new JLabel("Pending Deliveries:"), gbc);

        gbc.gridx = 1;
        deliveryPanel.add(unassignedDeliveryDropdown, gbc);

        // Choisir un livreur
        gbc.gridx = 0;
        gbc.gridy = 3;
        deliveryPanel.add(new JLabel("Choose Courier:"), gbc);

        gbc.gridx = 1;
        deliveryPanel.add(courierDeliveryDropdown, gbc);

        // Bouton pour affecter le livreur
        gbc.gridx = 0;
        gbc.gridy = 4;
        deliveryPanel.add(assignCourierButton, gbc);

        //tabPan.addTab("Delivery", deliveryPanel);
        deliveryPanel.revalidate();  // Met à jour le layout
        deliveryPanel.repaint();  // Redessine le panel
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

        JScrollPane scrollPane = new JScrollPane(courierList);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        managementPanel.add(scrollPane, gbc);

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


        // Bouton pour supprimer le livreur sélectionné
        removeCourierButton = new JButton("Remove courier");
        gbc.gridy = 2;
        managementPanel.add(removeCourierButton, gbc);

        tabPan.addTab("Courier Management", managementPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("errorMessage")) {
            JOptionPane.showMessageDialog(this, evt.getNewValue());
        }
        if (evt.getPropertyName().equals("courierRouteDeliveries")) {
            //maj de la liste des livraisons du livreur
        }
        if (evt.getPropertyName().equals("map")) {
            mapPanel.removeAll();
            map.setCentre((Vertex) evt.getNewValue());
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
            tabPan.setComponentAt(0,mainPanelMap);
        }
        if (evt.getPropertyName().equals("vertexArrayList")) { //Test display vertex
            ArrayList<Vertex> vertexArrayList = (ArrayList<Vertex>) evt.getNewValue();
            for(Vertex vertex : vertexArrayList){ //Test affichage intersections
                map.displayVertex(vertex);
            }
        }
        if (evt.getPropertyName().equals("segmentArrayList")) { //Test display segment
            ArrayList<Segment> segmentArrayList = (ArrayList<Segment>) evt.getNewValue();
            if (!segmentArrayList.isEmpty()){
                for(Segment segment : segmentArrayList){
                    map.displaySegment(segment);
                }
            }
        }
        if (evt.getPropertyName().equals("courierArrayList")) {
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            updateCourierList(courierList);
            JOptionPane.showMessageDialog(this, "Courier list updated");
        }
        if (evt.getPropertyName().equals("pendingDeliveryArrayList")) {
            File selectedFile = fileChooserDelivery.getSelectedFile();
            deliveryPanel.remove(deliveryButton);
            showSettingsDelivery(selectedFile);
            unassignedModel.removeAllElements();
            ArrayList<Delivery> deliveryArrayList = (ArrayList<Delivery>) evt.getNewValue();
            for (Delivery delivery : deliveryArrayList) {
                String deliveryString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
                unassignedModel.addElement(deliveryString);
            }
        }
        if (evt.getPropertyName().equals("pendingDeliveryRemoved")) {
            Delivery delivery = (Delivery) evt.getNewValue();
            String deliveryString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
            unassignedModel.removeElement(deliveryString);
        }
    }

    private void updateCourierList(ArrayList<Courier> newCourierList) {
        couriers.clear();
        for (Courier courier : newCourierList) {
            couriers.add(courier.getFirstName()+ " " + courier.getLastName());
        }
        courierList.setListData(couriers);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
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

    public void setSettingsDelivery(boolean bool) {
        this.settingsDelivery = bool;
    }

    public boolean getSettingsDelivery() {
        return this.settingsDelivery;
    }

    public JComboBox<String> getCourierDeliveryComboBox() {
        return courierDeliveryDropdown;
    }

    public JComboBox<String> getPendingDeliveryComboBox() {
        return unassignedDeliveryDropdown;
    }

    public JList<String> getCourierList() {
        return courierList;    }
}

