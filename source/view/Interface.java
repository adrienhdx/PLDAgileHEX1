package source.view;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Interface implements PropertyChangeListener {

    private JPanel panelMain;
    private JButton chargerLaCarteButton;
    private JButton chargerLesLivraisonsButton;
    private JComboBox comboBox1;
    private JButton visualiserLItinéraireButton;
    private JPanel panelUser;
    private JPanel panelMap;
    private JScrollPane scrollPanelMap;
    private MapDisplay map;

    public Interface() {
        JFrame frame = new JFrame("App v0");
        frame.setContentPane(panelMain);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = dimension.height;
        int screenWidth = dimension.width;
        frame.setSize(screenWidth, screenHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    };

    private void createUIComponents() {
        comboBox1 = new JComboBox<>();
        map = new MapDisplay();
        scrollPanelMap = new JScrollPane(map.getMapViewer());
        // TODO: place custom component creation code here
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
