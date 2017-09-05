package jdz.farmKing.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdz.farmKing.main.Main;

public class ErrorLogger {
	/**
	 * Writes an error log to a file, given an exception and extra data
	 * 
	 * @param e
	 */
	public static void createLog(Exception e, String... extraData) {
		PrintWriter pw = new PrintWriter(new StringWriter());
		e.printStackTrace(pw);
		pw.println();
		pw.println("Extra data:");
		for (String s : extraData)
			pw.println('\t' + s);
		String exceptionAsString = pw.toString();
		createLog(exceptionAsString);
	}

	/**
	 * Writes an error log to a file, given an exception
	 * 
	 * @param e
	 */
	public static void createLog(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		createLog(exceptionAsString);
	}

	/**
	 * Writes an message to an error log file
	 * 
	 * @param e
	 */
	public static void createLog(String s) {
		Main.plugin.getLogger().info("An error occurred. Check the Error log file for details.");
		String logsDir = Main.plugin.getDataFolder() + File.separator + "Logs";
		String fileDir = logsDir + File.separator + "Error  "
				+ new SimpleDateFormat("yyyy-MM-dd  HH-mm-ss").format(new Date()) + ".txt";
		File logs = new File(logsDir);
		if (!logs.exists())
			logs.mkdir();
		File file = new File(fileDir);
		try {
			if (!file.exists())
				file.createNewFile();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			bfw.write("An error occurred in the plugin. If you can't work out the issue "
					+ "from this file, send this file to the plugin developer with a description of the failure.");
			bfw.newLine();
			bfw.newLine();
			bfw.write(s);
			bfw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
