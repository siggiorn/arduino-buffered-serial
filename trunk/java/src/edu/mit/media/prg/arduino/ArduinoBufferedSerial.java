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

import java.nio.ByteBuffer;

public class ArduinoBufferedSerial {

	public interface SerialDevice{
		public int available();
		public byte read();
		public void write(byte[] buffer);
	}

	private SerialDevice serial;
	private byte[] rcv_bytes = new byte[1024];

	private ByteBuffer rcv_buffer = ByteBuffer.wrap(rcv_bytes);

	private SerialPacketHandler handler;

	private byte rcv_checksum = 0;

	public ArduinoBufferedSerial(SerialDevice serial_device, SerialPacketHandler handler){
		this.serial = serial_device;
		this.handler = handler;
	}

	public void sendBuffer(ByteBuffer send){
		send.flip();
		byte[] out_buffer = new byte[send.limit() + 5];
		ByteBuffer out = ByteBuffer.wrap(out_buffer);
		out.put(send);

		// Calculate checksum
		byte checksum_calc = 0;
		for(int i=0; i<send.position(); i++){
			checksum_calc += send.get(i);
		}

		out.put(checksum_calc);
		out.put((byte)1);
		out.put((byte)2);
		out.put((byte)3);
		out.put((byte)4);

		serial.write(out_buffer);
	}

	public void update() {
		// Handle incoming serial stuff
		while(serial.available() > 0){
			handleIncomingByte(serial.read());
		}
	}

	private byte b1, b2, b3, b4;
	private void handleIncomingByte(byte in){

		rcv_buffer.put(in);
		rcv_checksum += in;
		b1 = b2; b2 = b3; b3 = b4; b4 = in;

		// Received whole package
		if( b1 == (byte)1 && b2 == (byte)2 && b3 == (byte)3 && b4 == (byte)4 ){
			byte received_checksum = rcv_buffer.get( rcv_buffer.position()-5 );
			rcv_checksum -= 10 + received_checksum; // Remove header and received checksum
			if( received_checksum == rcv_checksum ){
				rcv_buffer.limit( rcv_buffer.position()-5 );
				rcv_buffer.position(0);

				ByteBuffer data = ByteBuffer.allocate(rcv_buffer.remaining());
				data.rewind();
				data.put(rcv_buffer);
				data.rewind();
				handler.handleSerialPacket(data, data.remaining());

			}
			else{
				System.out.println("Received bad packet, packet reports checksum "+((int)received_checksum&0xff)+" but we calculated checksum "+((int)rcv_checksum&0xff));
			}

			// Reset
			rcv_buffer.position(0);
			rcv_buffer.limit(rcv_buffer.capacity());
			rcv_checksum = 0;
		}
	}
}
