/*
Arduino Buffered Serial
A library that helps establish buffered serial communication with a 
host application.
Copyright (C) 2010 Sigurdur Orn Adalgeirsson (siggi@media.mit.edu)

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

#include <BufferedSerial.h>
#include <ByteBuffer.h>

BufferedSerial serial = BufferedSerial(256, 256);
ByteBuffer send_buffer;

int SEND_EXAMPLE = 3;

void setup()
{
  // initialize the serial communication:
  serial.init(0, 9600);
  serial.setPacketHandler(handlePacket);
 
  // Initialize the send buffer that we will use to send data
  send_buffer.init(30);
}

void loop() {
  serial.update();
 
  // If we are not busy sending then lets send something
  if( !serial.isBusySending() ){
    // Send some dummy data  
    send_buffer.clear();
    send_buffer.put(17);
    send_buffer.putInt(300);    
    send_buffer.putLong(-100000);    
    send_buffer.putFloat(3.14);    

    // We can either send a packet with a header and checksum (niiice)
    if(SEND_EXAMPLE == 0)
      serial.sendSerialPacket( &send_buffer );
    
    // Or if we are sending to say a device that uses a custom protocol,
    // we can send a raw byte buffer to it
    if(SEND_EXAMPLE == 1)
      serial.sendRawSerial( &send_buffer );
    
    // Or if we just want to simply send one byte we can do that,
    if(SEND_EXAMPLE == 2)
      serial.sendSerialByte(16);
  }
}

void handlePacket(ByteBuffer* packet){
  // Here we could do anything we want to the data but for now we will just send it back
  if( SEND_EXAMPLE == 3 ){
    send_buffer.clear();
    while( packet->getSize() > 0 )
      send_buffer.put( packet->get() );
    serial.sendSerialPacket( &send_buffer );
  }
}

