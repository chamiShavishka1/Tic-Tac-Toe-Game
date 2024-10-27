package lk.ijse.gdse68.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.gdse68.util.DEPAlert;

import java.io.IOException;

public class CreatePlayerController {
    public TextField txtName;
    public JFXButton btnPlay;

    public void btnPlayOnAction(ActionEvent actionEvent) throws IOException {
        String name = txtName.getText();
        if (name.isBlank()){
            new DEPAlert(Alert.AlertType.ERROR, "Error", "Empty Name", "Name can't be empty").show();
            txtName.requestFocus();
            txtName.selectAll();
            return;
        }else if (!name.matches("[A-Za-z ]+")){
            new DEPAlert(Alert.AlertType.WARNING, "Error", "Invalid Name", "Please enter a valid name").show();
            txtName.requestFocus();
            txtName.selectAll();
            return;
        }
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Board.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        ((BoardController)(fxmlLoader.getController())).initData(name);
        stage.setResizable(false);
        stage.setTitle("Connect 4 Game - Player: " + name);
        stage.show();
        stage.centerOnScreen();
//        stage.setOnCloseRequest(Event::consume);
        btnPlay.getScene().getWindow().hide();
        Platform.runLater(stage::sizeToScene);
    }
}