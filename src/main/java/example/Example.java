package example;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import me.legrange.panstamp.EndpointNotFoundException;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.devicestore.MemoryStore;
import me.legrange.panstamp.devicestore.PersistentMemoryStore;
import me.legrange.panstamp.xml.FileLibrary;

/**
 *
 * @author gideon
 */
public abstract class Example {

    private static final String PORT = "COM6";
    private static final int BAUD = 38400;
    protected Network nw;
    
    protected abstract void doExampleCode(Network nw) throws NetworkException;
    
    protected void run() throws NetworkException, InterruptedException, MalformedURLException {
//        nw = Network.openSerial(PORT, BAUD);
//        nw = Network.create(
//        		PORT, 
//        		BAUD, 
//        		new FileLibrary(new File("src/main/resources/devices")),
//        		new PersistentMemoryStore(new File("resources/store.db")));
        
        nw = Network.create(
        		PORT, 
        		BAUD, 
        		new FileLibrary(new File("src/main/resources/devices")),
        		new MemoryStore());
        
        nw.setDefaultListener(new PanStampListener() {
				
				@Override
				public void syncStateChange(PanStamp dev, int syncState) {
					System.out.println("Cenas tolas 2");
					
				}
				
				@Override
				public void syncRequired(PanStamp dev) {
					System.out.println("Cenas tolas 3");
					
				}
				
				@Override
				public void registerDetected(PanStamp dev, Register reg) {
					if(reg.hasValue()) {
						try {
							System.out.println( Arrays.toString(dev.getMAC()) + " --> " + reg.getName() + " (" + reg.getId() + ")" );
						} catch (NetworkException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if(reg.getId() == 11)
							try {
								System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(reg.getEndpoint("Device UID").getValue().toString().getBytes()));
							} catch (EndpointNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NetworkException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else if(reg.getId() == 12)
							try {
								System.out.println(reg.getEndpoint("Device Password").getValue().toString());
							} catch (EndpointNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NetworkException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else if(reg.getId() == 10)
							try {
								System.out.println("TX --> " + dev.getTxInterval());
							} catch (EndpointNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NetworkException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					} else
						System.out.println("Unkown Data");
					
				}
				
				@Override
				public void productCodeChange(PanStamp dev, int manufacturerId, int productId) {
					System.out.println("Cenas tolas");
					
				}
			});
        
        nw.open();
//        nw.setDeviceStore(new PersistentMemoryStore());
         doExampleCode(nw);
          Thread.sleep(100000);
         nw.close();
         System.exit(0);
    }
   

}
