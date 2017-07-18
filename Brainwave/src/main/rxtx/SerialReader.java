package main.rxtx;

import java.io.IOException;
import java.io.InputStream;


public  class SerialReader implements Runnable 
{
    private InputStream in;
        
    public SerialReader ( InputStream in ){
        this.in = in;
    }
    
    public void run ()
    {
        byte[] buffer = new byte[1024];
        int len = -1;
        
        System.out.println("GO");
        
        try
        {
            while ( ( len = this.in.read(buffer)) > -1 )
            {
//                System.out.print(new String(buffer,0,len));
            	for(int i=0; i<len;i++){            		
            		String hexa = Integer.toHexString( buffer[i]);
//            		System.out.println( hexa );
            	}
            }
        }
        catch ( IOException e ){
            e.printStackTrace();
        }
    }
    

}