package cn.eben.pcagent.agents;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;
import cn.eben.pcagent.utils.DiskMemory;
import cn.eben.support.v1.util.DeviceProperties;

public class AgentSysInfo implements AgentBase{
	public static final String TAG = "AgentSysInfo";
	
//	{ver:1,op:getsysinfo}
//
//	{result:ok,code:0,dev:¡±Eben T8¡±,SN:H7xxxx,fwver:2.2,total:16G,free:7.3G}
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		
		AgentLog.debug(TAG, "processCmd : "+data);

		
		AgentLog.debug(TAG,"board, "+Build.BOARD
				+",nootloader, "+Build.BOOTLOADER
				+",cpu_abi,"+Build.CPU_ABI+
				",device, "+Build.DEVICE
				+", display, "+Build.DISPLAY
				+",fp, "+Build.FINGERPRINT
				+", hw ,"+Build.HARDWARE
				+",id,"+Build.ID
				+",famufacturer,"+Build.MANUFACTURER
				+", model, "+Build.MODEL
				+",product, "+Build.PRODUCT
				+", codename, "+Build.VERSION.CODENAME
				+", release, "+Build.VERSION.RELEASE+
				",incremental, "+Build.VERSION.INCREMENTAL
				+", serial, "+Build.SERIAL
				+",tags, "+Build.TAGS+
				",user,"+Build.USER);
		
		String dev = Build.MODEL;
		
		String id = Build.ID;
		String simpleId = null;
		if(null != id) {
			String[]  part= new String(id).split("\\.");

			
			if(part.length >=2) {
				simpleId = part[0]+"."+part[1];
			}
		}
		
		AgentLog.debug(TAG, "dev , "+dev+", id, "+id+",smpleid ,"+simpleId);
		File exDir = Environment.getExternalStorageDirectory();
		
		File dataFile = Environment.getDataDirectory();
		
		File mountFile = Environment.getExternalStoragePublicDirectory(Environment.MEDIA_MOUNTED);
		
//		File mountFile 
		
		AgentLog.debug(TAG, "exDir, "+exDir.toString()+", dataFile, "+dataFile.toString()
				+"mountFile, "+mountFile.toString());
		
		long total = DiskMemory.totalMemory(exDir.toString());
		long free = DiskMemory.freeMemory(exDir.toString());
		
		AgentLog.debug(TAG, "mem toal : free ,"+total+", "+free);

		String sn = DeviceProperties.getSn();
		AgentLog.debug(TAG, "sn : "+sn);
		
		
		JSONObject jo = new JSONObject();
		if(null != dev) {
			try {
				jo.put("dev", dev);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != sn) {
			try {
				jo.put("SN", sn);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != simpleId) {
			try {
				jo.put("fwver", simpleId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(null != id) {
			try {
				jo.put("fwver", id);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(total>0) {
			try {
				jo.put("total", String.valueOf(total));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(free >=0) {
			try {
				jo.put("free", free);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			jo.put("result", "ok");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jo.put("code", "0");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PduBase pdu = new PduBase(jo.toString());
		
		return pdu;
	}


}
