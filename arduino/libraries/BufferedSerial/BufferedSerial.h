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

#ifndef BufferedSerial_h
#define BufferedSerial_h

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

#include "ByteBuffer.h"

typedef void (*voidFuncPtr)(ByteBuffer*);

#if defined(__AVR_ATmega8__)
	#define UCSRA	UCSRA
	#define UDRE	UDRE
#else
	#define UCSRA	UCSR0A
	#define UDRE	UDRE0
#endif

#if defined(__AVR_ATmega1280__)
	#define UCSRA1	UCSR1A
	#define UCSRA2	UCSR2A
	#define UCSRA3	UCSR3A
#endif


class BufferedSerial
{
public:
	BufferedSerial(unsigned int in_buf_size, unsigned int out_buf_size);
	void init(int serial_port, long baud_rate);
	void setPacketHandler(void (*rx_func)(ByteBuffer*));

	void update();
	bool isBusySending();

	int sendSerialByte(byte b);
	int sendSerialPacket(ByteBuffer* packet);
	int sendRawSerial(ByteBuffer* packet);

private:
	void handleIncomingByte(byte incoming);
	void handlePacketDefault(ByteBuffer* packet);

	voidFuncPtr handlePacketFunction;

	byte _serial_port;
	ByteBuffer* incoming_buffer;
	ByteBuffer* outgoing_buffer;
	ByteBuffer* temp_buffer;
	byte serial_in_checksum;
	byte byte1;
	byte byte2;
	byte byte3;
	byte byte4;
};

#endif

