package com.petarzlatev.languageclasses;

import java.io.File;

import com.petarzlatev.languageclasses.controller.dialog.DialogControllerActions;
import com.petarzlatev.languageclasses.model.User;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class SystemLibrary {

	private static User currentUser;
	private static final int CLIENT_CODE_STACK_INDEX;
	static {
		int i = 0;
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			i++;
			if (ste.getClassName().equals(SystemLibrary.class.getName())) {
				break;
			}
		}
		CLIENT_CODE_STACK_INDEX = i;
	}

	public static String methodName() {
		return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName() + "()";
	}

	public static String methodName(int offSet) {
		return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + offSet].getMethodName() + "()";
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User currentUser) {
		SystemLibrary.currentUser = currentUser;
	}

	/****************************************************
	 * Enums
	 ****************************************************/

	public static enum Language {
		SPANISH(Messages.getString("LanguageType.SPANISH")), ENGLISH(Messages.getString("LanguageType.ENGLISH"));

		private String name;

		Language(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};

	public static enum OperType {
		NEW, EDIT
	}

	/****************************************************
	 * Constants
	 ****************************************************/

	public static String HIDDEN_DATA = "*************";
	public static String DOUBLE_QUOTE = "\"";

	/****************************************************
	 * Dialogs *
	 ****************************************************/

	public static File getTextFile(Window owner, String title) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		return fileChooser.showOpenDialog(owner);
	}

	public static Dialog<ButtonType> getDialog(Window owner, String title, String header) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.initOwner(owner);

		return dialog;
	}

	/****************************************************
	 * EventHandlers *
	 ****************************************************/
	public static EventHandler<KeyEvent> dialogKeyPressedHandler() {
		return event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				event.consume();
			}
		};
	}

	public static EventHandler<KeyEvent> dialogOkBtnKeyPressedHandler(Dialog<ButtonType> dialog,
			DialogControllerActions controller) {
		return event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				event.consume();
			} else if (event.getCode() == KeyCode.ENTER) {
				if (!controller.validateData()) {
					event.consume();
				} else {
					dialog.setResult(ButtonType.OK);
					dialog.close();
				}
			}
		};
	}

	public static EventHandler<KeyEvent> dialogCancelBtnKeyPressedHandler(Dialog<ButtonType> dialog,
			Window dialogWindow) {
		return event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				event.consume();
			} else if (event.getCode() == KeyCode.ENTER) {
				if (SystemMessages.showYesNoQuery(dialogWindow, Messages.getString("System.EXIT"),
						Messages.getString("System.ARE_YOU_SURE"))) {
					dialog.setResult(ButtonType.CANCEL);
					dialog.close();
				}
			}
		};
	}

	public static EventHandler<ActionEvent> dialogDataValidator(DialogControllerActions controller) {
		return event -> {
			if (!controller.validateData()) {
				event.consume();
			}
		};
	}

	public static EventHandler<ActionEvent> dialogExitHandler(Window dialogWindow) {
		return event -> {
			if (!SystemMessages.showYesNoQuery(dialogWindow, Messages.getString("System.EXIT"),
					Messages.getString("System.ARE_YOU_SURE"))) {
				event.consume();
			}
		};
	}

}
