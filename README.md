# Overview

This project was previously hosted here: 
<https://code.google.com/p/arduino-buffered-serial>

This project includes Arduino libraries to perform buffered, interrupt driven, serial communication with a host application (Processing example included). This provides significant benefit over the good-old Serial.println("some ascii") method both as it has more efficient storage of numerical data as integers, longs, bytes or floats rather than ascii coded strings, as well as being interrupt driven so that it doesn't block the operation of the device.


# Arduino Examples


### Serial Echoing

```c
#include <BufferedSerial.h>
#include <ByteBuffer.h>

BufferedSerial serial = BufferedSerial(256, 256);
ByteBuffer send_buffer;

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
}

void handlePacket(ByteBuffer* packet){
   // Here we could do anything we want to the data but for now we will just send it back
   send_buffer.clear();
   while( packet->getSize() > 0 )
     send_buffer.put( packet->get() );
   serial.sendSerialPacket( &send_buffer );
 }
```

### Explicit Sending
```c
#include <BufferedSerial.h>
#include <ByteBuffer.h>

BufferedSerial serial = BufferedSerial(256, 256);
ByteBuffer send_buffer;

int SEND_EXAMPLE = 3;

void setup() {
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
 

    serial.sendSerialPacket( &send_buffer );
   }
}

// Do nothing with incoming data
void handlePacket(ByteBuffer* packet){ }

}
``` 

# Processing Example 
(To interface with the Ardunio Echo)

```java
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
``` 