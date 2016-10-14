/*
 * Copyright 2015 gideon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package example;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class ListDevices extends Example {
    
    public static void main(String...args) throws Exception {
    	
    	new Thread(()-> {
    		if (Boolean.parseBoolean(System.getenv("RUNNING_IN_ECLIPSE"))) {
	           System.out.println("Click this console and press ENTER to shutdown gracefully.");
	           try {
	               System.in.read();
	           } catch (IOException e) {
	               e.printStackTrace();
	           }
	           System.exit(0);
    		}
    	}).start();
    	
        new ListDevices().run();
        
     // shutdown gracefully if using eclipse
        
    }

    @Override
    protected void doExampleCode(Network nw) throws NetworkException {
//        try {
//        	nw.setDeviceLibrary(new XmlDeviceLibrary(new FileLibrary(new File("devices")), new ClassLoaderLibrary()));
//        	nw.setDeviceLibrary(new XmlD);
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ListDevices.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
//        nw.setDeviceLibrary(new HttpLibrary(new URL("https://raw.githubusercontent.com/panStamp/panstamp/master/devices/")));
        List<PanStamp> devices = nw.getDevices();
        System.out.println("Listing devices");
        for (PanStamp device : devices) {
//        	device.addListener(new PanStampListener() {
//				
//				@Override
//				public void syncStateChange(PanStamp dev, int syncState) {
//					System.out.println("Cenas tolas 2");
//					
//				}
//				
//				@Override
//				public void syncRequired(PanStamp dev) {
//					System.out.println("Cenas tolas 3");
//					
//				}
//				
//				@Override
//				public void registerDetected(PanStamp dev, Register reg) {
//					if(reg.hasValue() && !reg.isStandard())
//						System.out.println(dev.getAddress() + " --> " + reg.getName());
//					else
//						System.out.println("Unkown Data");
//					
//				}
//				
//				@Override
//				public void productCodeChange(PanStamp dev, int manufacturerId, int productId) {
//					System.out.println("Cenas tolas");
//					
//				}
//			});
            System.out.printf("panStamp with address %d is on the network\n", device.getAddress(), device.getName());
        	//if(device.getAddress() == 17) {
        		List<Register> regs = device.getRegisters();
                for (Register register : regs) {
    				System.out.println(register.getId());
    				
	                for(Endpoint e : register.getEndpoints()) {
	                	try {
	                		System.out.println("\t" + register.getId() + " -> " + e.getName() + " : " + e.getValue());
	                	} catch (Exception ex) {
	                		System.out.println("\t" + register.getId() + " -> " + e.getName() + " : Error");
	                	}
	                	
	                }
    			}
                
                //Register r = device.getRegister(12);
                
                
//                final Endpoint<Double> e = r.getEndpoint("Temperature");
//                e.setUnit("F");
//                e.addListener(new EndpointListener<Double>() {
//
//                    @Override
//                    public void valueReceived(Endpoint ep, Double value) {
//                        System.out.printf("Temperature is %.1f%s\n", value, e.getUnit());
//                    }
//                });
        	//}
        }
        
    }

}
