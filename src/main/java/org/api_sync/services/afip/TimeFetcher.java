package org.api_sync.services.afip;


import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Date;

@Slf4j
public class TimeFetcher {
	
	private static final String NTP_SERVER = "time.afip.gov.ar";
	private static final int NTP_PORT = 123;
	private static final int TIMEOUT_MS = 2000;
	private static final long SEVENTY_YEARS = 2208988800L;
	
	public Date getServerTime() {
		try (DatagramChannel channel = DatagramChannel.open()) {
			channel.configureBlocking(false);
			SocketAddress serverAddress = new InetSocketAddress(NTP_SERVER, NTP_PORT);
			
			ByteBuffer buffer = ByteBuffer.allocate(48);
			buffer.put((byte) 0b00100011); // LI = 0, VN = 4, Mode = 3 (client)
			for (int i = 1; i < 48; i++) buffer.put((byte) 0);
			buffer.flip();
			
			channel.send(buffer, serverAddress);
			
			long startTime = System.currentTimeMillis();
			buffer.clear();
			
			while (System.currentTimeMillis() - startTime < TIMEOUT_MS) {
				SocketAddress responseAddress = channel.receive(buffer);
				if (responseAddress != null) {
					buffer.flip();
					// Skip to transmit timestamp (offset 40)
					buffer.position(40);
					long secondsSince1900 = Integer.toUnsignedLong(buffer.getInt());
					long msSince1970 = (secondsSince1900 - SEVENTY_YEARS) * 1000;
					return new Date(msSince1970);
				}
			}
			
			throw new IOException("Timeout: no response from NTP server");
		} catch (Exception e) {
			log.error("Failed to fetch NTP time: " + e.getMessage());
			return new Date(); // fallback to local time
		}
	}

}

