package me.legrange.swap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// IMPORTS
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public final class PureComPort {
	
    /**
     * open the com port.
     *
     * @param portName Name/device file to open
     * @param speed Speed at which to communicate
     * @return The ComPort object created
     * @throws SerialException Thrown if there is a problem opening the port.
     */
    static PureComPort open(String portName, int speed) throws SerialException {
    	PureComPort port = new PureComPort();
        port.findAndOpen(portName, speed);
        
        return port;
    }

    /**
     * send a line of text to the com port
     */
    void send(String msg) throws SerialException {
        try {
            synchronized (outLock) {
                out.write(msg.getBytes());
                out.flush();
            }
        } catch (IOException ex) {
            throw new SerialException("IO error sending data to serial port: " + ex.getMessage(), ex);
        }
    }

    /**
     * read a line of text from the com port
     */
    String read() throws SerialException {
        try {
            StringBuilder line = new StringBuilder();
            synchronized (inLock) {
                while (true) {
                    int val = in.read();
                    if (val < 0) {
                        return line.toString();
                    }
                    switch (val) {
                        case '\n':
                        case '\r':
                            if (line.length() > 0) {
                                return line.toString();
                            }
                            break;
                        default:
                            line.append((char) val);
                    }
                }
            }
        } catch (IOException ex) {
            throw new SerialException("IO error reading data from serial port: " + ex.getMessage(), ex);
        }
    }

    /**
     * Close serial port
     */
    void close() throws SerialException {
        try {
        	if(in != null)
        		in.close();
        	
        	if(out != null)
        		out.close();
        	
        	if(port != null)
        		port.close();
        } catch (IOException ex) {
            throw new SerialException(ex.getMessage(), ex);
        }
    }

	private PureComPort() {
		// TODO Auto-generated constructor stub
	}

    /**
     * find the port, open it and configure it to the correct baud, stop, parity
     * etc
     */
    private void findAndOpen(String portName, int baudRate) throws SerialException {
    	    	
        CommPortIdentifier portId;
        try {
            portId = CommPortIdentifier.getPortIdentifier(portName);
        } catch (NoSuchPortException ex) {
            throw new SerialException(String.format("Could not find serial port '%s'", portName), ex);
        }
        try {
            port = (SerialPort) portId.open(getClass().getSimpleName(), timeout);
            port.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            //port.enableReceiveTimeout(MAX_TIMEOUT);
            //port.enableReceiveThreshold(0);
            in = port.getInputStream();
            out = port.getOutputStream();
            out.flush();
        } catch (PortInUseException ex) {
            throw new SerialException(String.format("Serial port '%s' is in use", portName), ex);
        } catch (UnsupportedCommOperationException | IOException ex) {
            throw new SerialException(String.format("Error accessing serial port '%s': %s", portName, ex.getMessage()), ex);
        }
    }

    private InputStream in;
    private OutputStream out;
    private SerialPort port;
    private final Object inLock = new Object();
    private final Object outLock = new Object();
    private static final int timeout = 60000;

}
