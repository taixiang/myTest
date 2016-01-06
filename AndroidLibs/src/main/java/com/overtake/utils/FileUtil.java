package com.overtake.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.os.Environment;

public final class FileUtil {

	public static String getDocumentDirectory(String folder) {

		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {

			File sdFile = Environment.getExternalStorageDirectory();
			return sdFile.getAbsolutePath() + folder;
		}

		return "";
	}

	public static boolean fileExist(String filePath)
	{
		File file = new File(filePath);
		if(file.exists())
			return true;
		return false;
	}

	public static void createNewFile(String filePath) {
		File file = new File(filePath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean removeFile(String filePath) {

		File file = new File(filePath);

		if (file.isFile())
			return file.delete();

		return false;
	}

	public static boolean removeFolder(String folder) {
		File file = new File(folder);

		if (!file.isDirectory())
			return false;

		boolean success = true;
		File[] files = file.listFiles();
		for (File f : files) {

			if (f.isDirectory()) {

				removeFolder(f.getPath());

			} else {

				success = f.delete();
			}
		}

		if (success)
			file.delete();

		return success;
	}

	public static boolean createFolder(String folder) {

		File file = new File(folder);

		if (file.isDirectory()) {

			return file.mkdir();
		}

		return false;
	}

	public static boolean writeStringToFile(String content, String filePath) {

		File file = new File(filePath);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		FileWriter writer = null;


		try {

			writer = new FileWriter(file);
			writer.write(content);

		} catch (IOException e) {

			OTLog.i("FileUtil", e.getStackTrace());

		} finally {

			try {

				if (writer != null) {

					writer.close();
					return true;
				}

			} catch (IOException e) {
				OTLog.i("FileUtil", e.getStackTrace());
			}
		}

		return false;
	}

	public static String readStringFromFile(String filePath) {

		InputStreamReader streamReader = null;
		try {
			streamReader = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			OTLog.i("FileUtil", e.getStackTrace());

			return null;
		} catch (FileNotFoundException e) {
			OTLog.i("FileUtil", e.getStackTrace());

			return null;
		}

		String line = null;
		StringBuilder sb = new StringBuilder();
		try {

			while ((line = new BufferedReader(streamReader).readLine()) != null) {
				sb.append(line).append("\r\n");
			}

		} catch (IOException e) {

		} finally {

			try {
				streamReader.close();
			} catch (IOException e) {
				OTLog.i("FileUtil", e.getStackTrace());

			}
		}

		return sb.toString();
	}

	public static String getFileNameWithoutExtension(File file) {

		String fileName = file.getName();
		if (fileName == null || fileName.length() == 0)
			return "";
		int index = fileName.lastIndexOf(".");
		if (index == -1)
			return "";
		return fileName.substring(0, index);
	}

	/**
	 * if necessary, create directory of parameter dir
	 *
	 * @param dirFile
	 *            File
	 * @return boolean
	 */
	public static boolean makeDirExist(File dirFile) {
		if (dirFile.exists()) {
			return true;
		}

		if (dirFile.mkdirs()) {
			return true;
		}

		return false;
	}

	public static boolean makeDirExist(String dir) {
		return makeDirExist(new File(dir));
	}

	public static void deleteFileWithoutCheckReturnValue(String file) {
		deleteDirectory(new File(file));
	}

	public static void deleteFileWithoutCheckReturnValue(File file) {
		deleteDirectory(file);
	}

	/** 递归的删除目录 */
	public static boolean deleteDirectory(File file) {
		if (file == null) {
			return false;
		}

		if (file.isFile()) {
			return file.delete();
		}

		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				deleteDirectory(files[i]);
			}
		}
		return file.delete();
	}

	/**
	 * func: remove the file, and create the parent directly if not have been
	 * created.
	 *
	 * @param file
	 * @return
	 */
	public static boolean ensureEmptyFile(File file) {
		File parentFile = file.getParentFile();
		if (parentFile != null && parentFile.exists()) {
			if (file.exists()) {
				return file.delete();
			}
		} else if (parentFile != null && !parentFile.mkdirs()) {
			return false;
		}
		return true;
	}
}
