package me.legrange.panstamp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.InputEndpoint;
import me.legrange.panstamp.NodeNotFoundException;
import me.legrange.panstamp.OutputEndpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.def.ClassLoaderLibrary;
import me.legrange.panstamp.def.Device;
import me.legrange.panstamp.def.DeviceLibrary;
import me.legrange.panstamp.def.EndpointDef;
import me.legrange.swap.CommandMessage;
import me.legrange.swap.Message;
import me.legrange.swap.MessageListener;
import me.legrange.swap.QueryMessage;
import me.legrange.swap.SWAPException;
import me.legrange.swap.SWAPModem;
import me.legrange.swap.SerialException;
import me.legrange.swap.StatusMessage;

/**
 * A gateway connecting a PanStampImpl network to your code using the
 * PanStampImpl modem connected to the local serial port.
 *
 * @author gideon
 */
public final class SerialGateway extends Gateway {

    public static SerialGateway openSerial(String port, int baud) throws ModemException {
        SerialGateway gw = new SerialGateway(new ClassLoaderLibrary());
        gw.start(port, baud);
        return gw;
    }

    /**
     * Disconnect from the modem and close the gateway
     *
     * @throws me.legrange.panstamp.impl.ModemException
     */
    @Override
    public void close() throws ModemException {
        try {
            modem.close();
            devices.clear();
        } catch (SerialException e) {
            throw new ModemException(e.getMessage(), e);
        }
    }

    /**
     * use to check if a device with the given address is known
     *
     * @param address Address of the device we're looking for.
     * @return True if a device with the given address is known
     */
    @Override
    public boolean hasDevice(int address) {
        return devices.get(address) != null;
    }

    /**
     * return the device with the given name
     *
     * @param address Address of device to fins
     * @return The device
     * @throws me.legrange.panstamp.NodeNotFoundException
     */
    @Override
    public PanStamp getDevice(int address) throws NodeNotFoundException {
        PanStampImpl mote = devices.get(address);
        if (mote == null) {
            throw new NodeNotFoundException(String.format("No device found for address %02x", address));
        }
        return mote;
    }

    /**
     * return the devices associated with this gateway
     *
     * @return The collection of devices
     */
    @Override
    public Collection<PanStamp> getDevices() {
        List<PanStamp> res = new ArrayList<>();
        res.addAll(devices.values());
        return res;
    }

    Endpoint getEndpoint(PanStampImpl ps, String name) throws GatewayException {
        Register reg = ps.getRegister(0);
        byte val[] = reg.getValue();
        int manId = val[0] << 24 | val[1] << 16 | val[2] << 8 | val[3];
        int productId = val[4] << 24 | val[5] << 16 | val[6] << 8 | val[7];
        Device dev = lib.findDevice(manId, productId);
        EndpointDef epDef = dev.getEndpoint(name);
        switch (epDef.getDirection()) {
            case IN:
                return getInputEndpoint(ps, epDef);
            case OUT:
                return getOutputEndpoint(ps, epDef);
        }
        return null; // FIX ME. Need to handle impossible ase of direction being weird. 
    }

    private InputEndpoint getInputEndpoint(PanStamp ps, EndpointDef epDef) throws NoSuchUnitException {
        switch (epDef.getType()) {
            case NUMBER:
                return new NumberInputEndpoint(ps, epDef);
            case STRING:
                return new StringInputEndpoint(ps, epDef);
            default:
                throw new NoSuchUnitException(String.format("Unknown end point type '%s'. BUG!", epDef.getType()));
        }
    }

    private OutputEndpoint getOutputEndpoint(PanStamp ps, EndpointDef epDef) throws NoSuchUnitException {
        switch (epDef.getType()) {
            case NUMBER:
                return new NumberOutputEndpoint(ps, epDef);
            case STRING:
                return new StringOutputEndpoint(ps, epDef);
            default:
                throw new NoSuchUnitException(String.format("Unknown end point type '%s'. BUG!", epDef.getType()));
        }
    }

    /**
     * set a remote register
     */
    void setRegister(PanStampImpl mote, int register, byte[] value) throws ModemException {
        Message msg = new CommandMessage();
        msg.setReceiver(mote.getAddress());
        msg.setRegisterAddress(mote.getAddress());
        msg.setRegisterID(register);
        msg.setRegisterValue(value);
        send(msg);
    }

    /**
     * request a remote register
     */
    void requestRegister(PanStampImpl mote, int register) throws ModemException {
        Message msg = new QueryMessage();
        msg.setReceiver(mote.getAddress());
        msg.setRegisterAddress(mote.getAddress());
        msg.setRegisterID(register);
        send(msg);
    }

    /**
     * send a message to a mote
     */
    void send(Message msg) throws ModemException {
        System.out.printf("send: %s\n", msg);
        try {
            modem.send(msg);
        } catch (SerialException ex) {
            throw new ModemException(ex.getMessage(), ex);
        }
    }

    public SerialGateway(DeviceLibrary lib) {
        this.lib = lib;
    }

    private void start(String port, int baud) throws ModemException {
        devices = new HashMap<>();
        receiver = new Receiver();
        try {
            modem = SWAPModem.open(port, baud);
        } catch (SWAPException ex) {
            throw new ModemException(ex.getMessage(), ex);
        }
        modem.addMessageListener(receiver);
    }

    /**
     * update the network based on a received message
     */
    private void updateNetwork(Message msg) throws ModemException {
        updateMote(msg.getSender(), 0);
        if (msg.getRegisterAddress() != msg.getSender()) {
            updateMote(msg.getRegisterAddress(), msg.getSender());
        }
    }

    /**
     * update the mote
     */
    private void updateMote(int address, int route) throws ModemException {
        PanStampImpl mote;
        if (hasDevice(address)) {
            try {
                mote = (PanStampImpl) getDevice(address);
            } catch (NodeNotFoundException ex) {
                logger.severe(String.format("Could not find node %02x for update.", address));
                return;
            }
        } else {
            mote = new PanStampImpl(this, address);
            requestRegister(mote, 0);
            devices.put(address, mote);
        }
        mote.setLastSeen(System.currentTimeMillis());
        mote.setRoute(route);
    }

    private SWAPModem modem;
    private Receiver receiver;
    private Map<Integer, PanStampImpl> devices;
    private static final Logger logger = Logger.getLogger(SerialGateway.class.getName());
    private final DeviceLibrary lib;

    /**
     * A receiver for incoming messages
     */
    private class Receiver implements MessageListener {

        @Override
        public void queryReceived(QueryMessage msg) {
            try {
                updateNetwork(msg);
            } catch (ModemException ex) {
                java.util.logging.Logger.getLogger(SerialGateway.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void statusReceived(StatusMessage msg) {
            try {
                updateNetwork(msg);
                PanStampImpl mote = (PanStampImpl) getDevice(msg.getSender());
                mote.update(msg.getRegisterID(), msg.getRegisterValue());
            } catch (GatewayException ex) {
                java.util.logging.Logger.getLogger(SerialGateway.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void commandReceived(CommandMessage msg) {
            try {
                updateNetwork(msg);
            } catch (GatewayException ex) {
                java.util.logging.Logger.getLogger(SerialGateway.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
