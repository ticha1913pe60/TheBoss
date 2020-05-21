package com.petarzlatev.languageclasses;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SystemFormatter {

	public static NumberFormat formatAmount(int min, int max, RoundingMode mode) {
		NumberFormat df = DecimalFormat.getInstance();
		df.setMinimumFractionDigits(min);
		df.setMaximumFractionDigits(max);
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

	public static String formatNameFirstLetter(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}

}
