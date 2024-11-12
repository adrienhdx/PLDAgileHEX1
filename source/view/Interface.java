package source.view;

import source.controller.Controller;
import source.model.Courier;
import source.model.Delivery;
import source.model.Segment;
import source.model.Vertex;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Interface extends JFrame implements PropertyChangeListener {
    private JTabbedPane tabPan = new JTabbedPane();
    private JPanel mapPanel, deliveryPanel, controlMapPanel, mainPanelMap, mainPanelDeliveries, controlDeliveriesPanel,courierInfoPanel;
    private JScrollPane scrollPanelMap, scrollPanelDeliveriesMap;
    private JButton mapButton, deliveryButton, assignCourierButton, addCourierButton, removeCourierButton, exportRoutes, importRoutes, waitingListButton, exportWaitingListButton;
    private JComboBox<String> unassignedDeliveryDropdown, courierDeliveryDropdown, courierMapDropdown, waitingListDropdown;
    private DefaultComboBoxModel<String> unassignedModel, courierModel, courierMapModel, courierDeliveryModel, waitingListModel;
    private Vector<String> couriers,selectedCourierVectorCourierTab,  selectedCourierVectorDeliveryTab;
    private JList<String> courierList, courierListMapTab, selectedCourierListCourierTab, selectedCourierListDeliveryTab;
    private MapDisplay map, mapDelivery, routeMap;
    private JFileChooser fileChooserDelivery, fileChooserMap, fileExportWaitingList, fileExportRoutes, fileImportRoutes;
    private JTextField courierFieldFirstName, courierFieldLastName, courierFieldPhoneNumber;
    private JLabel firstNameOfSelectedCourier, lastNameOfSelectedCourier, phoneNumberOfSelectedCourier, mapLoadingBeforeDelivery;
    private JSplitPane splitPaneCourier;
    private Vertex mapDefault ;
    private List<Color> availableColors;
    private Map<String, Color> routeColors;

    public void addController(Controller controller) {
        fileChooserDelivery.addActionListener(controller);
        fileChooserMap.addActionListener(controller);
        fileExportWaitingList.addActionListener(controller);
        fileExportRoutes.addActionListener(controller);
        fileImportRoutes.addActionListener(controller);
        addCourierButton.addActionListener(controller);
        assignCourierButton.addActionListener(controller);
        waitingListButton.addActionListener(controller);
        exportWaitingListButton.addActionListener(controller);
        removeCourierButton.addActionListener(controller);
        courierList.addListSelectionListener(controller);
        courierListMapTab.addListSelectionListener(controller);
        courierMapDropdown.addActionListener(controller);
        courierDeliveryDropdown.addActionListener(controller);
        waitingListDropdown.addActionListener(controller);
        unassignedDeliveryDropdown.addActionListener(controller);
    }

    public Interface() {
        couriers = new Vector<>();
        selectedCourierVectorDeliveryTab = new Vector<>();
        selectedCourierVectorCourierTab = new Vector<>();
        fileChooserDelivery = new JFileChooser();
        fileChooserDelivery.setCurrentDirectory(new File("./resources"));
        fileChooserMap = new JFileChooser();
        fileChooserMap.setCurrentDirectory(new File("./resources"));
        fileExportWaitingList = new JFileChooser();
        fileExportWaitingList.setCurrentDirectory(new File("."));
        fileExportRoutes = new JFileChooser();
        fileExportRoutes.setCurrentDirectory(new File("."));
        fileImportRoutes = new JFileChooser();
        fileImportRoutes.setCurrentDirectory(new File("."));
        unassignedModel = new DefaultComboBoxModel<>();
        courierModel = new DefaultComboBoxModel<>(couriers);
        courierMapModel = new DefaultComboBoxModel<>(couriers);
        courierDeliveryModel = new DefaultComboBoxModel<>(couriers);
        waitingListModel = new DefaultComboBoxModel<>();
        unassignedDeliveryDropdown = new JComboBox<>(unassignedModel);
        courierDeliveryDropdown = new JComboBox<>(courierDeliveryModel);
        waitingListDropdown = new JComboBox<>(waitingListModel);
        assignCourierButton = new JButton("Assign the delivery to this courier");
        waitingListButton = new JButton("Put the delivery in the waiting list");
        exportWaitingListButton = new JButton("Export deliveries from the waiting list");
        courierMapDropdown = new JComboBox<>(courierMapModel);
        map = new MapDisplay();
        mapDelivery = new MapDisplay();
        routeMap = new MapDisplay();
        scrollPanelMap = new JScrollPane(map.getMapViewer());
        scrollPanelDeliveriesMap = new JScrollPane(mapDelivery.getMapViewer());
        courierList = new JList<>(courierModel);
        courierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedCourierListDeliveryTab = new JList<>();
        selectedCourierListDeliveryTab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedCourierListCourierTab = new JList<>();
        selectedCourierListCourierTab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courierListMapTab = new JList<>(couriers);
        courierListMapTab.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        availableColors = new ArrayList<>(Arrays.asList(
                Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.ORANGE, new Color(150, 131, 236), new Color(20, 148, 20),
                new Color(255, 20, 147)
        ));
        routeColors = new HashMap<>();


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
        deliveryPanel.setLayout(new BoxLayout(deliveryPanel, BoxLayout.Y_AXIS));
        mapLoadingBeforeDelivery = new JLabel("You must load a map before loading deliveries.");
        mapLoadingBeforeDelivery.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        mapLoadingBeforeDelivery.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deliveryButton = new JButton("Load Delivery");
        deliveryButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        deliveryButton.setEnabled(false);
        deliveryPanel.add(deliveryButton);
        deliveryPanel.add(mapLoadingBeforeDelivery);
        tabPan.addTab("Delivery", deliveryPanel);

        deliveryButton.addActionListener(e -> {
            fileChooserDelivery.showOpenDialog(null);
        });
    }

    private void showSettingsDelivery(){
        deliveryPanel.removeAll();
        deliveryButton.setText("Reset and load a new delivery file");

        mapDelivery.setCentre(mapDefault);

        // Initialisation du panneau principal
        mainPanelDeliveries = new JPanel(new BorderLayout());

        // Config panneau de controle
        controlDeliveriesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10 );
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        controlDeliveriesPanel.add(deliveryButton, gbc);

        // Livraisons non attribuées
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        controlDeliveriesPanel.add(new JLabel("Pending Deliveries :"), gbc);

        gbc.gridx = 1;
        controlDeliveriesPanel.add(unassignedDeliveryDropdown, gbc);

        // Choisir un livreur
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlDeliveriesPanel.add(new JLabel("Choose Courier :"), gbc);

        gbc.gridx = 1;
        controlDeliveriesPanel.add(courierDeliveryDropdown, gbc);

        //JLabel pour la liste des livraisons deja assignée au livreur choisi
        gbc.gridx = 0;
        gbc.gridy = 3;
        controlDeliveriesPanel.add(new JLabel("Delivery list of the selected courier :"), gbc);

        // Liste des livraisons du courier
        JScrollPane scrollPaneDelivery = new JScrollPane(selectedCourierListDeliveryTab);
        scrollPaneDelivery.setPreferredSize(new Dimension(200, 100));
        scrollPaneDelivery.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneDelivery.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        gbc.gridx = 1;
        controlDeliveriesPanel.add(scrollPaneDelivery, gbc);

        // Bouton pour affecter le livreur
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        controlDeliveriesPanel.add(assignCourierButton, gbc);

        //Bouton pour passer la livraison en attente
        gbc.gridx = 0;
        gbc.gridy = 5;
        controlDeliveriesPanel.add(waitingListButton, gbc);

        //JLabel pour la liste des livraisons deja assignée au livreur choisi
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        controlDeliveriesPanel.add(new JLabel("Deliveries in the waiting list : "), gbc);

        //Liste des livraison en attente
        gbc.gridx = 1;
        controlDeliveriesPanel.add(waitingListDropdown, gbc);


        //Bouton exporter les livraisons en attente
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        controlDeliveriesPanel.add(exportWaitingListButton, gbc);

        controlDeliveriesPanel.setBorder(BorderFactory.createTitledBorder("Management Deliveries Panel"));

        exportWaitingListButton.addActionListener(e -> {
            LocalDate actualDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            fileExportWaitingList.setSelectedFile(new File("waitingList-" + actualDate.format(formatter) + ".xml"));
            if (fileExportWaitingList.showSaveDialog(null) == JFileChooser.APPROVE_OPTION && waitingListDropdown.getSelectedItem() != null) {
                JOptionPane.showMessageDialog(null, "File has been saved at " + fileExportWaitingList.getSelectedFile().getAbsolutePath());
            } else {
                if (waitingListDropdown.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "File has not been saved: waiting list is empty");
                } else {
                    JOptionPane.showMessageDialog(null, "File has not been saved.");
                }
            };
        });

        // Ajout de controlMapPanel et scrollPanelDeliveries dans mainPanelMap
        mainPanelDeliveries.add(controlDeliveriesPanel, BorderLayout.EAST);
        mainPanelDeliveries.add(scrollPanelDeliveriesMap, BorderLayout.CENTER);
        tabPan.setComponentAt(1,mainPanelDeliveries);
    }

    private void setupCourierManagementPanel() {
        JPanel mainManagementPanel = new JPanel(new BorderLayout());

        // Configuration de courierInfoPanel
        courierInfoPanel = new JPanel(new GridBagLayout());
        courierInfoPanel.setVisible(false);
        courierInfoPanel.setBorder(BorderFactory.createTitledBorder("Selected Courier Informations"));

        // Labels et ComboBox
        firstNameOfSelectedCourier = new JLabel("First Name: ");
        lastNameOfSelectedCourier = new JLabel("Last Name: ");
        phoneNumberOfSelectedCourier = new JLabel("Phone Number: ");
        JLabel listOfDeliveries = new JLabel("List of Deliveries: ");

        // Liste des livraisons du courier
        JScrollPane scrollPaneDelivery = new JScrollPane(selectedCourierListCourierTab);
        scrollPaneDelivery.setPreferredSize(new Dimension(200, 100));
        scrollPaneDelivery.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneDelivery.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Configuration des contraintes de GridBagLayout pour un alignement vertical centré
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.anchor = GridBagConstraints.CENTER;  // Centrer horizontalement
        gbc1.insets = new Insets(10, 0, 10, 0);   // Espacement entre les éléments
        gbc1.gridx = 0;
        gbc1.fill = GridBagConstraints.HORIZONTAL;

        // Ajouter les composants avec un espacement vertical uniforme
        gbc1.gridy = 0;
        courierInfoPanel.add(firstNameOfSelectedCourier, gbc1);

        gbc1.gridy = 1;
        courierInfoPanel.add(lastNameOfSelectedCourier, gbc1);

        gbc1.gridy = 2;
        courierInfoPanel.add(phoneNumberOfSelectedCourier, gbc1);

        gbc1.gridy = 3;
        courierInfoPanel.add(listOfDeliveries, gbc1);

        gbc1.gridy = 4;
        courierInfoPanel.add(scrollPaneDelivery, gbc1);

        // Configuration de managementPanel
        JPanel managementPanel = new JPanel(new GridBagLayout());
        managementPanel.setBorder(BorderFactory.createTitledBorder("Management Panel"));

        // Organisation de la managementPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Réduction de l'espacement entre les composants pour rapprocher les zones de texte
        gbc.anchor = GridBagConstraints.WEST;  // Alignement des composants à gauche

        // Utilisation d'un modèle dynamique pour faciliter les modifications
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel listActualCourier = new JLabel("Actual Courier List :");
        listActualCourier.setFont(new Font("Georgia", Font.BOLD, 16));  // Mettre le texte en gras et plus grand
        managementPanel.add(listActualCourier, gbc);

        // JScrollPane pour afficher la liste des livreurs
        JScrollPane scrollPane = new JScrollPane(courierList);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Le JScrollPane occupe une colonne
        managementPanel.add(scrollPane, gbc);

        // Bouton "Remove Courier" à droite de la liste
        removeCourierButton = new JButton("Remove courier");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;  // Le bouton occupe une seule colonne
        gbc.fill = GridBagConstraints.HORIZONTAL; // Le bouton remplit la hauteur disponible
        managementPanel.add(removeCourierButton, gbc);

        // Ajouter le "New Courier Info" avant les champs de saisie
        JLabel newCourierInfoLabel = new JLabel("New Courier Info :");
        newCourierInfoLabel.setFont(new Font("Georgia", Font.BOLD, 16));  // Mettre le texte en gras et plus grand
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;  // Il occupe toute la largeur disponible
        gbc.anchor = GridBagConstraints.CENTER;
        managementPanel.add(newCourierInfoLabel, gbc);

        // Champ de texte pour "First Name"
        courierFieldFirstName = new JTextField(15);
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        managementPanel.add(new JLabel("First Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldFirstName, gbc);

        // Champ de texte pour "Last Name"
        courierFieldLastName = new JTextField(15);
        gbc.gridy = 5;
        gbc.gridx = 0;
        managementPanel.add(new JLabel("Last Name :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldLastName, gbc);

        // Champ de texte pour "Phone number"
        courierFieldPhoneNumber = new JTextField(15);
        gbc.gridy = 6;
        gbc.gridx = 0;
        managementPanel.add(new JLabel("Phone number :"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        managementPanel.add(courierFieldPhoneNumber, gbc);

        // Section des boutons
        // Bouton pour ajouter un nouveau livreur
        addCourierButton = new JButton("Add Courier");
        gbc.gridx = 0;
        gbc.gridy = 7;
        //gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        managementPanel.add(addCourierButton, gbc);

        // Division de mainManagementPanel en deux avec un JSplitPane
        splitPaneCourier = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, managementPanel, courierInfoPanel);
        splitPaneCourier.setResizeWeight(0.5); // Division égale
        splitPaneCourier.setDividerSize(5); // Largeur du séparateur

        // Ajout de splitPane dans mainManagementPanel
        mainManagementPanel.add(splitPaneCourier, BorderLayout.CENTER);

        tabPan.addTab("Courier Management", mainManagementPanel);

    }

    private void setupMapManagementPanel(){
        deliveryButton.setEnabled(true);
        deliveryPanel.remove(mapLoadingBeforeDelivery);
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

        JScrollPane scrollPaneCouriers = new JScrollPane(courierListMapTab);
        scrollPaneCouriers.setPreferredSize(new Dimension(200, 200));
        scrollPaneCouriers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneCouriers.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneCouriers.setAlignmentX(Component.CENTER_ALIGNMENT);

        courierListMapTab.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Use default renderer setup
                JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof String) {
                    Color color = routeColors.get(value);
                    if (color != null) {
                        c.setForeground(color); // Set color if found in the map
                        System.out.println(color);
                    } else {
                        c.setForeground(Color.BLACK); // Default color if no entry in map
                    }
                }
                return c;
            }
        });

        // Disable automatic selection on single click by using a custom ListSelectionModel.
        courierListMapTab.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Override to do nothing on single-click, selection is handled in the MouseListener
            }
        });

        courierListMapTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = courierListMapTab.locationToIndex(e.getPoint());
                if (index != -1) {  // Check if click is within an item
                    if (courierListMapTab.isSelectedIndex(index)) {
                        courierListMapTab.removeSelectionInterval(index, index);  // Deselect item if selected
                    } else {
                        courierListMapTab.addSelectionInterval(index, index);    // Select item if not selected
                    }
                }
            }
        });

        exportRoutes = new JButton("Export Routes");
        exportRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

        exportRoutes.addActionListener(e -> {
            LocalDate actualDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            if (courierListMapTab.getSelectedValuesList().size() == 1) {
                fileExportRoutes.setSelectedFile(new File("exportedRoute-" + actualDate.format(formatter) + ".xml"));
                if (fileExportRoutes.showSaveDialog(null) == JFileChooser.APPROVE_OPTION && !getCourierMapList().isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(null, "File has been saved at " + fileExportRoutes.getSelectedFile().getAbsolutePath());
                } else {
                    if (getCourierMapList().isSelectionEmpty()) {
                        JOptionPane.showMessageDialog(null, "File has not been saved, please select a courier.");
                    } else {
                        JOptionPane.showMessageDialog(null, "File has not been saved.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "You must select only one courier to export its route.");
            }
        });

        importRoutes = new JButton("Import Routes");
        importRoutes.setAlignmentX(Component.CENTER_ALIGNMENT);

        importRoutes.addActionListener(e -> {
            fileImportRoutes.showOpenDialog(null);
        });

        // Ajout des composants avec des espaces entre eux
        controlMapPanel.add(select);
        controlMapPanel.add(Box.createVerticalStrut(componentSpacing)); // Espacement
        controlMapPanel.add(scrollPaneCouriers);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("errorMessage")) {
            JOptionPane.showMessageDialog(this, evt.getNewValue());
        }
        if (evt.getPropertyName().equals("map")) {
            mapDefault = (Vertex) evt.getNewValue();
            map.setCentre((Vertex) evt.getNewValue());
            this.setupMapManagementPanel();
        }
        if (evt.getPropertyName().equals("displayEntrepot")) {
            Vertex entrepotAddress = (Vertex) evt.getNewValue();
            map.displayVertex(entrepotAddress, "Warehouse", Color.red,true);
        }
        if (evt.getPropertyName().equals("resetMap")) {
            map.hideAll();
        }
        if (evt.getPropertyName().equals("displayVerticesMainMap")) {
            ArrayList<Vector> vertexVectorArrayList = (ArrayList<Vector>) evt.getNewValue();
            for (Vector vector : vertexVectorArrayList) {
                if (vector.get(1).equals("PICK_UP")) {
                    map.displayVertex((Vertex) vector.getFirst(), Integer.toString(vertexVectorArrayList.indexOf(vector)+1),Color.cyan,false);
                } else {
                    map.displayVertex((Vertex) vector.getFirst(), Integer.toString(vertexVectorArrayList.indexOf(vector)+1),Color.orange,false);
                }
            }
        }
        if (evt.getPropertyName().equals("displayDelivery")) {
            ArrayList<Vertex> deliveryVertices = (ArrayList<Vertex>) evt.getNewValue();
            mapDelivery.hideAll();
            mapDelivery.displayVertex(deliveryVertices.getFirst(), "PICK UP",Color.cyan,false);
            mapDelivery.displayVertex(deliveryVertices.getLast(), "DELIVERY",Color.orange,false);
        }
        if (evt.getPropertyName().equals("createNewMap")) {
            ArrayList<Vertex> routeVertices = (ArrayList<Vertex>) evt.getNewValue();
            routeMap.setCentre(routeVertices.getFirst());
            routeMap.hideAll();
            this.setupMapManagementPanel();
            for(Vertex vertex : routeVertices){
                routeMap.displayVertex(vertex);
            }
            JFrame routeFrame = new JFrame();
            routeFrame.setSize(300, 300);
            routeFrame.setLocationRelativeTo(this);
            routeFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            routeFrame.setContentPane(new JScrollPane(routeMap.getMapViewer()));
            routeFrame.setTitle("Route Display");
            routeFrame.setVisible(true);
        }
        if (evt.getPropertyName().equals("displaySegmentsRouteMap")) {
            ArrayList<Segment> segmentArrayList = (ArrayList<Segment>) evt.getNewValue();
            for (Segment segment : segmentArrayList) {
                routeMap.displaySegment(segment, Color.BLUE);
            }
        }
        if (evt.getPropertyName().equals("displaySegmentsMainMap")) {
            ArrayList<Segment> segmentArrayList = (ArrayList<Segment>) evt.getNewValue();
            Random random = new Random();
            String courierID = (String) evt.getOldValue(); // ID = FirstName + " " + LastName
            Color color;
            if (routeColors.containsKey(courierID)) {
                color = routeColors.get(courierID);
            } else {
                color = availableColors.remove(random.nextInt(availableColors.size()));
                routeColors.put(courierID, color);
            }

            if (!segmentArrayList.isEmpty()){
                for(Segment segment : segmentArrayList){
                    map.displaySegment(segment,color);
                }
            }

            courierListMapTab.repaint();
        }
        if (evt.getPropertyName().equals("courierArrayList")) {
            ArrayList<Courier> courierList  = (ArrayList<Courier>) evt.getNewValue();
            courierInfoPanel.setVisible(!courierList.isEmpty());
            splitPaneCourier.setDividerLocation(0.7);
            updateCourierList(courierList);
            courierFieldFirstName.setText("");
            courierFieldLastName.setText("");
            courierFieldPhoneNumber.setText("");
        }
        if (evt.getPropertyName().equals("pendingDeliveryArrayList")) {
            showSettingsDelivery();
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
        if (evt.getPropertyName().equals("deliveryListDeliveryTab")){
            ArrayList<Delivery>  deliveryList = (ArrayList<Delivery>) evt.getNewValue();
            updateDeliveryListDeliveryTab(deliveryList);
        }
        if (evt.getPropertyName().equals("deliveryListCourierTab")){
            ArrayList<Delivery>  deliveryList = (ArrayList<Delivery>) evt.getNewValue();
            updateDeliveryListCourierTab(deliveryList);
        }
        if(evt.getPropertyName().equals("courierInfo")){
            Courier courier = (Courier) evt.getNewValue();
            firstNameOfSelectedCourier.setText("First Name : " + courier.getFirstName());
            lastNameOfSelectedCourier.setText("Last Name : " + courier.getLastName());
            phoneNumberOfSelectedCourier.setText("Phone Number : " + courier.getPhoneNum());
        }
        if(evt.getPropertyName().equals("updateWaitingList")){
            ArrayList<Delivery>  deliveryWaitingList = (ArrayList<Delivery>) evt.getNewValue();
            waitingListModel.removeAllElements();
            for (Delivery delivery : deliveryWaitingList) {
                String deliveryWaitingString = delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId();
                waitingListModel.addElement(deliveryWaitingString);
                unassignedModel.removeElement(deliveryWaitingString);
            }
        }
        if(evt.getPropertyName().equals("courierDeleted")) {
            String courierID = (String) evt.getNewValue();
            Color associatedColor = routeColors.get(courierID);
            if (associatedColor != null) {
                routeColors.remove(courierID);
                availableColors.add(associatedColor);
            }
            map.hideAll();
            courierListMapTab.clearSelection();
            firstNameOfSelectedCourier.setText("First Name :");
            lastNameOfSelectedCourier.setText("Last Name :");
            phoneNumberOfSelectedCourier.setText("Phone Number :");
            updateDeliveryListCourierTab(new ArrayList<>());
            updateDeliveryListDeliveryTab(new ArrayList<>());
        }
    }

    private void updateCourierList(ArrayList<Courier> newCourierList) {
        couriers.clear();
        courierMapDropdown.removeAllItems();
        courierDeliveryDropdown.removeAllItems();
        for (Courier courier : newCourierList) {
            couriers.add(courier.getFirstName()+ " " + courier.getLastName());
        }
        courierList.setListData(couriers);
        courierListMapTab.setListData(couriers);
    }

    private void updateDeliveryListDeliveryTab(ArrayList<Delivery> newDeliveryList) {
        selectedCourierVectorDeliveryTab.clear();
        for (Delivery delivery : newDeliveryList) {
            selectedCourierVectorDeliveryTab.add(delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId());
        }
        selectedCourierListDeliveryTab.setListData(selectedCourierVectorDeliveryTab);
    }

    private void updateDeliveryListCourierTab(ArrayList<Delivery> newDeliveryList) {
        selectedCourierVectorCourierTab.clear();
        for (Delivery delivery : newDeliveryList) {
            selectedCourierVectorCourierTab.add(delivery.getPickUpPt().getId() + "-" + delivery.getDeliveryPt().getId());
        }
        selectedCourierListCourierTab.setListData(selectedCourierVectorCourierTab);
    }


    // Getters
    public JFileChooser getFileChooserDelivery() {
        return fileChooserDelivery;
    }

    public JFileChooser getFileChooserMap() {
        return fileChooserMap;
    }

    public JFileChooser getFileChooserImport() {
        return fileImportRoutes;
    }

    public JFileChooser getFileExportWaitingList() { return fileExportWaitingList; }

    public JFileChooser getFileExportRoutes() { return fileExportRoutes; }

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

    public JComboBox<String> getCourierDeliveryComboBox() {
        return courierDeliveryDropdown;
    }

    public JComboBox<String> getPendingDeliveryComboBox() {
        return unassignedDeliveryDropdown;
    }

    public JList<String> getCourierList() {
        return courierList;
    }

    public JList<String> getCourierMapList() {
        return courierListMapTab;
    }

    public JComboBox<String> getWaitingList() { return waitingListDropdown; }

    public JButton getWaitingListButton() {return waitingListButton;}

}

