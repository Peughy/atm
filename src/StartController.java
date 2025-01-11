import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class StartController {

    private double xOffset = 0;
    private double yOffset = 0;

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
    public void getApp(ActionEvent event){
        String numCompteText = numCompte.getText();

        if(!numCompteText.isEmpty()){
            ConnexionDatabase connexionDatabase = new ConnexionDatabase();
            try {
                // recuperation de la connexion
                Connection connection = connexionDatabase.connexiondb();

                // ecriture de la requette
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM compte WHERE NUM = ?");

                // passage des parametres
                preparedStatement.setString(1, numCompteText);

                // execution
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){

                    // recuperation des valeur 
                    String numCompte = resultSet.getString("num");
                    
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
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

                        // envoi des donnees
                        MainController mainController = loader.getController();
                        mainController.init(numCompte);

                        stage.setScene(scene);
                        stage.show();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }

                }
                else{
                    JOptionPane.showMessageDialog(null, "Aucun compte ne correspond au numero entre veuillez reessayer!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erreur: " + e.getMessage());
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Veuillez completer tous les champs!");
        }
    }
   
}
