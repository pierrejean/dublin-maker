package main.rxtx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class PortCommunication {
	private final AtomicInteger data = new AtomicInteger(0);
	private InputStreamReader reader = new InputStreamReader(System.in);
	private String portName = "";
	private SerialPort serialPort = null;
	private InputStream in = null;
	private OutputStream out = null;
	private Thread serialReaderThread = null;
	private Thread serialWriteThread = null;

	
	public PortCommunication(String portName) {
		this.portName = portName;
	}

	public void connect() throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				this.serialPort = (SerialPort) commPort;
				this.serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				this.in = this.serialPort.getInputStream();
				this.out = this.serialPort.getOutputStream();

				this.serialReaderThread = new Thread(new SerialReader(in));
				this.serialReaderThread.start();
				
				this.serialWriteThread = new Thread(new SerialWriter(out, data));
				this.serialWriteThread.start();				
				

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public void sendMessage(char key) {
		System.out.println("READ KEYBOARD:" + key);
		data.set(key);

	}

	public AtomicInteger getData() {
		return data;
	}

	public void close() {
		
		System.out.println("CLOSE");
		
		if ( this.serialWriteThread != null ){
			this.serialWriteThread.interrupt();	
    	}
		if ( this.serialReaderThread != null ){
			this.serialReaderThread.interrupt();	
    	}		
		
	    if ( this.serialPort != null) {
	        try {	           
	            this.out.close();
	            this.in.close();
	        } catch (IOException ex) {
	            // don't care
	        }
	        this.serialPort.close();
	    }
	}

}
