package cn.eben.pcagent.agents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import cn.eben.agents.service.PduBase;
import cn.eben.pcagent.AgentLog;

import com.thtf.ldydgz.android.datamanager.update.service.IUpdateHelperService;
import com.thtf.ldydgz.android.datamanager.update.service.IUpdateHelperServiceCallback;

public class AgentPeijianSys implements AgentBase {

	public static final String TAG = "AgentPeijianSys";
	public static Context mContext = null;
	private boolean isConnected = false;
	private IUpdateHelperService mUpdateService = null;
	private int mUpdateSuccess = -1;

	private static final int TIMEOUT = 450;
	private int mUpdateStatus = -1;
	private int mReadyStatus = -1;
	private String mResultMsg;
	
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

		Intent intent = new Intent("com.thtf.ldydgz.android.datamanager.service.UpdateHelperService");
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		boolean isBindSuccess = mContext.bindService(intent, mCurConnection, Context.BIND_AUTO_CREATE);

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
			int i = 0;
			while(!isConnected && i < TIMEOUT)
			{
				i = i + 1;
				Thread.sleep(2000);
			}
			
	    	if (i >= TIMEOUT)
				return new PduBase("{\"result\":\"Peijian service disconnect\",\"code\":100}");
	    		
			op = jo.getString("op");
			if(op.equals("updatepeijian"))
			{
				//佩剑系统更新：UpdatePeijian
				//{ver:1,op:updatepeijian }
				//{result:ok,code:0}
				//或者:{result:reason,code:x}
				mUpdateService.updateAppsData();
				i = 0;
				while (mUpdateStatus == -1 && i < TIMEOUT)
				{
					i = i + 1;
					Thread.sleep(2000);
				}
				if(i >= TIMEOUT)
				{
					mContext.unbindService(mCurConnection);
					return new PduBase("{\"result\":\"query update result timeout!\",\"code\":102}");
				}

				if(mUpdateStatus == 0)
				{
					mContext.unbindService(mCurConnection);
					return new PduBase("{\"result\":\"" + mResultMsg + "\",\"code\":102}");
				}
				else if(mUpdateStatus != 1)
				{
					mContext.unbindService(mCurConnection);
					return new PduBase("{\"result\":\"unknown error!\",\"code\":5}");
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

					for (i = 0; i < jAppList.length(); i++) {
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
					return new PduBase("{\"result\":\"Peijian get app list failed\",\"code\":103}");
				}
			}
			else if(op.equals("ispeijianready"))
			{
				//佩剑系统更新准备：IsPeijianReady
				//{“ver”:1,”op”:”ispeijianready” }
				//{“result”:”ok”,”code”:0,”ready”:1/0}
				//或者:{“result”:”reason”,”code”:x}
				mUpdateService.readyToUpdate();
				i = 0;
				while (mReadyStatus == -1 && i < TIMEOUT)
				{
					i = i + 1;
					Thread.sleep(2000);
				}
				if(i >= TIMEOUT)
				{
					mContext.unbindService(mCurConnection);
					return new PduBase("{\"result\":\"query ready to update status timeout!\",\"code\":101}");
				}
				
				mContext.unbindService(mCurConnection);
				if(mReadyStatus == -1)
					return new PduBase("{\"result\":\"Peijian sys not ready to update!\",\"code\":101}");
				else
				{
					jResult.put("result", "ok");
					jResult.put("code", 0);
					jResult.put("ready", mReadyStatus);
					return new PduBase(jResult.toString());
				}	}
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
			return new PduBase("{\"result\":\"Peijian call failed\",\"code\":104}");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private IUpdateHelperServiceCallback mUpdateServiceCallback = new IUpdateHelperServiceCallback.Stub() {
		
		@Override
		public void updateDataStatus(int statusCode, String msg) throws RemoteException {
			//mUpdateServiceCallbackHandler.sendMessage(mUpdateServiceCallbackHandler.obtainMessage(UPDATE_DATA_STATUS, statusCode, -1, msg));
			mUpdateStatus = statusCode;
			mResultMsg = msg;
		}
		
		@Override
		public void readyUpdateStatus(int statusCode, String msg) throws RemoteException {
			//mUpdateServiceCallbackHandler.sendMessage(mUpdateServiceCallbackHandler.obtainMessage(READY_UPDATE_STATUS, statusCode, -1, msg));
			mReadyStatus = statusCode;
			mResultMsg = msg;
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
			mUpdateService = IUpdateHelperService.Stub.asInterface(service);
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
