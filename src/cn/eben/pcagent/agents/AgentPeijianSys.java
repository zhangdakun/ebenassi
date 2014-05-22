package cn.eben.pcagent.agents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thtf.ldydgz.android.update.service.IUpdateService;
import com.thtf.ldydgz.android.update.service.IUpdateServiceCallback;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;

public class AgentPeijianSys implements AgentBase {

	public static final String TAG = "AgentPeijianSys";
	public static Context mContext = null;
	private boolean isConnected = false;
	private IUpdateService mUpdateService = null;
	private int mUpdateSuccess = -1;

	private static final int READY_UPDATE_STATUS = 0x1001;
	private static final int UPDATE_DATA_STATUS = 0x1002;

	public static void SetContext(Context context)
	{
		mContext = context;
	}
	
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub

		AgentLog.debug(TAG, "processCmd : " + data);

		boolean isBindSuccess = mContext.bindService(new Intent("com.thtf.ldydgz.android.service.UpdateService"), mCurConnection, Context.BIND_AUTO_CREATE);

		JSONObject jo;
		try {
			jo = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PduBase("{\"result\":\"error,not a json data\",\"code\":1}");
		}
		
		JSONObject jResult = new JSONObject();
		String op = null;
		try {
	    	if (!isConnected || mUpdateService == null)
				return new PduBase("{\"result\":\"Peijian service disconnect\",\"code\":100}");
	    		
			op = jo.getString("op");
			if(op.equals("updatepeijian"))
			{
				//佩剑系统更新：UpdatePeijian
				//{ver:1,op:updatepeijian }
				//{result:ok,code:0}
				//或者:{result:reason,code:x}
				if(!mUpdateService.isDirectUpdate())
				{
					mContext.unbindService(mCurConnection);
					return new PduBase("{\"result\":\"Peijian in using\",\"code\":101}");
				}
				String sStatus = mUpdateService.updateAppsData();
			    //{
			    //"update_status_code" : 1,
			    //"update_status_msg" : "Ok"
			    //"update_status_code" : 0,
			    //"update_status_msg" : "Cancel"
			    //}
				JSONObject jStatus = new JSONObject(sStatus);
				int nStatus = jStatus.getInt("update_status_code");
				if(nStatus != 1)
				{
					mContext.unbindService(mCurConnection);
					String sReason = jStatus.getString("update_status_msg");
					return new PduBase("{\"result\":\"" + sReason + "\",\"code\":101}");
				}
			}
			else if(op.equals("getpeijianapps"))
			{
				//佩剑系统应用清单：GetPeijianApps
				//{ver:1,op:getpeijianapps}
				//{result:ok,code:0, apps:[com.android.peijian.datamgr,…]}
				//或者:{result:reason,code:x}
				String sAppList = mUpdateService.getAppsInfo();
				if(sAppList != null)
				{
					JSONArray jAppList = new JSONArray(sAppList);
					JSONArray ja = new JSONArray();
				     //{
				     //"app_name" : "datamanager",
				     //"app_package_name" : "com.thtf.ldydgz.android.datamanager",
				     //"app_version" : "v2.3.4",
				     //"app_version_code" : 17,
				     //"data_version" : "v.2.3.4",
				     //"data_version_code" : 17
				     //}

					for (int i = 0; i < jAppList.length(); i++) {
						JSONObject jApp = jAppList.getJSONObject(i);
						String sAppPkg = jApp.getString("app_package_name");
						if(sAppPkg != null)
							ja.put(sAppPkg);
					}
					jResult.put("apps", ja);
				}
				else
				{
					mContext.unbindService(mCurConnection);
					return new PduBase("{\"result\":\"Peijian get app list failed\",\"code\":102}");
				}
			}
			else
			{
				mContext.unbindService(mCurConnection);
				return new PduBase("{\"result\":\"error,not supported op\",\"code\":2}");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mContext.unbindService(mCurConnection);
			return new PduBase("{\"result\":\"error,not found key\",\"code\":3}");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mContext.unbindService(mCurConnection);
			return new PduBase("{\"result\":\"Peijian call failed\",\"code\":103}");
		}

		try {
			jResult.put("result", "ok");
			jResult.put("code", 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mContext.unbindService(mCurConnection);
	    
		return new PduBase(jResult.toString());
	}

	/*private Handler mUpdateServiceCallbackHandler = new Handler() {
		
		@Override
		public void handleMessage(android.os.Message msg) {
			String result = "";
			switch (msg.what) {
			case READY_UPDATE_STATUS:
				//result = "用户是否希望立即更新? " + (msg.arg1 == 1 ? " yes " : " no ") + "; msg = " + String.valueOf(msg.obj) + ";[end]";
				//Log.d(MainActivity.class.getName(), result);
				//Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
				break;
				
			case UPDATE_DATA_STATUS:
				//result = "用户更新数据状态: " + (msg.arg1 == 1 ? " 成功 " : " 失败 ") + "; msg = " + String.valueOf(msg.obj) + ";[end]";
				//Log.d(MainActivity.class.getName(), result);
				//Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
				mUpdateSuccess = msg.arg1;
				break;

			default:
				break;
			}
		}
	};*/
	
	private IUpdateServiceCallback mUpdateServiceCallback = new IUpdateServiceCallback.Stub() {
		
		@Override
		public void updateDataStatus(int statusCode, String msg) throws RemoteException {
			//mUpdateServiceCallbackHandler.sendMessage(mUpdateServiceCallbackHandler.obtainMessage(UPDATE_DATA_STATUS, statusCode, -1, msg));
		}
		
		@Override
		public void readyUpdateStatus(int statusCode, String msg) throws RemoteException {
			//mUpdateServiceCallbackHandler.sendMessage(mUpdateServiceCallbackHandler.obtainMessage(READY_UPDATE_STATUS, statusCode, -1, msg));
		}
	};
	
	private ServiceConnection mCurConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (mUpdateServiceCallback != null) {
				try {
		            mUpdateService.unregisterCallback(mUpdateServiceCallback);
	            } catch (RemoteException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
	            }
			}
			
			mUpdateService = null;
			isConnected = false;
			//Toast.makeText(getBaseContext(), "onServiceDisconnected", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mUpdateService = IUpdateService.Stub.asInterface(service);
			if (mUpdateServiceCallback != null) {
				try {
		            mUpdateService.registerCallback(mUpdateServiceCallback);
	            } catch (RemoteException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
	            }
			}
			
			isConnected = true;
			//Toast.makeText(getBaseContext(), "onServiceConnected", Toast.LENGTH_SHORT).show();
		}
	};
}
