package cn.eben.pcagent.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.agents.AgentMgr;




public class AgentRunnable implements Runnable {
//    private static final AtomicInteger a = new AtomicInteger(0);
    private final Socket localSocket;
    private DataOutputStream output;
    private DataInputStream input;

//    private String h;
    private String address;

    
    public static final int EBEN_HEAD = 0x5757;
    
    public static final String TAG = "AgentRunnable";
    public AgentRunnable(Socket socket)
    {


//        h = null;
//        i = null;

//        h = (new StringBuilder()).append("").append(a.incrementAndGet()).toString();
        localSocket = socket;

        address = localSocket.getInetAddress().getHostAddress();
        
//        AgentLog.debug(TAG, "para : h, i, == "+h+", "+i);

    }
    public boolean isLocal()
    {
        return "127.0.0.1".equalsIgnoreCase(address);
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(null == localSocket) {
			return;
		}
		

        Object aobj[] = new Object[2];
        aobj[0] = localSocket.getLocalAddress();
        aobj[1] = localSocket.getInetAddress();

        byte abyte0[];
        try {
        	AgentLog.debug("agentrunnable", "run agent");
			input = new DataInputStream(new BufferedInputStream(localSocket.getInputStream(), 8096));

	        output = new DataOutputStream(new BufferedOutputStream(localSocket.getOutputStream(), 8096));
	        abyte0 = new byte[4];
//	        l = Thread.currentThread();
	        AgentMgr mgr = new AgentMgr();
        	int i1;
        	int k1;
//        	String s6;
        	byte abyte2[];
        	PduBase pdubase2;
        	PduBase resPduBase;

	        while(!localSocket.isClosed()) {
		        if(/*d.available() <= 0 || d.available() >= 4*/true) 
		        {

		        	i1 = input.readInt();   	
		        	if(isLocal()) {
		        		// is local 127 address , do something to pref
		        		if(i1 == EBEN_HEAD) 
		        		{
		        			// this is a heart beat ,not heart beat a flag for own
//		        			break;
				        	k1 = input.readInt();
//				        	if(k1 <= 0 || (long)k1 > 0x19000L) 
				        	{
				                abyte2 = new byte[k1];
				                input.readFully(abyte2);// btye2 is a protocol data ,should be a string
				                
				                pdubase2 = new PduBase((short) 1, abyte2);
				                
				                resPduBase = mgr.processPdu(pdubase2);
				                
				                sendRespons(resPduBase);
				        	}
				        	
		        		} else {
		        			AgentLog.error(TAG, "error msg head: "+i1);
		        		}

		                
		        	}
		        	

		        } else {
//		            Object aobj8[] = new Object[1];
//		            aobj8[0] = Integer.valueOf(input.read(abyte0));
//		            
//		            AgentLog.error(TAG, "error msg : "+new String(abyte0));
		        }
	        }
        
		}catch(EOFException e) {
			e.printStackTrace();
			try {
				input.close();

				output.close();
				localSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				input.close();

				output.close();
				localSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				input.close();

				output.close();
				localSocket.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}		
		}
        
        

	}

	public boolean sendRespons(PduBase pdubase) {
		
//		AgentLog.debug("agent", "send response : "+pdubase.getData());
		try {
			output.writeInt(EBEN_HEAD);
			if(null != pdubase && null != pdubase.pdu)  {
				AgentLog.debug("agent", "send response : "+pdubase.getData());
	            output.writeInt(pdubase.pdu.length);
	            output.write(pdubase.pdu);
			} else {
				byte[] ack = "{result:\"error\",code:\"1\"}".getBytes("utf-8");
	            output.writeInt(ack.length);
	            output.write(ack);
			}
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
