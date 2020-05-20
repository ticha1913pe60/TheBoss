package com.petarzlatev.languageclasses.controller.dialog;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.PasswordUtils;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.SystemLibrary.OperType;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserDialogController implements Initializable, DialogControllerActions {
	private final UserDAO database = DataSource.getInstance().getUserDAO();
	@FXML
	private Label labelFirstName;
	@FXML
	private TextField firstName;
	@FXML
	private Label labelLastName;
	@FXML
	private TextField lastName;
	@FXML
	private Label labelPassword;
	@FXML
	private PasswordField passWord;
	@FXML
	private Label labelRePassword;
	@FXML
	private PasswordField rePassWord;
	@FXML
	private Label labelUserName;
	@FXML
	private TextField userName;
	@FXML
	private CheckBox isAdmin;
	@FXML
	private DialogPane dialogPane;

	/****************************************************
	 * Initializable
	 ****************************************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelFirstName.setText(Messages.getString("UserFile.FIRSTNAME"));
		labelLastName.setText(Messages.getString("UserFile.LASTNAME"));
		labelPassword.setText(Messages.getString("UserFile.PASSWORD"));
		labelRePassword.setText(Messages.getString("UserFile.RE_PASSWORD"));
		labelUserName.setText(Messages.getString("UserFile.USERNAME"));
		isAdmin.setText(Messages.getString("UserFile.IS_ADMIN"));

		firstName.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		lastName.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		passWord.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		rePassWord.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		userName.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		isAdmin.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		dialogPane.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		firstName.selectAll();
	}

	/****************************************************
	 * Validateable
	 ****************************************************/
	@Override
	public boolean validateData() {
		boolean bRet = !firstName.getText().trim().isEmpty() && !lastName.getText().trim().isEmpty()
				&& !userName.getText().trim().isEmpty() && !passWord.getText().trim().isEmpty()
				&& !rePassWord.getText().trim().isEmpty();

		if (bRet) {
			String firstname = SystemLibrary.toUpperFirstLetter(firstName.getText().trim());
			String lastname = SystemLibrary.toUpperFirstLetter(lastName.getText().trim());
			String password = passWord.getText().trim();
			String rePassword = rePassWord.getText().trim();
			if (SystemLibrary.validateFullName(firstname, lastname)) {
				if (password.equals(rePassword)) {
					bRet = true;
				} else {
					SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_PASSWORD_MATCH"),
							Messages.getString("System.ERROR"));
					bRet = false;
				}
			} else {
				SystemLibrary
						.showErrorMsg(
								SystemLibrary.DOUBLE_QUOTE + firstname + " " + lastname + SystemLibrary.DOUBLE_QUOTE
										+ Messages.getString("Error.ERROR_INVALID_NAME"),
								Messages.getString("System.ERROR"));
				bRet = false;
			}
		} else {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_EMPTY_FIELD"),
					Messages.getString("System.ERROR"));
			bRet = false;
		}

		return bRet;
	}

	@Override
	public boolean setData(Person person) {
		User user = (User) person;
		userName.setText(user.getUsername());
		firstName.setText(user.getFirstName());
		lastName.setText(user.getLastName());
		passWord.setText(user.getPassword());
		rePassWord.setText(user.getPassword());
		isAdmin.setSelected(user.isAdmin());

		return true;
	}

	@Override
	public Person processData(OperType operType, Person user) throws SQLException {
		int userID;
		User newUser = (User) user;
		String firstname = SystemLibrary.toUpperFirstLetter(firstName.getText().trim());
		String lastname = SystemLibrary.toUpperFirstLetter(lastName.getText().trim());
		String username = userName.getText().trim();
		String password = passWord.getText().trim();
		String isadmin = (isAdmin.isSelected()) ? "T" : "F";
		if (operType == SystemLibrary.OperType.NEW) {
			String salt = PasswordUtils.getSalt(30);
			String securePassword = PasswordUtils.generateSecurePassword(password, salt);
			userID = database.addUser(firstname, lastname, username, securePassword, isadmin, salt);
			if (userID > 0) {
				newUser = new User(firstname, lastname, username, securePassword, isadmin, userID, salt);
			} else {
				SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_USER_EXISTS") + username,
						Messages.getString("System.ERROR"));
			}
		} else if (operType == SystemLibrary.OperType.EDIT) {
			if (!password.equalsIgnoreCase(newUser.getPassword())) {
				password = PasswordUtils.generateSecurePassword(password, newUser.getSalt());
			}
			if (database.updateUser(firstname, lastname, username, password, isadmin, user.getPersonID())) {
				newUser.update(firstname, lastname, username, password, isadmin);
			}
		}

		return newUser;
	}
}
