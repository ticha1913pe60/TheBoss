package com.petarzlatev.languageclasses.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.controller.dialog.DialogControllerActions;
import com.petarzlatev.languageclasses.controller.dialog.StudentDialogController;
import com.petarzlatev.languageclasses.dao.StudentDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

public class StudentsController extends Controller implements Initializable {
	private SystemLibrary.OperType operType;
	private final StudentDAO database = DataSource.getInstance().getStudentDAO();

	@FXML
	private Pane thePane;
	@FXML
	private TableColumn<Student, String> firstName;
	@FXML
	private TableColumn<Student, String> lastName;
	@FXML
	private TableColumn<Student, String> phoneNumber;
	@FXML
	private TableColumn<Student, Double> ratePerHour;
	@FXML
	private TableView<Student> theTable;
	@FXML
	private Menu menuStudents;
	@FXML
	private Menu menuOptions;
	@FXML
	private MenuItem menuItemImport;
	@FXML
	private MenuItem menuItemAdd;
	@FXML
	private MenuItem menuItemEdit;
	@FXML
	private MenuItem menuItemMain;
	@FXML
	private MenuItem menuItemSchedule;
	@FXML
	private MenuItem menuItemUsers;
	@FXML
	private MenuItem menuItemExit;
	@FXML
	private MenuItem menuItemLogOut;
	@FXML
	private MenuItem contextItemDelete;

	/****************************************************
	 * Initializable
	 ****************************************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuStudents.setText(Messages.getString("MenuItem.STUDENTS"));
		menuOptions.setText(Messages.getString("MenuItem.OPTIONS"));
		menuItemImport.setText(Messages.getString("MenuItem.IMPORT"));
		menuItemAdd.setText(Messages.getString("MenuItem.ADD"));
		menuItemEdit.setText(Messages.getString("MenuItem.EDIT"));
		menuItemMain.setText(Messages.getString("MenuItem.MAIN_MENU"));
		menuItemSchedule.setText(Messages.getString("MenuItem.SCHEDULE"));
		menuItemUsers.setText(Messages.getString("MenuItem.USERS"));
		menuItemExit.setText(Messages.getString("MenuItem.EXIT"));
		menuItemLogOut.setText(Messages.getString("MenuItem.LOG_OUT"));
		contextItemDelete.setText(Messages.getString("ContextItem.DELETE"));

		firstName.setText(Messages.getString("StudentColumn.FIRSTNAME"));
		lastName.setText(Messages.getString("StudentColumn.LASTNAME"));
		phoneNumber.setText(Messages.getString("StudentColumn.PHONE"));
		ratePerHour.setText(Messages.getString("StudentColumn.RATE"));

		menuItemLogOut.setDisable(SystemLibrary.noLogin());
		firstName.setCellValueFactory(new PropertyValueFactory<Student, String>("firstName"));
		lastName.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));
		phoneNumber.setCellValueFactory(new PropertyValueFactory<Student, String>("phoneNumber"));
		ratePerHour.setCellValueFactory(new PropertyValueFactory<Student, Double>("ratePerHour"));
		ratePerHour.setCellFactory(tc -> new TableCell<Student, Double>() {

			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				if (empty) {
					setText(null);
				} else {
					setText(String.format("%10.2f " + Messages.getString("System.LEV"), price));
					setAlignment(Pos.TOP_RIGHT);
				}
			}
		});

		Task<ObservableList<Student>> task = new Task<ObservableList<Student>>() {

			@Override
			protected ObservableList<Student> call() throws Exception {
				return FXCollections.observableArrayList(database.loadStudents());
			}
		};

		theTable.itemsProperty().bind(task.valueProperty());
		theTable.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleStudentEdit();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				handleMainMenu();
			} else if (event.getCode() == KeyCode.DELETE) {
				handleDelete();
			}
		});
		theTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				handleStudentEdit();
			}
		});
		new Thread(task).start();
		task.setOnSucceeded(e -> theTable.getSelectionModel().selectFirst());
	}

	/****************************************************
	 * EventHandlers *
	 ****************************************************/
	@FXML
	public void handleStudentNew() {
		operType = SystemLibrary.OperType.NEW;
		try {
			handleStudentChange(null);
		} catch (IOException e) {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
		} catch (SQLException e) {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
		}
	}

