import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HistoriqueController {

    private double xOffset = 0;
    private double yOffset = 0;
    private String numero;

    @FXML
    private AnchorPane root;

    @FXML
    private ListView<String> histView;

    // recupere les valeurs
    public void init(String num){
        this.numero = num;

        // connection a la bd
        ConnexionDatabase connexionDatabase = new ConnexionDatabase();
        try {
            // connection a l'objet
            Connection connection = connexionDatabase.connexiondb();

            // ecriture de la requete
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM historique WHERE num = ?");

            // passage de parametre a la requete
            preparedStatement.setString(1, num);

            // execution
            ResultSet resultSet = preparedStatement.executeQuery();

            // parcours des valeurs
            while (resultSet.next()) {

                // formatage de date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'a' HH:mm");
                LocalDateTime localDateTime = (LocalDateTime) resultSet.getObject("date_retrait");
                String temp = "Retrait numero: " + resultSet.getInt("id") + 
                              "\nMontant: " + resultSet.getInt("montant") + " CFAF\n" + 
                              "Date: " + localDateTime.format(formatter) ; 

                // assignation de la variable a la liste
                histView.getItems().add(temp);
            }
        } catch (Exception e) {
            JOptionPane.showInputDialog(null, "Nous avons rencontre un probleme!");
        }

    }

    public void close(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public void reduce(){
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
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

    // pour telecharger l'historique
    public void download(){

        // recuperation du stage
        Stage primaryStage = (Stage) root.getScene().getWindow();

        // lasse permettant d'ouvrir l'explorateur de fichier
         FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer mon historique");

            // permet de filtrer les extension
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")
            );

            // Montrer la fenêtre pour choisir le fichier
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                // Écrire les items dans le fichier
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Historique du compte " + this.numero);
                        writer.newLine();
                        LocalDateTime date_now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'a' HH:mm");
                        writer.write("Date de telechargement " + date_now.format(formatter));
                        writer.newLine();
                        writer.write("--------------------------------------------------");
                        writer.newLine();
                        writer.newLine();
                        writer.newLine();
                    for (String item : histView.getItems()) {
                        writer.write(item);
                        writer.newLine();
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(null, "Fichier enregistré avec succès");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
    }
    
}
