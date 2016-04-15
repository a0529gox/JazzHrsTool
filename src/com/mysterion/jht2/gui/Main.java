package com.mysterion.jht2.gui;

import com.mysterion.jht2.log.AnnoyLogger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
			
			// 載入FXML內容並轉換為Parent
			Parent fxmlRoot = loader.load();
			
			Controller controller = loader.getController();

			Scene scene = new Scene(fxmlRoot);

			primaryStage.setTitle("JazzHrsTool v2.0");

			primaryStage.setScene(scene);
			
			controller.setStage(primaryStage);

			// Show Stage
			primaryStage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
			AnnoyLogger.severe(ex);
		}
	}

}