	@FXML
	public void handleStudentEdit() {
		operType = SystemLibrary.OperType.EDIT;
		Student student = theTable.getSelectionModel().getSelectedItem();

		if (student != null) {
			try {
				handleStudentChange(student);
			} catch (IOException e) {
				SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
						Messages.getString("System.ERROR"));
			} catch (SQLException e) {
				SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
						Messages.getString("System.ERROR"));
			}
		} else {
			SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_SELECT_ROW"),
					Messages.getString("System.ERROR"));
		}
	}

	private void handleStudentChange(Student student) throws IOException, SQLException {
		boolean bContinue = true;

		// load scene from fxml
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/studentDialogPane.fxml"));
		Dialog<ButtonType> dialog = SystemLibrary.getDialog(thePane.getScene().getWindow(),
				operType == SystemLibrary.OperType.EDIT ? Messages.getString("StudentFile.EDIT")
						: Messages.getString("StudentFile.ADD"),
				Messages.getString("StudentFile.DATA"));
		dialog.getDialogPane().setContent(loader.load());

		// add OK/Cancel buttons
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		DialogControllerActions controller = loader.getController();
		Window dialogWindow = dialog.getDialogPane().getScene().getWindow();

		// add event handlers for OK/Cancel buttons
		final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDefaultButton(false);
		okButton.setOnKeyPressed(SystemLibrary.dialogOkBtnKeyPressedHandler(dialog, controller));
		okButton.addEventFilter(ActionEvent.ACTION, SystemLibrary.dialogDataValidator(controller));
		okButton.setText(Messages.getString("System.OK_BUTTON"));
		final Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		cancelButton.setCancelButton(false);
		cancelButton.setOnKeyPressed(SystemLibrary.dialogCancelBtnKeyPressedHandler(dialog, dialogWindow));
		cancelButton.addEventFilter(ActionEvent.ACTION, SystemLibrary.dialogExitHandler(dialogWindow));
		cancelButton.setText(Messages.getString("System.CANCEL_BUTTON"));

		// set style for scene
		dialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/fxml/css/" + SystemLibrary.getUITheme()).toExternalForm());
		dialog.getDialogPane().getStyleClass().add("myDialog");

		// set controls defaults
		if (operType == SystemLibrary.OperType.EDIT) {
			controller.setData(student);
		} else {
			bContinue = ((StudentDialogController) controller).setRatePerHour();
		}
		if (bContinue) {

			// show dialog
			Optional<ButtonType> result = dialog.showAndWait();

			// process result
			if (result.isPresent() && result.get() == ButtonType.OK) {
				student = (Student) controller.processData(operType, student);
				if (student != null) {
					if (operType == SystemLibrary.OperType.NEW) {
						theTable.getItems().add(student);
					} else {
						theTable.refresh();
					}
					theTable.getSelectionModel().select(student);
				}
			}
		}
	}

	@FXML
	public void handleImportStudents() {
		boolean bRet = true;
		int newID = 0;
		int count = 0;

		File selectedFile = SystemLibrary.getTextFile(thePane.getScene().getWindow(),
				Messages.getString("StudentFile.IMPORT_FILE"));
		if (selectedFile != null) {
			try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(selectedFile)))) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] students = line.replaceAll("\\s", "").split(",");
					if (students.length == 4) {
						try {
							if (SystemLibrary.validateFullName(students[0], students[1])) {
								if (!students[2].isEmpty() && SystemLibrary.validatePhoneNumber(students[2])) {
									students[0] = SystemLibrary.toUpperFirstLetter(students[0]);
									students[1] = SystemLibrary.toUpperFirstLetter(students[1]);
									students[2] = SystemLibrary.formatPhone(students[2]);
									newID = database.addStudent(students[0], students[1], students[2],
											Double.parseDouble(students[3]));
									if (newID > 0) {
										theTable.getItems().add(new Student(students[0], students[1], students[2],
												Double.parseDouble(students[3]), newID));
										count++;
									}
								} else {
									SystemLibrary.showErrorMsg(
											SystemLibrary.DOUBLE_QUOTE + students[2] + SystemLibrary.DOUBLE_QUOTE
													+ Messages.getString("Error.ERROR_INVALID_PHONE"),
											Messages.getString("System.ERROR"));
									bRet = false;
									break;
								}
							} else {
								SystemLibrary.showErrorMsg(SystemLibrary.DOUBLE_QUOTE + students[0] + " " + students[1]
										+ SystemLibrary.DOUBLE_QUOTE + Messages.getString("Error.ERROR_INVALID_NAME"),
										Messages.getString("System.ERROR"));
								bRet = false;
								break;
							}
						} catch (NumberFormatException e) {
							SystemLibrary.showErrorMsg(
									Messages.getString("Error.ERROR_INVALID_NUMBER") + " " + e.getMessage(),
									Messages.getString("System.ERROR"));
							bRet = false;
							break;
						} catch (SQLException e) {
							SystemLibrary.showErrorMsg(
									Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
									Messages.getString("System.ERROR"));
							bRet = false;
							break;
						}

					} else {
						SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_INVALID_DATA_FORMAT") + line,
								Messages.getString("System.ERROR"));
						bRet = false;
						break;
					}
				}
				if (bRet) {
					SystemLibrary.showMsgBox(
							count + (count == 1 ? Messages.getString("MSG_IMPORT_SINGLE_ROW")
									: Messages.getString("MSG_IMPORT_MULTIPLE_ROWS")),
							Messages.getString("System.MSG"));
				}
			} catch (FileNotFoundException e) {
				SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_FILE_NOT_FOUND") + " " + e.getMessage(),
						Messages.getString("System.ERROR"));
			}
		}
	}
}
