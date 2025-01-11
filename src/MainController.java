// import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class MainController {

    private double xOffset = 0;
    private double yOffset = 0;

    // infos users
    private String numero;
    private double solde;

    @FXML
    private Label welcomLayout;

    @FXML
    private AnchorPane root;
    
    @FXML
    private Label amount;
    
    public void close(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void reduce(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    // recupere les valeurs
    public void init(String num){
        this.numero = num;

        // connexion a la bd
        ConnexionDatabase connexionDatabase = new ConnexionDatabase();

        Connection connection = connexionDatabase.connexiondb(); 
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("SELECT amount FROM compte WHERE num = ?");
            ps.setString(1, this.numero);
            // valider la requete
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                this.solde = resultSet.getDouble("amount");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }


        welcomLayout.setText("Bienvenu, " + this.numero);
        this.amount.setText("********");
    }

    // change scene to retrait scene
    public void faireRetrait(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("retrait.fxml"));
            Parent parent = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            Scene scene = new Scene(parent);

            // capture de l'etat de la souris
            parent.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });

            // detection de la position du cursor lorsque ca se depasse
            parent.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });

            // passage des informations
            RetraitController retraitController = loader.getController();
            retraitController.init(this.numero);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Nous avons recontres une erreur!");
        }
    }


// change scene to historique scene
    public void consulterHistorique(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("historique.fxml"));
            Parent parent = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            Scene scene = new Scene(parent);

            // capture de l'etat de la souris
            parent.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });

            // detection de la position du cursor lorsque ca se depasse
            parent.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });


            // passage du numero
            HistoriqueController historiqueController = loader.getController();
            historiqueController.init(this.numero);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Nous avons recontres une erreur!");
        }
    }

    public void hideDisplay(){
        if(!amount.getText().equals("********")){
            amount.setText("********");
        }
        else{
            // affichage formatee 100000 -> 100 000
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
            amount.setText(numberFormat.format(solde));
        }
    }
}
