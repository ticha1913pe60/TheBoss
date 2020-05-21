package com.petarzlatev.languageclasses.controller.dialog;

import java.math.RoundingMode;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemFormatter;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.SystemLibrary.OperType;
import com.petarzlatev.languageclasses.SystemMessages;
import com.petarzlatev.languageclasses.SystemProperties;
import com.petarzlatev.languageclasses.SystemValidation;
import com.petarzlatev.languageclasses.dao.StudentDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.Student;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StudentDialogController implements Initializable, DialogControllerActions {
	private final StudentDAO database = DataSource.getInstance().getStudentDAO();

	@FXML
	private TextField firstName;
	@FXML
	private Label labelFirstName;
	@FXML
	private TextField lastName;
	@FXML
	private Label labelLastName;
	@FXML
	private TextField phoneNumber;
	@FXML
	private Label labelPhoneNumber;
	@FXML
	private TextField ratePerHour;
	@FXML
	private Label labelRatePerHour;
	@FXML
	private DialogPane dialogPane;
	@FXML
	private Label labelCurrency;

	private String getRatePerHourToSet(double rateToSet) {
		return SystemFormatter.formatAmount(2, 2, RoundingMode.DOWN).format(rateToSet);
	}

	/****************************************************
	 * Initializable
	 ****************************************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		labelFirstName.setText(Messages.getString("StudentFile.FIRSTNAME"));
		labelLastName.setText(Messages.getString("StudentFile.LASTNAME"));
		labelPhoneNumber.setText(Messages.getString("StudentFile.PHONE"));
		labelRatePerHour.setText(Messages.getString("StudentFile.RATE"));
		labelCurrency.setText(Messages.getString("StudentFile.CURRENCY"));
		firstName.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		lastName.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		phoneNumber.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		ratePerHour.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		dialogPane.setOnKeyPressed(SystemLibrary.dialogKeyPressedHandler());
		ratePerHour.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!SystemValidation.validateRatePerHour(newValue)) {
					ratePerHour.setText(oldValue);
				}
			}
		});
	}

	/****************************************************
	 * DialogControllerActions
	 ****************************************************/
	@Override
	public boolean setData(Person person) {
		Student student = (Student) person;
		firstName.setText(student.getFirstName());
		lastName.setText(student.getLastName());
		phoneNumber.setText(student.getPhoneNumber());
		ratePerHour.setText(getRatePerHourToSet(student.getRatePerHour()));

		return true;
	}

	@Override
	public Person processData(OperType operType, Person person) throws SQLException {
		int studentID;
		Student newStudent = (Student) person;
		String firstname = SystemFormatter.formatNameFirstLetter(firstName.getText().trim());
		String lastname = SystemFormatter.formatNameFirstLetter(lastName.getText().trim());
		String phone = SystemFormatter.formatPhone(phoneNumber.getText().trim());
		try {
			double rate = Double.parseDouble(ratePerHour.getText().replace(",", "."));

			if (operType == SystemLibrary.OperType.NEW) {
				studentID = database.addStudent(firstname, lastname, phone, rate);
				if (studentID > 0) {
					newStudent = new Student(firstname, lastname, phone, rate, studentID);
				} else {
					SystemMessages
							.showErrorMsg(
									Messages.getString("Error.ERROR_STUDENT_EXISTS") + SystemLibrary.DOUBLE_QUOTE
											+ firstname + " " + lastname + SystemLibrary.DOUBLE_QUOTE,
									Messages.getString("System.ERROR"));
				}
			} else if (operType == SystemLibrary.OperType.EDIT) {
				if (database.updateStudent(firstname, lastname, phone, rate, newStudent.getPersonID())) {
					newStudent.update(firstname, lastname, phone, rate);
				}
			}
		} catch (NumberFormatException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_INVALID_NUMBER") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
		}

		return newStudent;
	}

	@Override
	public boolean validateData() {
		boolean bRet = !firstName.getText().trim().isEmpty() && !lastName.getText().trim().isEmpty()
				&& !phoneNumber.getText().trim().isEmpty();

		if (bRet) {
			String firstname = SystemFormatter.formatNameFirstLetter(firstName.getText().trim());
			String lastname = SystemFormatter.formatNameFirstLetter(lastName.getText().trim());
			String phone = phoneNumber.getText().trim();
			if (SystemValidation.validateFullName(firstname, lastname)) {
				if (!phone.isEmpty() && SystemValidation.validatePhoneNumber(phone)) {
					try {
						bRet = Double.parseDouble(ratePerHour.getText().replace(",", ".")) > 0;
						if (!bRet) {
							SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_EMPTY_FIELD"),
									Messages.getString("System.ERROR"));
						}
					} catch (NumberFormatException e) {
						SystemMessages.showErrorMsg(
								Messages.getString("Error.ERROR_INVALID_NUMBER") + " " + e.getMessage(),
								Messages.getString("System.ERROR"));
						bRet = false;
					}
				} else {
					SystemMessages.showErrorMsg(
							SystemLibrary.DOUBLE_QUOTE + phone + SystemLibrary.DOUBLE_QUOTE
									+ Messages.getString("Error.ERROR_INVALID_PHONE"),
							Messages.getString("System.ERROR"));
					bRet = false;
				}
			} else {
				SystemMessages
						.showErrorMsg(
								SystemLibrary.DOUBLE_QUOTE + firstname + " " + lastname + SystemLibrary.DOUBLE_QUOTE
										+ Messages.getString("Error.ERROR_INVALID_NAME"),
								Messages.getString("System.ERROR"));
				bRet = false;
			}
		} else {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_EMPTY_FIELD"),
					Messages.getString("System.ERROR"));
			bRet = false;
		}

		return bRet;
	}

	public boolean setRatePerHour() {
		boolean bRet = true;

		try {
			ratePerHour.setText(getRatePerHourToSet(Double.parseDouble(SystemProperties.getDefaultRatePerHour())));
		} catch (NullPointerException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "RATE_PER_HOUR",
					Messages.getString("System.ERROR"));
			bRet = false;
		} catch (NumberFormatException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_CONFIG_PARAM") + "RATE_PER_HOUR",
					Messages.getString("System.ERROR"));
			bRet = false;
		}

		return bRet;
	}

}
