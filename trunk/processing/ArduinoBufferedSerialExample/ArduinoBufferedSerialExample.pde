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

import java.nio.ByteBuffer;
import processing.serial.*;

ArduinoBufferedSerial serialMan;

void setup(){
  size(300,300);
  
  // Create a serial manager using the first serial port name listed
  serialMan = new ArduinoBufferedSerial(
    new PSerialDevice(this, Serial.list()[0], 9600),
    new Handler());
}

void draw(){
  // We always need to update the serial manager in our draw or update method
  serialMan.update();
}

void mouseDragged(){
  // Here we simply create a buffer and put the XY coordinates of the mouse into it
  ByteBuffer sendBuffer = ByteBuffer.allocateDirect(8);
  sendBuffer.putInt(mouseX);  
  sendBuffer.putInt(mouseY);  
  serialMan.sendBuffer(sendBuffer);
}

// This class implements SerialPacketHandler and is used to handle the data
// coming from the Arduino
class Handler implements SerialPacketHandler{
  public void handleSerialPacket(ByteBuffer bb, int length){
    if( length >= 8 ){        
      int x = bb.getInt();        
      int y = bb.getInt();        
      println("X: "+x+", Y:"+y);
    }
  }
}
