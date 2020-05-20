package com.petarzlatev.languageclasses.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.PersonDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.Student;
import com.petarzlatev.languageclasses.model.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class Controller {
	private PersonDAO database;
	@FXML
	private Pane thePane;
	@FXML
	private TableView<Person> theTable;

	public void switchScene(Stage primaryStage, String title, String fxml, int width, int height) {
		try {
			primaryStage.setTitle(title + " - " + SystemLibrary.getCurrentUser().getUsername());
			Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxml)), width, height);
			scene.getStylesheets()
					.add(getClass().getResource("/fxml/css/" + SystemLibrary.getUITheme()).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
		} catch (IOException e) {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
		}
	}

	@FXML
	public void handleMainMenu() {
		if (SystemLibrary.yesNoQuery(thePane.getScene().getWindow(), Messages.getString("MainMenu.TITLE"),
				Messages.getString("System.ARE_YOU_SURE"))) {
			switchScene((Stage) thePane.getScene().getWindow(), Messages.getString("LoginScene.MAINTITLE"),
					"/fxml/main.fxml", 300, 200);
		}
	}

	@FXML
	public void handleExit() {
		if (SystemLibrary.yesNoQuery(thePane.getScene().getWindow(), Messages.getString("System.EXIT"),
				Messages.getString("System.ARE_YOU_SURE"))) {
			Platform.exit();
		}
	}

	@FXML
	public void handleLogout() {
		if (SystemLibrary.yesNoQuery(thePane.getScene().getWindow(), Messages.getString("MainMenu.LOGOUT"),
				Messages.getString("System.ARE_YOU_SURE"))) {
			switchScene((Stage) thePane.getScene().getWindow(), Messages.getString("LoginScene.TITLE"),
					"/fxml/login.fxml", 300, 200);
		}
	}

	@FXML
	public void handleStudents() {
		if (this instanceof MainController || SystemLibrary.yesNoQuery(thePane.getScene().getWindow(),
				Messages.getString("MainMenu.STUDENTS"), Messages.getString("System.ARE_YOU_SURE"))) {
			switchScene((Stage) thePane.getScene().getWindow(), Messages.getString("MainMenu.STUDENTS"),
					"/fxml/students.fxml", 1000, 500);
		}
	}

	@FXML
	public void handleSchedule() {
		if (this instanceof MainController || SystemLibrary.yesNoQuery(thePane.getScene().getWindow(),
				Messages.getString("MainMenu.SCHEDULE"), Messages.getString("System.ARE_YOU_SURE"))) {
			switchScene((Stage) thePane.getScene().getWindow(), Messages.getString("MainMenu.SCHEDULE"),
					"/fxml/schedule.fxml", 1000, 500);
		}
	}

	@FXML
	public void handleUsers() {
		if (this instanceof MainController || SystemLibrary.yesNoQuery(thePane.getScene().getWindow(),
				Messages.getString("MainMenu.USERS"), Messages.getString("System.ARE_YOU_SURE"))) {
			switchScene((Stage) thePane.getScene().getWindow(), Messages.getString("MainMenu.USERS"),
					"/fxml/users.fxml", 1000, 500);
		}
	}

	@FXML
	public void handleDelete() {
		Person person = theTable.getSelectionModel().getSelectedItem();

		if (person != null) {
			if (SystemLibrary.yesNoQuery(thePane.getScene().getWindow(), Messages.getString("System.DELETE"),
					Messages.getString("System.ARE_YOU_SURE"))) {
				try {
					if (person instanceof Student) {
						database = (PersonDAO) DataSource.getInstance().getStudentDAO();
					} else if (person instanceof User) {
						database = (PersonDAO) DataSource.getInstance().getUserDAO();
					}
					if (database.deletePerson(person)) {
						theTable.getItems().remove(person);
						theTable.getSelectionModel().selectFirst();
					}
				} catch (SQLException e) {
					SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
							Messages.getString("System.ERROR"));
				}
			}
		} else {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_SELECT_ROW"), Messages.getString("System.ERROR"));
		}
	}

}
