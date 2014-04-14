package cn.eben.pcagent;

import android.util.Log;

public class AgentLog {

    public static final int DISABLED = -1;
    
    /**
     * Log level ERROR: used to log error messages.
     */
    public static final int ERROR = 0;
    
    /**
     * Log level INFO: used to log information messages.
     */
    public static final int INFO = 1;
    
    /**
     * Log level DEBUG: used to log debug messages.
     */
    public static final int DEBUG = 2;
    
    /**
     * Log level TRACE: used to trace the program execution.
     */
    public static final int TRACE = 3;
    
    public static int level = DEBUG;
    
	public static final void debug(String tag,String msg) {
		if(level >= DEBUG) {
		    Log.d(tag, msg);
		}
	}
	
	public static final void info(String tag,String msg) {
		if(level >= INFO) {
		    Log.d(tag, msg);
		}
	}
	
	public static final void error(String tag,String msg) {
		if(level >= ERROR) {
		    Log.e(tag, msg);
		}
	}	
}
