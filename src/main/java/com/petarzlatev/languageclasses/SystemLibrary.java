package com.petarzlatev.languageclasses;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.petarzlatev.languageclasses.controller.dialog.DialogControllerActions;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;
import com.petarzlatev.languageclasses.model.Student;
import com.petarzlatev.languageclasses.model.User;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class SystemLibrary {

	private static User currentUser;
	private static Properties props;
	private static String logFileName;
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

	/****************************************************
	 * Getter/Setter
	 ****************************************************/
	public static String getLogFileName() {
		return logFileName;
	}

	public static String methodName() {
		return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName() + "()";
	}

	public static String methodName(int offSet) {
		return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + offSet].getMethodName() + "()";
	}

	private static void setLogFileName(String fileName) {
		logFileName = fileName;
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User currentUser) {
		SystemLibrary.currentUser = currentUser;
	}

	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static void getSystemProperties() throws IOException {
		FileReader reader = new FileReader(new File("config" + File.separator + "System.properties"));
		props = new Properties();

		props.load(reader);

		reader.close();

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

	private static enum Themes {
		THEME1("dark_blue.css"), THEME2("yellow.css"), THEME3("sky_blue.css");

		private String name;

		Themes(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};

	public static enum OperType {
		NEW, EDIT
	}

	private static enum MessageLanguage {
		ENGLISH("english"), SPANISH("spanish"), BULGARIAN("bulgarian");

		private String name;

		MessageLanguage(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	};

	/****************************************************
	 * Constants
	 ****************************************************/

	public static String HIDDEN_DATA = "*************";
	public static String DOUBLE_QUOTE = "\"";

	/****************************************************
	 * Formatters
	 ****************************************************/
	public static NumberFormat formatAmount(int min, int max, RoundingMode mode) {
		NumberFormat df = DecimalFormat.getInstance();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);

		return df;
	}

	public static String formatPhone(String phone) {
		phone = phone.replaceAll("\\s", "").replaceAll("-", "");
		switch (phone.length()) {
		case 10:
			phone = "+359" + phone.substring(1);
			break;

		case 13:
			break;

		default:
			phone = "+" + phone.substring(2);

		}
		return phone;
	}

	public static String toUpperFirstLetter(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}

	/****************************************************
	 * Validation
	 ****************************************************/
	public static boolean validatePhoneNumber(String phoneNumber) {
		return phoneNumber.matches("^((.)*\\s)?((\\+|00)359|0)8[7-9][2-9][ -]?\\d{3}[ -]?\\d{3}")
				|| phoneNumber.matches("^((.)*\\s)?((\\+|00)359|0)8[7-9][2-9][ -]?\\d{2}[ -]?\\d{2}[ -]\\d{2}");
	}

	public static boolean validateName(String name) {
		return name.matches("[а-яА-ЯA-Za-z]*");
	}

	public static boolean validateFullName(String firstName, String lastName) {
		return !firstName.isEmpty() && !lastName.isEmpty() && SystemLibrary.validateName(firstName)
				&& SystemLibrary.validateName(lastName);
	}

	public static String getUITheme() {
		String theme;

		try {
			switch (getProperty("THEME")) {
			case "1":
				theme = Themes.THEME1.getName();
				break;
			case "2":
				theme = Themes.THEME2.getName();
				break;
			case "3":
				theme = Themes.THEME3.getName();
				break;
			default:
				theme = Themes.THEME1.getName();
				break;
			}
		} catch (NullPointerException e) {
			theme = Themes.THEME1.getName();
		}
		return theme;
	}

	public static Locale getDefaultLocale() {
		try {
			switch (getProperty("MSG_LANGUAGE")) {
			case "1":
				return new Locale("en", "EN");
			case "2":
				return new Locale("es", "ES");
			case "3":
				return new Locale("bg", "BG");
			default:
				return new Locale("en", "EN");
			}
		} catch (NullPointerException e) {
			return new Locale("en", "EN");
		}
	}

	public static String getMessageLanguage() {
		String msgLang;

		try {
			switch (getProperty("MSG_LANGUAGE")) {
			case "1":
				msgLang = MessageLanguage.ENGLISH.getName();
				break;
			case "2":
				msgLang = MessageLanguage.SPANISH.getName();
				break;
			case "3":
				msgLang = MessageLanguage.BULGARIAN.getName();
				break;
			default:
				msgLang = MessageLanguage.ENGLISH.getName();
				break;
			}
		} catch (NullPointerException e) {
			msgLang = MessageLanguage.ENGLISH.getName();
		}

		return msgLang;
	}

	/****************************************************
	 * MessageDialogs *
	 ****************************************************/
	public static boolean yesNoQuery(Window owner, String title, String header) {
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
				.add(SystemLibrary.class.getResource("/fxml/css/" + SystemLibrary.getUITheme()).toExternalForm());
		dialog.getDialogPane().getStyleClass().add("myDialog");
		Optional<ButtonType> result = dialog.showAndWait();

		return result.isPresent() && result.get() == ButtonType.YES;
	}

	private static void showMsgDialog(String header, String title, AlertType type) {
		Alert alert = new Alert(type);
		alert.setHeaderText(header);
		alert.setTitle(title);
		alert.getDialogPane().getStylesheets()
				.add(SystemLibrary.class.getResource("/fxml/css/" + getUITheme()).toExternalForm());
		alert.getDialogPane().getStyleClass().add("myDialog");
		alert.showAndWait();
	}

	public static void showErrorMsg(String header, String title) {
		showMsgDialog(header, title, AlertType.ERROR);
		logEvent(header, Level.SEVERE, SystemLibrary.class.getName() + " " + SystemLibrary.methodName(1));
	}

	public static void showMsgBox(String header, String title) {
		showMsgDialog(header, title, AlertType.INFORMATION);
		logEvent(header, Level.INFO, SystemLibrary.class.getName() + " " + SystemLibrary.methodName());
	}

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
				if (SystemLibrary.yesNoQuery(dialogWindow, Messages.getString("System.EXIT"),
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
			if (!SystemLibrary.yesNoQuery(dialogWindow, Messages.getString("System.EXIT"),
					Messages.getString("System.ARE_YOU_SURE"))) {
				event.consume();
			}
		};
	}

	/****************************************************
	 * Logging *
	 ****************************************************/
	public static boolean createDatabaseIfNotExist() {

		if (createDatabase()) {
			SessionFactory factory = new Configuration().configure(DataSource.getHibernateCreateConfigFile())
					.addAnnotatedClass(Lesson.class).addAnnotatedClass(Student.class).buildSessionFactory();
			Session session = factory.getCurrentSession();

			try {
				session.beginTransaction();
				User defaultUser = new User("Kristina", "Paskulova", "pakri",
						"8VoeSn7gIjWB+/espWp2we4CCkEzzd5yua0QEj3jQhY=", "T", 0, "6fhUbVwn7b5EjmOjrA0UFvMU1nTH9L");
				session.save(defaultUser);
				session.getTransaction().commit();

				return true;
			} finally {
				session.close();
				factory.close();
			}
		}

		return true;
	}

	public static void runLogger() {
		Logger logger = Logger.getLogger("LogFile");
		FileHandler fh;

		try {
			setLogFileName(getProperty("LOG_PATH") + "ErrorLog_"
					+ DateTimeFormatter.ofPattern("MM_dd_yyyy").format(LocalDate.now()) + ".log");
			fh = new FileHandler(getLogFileName(), true);
			fh.setFormatter(new LogFormatter());
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			fh.close();
		} catch (NullPointerException e) {
			System.out.println(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "LOG_PATH");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logEvent(String message, Level level, String sourceClassName) {
		logPrivateEvent(message, level, sourceClassName, "");
	}

	public static void logEvent(String message, Level level, String sourceClassName, String sourceMethodName) {
		logPrivateEvent(message, level, sourceClassName, sourceMethodName);
	}

	private static void logPrivateEvent(String message, Level level, String sourceClassName, String sourceMethodName) {
		try {
			Logger logger = Logger.getLogger("LogFile");
			FileHandler fh = new FileHandler(getLogFileName(), true);
			fh.setFormatter(new LogFormatter());
			logger.addHandler(fh);
			LogRecord log = new LogRecord(level, message);
			log.setSourceClassName(sourceClassName);
			log.setSourceMethodName(sourceMethodName);
			logger.log(log);
			fh.close();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean noLogin() {
		try {
			return !SystemLibrary.getProperty("LOGIN").equals("T");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static boolean createDatabase() {
		try {
			return SystemLibrary.getProperty("CREATE_DATABASE").equals("T");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static boolean useHibernate() {
		try {
			return SystemLibrary.getProperty("HIBERNATE").equals("T");
		} catch (NullPointerException e) {
			return false;
		}
	}

}
