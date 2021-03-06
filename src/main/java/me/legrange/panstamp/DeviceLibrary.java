package me.legrange.panstamp;

import me.legrange.panstamp.definition.DeviceDefinition;

/**
 * A device library can be implemented to find a device definition based on a panStamp's
 * manufacturer ID and product ID.  
 * 
 * The device definitions are found in XML files, and while application developers 
 * will typically not implement this interface, it can be implemented to define 
 * custom ways of loading XML files. 
 * 
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public interface DeviceLibrary {
    
    /** Checks if a device definition for the supplied manufacturer ID and product ID is available. 
     * 
     * @param manufacturedID Manufacturer ID for device.
     * @param productId Product ID for device. 
     * @return True if the definition is available. 
     * @throws NetworkException If there is a problem loading a device definition. 
     */
    boolean hasDeviceDefinition(int manufacturedID, int productId) throws NetworkException;

    /** get the device definition based on the supplied manufacturer ID and product ID. 
     * 
     * @param manufacturedID Manufacturer ID for device.
     * @param productId Product ID for device. 
     * @return The device definition. 
     * @throws NetworkException If there is a problem loading a device definition. 
     */
    DeviceDefinition getDeviceDefinition(int manufacturedID, int productId) throws NetworkException;
        
}
