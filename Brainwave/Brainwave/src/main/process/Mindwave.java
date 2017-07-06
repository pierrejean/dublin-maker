package main.process;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.Settings;
import main.util.Liste;

public class Mindwave implements Runnable {

	
	private JSONParser jsonParser = new JSONParser();
	
//	private boolean started = false;
	private AtomicInteger data;
	private Scanner scanner = null;

	public Mindwave(final AtomicInteger data) {
		SocketChannel channel = null;
		try {
			channel = SocketChannel.open(new InetSocketAddress(Settings.TCP_HOST, Settings.TCP_PORT));
			channel.write(
					Charset.forName("US-ASCII").newEncoder().encode(CharBuffer.wrap(Settings.MESSAGE_START_JSON)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.data = data;
		this.scanner = new Scanner(channel);
		
	}
	

	@Override
	public void run() {
		int i = 0;
		while( ! Thread.currentThread().isInterrupted() ) {
		
			// this.started = true;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				System.out.println( line );
				
//				byte[] buffer = line.getBytes();
//				for(int j=0;j<buffer.length;j++){
//					String hexa = Integer.toHexString( buffer[j]);	        		
//	        		System.out.println( hexa );
//					
//				}
        		
	
				if (i == 0) { // Forget first line there is special caracters into
					i = 1;
				} else {
					
					int poorSignalLevel = getValueFromJson(line, "poorSignalLevel");
					if ( poorSignalLevel != -1 ) {
						System.out.println(".");
					}
					
					int value = getValueFromJson(line, "rawEeg");
					if (value != -1) {
						System.out.println(  System.nanoTime() + ": "+ line );
						// data.set( value );
						i++;
					}
				}
	
			}
			
			
			
			try {
	              Thread.sleep(10);
	          } catch (InterruptedException e) {
	              scanner.close();
	              Thread.currentThread().interrupt();
	              return;
	          }
		}
	}


	private int getValueFromJson(String jsonLine, String itemLabel) {
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonLine);
			Long rawValue = (Long) jsonObject.get(itemLabel);
			if (rawValue != null) {				
				return  rawValue.intValue();
			}

		} catch (ParseException e) {
			System.out.println("Error parse line " + jsonLine + " for " + itemLabel);
		}

		return -1;
	}


}
