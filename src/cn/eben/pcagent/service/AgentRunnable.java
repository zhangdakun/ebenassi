package cn.eben.pcagent.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import cn.eben.pcagent.agents.AgentMgr;




public class AgentRunnable implements Runnable {
    private static final AtomicInteger a = new AtomicInteger(0);
    private final Socket b;
    private DataOutputStream c;
    private DataInputStream d;
//    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
//    private b k;
    private Thread l;
//    private final d m;
    
    public static final int HEAD_FLAG = 0x360360;
    
    public AgentRunnable(Socket socket)
    {
//        e = getClass().getSimpleName();
        f = "";
        g = "";
        h = null;
        i = null;
        j = null;
//        k = null;
        l = null;
        h = (new StringBuilder()).append("").append(a.incrementAndGet()).toString();
        b = socket;
//        m = d1;
        i = b.getInetAddress().getHostAddress();
//        e = (new StringBuilder()).append(e).append("(").append(a()).append(")").toString();
    }
    public boolean g()
    {
        return "127.0.0.1".equalsIgnoreCase(i);
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(null == b) {
			return;
		}
		
//        String s1 = e;
        Object aobj[] = new Object[2];
        aobj[0] = b.getLocalAddress();
        aobj[1] = b.getInetAddress();
//        com.qihoo360.mobilesafe.util.h.b(s1, "Session Started of Socket, Local: %s, INet: %s", aobj);
        byte abyte0[];
        try {
        	
			d = new DataInputStream(new BufferedInputStream(b.getInputStream(), 8096));

	        c = new DataOutputStream(new BufferedOutputStream(b.getOutputStream(), 8096));
	        abyte0 = new byte[4];
	        l = Thread.currentThread();
	        
        	int i1;
        	int k1;
        	String s6;
        	byte abyte2[];
        	PduBase pdubase2;
        	PduBase resPduBase;
//	        k = new b(this, l, 200L, 1000L);
	        while(!b.isClosed()) {
		        if(d.available() <= 0 || d.available() >= 4) {

		        	i1 = d.readInt();   	
		        	if(g()) {
		        		// is local 127 address , do something to pref
		        		if(i1 == HEAD_FLAG) 
		        		{
		        			// this is a heart beat ,not heart beat a flag for own
//		        			break;
				        	k1 = d.readInt();
//				        	if(k1 <= 0 || (long)k1 > 0x19000L) 
				        	{
				                abyte2 = new byte[k1];
				                d.readFully(abyte2);// btye2 is a protocol data ,should be a string
				                
				                pdubase2 = new PduBase((short) 1, abyte2);
				                
				                resPduBase = new AgentMgr().processPdu(pdubase2);
				                
				                sendRespons(resPduBase);
				        	}
		        		}
		        		
//		                s6 = e;
//		                aobj7 = new Object[1];
//		                aobj7[0] = Integer.valueOf(i1);
		                
		        	}
		        	

		        } else {
	//	            String s7 = e;
		            Object aobj8[] = new Object[1];
		            aobj8[0] = Integer.valueOf(d.read(abyte0));
		        }
	        }
        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

	}

	public boolean sendRespons(PduBase pdubase) {
		
		
		try {
			c.writeInt(HEAD_FLAG);
            c.writeInt(pdubase.pdu.length);
            c.write(pdubase.pdu);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
