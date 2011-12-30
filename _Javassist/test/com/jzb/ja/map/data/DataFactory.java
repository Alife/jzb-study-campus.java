/**
 * 
 */
package com.jzb.ja.map.data;

import java.util.HashMap;

import com.jzb.ja.map.MapData;
import com.jzb.ja.map.TD;


/**
 * @author n63636
 * 
 */
public class DataFactory {

    /**
     * 
     * @return 
     * <dataIn> 
     *    <datoA>30/01/2011</datoA> 
     *    <complex> 
     *      <imp>1000</imp> 
     *      <cur>€</cur> 
     *    </complex> 
     *    <datoB>hello</datoB> 
     *  </dataIn>
     */
    public static HashMap createDataIn() {
        HashMap dataIn = new HashMap();

        dataIn.put("datoA", "30/01/2011");

        HashMap complex = new HashMap();
        complex.put("imp", "1000");
        complex.put("cur", "€");
        dataIn.put("complex", complex);

        dataIn.put("datoB", "hello");

        return dataIn;
    }
    
    
    /**
     * 
     * @return 
     * <dataOut> 
     *    <datoX/> 
     *    <datoY1/> 
     *    <datoY2/> 
     *    <datoZ/> 
     *  </dataOut>
     */
    public static HashMap createDataOut() {
        
        HashMap dataOut = new HashMap();

        dataOut.put("datoX", new TD());
        dataOut.put("datoY1", new TD());
        dataOut.put("datoY2", new TD());
        dataOut.put("datoZ", new TD());

        return dataOut;
    }
    
    public static MapData[] createMapper() {
        MapData[] mapping = new MapData[4];
        
        mapping[0] = new MapData("datoA","datoX",new STDConverter());
        mapping[1] = new MapData("complex.imp","datoY1",new STIConverter());
        mapping[2] = new MapData("complex.cur","datoY2",new NullConverter());
        mapping[3] = new MapData("datoB","datoZ",new NullConverter());
        
        return mapping;
    }
}
