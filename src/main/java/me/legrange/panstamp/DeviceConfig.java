package me.legrange.panstamp;

/**
 * PanStamp device configuration. Use this interface to read
 * and change the configuration of a remote panStamp device.
 * @author gideon
 */
public interface DeviceConfig {
    
    /** Return the address of the device
     * @return The address  */
    int getAddress();
    
    /** Return the network channel *
     * 
     * @return The channel
     * @throws GatewayException 
     */
    int getChannel() throws GatewayException;
    /** Return the network ID
     * 
     * @return The network ID
     * @throws GatewayException 
     */
    int getNetwork() throws GatewayException;
    
    /** Return the current security option
     * @return The security option
     * @throws me.legrange.panstamp.GatewayException  */
    int getSecurityOption() throws GatewayException;

    /** Return the transmit interval
     * @return The interval
     * @throws me.legrange.panstamp.GatewayException  */
    int getTxInterval() throws GatewayException;

    
    void setAddress(int addr) throws GatewayException;
    void setNetwork(int network) throws GatewayException;
    void setChannel(int channel) throws GatewayException;
    void setSecurityOption(int option) throws GatewayException;
    void setTxInterva(int txInterval)throws GatewayException;


}
