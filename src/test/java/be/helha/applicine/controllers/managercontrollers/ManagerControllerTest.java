package be.helha.applicine.controllers.managercontrollers;

import be.helha.applicine.client.controllers.managercontrollers.ManagerController;
import be.helha.applicine.common.models.Movie;
import be.helha.applicine.common.models.Viewable;
import org.junit.jupiter.api.Test;

import javax.swing.text.View;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerControllerTest {

    @Test
    void getMovieFrom() {
        int id = 0;
        ManagerController managerController = new ManagerController();
        managerController.getMovieFrom(id);
        try {
            assertNotEquals(0, managerController.getMovieFrom(id).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void fullFieldMovieListFromDB() {
        ManagerController managerController = new ManagerController();
        try {
            managerController.fullFieldMovieListFromDB();
//            System.out.println(list.size());
//            assertNotEquals(0, list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}