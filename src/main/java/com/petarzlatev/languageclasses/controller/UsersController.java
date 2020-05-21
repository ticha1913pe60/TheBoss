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
import com.petarzlatev.languageclasses.PasswordUtils;
import com.petarzlatev.languageclasses.SystemFormatter;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.SystemMessages;
import com.petarzlatev.languageclasses.SystemProperties;
import com.petarzlatev.languageclasses.SystemValidation;
import com.petarzlatev.languageclasses.controller.dialog.DialogControllerActions;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class UsersController extends Controller implements Initializable {
	private SystemLibrary.OperType operType;
	private final UserDAO database = DataSource.getInstance().getUserDAO();
	@FXML
	private Pane thePane;
	@FXML
	private TableColumn<User, String> username;
	@FXML
	private TableColumn<User, String> password;
	@FXML
	private TableColumn<User, String> firstName;
	@FXML
	private TableColumn<User, String> lastName;
	@FXML
	private TableColumn<User, String> isAdmin;
	@FXML
	private TableView<User> theTable;
	@FXML
	private Menu menuUsers;
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
	private MenuItem menuItemStudents;
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
		menuUsers.setText(Messages.getString("MenuItem.USERS"));
		menuOptions.setText(Messages.getString("MenuItem.OPTIONS"));
		menuItemImport.setText(Messages.getString("MenuItem.IMPORT"));
		menuItemAdd.setText(Messages.getString("MenuItem.ADD"));
		menuItemEdit.setText(Messages.getString("MenuItem.EDIT"));
		menuItemMain.setText(Messages.getString("MenuItem.MAIN_MENU"));
		menuItemSchedule.setText(Messages.getString("MenuItem.SCHEDULE"));
		menuItemStudents.setText(Messages.getString("MenuItem.STUDENTS"));
		menuItemExit.setText(Messages.getString("MenuItem.EXIT"));
		menuItemLogOut.setText(Messages.getString("MenuItem.LOG_OUT"));
		contextItemDelete.setText(Messages.getString("ContextItem.DELETE"));

		username.setText(Messages.getString("UserColumn.USERNAME"));
		password.setText(Messages.getString("UserColumn.PASSWORD"));
		firstName.setText(Messages.getString("UserColumn.FIRSTNAME"));
		lastName.setText(Messages.getString("UserColumn.LASTNAME"));
		isAdmin.setText(Messages.getString("UserColumn.IS_ADMIN"));

		menuUsers.setDisable(!SystemLibrary.getCurrentUser().isAdmin());
		menuItemLogOut.setDisable(SystemProperties.noLogin());
		username.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		password.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
		firstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
		lastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
		password.setCellFactory(tc -> new TableCell<User, String>() {

			@Override
			protected void updateItem(String pass, boolean empty) {
				super.updateItem(pass, empty);
				if (empty) {
					setText(null);
				} else {
					if (SystemLibrary.getCurrentUser().isAdmin()) {
						setText(pass);
					} else {
						setText(SystemLibrary.HIDDEN_DATA);
					}
				}
			}
		});
		isAdmin.setCellValueFactory(new PropertyValueFactory<User, String>("isAdmin"));
		isAdmin.setCellFactory(tc -> new TableCell<User, String>() {

			@Override
			protected void updateItem(String admin, boolean empty) {
				super.updateItem(admin, empty);
				if (empty) {
					setText(null);
				} else {
					if (admin.equals("T")) {
						setText(Messages.getString("System.YES"));
					} else {
						setText(Messages.getString("System.NO"));
					}
				}
			}
		});

		Task<ObservableList<User>> task = new Task<ObservableList<User>>() {

			@Override
			protected ObservableList<User> call() throws Exception {
				return FXCollections.observableArrayList(database.loadUsers());
			}

		};
		theTable.itemsProperty().bind(task.valueProperty());
		theTable.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleUserEdit();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				handleMainMenu();
			} else if (event.getCode() == KeyCode.DELETE) {
				if (SystemLibrary.getCurrentUser().isAdmin()) {
					handleDelete();
				} else {
					SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_NOT_ADMIN"),
							Messages.getString("System.ERROR"));
				}
			}
		});
		theTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				handleUserEdit();
			}
		});

		new Thread(task).start();
		task.setOnSucceeded(e -> theTable.getSelectionModel().selectFirst());
	}

	/****************************************************
	 * EventHandlers *
	 ****************************************************/
	@FXML
	public void handleImportUsers() {
		boolean bRet = true;
		int newID = 0;
		int count = 0;

		File selectedFile = SystemLibrary.getTextFile(thePane.getScene().getWindow(),
				Messages.getString("UserFile.IMPORT_FILE"));
		if (selectedFile != null) {
			try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(selectedFile)))) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] users = line.replaceAll("\\s", "").split(",");
					if (users.length == 5) {
						try {
							if (SystemValidation.validateFullName(users[2], users[3])) {
								users[2] = SystemFormatter.formatNameFirstLetter(users[2]);
								users[3] = SystemFormatter.formatNameFirstLetter(users[3]);
								String salt = PasswordUtils.getSalt(30);
								String password = PasswordUtils.generateSecurePassword(users[1], salt);
								newID = database.addUser(users[2], users[3], users[0], password, users[4], salt);
								if (newID > 0) {
									theTable.getItems().add(
											new User(users[2], users[3], users[0], password, users[4], newID, salt));
									count++;
								}
							} else {
								SystemMessages.showErrorMsg(SystemLibrary.DOUBLE_QUOTE + users[2] + " " + users[3]
										+ SystemLibrary.DOUBLE_QUOTE + Messages.getString("Error.ERROR_INVALID_NAME"),
										Messages.getString("System.ERROR"));
								bRet = false;
								break;
							}
						} catch (NumberFormatException e) {
							SystemMessages.showErrorMsg(
									Messages.getString("Error.ERROR_INVALID_NUMBER") + " " + e.getMessage(),
									Messages.getString("System.ERROR"));
							bRet = false;
							break;
						} catch (SQLException e) {
							SystemMessages.showErrorMsg(
									Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
									Messages.getString("System.ERROR"));
							bRet = false;
							break;
						}

					} else {
						SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_INVALID_DATA_FORMAT") + line,
								Messages.getString("System.ERROR"));
						bRet = false;
						break;
					}
				}
				if (bRet) {
					SystemMessages.showMsgBox(
							count + (count == 1 ? Messages.getString("MSG_IMPORT_SINGLE_ROW")
									: Messages.getString("MSG_IMPORT_MULTIPLE_ROWS")),
							Messages.getString("System.MSG"));
				}
			} catch (FileNotFoundException e) {
				SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_FILE_NOT_FOUND") + " " + e.getMessage(),
						Messages.getString("System.ERROR"));
			}
		}
	}

	@FXML
	public void handleUserNew() {
		operType = SystemLibrary.OperType.NEW;
		try {
			handleUserChange(null);
		} catch (IOException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
		} catch (SQLException e) {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
					Messages.getString("System.ERROR"));
		}
	}

	@FXML
	public void handleUserEdit() {
		if (SystemLibrary.getCurrentUser().isAdmin()) {
			operType = SystemLibrary.OperType.EDIT;
			User user = theTable.getSelectionModel().getSelectedItem();
			if (user != null) {
				try {
					handleUserChange(user);
				} catch (IOException e) {
					SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_LOADING_SCENE") + " " + e.getMessage(),
							Messages.getString("System.ERROR"));
				} catch (SQLException e) {
					SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
							Messages.getString("System.ERROR"));
				}

			} else {
				SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_SELECT_ROW"),
						Messages.getString("System.ERROR"));
			}
		} else {
			SystemMessages.showErrorMsg(Messages.getString("Error.ERROR_NOT_ADMIN"), Messages.getString("System.ERROR"));
		}
	}

	private void handleUserChange(User user) throws IOException, SQLException {
		// load scene from fxml
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/userDialogPane.fxml"));
		Dialog<ButtonType> dialog = SystemLibrary.getDialog(thePane.getScene().getWindow(),
				operType == SystemLibrary.OperType.EDIT ? Messages.getString("UserFile.EDIT")
						: Messages.getString("UserFile.ADD"),
				Messages.getString("UserFile.DATA"));
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
				.add(getClass().getResource("/fxml/css/" + SystemProperties.getUITheme()).toExternalForm());
		dialog.getDialogPane().getStyleClass().add("myDialog");

		// set controls defaults
		if (user != null) {
			controller.setData(user);
		}

		// show dialog
		Optional<ButtonType> result = dialog.showAndWait();

		// process result
		if (result.isPresent() && result.get() == ButtonType.OK) {
			user = (User) controller.processData(operType, user);
			if (user != null) {
				if (operType == SystemLibrary.OperType.NEW) {
					theTable.getItems().add(user);
				} else {
					theTable.refresh();
				}
				theTable.getSelectionModel().select(user);
			}
		}
	}

}
