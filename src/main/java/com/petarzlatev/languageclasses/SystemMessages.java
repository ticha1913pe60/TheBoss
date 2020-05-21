package com.petarzlatev.languageclasses;

import java.util.Optional;
import java.util.logging.Level;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class SystemMessages {

	public static boolean showYesNoQuery(Window owner, String title, String header) {
		Dialog<ButtonType> dialog = SystemLibrary.getDialog(owner, title, header);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);

		final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
		yesButton.setDefaultButton(false);
		yesButton.setText(Messages.getString("System.YES"));
		yesButton.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				event.consume();
			} else if (event.getCode() == KeyCode.ENTER) {
				dialog.setResult(ButtonType.YES);
				dialog.close();
			}
		});
		final Button noButton = ((Button) dialog.getDialogPane().lookupButton(ButtonType.NO));
		noButton.setCancelButton(false);
		noButton.setText(Messages.getString("System.NO"));
		noButton.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				event.consume();
			} else if (event.getCode() == KeyCode.ENTER) {
				dialog.setResult(ButtonType.NO);
				dialog.close();
			}
		});

		dialog.getDialogPane().getStylesheets()
				.add(SystemLibrary.class.getResource("/fxml/css/" + SystemProperties.getUITheme()).toExternalForm());
		dialog.getDialogPane().getStyleClass().add("myDialog");
		Optional<ButtonType> result = dialog.showAndWait();

		return result.isPresent() && result.get() == ButtonType.YES;
	}

	private static void showMsgDialog(String header, String title, AlertType type) {
		Alert alert = new Alert(type);
		alert.setHeaderText(header);
		alert.setTitle(title);
		alert.getDialogPane().getStylesheets()
				.add(SystemLibrary.class.getResource("/fxml/css/" + SystemProperties.getUITheme()).toExternalForm());
		alert.getDialogPane().getStyleClass().add("myDialog");
		alert.showAndWait();
	}

	public static void showErrorMsg(String header, String title) {
		showMsgDialog(header, title, AlertType.ERROR);
		SystemLogger.logEvent(header, Level.SEVERE, SystemLibrary.class.getName() + " " + SystemLibrary.methodName(1));
	}

	public static void showMsgBox(String header, String title) {
		showMsgDialog(header, title, AlertType.INFORMATION);
		SystemLogger.logEvent(header, Level.INFO, SystemLibrary.class.getName() + " " + SystemLibrary.methodName());
	}

}
