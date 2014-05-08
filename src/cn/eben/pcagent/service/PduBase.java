package cn.eben.pcagent.service;

import java.io.UnsupportedEncodingException;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class PduBase implements Parcelable {

    private String data;
    private String b[];
    public short style;
    public byte pdu[];
    
    public PduBase(String data) {
        style = 1;
        try {
			pdu = (byte[])data.getBytes("utf-8").clone();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public PduBase(Parcel parcel)
    {
        style = 0;
        pdu = null;
        data = null;
        b = null;
        style = (short)parcel.readInt();
        String s = parcel.readString();
        try
        {
            if(!TextUtils.isEmpty(s))
                pdu = s.getBytes("utf-8");
            else
                pdu = null;
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            unsupportedencodingexception.printStackTrace();
            pdu = null;
        }
    }
    
    public PduBase(short word0, byte abyte0[])
    {
        style = 0;
        pdu = null;
        data = null;
        b = null;
        style = word0;
        pdu = abyte0;
    }

//    public short b()
//    {
//        return style;
//    }

    public String getData()
 {
		String s;

		if (data == null)
			try {
				if (pdu == null) {
					s = null;

				} else {
					data = new String(pdu, "utf-8");
				}
			} catch (Exception exception) {
		}
		s = data;

		return s;
	}

//    public String[] d()
//    {
//        if(b == null)
//        {
//            String s = getData();
//            if(s != null)
//            {
//                String as[] = s.split(":");
//                if(as.length >= 2)
//                {
//                    String as1[] = new String[-1 + as.length];
//                    System.arraycopy(as, 1, as1, 0, -1 + as.length);
//                    b = as1;
//                }
//            }
//        }
//        return b;
//    }

    public int describeContents()
    {
        return 0;
    }

//    public String e()
//    {
//        String s;
//        try
//        {
//            s = (new String(pdu)).split(":")[0];
//        }
//        catch(Exception exception)
//        {
//            exception.printStackTrace();
//            s = null;
//        }
//        return s;
//    }

//    public String f()
//    {
//        String s = null;
//        try
//        {
//            String as[] = (new String(pdu)).split(":");
//            if(as.length > 1)
//                s = as[1];
//        }
//        catch(Exception exception)
//        {
//            exception.printStackTrace();
//        }
//        return s;
//    }

//    public String toString()
//    {
//        Object aobj[] = new Object[2];
//        aobj[0] = Integer.valueOf(b());
//        aobj[1] = getData();
//        return String.format("Pdu( %d ): %s", aobj);
//    }
    


	@Override
	public void writeToParcel(Parcel parcel, int i) {
		// TODO Auto-generated method stub
        parcel.writeInt(style);
        try {
			parcel.writeString(new String(pdu, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
