package me.legrange.panstamp.core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.RegisterListener;
import me.legrange.panstamp.def.EndpointDef;
import me.legrange.panstamp.def.Unit;

/**
 * Abstract implementation of an endpoint that can be extended into endpoints
 * supporting different data times.
 *
 * @param <T> The data type supported by the endpoint.
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange *
 */
public abstract class AbstractEndpoint<T> implements Endpoint<T> {

    @Override
    public String getName() {
        return epDef.getName();
    }

    @Override
    public final List<String> getUnits() {
        List<String> res = new LinkedList<>();
        for (Unit u : epDef.getUnits()) {
            res.add(u.getName());
        }
        return res;
    }

    @Override
    public final boolean hasValue() {
        return reg.hasValue();
    }

    @Override
    public synchronized void addListener(EndpointListener<T> el) {
        if (listeners.isEmpty()) {
            getRegister().addListener(listener);
        }
        listeners.add(el);
    }

    @Override
    public synchronized void removeListener(EndpointListener<T> el) {
        listeners.remove(el);
        if (listeners.isEmpty()) {
            getRegister().removeListener(listener);
        }
    }

    @Override
    public final T getValue(String unit) throws GatewayException {
        return transformIn(getValue(), getUnit(unit));
    }

    @Override
    public void setValue(String unit, T value) throws GatewayException {
        setValue(transformOut(value, getUnit(unit)));
    }

    @Override
    public Register getRegister() {
        return reg;
    }

    /**
     * Transform the output value from a value in the given unit
     *
     * @param value The value to transform
     * @param unit The unit from which to transform it
     * @return The transformed value
     */
    protected abstract T transformOut(T value, Unit unit);

    /**
     * Transform the input value to a value in the given unit
     *
     * @param value The value to transform
     * @param unit The unit to which to transform it
     * @return The transformed value
     */
    protected abstract T transformIn(T value, Unit unit);

    protected final Unit getUnit(String name) throws NoSuchUnitException {
        for (Unit u : epDef.getUnits()) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        throw new NoSuchUnitException(String.format("No unit '%s' found in endpoint '%s'", name, getName()));
    }

    protected AbstractEndpoint(RegisterImpl reg, EndpointDef epDef) {
        this.reg = reg;
        this.epDef = epDef;
        this.listeners = new CopyOnWriteArrayList<>();

    }

    void destroy() {
        listeners.clear();
    }

    /**
     * Get the executor service used to service library threads
     */
    private ExecutorService pool() {
        return reg.getPool();
    }

    protected final RegisterImpl reg;
    protected final EndpointDef epDef;
    private final CopyOnWriteArrayList<EndpointListener<T>> listeners;

    private final RegisterListener listener = new AbstractRegisterListener() {
        @Override
        public void valueReceived(final Register reg, final byte[] value) {
            for (final EndpointListener<T> l : listeners) {
                pool().submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            l.valueReceived(AbstractEndpoint.this, getValue());
                        } catch (GatewayException ex) {
                            Logger.getLogger(AbstractEndpoint.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
                );
            }
        }
    };

}
