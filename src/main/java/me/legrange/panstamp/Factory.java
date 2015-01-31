package me.legrange.panstamp;

import me.legrange.panstamp.def.ClassLoaderLibrary;
import me.legrange.panstamp.core.GatewayImpl;
import me.legrange.panstamp.store.MemoryStore;
import me.legrange.swap.SWAPModem;
import me.legrange.swap.serial.SerialModem;
import me.legrange.swap.tcp.TcpModem;

/**
 * Factory used to construct panStamp gateway instances. 
 * 
 * @author gideon
 */
public final class Factory {
    
    /** Create a new serial gateway (gateway attached to a serial port) with the given port and speed, 
     * and with the default device library and data store. 
     * 
     * @param port The serial port to open, for example COM1 or /dev/ttyS0
     * @param baud The speed at which to open it, for example 34800 
     * @return The newly created gateway. 
     */
    public static Gateway createSerial(String port, int baud) {
        return createSerial(port, baud, new ClassLoaderLibrary(), new MemoryStore());
    }
 
    /** Create a new serial gateway (gateway attached to a serial port) with the given port and speed, 
     * and with the given device library and data store. 
     * 
     * @param port The serial port to open, for example COM1 or /dev/ttyS0
     * @param baud The speed at which to open it, for example 34800 
     * @param lib The device library to use with the gateway. 
     * @param store The data store to use for keeping track of devices. 
     * @return The newly created gateway. 
     */
    public static Gateway createSerial(String port, int baud, DeviceLibrary lib, DataStore store) {
        SerialModem sm = new SerialModem(port, baud);
        return createGateway(sm, lib, store);
    }

    /** Create a new TCP/IP gateway (gateway attached to a remote TCP service) with the given host and port, 
     * and with the default device library and data store. 
     * 
     * @param host The host name to which to connect, for example 'localhost' or '192.168.1.1'
     * @param port The TCP port to which to connect.
     * @return The newly created gateway 
     */
    public static Gateway createTcp(String host, int port) {
        return createTcp(host, port, new ClassLoaderLibrary(), new MemoryStore());
    }
    
    /** Create a new TCP/IP gateway (gateway attached to a remote TCP service) with the given host and port, 
     * and with the given device library and data store. 
     * 
     * @param host the host to which to connect. 
     * @param port The TCP port to which to connect.
     * @param lib The device library to use with the gateway. 
     * @param store The data store to use for keeping track of devices. 
     * @return The newly created gateway. 
     */
    public static Gateway createTcp(String host, int port, DeviceLibrary lib, DataStore store) {
        TcpModem tm = new TcpModem(host, port);
        return createGateway(tm, lib, store);
    }
    
    private static Gateway createGateway(SWAPModem modem, DeviceLibrary lib, DataStore store) {
        return new GatewayImpl(modem, lib, store);
    }
}