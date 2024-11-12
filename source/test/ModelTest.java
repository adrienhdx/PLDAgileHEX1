package source.test;

import org.junit.jupiter.api.Test;
import source.model.*;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {


    @Test
    void createCourier() {
        Model model = new Model();
        Courier courier = model.createCourier("Adrien", "Houdoux", "0626510894");
        assertEquals("Adrien", courier.getFirstName());
    }

    @Test
    void addCourier() {
        Model model = new Model();
        Courier courier = model.createCourier("Adrien", "Houdoux", "0626510894");
        model.addCourier(courier);
        assertEquals(1, model.getCourierArrayList().size());
    }

    @Test
    void getCourier() {
        Model model = new Model();
        Courier courier = model.createCourier("Adrien", "Houdoux", "0626510894");
        model.addCourier(courier);
        assertEquals(courier, model.getCourier("Adrien", "Houdoux"));
    }

    @Test
    void deleteCourier() {
        Model model = new Model();
        Courier courier = model.createCourier("Adrien", "Houdoux", "0626510894");
        model.addCourier(courier);
        model.deleteCourier(courier);
        assertEquals(0, model.getCourierArrayList().size());
    }
}