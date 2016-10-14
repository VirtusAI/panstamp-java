package example;

import java.io.File;
import java.net.MalformedURLException;

import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
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
        nw = Network.create(PORT, BAUD, new FileLibrary(new File("src/main/resources/devices")),
        		new PersistentMemoryStore(new File("resources/store.db")));
        
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
					if(reg.hasValue() && !reg.isStandard())
						System.out.println(dev.getAddress() + " --> " + reg.getName());
					else
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
