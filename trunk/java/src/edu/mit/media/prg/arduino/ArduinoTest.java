package edu.mit.media.prg.arduino;

import java.nio.ByteBuffer;

import processing.core.PApplet;
import processing.serial.Serial;

/**
 * @author siggi
 * @date Jun 25, 2013
 */
public class ArduinoTest extends PApplet implements SerialPacketHandler {

	ArduinoBufferedSerial arduino;
	ByteBuffer sendBuffer = ByteBuffer.allocate(256);

	public ArduinoTest(){		
		System.out.println("Available serial ports:");
		String[] ports = Serial.list();
		for (String port : ports) {
			System.out.println(" - "+port);
		}
		
		/*
		 * Create serial object using Processing's serial library, choosing first serial port listed (change to what you like)
		 */
		PSerialDevice serial = new PSerialDevice(this, ports[0], 9600);
		
		/*
		 * Create arduino object, register ourselves as a handler for incoming data
		 */
		arduino = new ArduinoBufferedSerial(serial, this);
	}

	@Override
	public void setup() {
		size(300, 300);
	}
	
	@Override
	public void draw() {
		/*
		 * We need to update the arduino object regularly, this is where received packets are processed etc.
		 */
		arduino.update();
	}

	/*
	 * Every now and then we can send an arbitrary packet to the Arduino
	 */
	@Override
	public void keyTyped() {
		sendBuffer.clear();
		sendBuffer.putInt(10);	// Note that int means 32-bit in Java, 16-bit in Arduino (Java int is called long in Arduino)
		sendBuffer.putFloat(3.14f);
		arduino.sendBuffer( sendBuffer );
	}
	
	/*
	 * And finally here we can read the incoming data
	 */
	public void handleSerialPacket(ByteBuffer bb, int length) {
		System.out.println("Received packet of size: "+length);
	}
}
