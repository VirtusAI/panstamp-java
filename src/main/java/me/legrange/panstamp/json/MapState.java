package me.legrange.panstamp.json;

import java.util.Map;
import me.legrange.panstamp.impl.StandardRegister;
import me.legrange.panstamp.store.RegisterState;

/**
 *
 * @author gideon
 */
class MapState implements RegisterState {

    @Override
    public byte[] getState(StandardRegister reg) {
        byte bytes[] = state.get(reg);
        if (bytes == null) {
            return new byte[]{};
        }
        return bytes;
    }
    
    MapState(Map<StandardRegister, byte[]> state) {
       this.state = state;
    }
    
    private final Map<StandardRegister, byte[]> state;
    
}
