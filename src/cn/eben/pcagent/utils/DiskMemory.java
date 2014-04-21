package cn.eben.pcagent.utils;


import cn.eben.pcagent.AgentLog;
import android.os.StatFs;

public class DiskMemory {
	public static final String TAG = "DiskMemory";
	public static long totalMemory(String path) {
		StatFs statFs = new StatFs(path);
		long Total = ((long)statFs.getBlockCount() * (long)statFs.getBlockSize()) ;
		AgentLog.debug(TAG, "TotalMemory path : " + path + " total +" + Total);
		return Total;
	}

	public static long freeMemory(String path) {
		StatFs statFs = new StatFs(path);
		long Free = ((long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize()) ;
		AgentLog.debug(TAG, "FreeMemory path : " + path + " Free +" + Free);
		return Free;
	}

	public static long busyMemory(String path) {
		StatFs statFs = new StatFs(path);
		long Total = ((long)statFs.getBlockCount() * (long)statFs.getBlockSize());
		long Free = ((long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize());
		long Busy = Total - Free;
		AgentLog.debug(TAG, "BusyMemory path : " + path + " Busy +" + Busy);

		return Busy;
	}
}
