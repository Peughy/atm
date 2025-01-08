import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class StartController {

    // recuperer les valeur des variables
    @FXML
    private AnchorPane root;

    @FXML
    private TextField numCompte;

    // methode pour fermer la fenetre
    public void close(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }


    // methode pour reduire la fenetre
    public void reduce(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    // recuperation des informations
   
}
