// import javafx.event.ActionEvent;

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
    public void init(String num, double amount){
        this.numero = num;
        this.solde = amount;

        welcomLayout.setText("Bienvenu, " + this.numero);
        this.amount.setText("********");
    }

    // change scene to retrait scene
    public void faireRetrait(ActionEvent event){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("retrait.fxml"));

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

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Nous avons recontres une erreur!");
        }
    }


// change scene to historique scene
    public void consulterHistorique(ActionEvent event){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("historique.fxml"));

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
