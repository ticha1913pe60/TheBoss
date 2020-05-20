package com.petarzlatev.languageclasses.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.PasswordUtils;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	/****************************************************
	 * Main method
	 ****************************************************/
	public static void main(String[] args) {
		launch(args);
	}

	/****************************************************
	 * Application
	 ****************************************************/
	@Override
	public void start(Stage primaryStage) throws Exception {
		Locale.setDefault(SystemLibrary.getDefaultLocale());
		try {
			if (!SystemLibrary.noLogin()) {
				selectStartScene(primaryStage, Messages.getString("LoginScene.TITLE"), "/fxml/login.fxml", 300, 200);
			} else {
				try {
					UserDAO database = DataSource.getInstance().getUserDAO();
					User currentUser = database.loadCurrentUser(SystemLibrary.getProperty("USER"));
					if (currentUser != null && PasswordUtils.verifyUserPassword(SystemLibrary.getProperty("PASS"),
							currentUser.getPassword(), currentUser.getSalt())) {
						SystemLibrary.setCurrentUser(currentUser);
						SystemLibrary.logEvent("User: " + currentUser.getUsername() + " logged in", Level.INFO,
								getClass().getName() + " " + SystemLibrary.methodName());
						selectStartScene(primaryStage, Messages.getString("LoginScene.MAINTITLE") + " - "
								+ SystemLibrary.getCurrentUser().getUsername(), "/fxml/main.fxml", 300, 200);
					} else {
						SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "USER, PASS", Level.SEVERE,
								getClass().getName() + " " + SystemLibrary.methodName());
					}
				} catch (NullPointerException e) {
					SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "USER, PASS", Level.SEVERE,
							getClass().getName() + " " + SystemLibrary.methodName());
				}
			}
		} catch (NullPointerException e) {
			SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "LOGIN", Level.SEVERE,
					getClass().getName() + " " + SystemLibrary.methodName());
		} catch (IOException e) {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(), Messages.getString("System.ERROR"));
		}
	}

	@Override
	public void init() {
		try {
			if (!DataSource.getInstance().open()) {
				Platform.exit();
			}
		} catch (SQLException e) {
			System.out.println(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage());
			Platform.exit();
		}
	}

	@Override
	public void stop() throws Exception {
		DataSource.getInstance().close();
	}

	private void selectStartScene(Stage primaryStage, String title, String resource, int width, int height)
			throws IOException {
		Scene scene = new Scene(FXMLLoader.load(getClass().getResource(resource)), width, height);
		scene.getStylesheets().add(getClass().getResource("/fxml/css/" + SystemLibrary.getUITheme()).toExternalForm());
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(event -> {
			if (!SystemLibrary.yesNoQuery(primaryStage.getScene().getWindow(),
					Messages.getString("System.EXIT"), Messages.getString("System.ARE_YOU_SURE"))) {
				event.consume();
			} else {
				SystemLibrary.logEvent("Application close request confirmed", Level.INFO, "", "Closing application");
			}
		});
		primaryStage.show();
	}
}
