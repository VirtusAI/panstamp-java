package me.legrange.panstamp.devicestore;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import me.legrange.panstamp.DeviceStateStore;
import me.legrange.panstamp.Register;

/**
 * 
 * Implementation of device store that persists changes to a file
 * 
 * @author Ricardo Teixeira
 *
 */

public class PersistentMemoryStore implements DeviceStateStore {

	public PersistentMemoryStore(File DBfile) throws StoreNotFoundException {
		if(DBfile.exists() && (!DBfile.canRead() || !DBfile.canWrite())) {			
			throw new StoreNotFoundException(String.format("Cannot open/read/write '%s'.", DBfile.getAbsolutePath()));
		} 
		
		try {
			this.db = DBMaker
				.fileDB(DBfile)
				.transactionEnable()
				.closeOnJvmShutdown()
				.make();
			
		} catch (DBException e) {
			Logger.getLogger(PersistentMemoryStore.class.getName()).log(Level.SEVERE, e.getMessage());
			
			// use in-memory database;	
			this.db = DBMaker
				.heapDB()
				.make();
		}
		
		this.cache = db
					.hashMap("cache")
					.keySerializer(Serializer.INT_ARRAY)
					.valueSerializer(Serializer.BYTE_ARRAY)
					.createOrOpen();
		
		// safe closing
		// Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
	}

    @Override
    public boolean hasRegisterValue(Register reg) {
        return mapForAddress(reg.getDevice().getAddress(), reg.getId()) != null;
    }

    @Override
    public byte[] getRegisterValue(Register reg) {
        return mapForAddress(reg.getDevice().getAddress(), reg.getId());
    }

    @Override
    public void setRegisterValue(Register reg, byte value[]) {
    	 cache.put(new int[]{reg.getDevice().getAddress(), reg.getId()}, value);
//         mapForAddress(reg.getDevice().getAddress()).put(reg.getId(), value);
         db.commit();
    }
    
	@Override
	public Set<Integer> getStoredAddresses() {
		return cache
					.getKeys()
					.stream()
					.map(tuple -> tuple[0])
					.collect(Collectors.toSet());
	}

	@Override
	public void close() {
		this.db.close();		
	}
    
    private byte[] mapForAddress(int address, int id) {
    	byte[] data = cache.get(new int[]{address, id});
    	if(data != null)
    		return data;
    	else if(cache.getKeys().stream().anyMatch(tuple -> tuple[0] == address)) {
    		cache.put(new int[]{address, id}, new byte[]{});
    		return cache.get(new int[]{address, id});
    	} else
    		return null;
    }
	
	private DB db;
	private HTreeMap<int[], byte[]> cache;
	
	private final static Logger log = Logger.getLogger(PersistentMemoryStore.class.getName());
	
}
