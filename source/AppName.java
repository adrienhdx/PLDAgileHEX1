package source;

import source.controller.Controller;
import source.model.Model;
import source.view.Interface;

public class AppName {

    public AppName() {
        Model model = new Model();
        Interface view = new Interface();
        Controller controler = new Controller(model, view);
        model.addPropertyChangeListener(view);

    }

    public static void main(String[] args) {
        new AppName();
    }
}
