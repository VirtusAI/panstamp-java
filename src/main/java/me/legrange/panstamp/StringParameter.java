package me.legrange.panstamp;

import me.legrange.panstamp.definition.ParameterDefinition;

/**
 * An parameter that supports the "str" type from the XML definitions and maps
 * these to Strings. 
 * 
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange *
 */
final public class StringParameter extends AbstractParameter<String> {

    StringParameter(Register reg, ParameterDefinition epDef) {
        super(reg, epDef);
        
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }
    
   
    @Override
    public String getValue() throws NetworkException {
        byte bytes[]  = reg.getValue();
        byte keep[] = new byte[par.getSize().getBytes()];
        System.arraycopy(bytes, par.getPosition().getBytePos(), keep, 0, par.getSize().getBytes());
        return new String(keep);
    }
  
    @Override
    public void setValue(String value) throws NetworkException {
        int len = par.getSize().getBytes();
        if (value.length() > len) {
            value = value.substring(0, len -1);
        } 
        else {
        }
        byte bytes[] = new byte[len];
        System.arraycopy(value.getBytes(), 0, bytes, par.getPosition().getBytePos(), value.length());
        reg.setValue(bytes);
    }

    @Override
    public String getDefault() {
        return par.getDefault();
    }
    
    
    
}
