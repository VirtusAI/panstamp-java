package me.legrange.panstamp.devicestore;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;

import me.legrange.panstamp.DeviceStateStore;
import me.legrange.panstamp.Register;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisStore implements DeviceStateStore {

	private static final String LOCALHOST = "localhost";
	private static final byte[] REDIS_KEY = "VirtusSensePanstampStore".getBytes(Charsets.US_ASCII);
	
	private static JedisPool pool;
	
	public RedisStore() {
		this.open(LOCALHOST);
	}
	
	public RedisStore(String redisURI) {
		this.open(redisURI);
	}
	
	private void open(String redisURI) {
		
		// force close of previous pool
		if(pool != null && !pool.isClosed()) {
			try {
				this.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		pool = new JedisPool(new JedisPoolConfig(), redisURI);
	}

	@Override
	public boolean hasRegisterValue(Register reg) {
		try (Jedis jedis = pool.getResource()) {
			byte[] tuple = new byte[] {(byte) reg.getDevice().getAddress(), (byte) reg.getId()};
			
			return jedis.hexists(REDIS_KEY, tuple);
		}
	}

	@Override
	public byte[] getRegisterValue(Register reg) {
		if(hasRegisterValue(reg)) {
			try (Jedis jedis = pool.getResource()) {
				byte[] tuple = new byte[] {(byte) reg.getDevice().getAddress(), (byte) reg.getId()};
				return jedis.hget(REDIS_KEY, tuple);
			}
		} else
			return null;
	}

	@Override
	public void setRegisterValue(Register reg, byte[] value) {
		try (Jedis jedis = pool.getResource()) {
			byte[] tuple = new byte[] {(byte) reg.getDevice().getAddress(), (byte) reg.getId()};
			
			jedis.hset(REDIS_KEY, tuple, value);
		}		
	}

	@Override
	public Set<Integer> getStoredAddresses() {
		try (Jedis jedis = pool.getResource()) {
			return 
				jedis
				.hkeys(REDIS_KEY).stream()
				.map(arr -> (int) arr[0])
				.collect(Collectors.toSet());
		}
	}

	@Override
	public void close() {
		pool.close();
		pool.destroy();
	}

}
