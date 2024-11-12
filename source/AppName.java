package source;

import source.controller.Controller;
import source.model.Model;
import source.view.Interface;
import javax.swing.*;

public class AppName {

    public AppName() {
        Model model = new Model();
        Interface view = new Interface();
        Controller controller = new Controller(model, view);

        model.addPropertyChangeListener(view);
        view.addController(controller);

    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                System.out.println(info.getName() + " - " + info.getClassName());
            }
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            new AppName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
