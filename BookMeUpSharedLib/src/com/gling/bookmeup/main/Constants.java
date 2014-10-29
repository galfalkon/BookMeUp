package com.gling.bookmeup.main;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yy, hh:mm a", Locale.US);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy", Locale.US);
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.US);
}
