package main.rxtx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class SerialWriter implements Runnable {
	private OutputStream out;
	private AtomicInteger data;

	public SerialWriter(OutputStream out, AtomicInteger data) {
		this.out = out;
		this.data = data;
	}

	public void run() {

		while (!Thread.currentThread().isInterrupted()) {
			
			try {
				int dataToSend = data.get();
				if (dataToSend != 0) {
					out.write(dataToSend);
					System.out.println("Serial: " + dataToSend);
					data.set(0);

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

}