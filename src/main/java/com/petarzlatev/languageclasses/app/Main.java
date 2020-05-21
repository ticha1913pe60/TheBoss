package com.petarzlatev.languageclasses.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.PasswordUtils;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.SystemLogger;
import com.petarzlatev.languageclasses.SystemMessages;
import com.petarzlatev.languageclasses.SystemProperties;
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
		Locale.setDefault(SystemProperties.getDefaultLocale());
		try {
			if (!SystemProperties.noLogin()) {
				selectStartScene(primaryStage, Messages.getString("LoginScene.TITLE"), "/fxml/login.fxml", 300, 200);
			} else {
				UserDAO database = DataSource.getInstance().getUserDAO();
				String defaultUserName = SystemProperties.getDefaultUserForNoLogin();
				if (defaultUserName != null) {
					User currentUser = database.loadCurrentUser(defaultUserName);
					if (currentUser != null) {
						String defaultPassWord = SystemProperties.getDefaultPassForNoLogin();
						if (defaultPassWord != null) {
							if (PasswordUtils.verifyUserPassword(defaultPassWord, currentUser.getPassword(),
									currentUser.getSalt())) {
								SystemLibrary.setCurrentUser(currentUser);
								SystemLogger.logEvent("User: " + currentUser.getUsername() + " logged in", Level.INFO,
										getClass().getName() + " " + SystemLibrary.methodName());
								selectStartScene(primaryStage,
										Messages.getString("LoginScene.MAINTITLE") + " - "
												+ SystemLibrary.getCurrentUser().getUsername(),
										"/fxml/main.fxml", 300, 200);
							} else {
								SystemLogger.logEvent(
										Messages.getString("Error.ERROR_CONFIG_PARAM") + "PASS: " + defaultPassWord,
										Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
							}
						} else {
							SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "PASS",
									Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
						}
					} else {
						SystemLogger.logEvent(
								Messages.getString("Error.ERROR_CONFIG_PARAM") + "USER: " + defaultUserName,
								Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
					}
				} else {
					SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "USER",
							Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
				}
			}
		} catch (NullPointerException e) {
			SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "LOGIN", Level.SEVERE,
					getClass().getName() + " " + SystemLibrary.methodName());
		} catch (IOException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
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
		scene.getStylesheets().add(getClass().getResource("/fxml/css/" + SystemProperties.getUITheme()).toExternalForm());
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(event -> {
			if (!SystemMessages.showYesNoQuery(primaryStage.getScene().getWindow(), Messages.getString("System.EXIT"),
					Messages.getString("System.ARE_YOU_SURE"))) {
				event.consume();
			} else {
				SystemLogger.logEvent("Application close request confirmed", Level.INFO, "", "Closing application");
			}
		});
		primaryStage.show();
	}
}
