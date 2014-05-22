package cn.eben.pcagent.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.agents.AgentPeijianSys;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class DaemonService extends Service {

	public static final String TAG = "DaemonService";
	
	public static final int PORT = 6001;
	
	private static final Class<?>[] mSetForegroundSignature = new Class[] {
	    boolean.class};
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
	    int.class, Notification.class};
	private static final Class<?>[] mStopForegroundSignature = new Class[] {
	    boolean.class};

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	void invokeMethod(Method method, Object[] args) {
	    try {
	        method.invoke(this, args);
	    } catch (InvocationTargetException e) {
	        // Should not happen.
	    	AgentLog.error("ApiDemos", "Unable to invoke method");
	    } catch (IllegalAccessException e) {
	        // Should not happen.
	    	AgentLog.error("ApiDemos", "Unable to invoke method");
	    }
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
	    // If we have the new startForeground API, then use it.
	    if (mStartForeground != null) {
	        mStartForegroundArgs[0] = Integer.valueOf(id);
	        mStartForegroundArgs[1] = notification;
	        invokeMethod(mStartForeground, mStartForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.
	    mSetForegroundArgs[0] = Boolean.TRUE;
	    invokeMethod(mSetForeground, mSetForegroundArgs);
	    mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
	    // If we have the new stopForeground API, then use it.
	    if (mStopForeground != null) {
	        mStopForegroundArgs[0] = Boolean.TRUE;
	        invokeMethod(mStopForeground, mStopForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.  Note to cancel BEFORE changing the
	    // foreground state, since we could be killed at that point.
	    mNM.cancel(id);
	    mSetForegroundArgs[0] = Boolean.FALSE;
	    invokeMethod(mSetForeground, mSetForegroundArgs);
	}


	public void initMethod() {
	    mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    try {
	        mStartForeground = getClass().getMethod("startForeground",
	                mStartForegroundSignature);
	        mStopForeground = getClass().getMethod("stopForeground",
	                mStopForegroundSignature);
	        return;
	    } catch (NoSuchMethodException e) {
	        // Running on an older platform.
	        mStartForeground = mStopForeground = null;
	    }
	    try {
	        mSetForeground = getClass().getMethod("setForeground",
	                mSetForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        throw new IllegalStateException(
	                "OS doesn't have Service.startForeground OR Service.setForeground!");
	    }
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//        initMethod();
//        startForegroundCompat(0x0700012,new Notification());
        
		if(null == socketThread) {
			socketThread = new SocketThread(this,PORT);
			socketThread.start();
		}
		
		AgentPeijianSys.SetContext(getApplicationContext());
	}

	private SocketThread socketThread;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
//		return super.onStartCommand(intent, flags, startId);
		AgentLog.debug(TAG, "onStartCommand");
//		if(null == socketThread
//				 || null == socketThread.getsocket() 
//				 || socketThread.getsocket().isClosed()) {
//
//			if( null != socketThread && null != socketThread.getsocket()) {
//				if(!socketThread.getsocket().isClosed()) {
//				try {
//					socketThread.getsocket().close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				}
//			}
//			if(null != socketThread && null != socketThread.getServer()) {
//				try {
//					socketThread.getServer().close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			socketThread = new SocketThread(this,6001);
//			socketThread.start();
//			
//		}
		
//		return START_STICKY;
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
        if(socketThread != null)
        {
        	socketThread.shutdown();
        	socketThread = null;
        }
        
		super.onDestroy();
	}

	public class SocketThread extends Thread{
//	    private static final boolean a;
	    private int port;
	    private ThreadPoolExecutor c;
	    private boolean isShut;
	    private ServerSocket e;
//	    private final ag f;
	    private final Context g;
	    private Socket socket = null;
	    
	    SocketThread(Context context, int port)
	    {
//	        b = 3880;
	        isShut = false;
	        this.port = port;
//	        f = ag1;
	        g = context;
	    }
	    public Socket getsocket() {
	    	return socket;
	    }
	    public ServerSocket getServer() {
	    	return e;
	    }
	    
	    public void shutdown()
	    {
	        if(!isShut)
	        {
	            isShut = true;
	            try
	            {
	                if(e != null)
	                    e.close();
	                join(3000L);
	            }
	            catch(Exception exception) {
	            	exception.printStackTrace();
	            }
	        }
	    }
	    
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
	        try {
	        	AgentLog.debug("socketThread", "run thread");
	        	
	        	if(null == e) {
//	        		AgentLog.error(TAG, " release one connect"); 
//	        		e.close();
//	        		e=null;
					e = new ServerSocket();

			        e.bind(new InetSocketAddress("127.0.0.1", port));
			        e.setReuseAddress(true);
			        e.setPerformancePreferences(100, 100, 1);
	        	}

	        	AgentLog.debug(TAG, "isbond, "+e.isBound()+",isclosed,  "+e.isClosed());
	        	
	        c = new ThreadPoolExecutor(10, 100, 60000L, TimeUnit.MICROSECONDS, 
	        		new ArrayBlockingQueue(10), 
	        		new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
	        
	        if(isShut) {
	        	AgentLog.error(TAG, "got shut down");
	        	c.shutdown();
	        	c = null;
	        	return;
	        }
	        
	        

	        socket = new Socket();
	        AgentRunnable agent;
	        socket = e.accept();
	        socket.setPerformancePreferences(10, 100, 1);
	        socket.setKeepAlive(true);
	        socket.setSoLinger(true, 30);
	        socket.setTcpNoDelay(true);
	        
	        agent = new AgentRunnable(socket);
	        c.execute(agent);
	        
//	        a1 = f.a(this, socket);
	        
			} 
//	        catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} 
	        catch (Exception e) {
				// TODO: handle exception
	        	e.printStackTrace();
				try {
					if(null != socket)
					socket.close();
					socket = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				if(c != null) {
				c.shutdown();
				c = null; 
				}
				

				
			}
		}
		
		
	}
}
