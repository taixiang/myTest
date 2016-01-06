package example.filedownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Utils {
	private static final String TAG = "Utils";
	private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;
	public static String[] title = {"应用汇","supercsman","simcitydeluxe_na"};
	public static String[] url = {
		"http://d-static.s.dajiahui.cn/test/hb.apk",
		"http://d-static.s.dajiahui.cn/test/hb150813.apk",
		"http://d-static.s.dajiahui.cn/test/hb150814.apk",
	};
	public static String APK_ROOT = "/sdcard/";
	public static int[] progress = {0,0,0};


	public static String getFileNameFromUrl(String url) {
		int index = url.lastIndexOf('?');
		String filename;
		if (index > 1) {
			filename = url.substring(url.lastIndexOf('/') + 1,index);
		} else
		{
			filename = url.substring(url.lastIndexOf('/') + 1);
		}

		if(filename==null || "".equals(filename.trim())){//如果获取不到文件名称
			filename = UUID.randomUUID()+ ".apk";//默认取一个文件名
		}
		return filename;
	}

	public static void installAPK(Context context, final String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String fileName = APK_ROOT + getFileNameFromUrl(url);
		intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
		context.startActivity(intent);
	}

	//SDcard 操作
	public static boolean isSdCardWrittenable() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static long getAvailableStorage() {

	String storageDirectory = null;
	storageDirectory = Environment.getExternalStorageDirectory().toString();

	Log.d(TAG, "getAvailableStorage. storageDirectory : " + storageDirectory);

	try {
		StatFs stat = new StatFs(storageDirectory);
		long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
		Log.d(TAG, "getAvailableStorage. avaliableSize : " + avaliableSize);
		return avaliableSize;
	} catch (RuntimeException ex) {
		Log.e(TAG, "getAvailableStorage - exception. return 0");
		return 0;
	}
	}

	public static boolean checkAvailableStorage() {
		Log.d(TAG,"checkAvailableStorage E");

		if(getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
			return false;
		}

		return true;
	}

	public static boolean isSDCardPresent(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}


	public static Bitmap getLoacalBitmap(String url) {
		try {
			 FileInputStream fis = new FileInputStream(url);
			 return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

		  } catch (FileNotFoundException e) {
			 e.printStackTrace();
			 return null;
		}
	}

	public static boolean isNetworkAvailabel(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED ||
					info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String size(long size) {
		if (size / (1024 * 1024)  > 0) {
		float tmpSize = (float)(size) / (float)(1024 * 1024);
		DecimalFormat df=new DecimalFormat("#.##");
			return "" + df.format(tmpSize) + "MB";
		}
		else if (size / 1024 > 0) {
			return "" + (size / (1024)) + "KB";
		}
		else
			return "" + size + "B";
	}
}
