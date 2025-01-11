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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RetraitController {

    private String numero;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField numCompte;

    @FXML
    private PasswordField mdp;

    @FXML
    private TextField montant;

    @FXML
    private ImageView imgConfirm;

    @FXML
    private Label msgConfirm;

    public void init(String num){
        this.numero = num;
        numCompte.setText(num);
    }

    public void close(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void reduce(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    // methode de retrait
    public void getRetrait(){

        // recuperation des donnees
        String numCompteText = numCompte.getText();
        String mdpText = mdp.getText();
        String montantText = montant.getText();

        if(!numCompteText.isEmpty() && !mdpText.isEmpty() && !montantText.isEmpty()){
            try {
                double montantEntrer = Integer.parseInt(montantText);
                ConnexionDatabase connexionDatabase = new ConnexionDatabase();
                try {
                    // recuperation de la connexion
                    Connection connection = connexionDatabase.connexiondb();

                    // ecriture de la requette
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM compte WHERE num = ? and mdp = ?");

                    // passage des parametres
                    preparedStatement.setString(1, numCompteText);
                    preparedStatement.setString(2, mdpText);

                    // execution
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()){
                        // recuperation du montant
                        double montant =  resultSet.getDouble("amount");

                        double montantRestant = montant - montantEntrer;
                        
                        // comparaison des prix
                        if(montantRestant > 0){

                            // requete de mise a jour
                            PreparedStatement ps = connection.prepareStatement("UPDATE compte SET amount = ? WHERE num = ? AND mdp = ?");

                            // passage des parametres
                            ps.setDouble(1, montantRestant);
                            ps.setString(2, numCompteText);
                            ps.setString(3, mdpText);

                            // validation de la requete
                            int rep = ps.executeUpdate();

                            if(rep > 0){
                                // sauvegarde de l'historique
                                PreparedStatement pssm = connection.prepareStatement("INSERT INTO historique(num, montant) VALUES(?, ?)");
                                pssm.setString(1, this.numero);
                                pssm.setDouble(2, montantEntrer);

                                int rep1 = pssm.executeUpdate();

                                if(rep1 > 0){
                                     // creation de l'image
                                    Image image = new Image("res/check.png");
                                    imgConfirm.setImage(image);
                                    msgConfirm.setText("Reussite");
                                }
                                else{
                                    // creation de l'image
                                    Image image = new Image("res/bad.png");
                                    imgConfirm.setImage(image);
                                    msgConfirm.setText("Echec");
                                    msgConfirm.setStyle("-fx-text-fill: red");
                                }
                            }
                            else{
                                // creation de l'image
                                Image image = new Image("res/bad.png");
                                imgConfirm.setImage(image);
                                msgConfirm.setText("Echec");
                                msgConfirm.setStyle("-fx-text-fill: red");
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Impossible de faire un retrait\nLe solde est insuffisant!");

                            // creation de l'image
                            Image image = new Image("res/bad.png");
                            imgConfirm.setImage(image);
                            msgConfirm.setText("Echec");
                            msgConfirm.setStyle("-fx-text-fill: red");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Aucun compte ne correspond au numero entre veuillez reessayer!");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erreur: " + e.getMessage());

                    // creation de l'image
                    Image image = new Image("res/bad.png");
                    
                    imgConfirm.setImage(image);
                    msgConfirm.setText("Echec");
                    msgConfirm.setStyle("-fx-text-fill: red");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());

                Image image = new Image("res/bad.png");
                imgConfirm.setImage(image);
                msgConfirm.setText("Echec");
                msgConfirm.setStyle("-fx-text-fill: red");
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Veuillez completer tous les champs!");
        }
    }


    // retour a la page d'acceuil
    public void home(ActionEvent event){
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
            mainController.init(this.numero);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
