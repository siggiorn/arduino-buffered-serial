/*
Arduino Buffered Serial
A library that helps establish buffered serial communication with a
host application.
Copyright (C) 2010 Sigurður Örn Aðalgeirsson (siggi@media.mit.edu)

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package edu.mit.media.prg.arduino;

import processing.core.PApplet;
import processing.serial.Serial;
import edu.mit.media.prg.arduino.ArduinoBufferedSerial.SerialDevice;

/**
 * @author siggi
 * @date Apr 24, 2013
 */
public class PSerialDevice implements SerialDevice{

	private Serial serial;

	public PSerialDevice(PApplet parent, String portName, int baudRate){
		serial = new Serial(parent, portName, baudRate);
	}

	@Override
	public int available() {
		return serial.available();
	}

	@Override
	public byte read() {
		return (byte) serial.readChar();
	}

	@Override
	public void write(byte[] buffer) {
		serial.write(buffer);
	}

}