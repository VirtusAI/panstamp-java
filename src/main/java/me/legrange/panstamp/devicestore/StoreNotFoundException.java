package me.legrange.panstamp.devicestore;

import me.legrange.panstamp.definition.DefinitionException;

/**
 * Thrown when there is an error parsing the XML device files. 
 * 
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange *
 */
public final class StoreNotFoundException extends DefinitionException {

    public StoreNotFoundException(String message) {
        super(message);
    }

    public StoreNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    
}
