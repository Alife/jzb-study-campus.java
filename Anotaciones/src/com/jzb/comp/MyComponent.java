/**
 * 
 */
package com.jzb.comp;

import com.jzb.an.MSFComponent;
import com.jzb.an.MSFEvent;
import com.jzb.an.MSFField;
import com.jzb.an.MSFMethod;

/**
 * @author n63636
 * 
 */

//@formatter:off
@MSFComponent(
        name = "uno", 
        namespace = "p1.p2",
        deprecated = false,
        events = {
                  @MSFEvent(name="ev1"),
                  @MSFEvent(name="ev2", data="com.data.CtxBean1")
        },
        documentation =
          "Era se una vez un documento <b>super chulo</b> que ven�a a decir de qu� iba este componente<br>" + 
          "Y adem�s era bastante extenso.<br>" +
          "Haciendo que la clase pesase un mont�n"
)
//@formatter:on
public class MyComponent {

    //@formatter:off
    @MSFField(
        documentation = 
          "Este m�todo es un ejemplo claro de c�mo podr�a documentarse." +
          "Utilizando un mont�n de l�neas de texto"
    )
    //@formatter:on
    private String m_myStr;

    //@formatter:off
    @MSFMethod(
        name = "myCalculation",
        documentation = 
          "Este m�todo es un ejemplo claro de c�mo podr�a documentarse." +
          "Utilizando un mont�n de l�neas de texto"
    )
    //@formatter:on
    public void calc() {
        System.out.println("Hola");
    }

    //@formatter:off
    @MSFField(
        name = "Field2",
        deprecated = true,
        documentation = 
          "Este m�todo es un ejemplo claro de c�mo podr�a documentarse." +
          "Utilizando un mont�n de l�neas de texto"
    )
    //@formatter:on
    public String getValue() {
        return m_myStr;
    }
}
