package com.petarzlatev.languageclasses.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.PasswordUtils;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.SystemLogger;
import com.petarzlatev.languageclasses.SystemMessages;
import com.petarzlatev.languageclasses.SystemProperties;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController implements Initializable {
	private UserDAO database = DataSource.getInstance().getUserDAO();
	@FXML
	private GridPane gridPane;
	@FXML
	private Label userLabel;
	@FXML
	private TextField username;
	@FXML
	private Label passLabel;
	@FXML
	private PasswordField password;
	@FXML
	private Button submitBtn;
	@FXML
	private Text welcomeText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		welcomeText.setText(Messages.getString("LoginScene.WELCOME"));
		userLabel.setText(Messages.getString("LoginScene.USERNAME"));
		passLabel.setText(Messages.getString("LoginScene.PASSWORD"));
		submitBtn.setText(Messages.getString("LoginScene.SUBMIT"));
	}

	/****************************************************
	 * EventHandlers *
	 ****************************************************/

	@FXML
	protected void handleSubmitKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			handleSubmitButtonAction();
		}
	}

	@FXML
	protected void handleSubmitButtonAction() {
		Stage primaryStage = (Stage) gridPane.getScene().getWindow();

		try {
			User currentUser = database.loadCurrentUser(username.getText());
			if (currentUser != null && PasswordUtils.verifyUserPassword(password.getText(), currentUser.getPassword(),
					currentUser.getSalt())) {
				SystemLibrary.setCurrentUser(currentUser);
				SystemLogger.logEvent("User: " + currentUser.getUsername() + " logged in", Level.INFO,
						getClass().getName() + " " + SystemLibrary.methodName());
				primaryStage.setTitle(Messages.getString("LoginScene.MAINTITLE") + " - "
						+ SystemLibrary.getCurrentUser().getUsername());
				try {
					Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/main.fxml")), 300, 200);
					scene.getStylesheets()
							.add(getClass().getResource("/fxml/css/" + SystemProperties.getUITheme()).toExternalForm());
					primaryStage.setScene(scene);
					primaryStage.centerOnScreen();
				} catch (IOException e) {
					SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
							Messages.getString("System.ERROR"));
				}
			} else {
				SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_LOGIN"), Messages.getString("System.ERROR"));
				password.selectAll();
			}
		} catch (SQLException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_NO_DATABASE"),
					Messages.getString("System.ERROR"));
			Platform.exit();
		} catch (PersistenceException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_NO_DATABASE"),
					Messages.getString("System.ERROR"));
			Platform.exit();
		}
	}
}
