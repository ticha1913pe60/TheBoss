package com.petarzlatev.languageclasses;

public class SystemValidation {

	public static boolean validatePhoneNumber(String phoneNumber) {
		return phoneNumber.matches("^((.)*\\s)?((\\+|00)359|0)8[7-9][2-9][ -]?\\d{3}[ -]?\\d{3}")
				|| phoneNumber.matches("^((.)*\\s)?((\\+|00)359|0)8[7-9][2-9][ -]?\\d{2}[ -]?\\d{2}[ -]\\d{2}");
	}

	public static boolean validateName(String name) {
		return name.matches("[а-яА-ЯA-Za-z]*");
	}

	public static boolean validateFullName(String firstName, String lastName) {
		return !firstName.isEmpty() && !lastName.isEmpty() && validateName(firstName) && validateName(lastName);
	}

}
