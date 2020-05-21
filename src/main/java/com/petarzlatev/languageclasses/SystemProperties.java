package com.petarzlatev.languageclasses;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class SystemProperties {
	private static Properties props;

	private static enum Themes {
		THEME1("dark_blue.css"), THEME2("yellow.css"), THEME3("sky_blue.css");

		private String name;

		Themes(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
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
	}

	public static void getSystemProperties() throws IOException {
		FileReader reader = new FileReader(new File("config" + File.separator + "System.properties"));
		props = new Properties();

		props.load(reader);

		reader.close();

	}

	private static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static boolean noLogin() {
		try {
			return !getProperty("LOGIN").equals("T");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static boolean createDatabase() {
		try {
			return getProperty("CREATE_DATABASE").equals("T");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static boolean useHibernate() {
		try {
			return getProperty("HIBERNATE").equals("T");
		} catch (NullPointerException e) {
			return false;
		}
	}

	public static String getDefaultUserForNoLogin() {
		try {
			return getProperty("USER");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getDefaultPassForNoLogin() {
		try {
			return getProperty("PASS");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getLogPath() {
		try {
			return getProperty("LOG_PATH");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getDBType() {
		try {
			return getProperty("DB_TYPE");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getDBDriver() {
		try {
			return getProperty("DB_DRIVER");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getDBPath() {
		try {
			return getProperty("DB_PATH");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getDBName() {
		try {
			return getProperty("DB_NAME");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getOracleUserName() {
		try {
			return getProperty("DB_USER");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getOraclePassWord() {
		try {
			return getProperty("DB_PASS");
		} catch (NullPointerException e) {
			return null;
		}
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

	public static String getCalendarStyle() {
		try {
			return "STYLE" + getProperty("STYLE");
		} catch (NullPointerException | IllegalArgumentException e) {
			return "STYLE";
		}
	}

	public static String getDefaultRatePerHour() throws NullPointerException, NumberFormatException {
		return SystemProperties.getProperty("RATE_PER_HOUR");
	}

}
