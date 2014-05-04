package cn.eben.pcagent.agents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;

 
import android.content.pm.PackageStats;
import android.content.pm.IPackageStatsObserver;

public class AgentAppInfo implements AgentBase{

	public static final String TAG = "AgentAppInfo";
	
	private String sdCardRoot;
	List<AppInfo> mlistAppInfo = null;
	class AppInfo {  
	    
	    private String appLabel;    //app label 
	    private Drawable appIcon ;  //cion 
	    private Intent intent ;     //intent  
	    private String pkgName ;    //package name  
	    
	    public String vn;
	    public int vc;
	    
	    public String icon;
	    public String activityName;
	    
	    PkgSizeObserver sizeob;
	    public long size;
	    public AppInfo(){}  
	      
	    public String getAppLabel() {  
	        return appLabel;  
	    }  
	    public void setAppLabel(String appName) {  
	        this.appLabel = appName;  
	    }  
	    public Drawable getAppIcon() {  
	        return appIcon;  
	    }  
	    public void setAppIcon(Drawable appIcon) {  
	        this.appIcon = appIcon;  
	    }  
	    public Intent getIntent() {  
	        return intent;  
	    }  
	    public void setIntent(Intent intent) {  
	        this.intent = intent;  
	    }  
	    public String getPkgName(){  
	        return pkgName ;  
	    }  
	    public void setPkgName(String pkgName){  
	        this.pkgName=pkgName ;  
	    }  
	}
	
	
//	{result:ok,code:0,apps:[{vercode:1,package:cn.eben.sync,name:appname,version:1.2,vercode:14,size:123k,icon:xxxx},…]}
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
//		sdCardRoot = Environment.getExternalStorageDirectory().getPath()+File.separator
//				+"EbenAgent"+File.separator;
		
		
		sdCardRoot=Contants.sdCardRoot;
		AgentLog.debug(TAG, "processCmd : "+data);
		AgentLog.debug(TAG, "sdcard root : "+sdCardRoot);
		JSONArray ja = new JSONArray();
		JSONObject jPackage = new JSONObject();
		
		queryAppInfo();
		
		if(null != mlistAppInfo) {
			for(AppInfo app:mlistAppInfo) {
				jPackage = new JSONObject();
				try {
					jPackage.put("package", app.pkgName);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					jPackage.put("name", app.getAppLabel());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					jPackage.put("version", app.vn);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					jPackage.put("size", app.size);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					jPackage.put("icon", app.icon);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					jPackage.put("vercode", app.vc);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				ja.put(jPackage);
			}
		}
		
		jPackage = new JSONObject();
		try {
			jPackage.put("result", "ok");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jPackage.put("code", 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			jPackage.put("apps", ja);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new PduBase(jPackage.toString());
	}

	public void queryAppInfo() {  
        PackageManager pm = App.getInstance().getApplicationContext().getPackageManager(); 
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);  
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
        mlistAppInfo = new ArrayList<AppInfo>();
        List<ResolveInfo> resolveInfos = pm  
                .queryIntentActivities(mainIntent, 0);  

//        List<ResolveInfo> resolveInfos = pm  
//                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));  
        if (mlistAppInfo != null) {  
            mlistAppInfo.clear();  
            for (ResolveInfo reInfo : resolveInfos) {  
                String activityName = reInfo.activityInfo.name; // Activity name  
                String pkgName = reInfo.activityInfo.packageName; //package name  
//                reInfo.activityInfo.applicationInfo.
                AppInfo appInfo = new AppInfo(); 
                long size = 0;
                try {
					size = queryPacakgeSize(pkgName,reInfo.activityInfo.applicationInfo.uid);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
                
                PackageInfo pinfo = null;
				try {
					pinfo = pm.getPackageInfo(pkgName, 0);

				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String vn = "";
				int vc=0;
				if(null != pinfo) {
                 vn = pinfo.versionName;
                 vc = pinfo.versionCode;
                
                 AgentLog.debug(TAG, "version name : "+vn+", version code : "+vc);
				}
                String appLabel = (String) reInfo.loadLabel(pm); // Label  
                Drawable icon = reInfo.loadIcon(pm); //icon  
                
                saveAsFile((BitmapDrawable) icon, sdCardRoot+pkgName+File.separator, String.valueOf(vc)+".png");
                
                Intent launchIntent = new Intent();  
                launchIntent.setComponent(new ComponentName(pkgName,  
                        activityName));
                // appinfo
                 
                appInfo.setAppLabel(appLabel);  
                appInfo.setPkgName(pkgName);  
                appInfo.setAppIcon(icon);  
                appInfo.setIntent(launchIntent);  
                appInfo.vc=vc;
                appInfo.vn=vn;
                appInfo.size = size;
                appInfo.icon = sdCardRoot+pkgName+File.separator+String.valueOf(vc)+".png";
                appInfo.activityName = activityName;
                mlistAppInfo.add(appInfo); //add to list  
                AgentLog.debug(TAG, appLabel + " activityName---" + activityName  
                        + " pkgName---" + pkgName);  
                
                AgentLog.debug(TAG, "pakage size ,"+size);
            }  
        }  
    }  
	

   class PkgSizeObserver extends IPackageStatsObserver.Stub{  
	   		public long cachesize= 0;
	   		public long datasize = 0;
	   		public long codesize = 0;
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)  
                throws RemoteException {  
            // TODO Auto-generated method stub  
        	synchronized (ob) {
				
			
           cachesize = pStats.cacheSize  ;  
            datasize = pStats.dataSize  ;   
            codesize = pStats.codeSize  ;   
//            totalsize = cachesize + datasize + codesize ;  
            AgentLog.debug(TAG, "cachesize--->"+cachesize+" datasize---->"+datasize+ " codeSize---->"+codesize)  ;
            ob.notify();
        	}
        }  
    }  
    //系统函数，字符串转换 long -String (kb)  
    private String formateFileSize(long size){  
        return Formatter.formatFileSize(App.getInstance().getApplicationContext(), size);   
    } 
    
    private Object ob = new Object();
    public long queryPacakgeSize(String pkgName,int userid) throws Exception{
        if ( pkgName != null){
            
            PackageManager pm = App.getInstance().getApplicationContext().getPackageManager();
            try {
            	Method getPackageSizeInfo = null;
//                try {
//					
//
//                getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
//				} catch (NoSuchMethodException e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					
//				}
            	getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
                PkgSizeObserver observer =  new PkgSizeObserver();
                if(null !=  getPackageSizeInfo) {
                	synchronized (ob) {
                		
                		getPackageSizeInfo.invoke(pm, pkgName,observer);
                		ob.wait(1000);
					}
                	
                }
                
                return observer.cachesize+observer.codesize+observer.datasize;
            } 
            catch(Exception ex){
                AgentLog.error(TAG, "NoSuchMethodException") ;
                ex.printStackTrace() ;
                throw ex ;  // 抛出异常
            } 
        }
        
        return 0;
    }
    
	private void saveAsFile(BitmapDrawable bd, String path, String name) {
		File dir = new File(path);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File target = new File(path + name);
		if (target.exists()) {
			AgentLog.debug(TAG, "icon exist : " + name);
		} else {
			Bitmap bm = bd.getBitmap();

			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(target);

				bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);

				outStream.flush();

				outStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}
}

