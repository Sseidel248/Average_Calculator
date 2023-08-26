package com.sseidel.average_calculator.application;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.concurrent.atomic.AtomicInteger;

public class MsgDialog {
    public static final int MR_YES = 1;
    public static final int MR_NO = 2;
    public static int showInvalidFileMsg(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setContentText("Error loading the file. Do you want to load another file?");
        alert.setHeaderText("Loading error");
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");
        alert.getButtonTypes().setAll(yesBtn, noBtn);
        //AtomicInteger da in lambda verwendet
        AtomicInteger result = new AtomicInteger(MR_NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                result.set(MR_YES);
            } else {
                result.set(MR_NO);
            }
        });
        return result.get();
    }

    public static void showIncorrectFileContent(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("The loaded file contains incorrect data. Please check your file.");
        alert.setHeaderText("Incorrect Content");
        ButtonType okBtn = new ButtonType("Ok");
        alert.getButtonTypes().setAll(okBtn);
        alert.showAndWait();
    }

    public static void showWrongFileFormat(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("The format of the file is incorrect and therefore it cannot be loaded.");
        alert.setHeaderText("Wrong format");
        ButtonType okBtn = new ButtonType("Ok");
        alert.getButtonTypes().setAll(okBtn);
        alert.showAndWait();
    }

    public static int showLoadAnotherFile(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setContentText("Do you want to load another file?");
        alert.setHeaderText("Load another file");
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");
        alert.getButtonTypes().setAll(yesBtn, noBtn);
        //AtomicInteger da in lambda verwendet
        AtomicInteger result = new AtomicInteger(MR_NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                result.set(MR_YES);
            } else {
                result.set(MR_NO);
            }
        });
        return result.get();
    }

}
