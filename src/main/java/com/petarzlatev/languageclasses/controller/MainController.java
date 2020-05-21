package com.petarzlatev.languageclasses.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemProperties;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class MainController extends Controller implements Initializable {
	@FXML
	private Pane thePane;
	@FXML
	private Button studentsBtn;
	@FXML
	private Button usersBtn;
	@FXML
	private Button scheduleBtn;
	@FXML
	private Button logoutBtn;

	/****************************************************
	 * Initializable
	 ****************************************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		studentsBtn.setText(Messages.getString("MainMenu.STUDENTS"));
		usersBtn.setText(Messages.getString("MainMenu.USERS"));
		scheduleBtn.setText(Messages.getString("MainMenu.SCHEDULE"));
		logoutBtn.setText(Messages.getString("MainMenu.LOGOUT"));
		logoutBtn.setDisable(SystemProperties.noLogin());
		studentsBtn.setMaxWidth(Double.MAX_VALUE);
		studentsBtn.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleStudents();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				if (!SystemProperties.noLogin()) {
					handleLogout();
				}
			}
		});
		usersBtn.setMaxWidth(Double.MAX_VALUE);
		usersBtn.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleUsers();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				if (!SystemProperties.noLogin()) {
					handleLogout();
				}
			}
		});
		scheduleBtn.setMaxWidth(Double.MAX_VALUE);
		scheduleBtn.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleSchedule();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				if (!SystemProperties.noLogin()) {
					handleLogout();
				}
			}
		});
		logoutBtn.setMaxWidth(Double.MAX_VALUE);
		logoutBtn.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleLogout();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				if (!SystemProperties.noLogin()) {
					handleLogout();
				}
			}
		});
	}
}
