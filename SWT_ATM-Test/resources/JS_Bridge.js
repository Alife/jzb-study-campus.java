  
// ========================================================================================
// Objeto que nos permite interactuar con la arquitectura cliente y presta servicios 
// de arquitectura, al nivel de JS, a la página
function ATMBridge() {
    
    this.xpcom_bridge = null;
    
    try {    
        // Solicita los permisos adecuados
        netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
                
        // Consigue una referencia al componente XPCOM del lado Java de la arq. Cliente
        var componentClass = Components.classes['@com.jzb.atm/XPCOMBridge;1'];
        this.xpcom_bridge = componentClass.createInstance(Components.interfaces.IXPCOM_ATMBridge);
        
        // Comprueba que existe
        if (this.xpcom_bridge == null){
            alert("XPCOMBridge is null!!");
            // Aquí habría que "terminar" la página puesto que no se puede hacer nada.
        }
        
        // Aquí se podría hacer algo en función de la versión (mínima requerida, etc).
        var v=this.xpcom_bridge.version;
        //alert("XPCOMBridge version = "+v);
        
    }
    catch(e) {
        // Aquí habría que "terminar" la página. Cómo??!??!?!?!?
        alert("XPCOM connection Error: name="+e.name + ", msg=" + e.message);
    }
}


// ------------------------------------------------------------------------------------
// Se opta por una función "getter" genérica (por ID) para "envolver" todos los objetos
// retornados (e incluso esta misma función) y añadir la petición del "UniversalXPConnect"
ATMBridge.prototype.getAPI = function(API_Name, dontThrowException) {
    
    try {
        // Solicita los permisos adecuados
        netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');    

        // Se solicita el API indicado y se le obliga a hacer un "casting" para
        // que no sea percibido como un nsISupports por el JS
        var api=this.xpcom_bridge.getAPI(API_Name);
        if(api!=null) {
            api.QueryInterface(Components.interfaces[API_Name]);
            return this.wrapObject(api); 
        } else { 
            throw new Error("Requested API doesn't exist");
        }
    }
    catch(e) {
        if(dontThrowException==true) {
            return null;
        }
        else {
            // Aquí habría que "terminar" la página
            alert("getAPI Error: name="+e.name + ", msg=" + e.message);
            throw e;
        }
    }
}  
// ------------------------------------------------------------------------------------
// Hace falta, no se muy bien por qué, crear el "delegate" en una función a parte para
// que los parámetros lleguen bien al destino y con el último valor del bucle

// NOTA: Se está llamando esta función de forma recursiva para crear delegates
//       de los parámetros que a su vez son de tipo función. Y, en ese caso,
//       se están generando wrappers de los XPCOM object que aparezcan en sus
//       parámetros de llamada. TAL VEZ, exista una forma más eficiente. Porque
//       me da la impresión de se están creando varias capas de wrappers
ATMBridge.prototype.createDelegate = function(self, obj, method, methodName) {

    //alert("createDelegate: "+methodName);

    return function() {

        // Solicita los permisos adecuados para el objeto "envuelto"
        netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
        
        // Crea "delegates" para los argumentos de tipo función puesto que, posiblemente, sean callBacks
        // y "envuelve" aquellos elementos que sean de tipo XPCOM object
        for(var n=0;n<arguments.length;n++) {
            if(typeof(arguments[n])=="function") {
                arguments[n] = self.createDelegate(self, null, arguments[n], "arguments[n]"); 
            }
            else if(arguments[n] instanceof Components.interfaces.nsISupports) {
                arguments[n] = self.wrapObject(arguments[n]); 
            }
        }

        // Delega la llamada
        var result = method.apply(obj,arguments);
        
        // En el caso de resultados no "nativos" (XPCOM objects) lo tiene que envolver
        if(result instanceof Components.interfaces.nsISupports) {
            result = self.wrapObject(result); 
        }
        return result;
    }
    
}
// ------------------------------------------------------------------------------------
// Todos los objetos XPCON son "envueltos" antes de ser retornados al llamante para
// añadir la petición del "UniversalXPConnect"
ATMBridge.prototype.wrapObject = function(obj) {

    // Crea un wrapper
    var wrapper = new Object();
    
    // Itera todos los métodos del objeto para crear su equivalente
    for(methodName in obj) {
        if(typeof(obj[methodName])=="function") {
            var func = this.createDelegate(this, obj, obj[methodName], methodName);
            wrapper[methodName] = func; 
        }
        else {
            // OJO: Esto se llevaría una copia del valor de la propiedad (read-only) no se
            //      puede hacer un "delegate" de esto.
            wrapper[methodName] = obj[methodName];
        }
    }
    
    return wrapper;
}

// ------------------------------------------------------------------------------------
ATMBridge.prototype.version = "1.2.3";
var theATMBridge = new ATMBridge();
// ========================================================================================


// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************


// ========================================================================================
function fireEvent(obj,evt){
	
	var fireOnThis = obj;
	if( document.createEvent ) {
	  var evObj = document.createEvent('MouseEvents');
	  evObj.initEvent( evt, true, false );
	  fireOnThis.dispatchEvent(evObj);
	} else if( document.createEventObject ) {
	  fireOnThis.fireEvent('on'+evt);
	}
}
// ------------------------------------------------------------------------------------
function html_say(textToSay, queue) {
    try{
        var tts = theATMBridge.getAPI("ITextToSpeech");
        tts.say(textToSay, queue);
    }
    catch(e) {
        alert(e);
    }
}      
// ------------------------------------------------------------------------------------
String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number) { 
    return typeof args[number] != 'undefined'
      ? args[number]
      : match
    ;
  });
};
// ========================================================================================


// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************


// ========================================================================================
function AccessibilityController() {
    this.components = new Array();
    this.lastDigit = null;
    this.repeatKeyCode = null;
    this.repeatAllLocution = null;
}
// ------------------------------------------------------------------------------------
AccessibilityController.prototype.activate = function(repeatKeyCode, repeatAllLocution){

    this.repeatKeyCode = repeatKeyCode;

    if(repeatAllLocution!=null) {
        if(repeatKeyCode!=null)
            this.repeatAllLocution = repeatAllLocution.format(repeatKeyCode);
        else
            this.repeatAllLocution = repeatAllLocution.format("");
    }
        
    this.sayAllLocutions();
}
// ------------------------------------------------------------------------------------
AccessibilityController.prototype.addComponent = function(index, accessibleComponent){
    
    if(this.components[index]!=null)
        alert("Warning - AccessibilityController, there is already a component at index: "+index);
        
    this.components[index] = accessibleComponent;
}
// ------------------------------------------------------------------------------------
AccessibilityController.prototype.repeatAllAgain = function(){
    html_say("",false);
    this.sayAllLocutions();
}
// ------------------------------------------------------------------------------------
AccessibilityController.prototype.sayAllLocutions = function(){
    for(var n=0;n<this.components.length;n++) {
        var component = this.components[n];
        // El sistema de array puede dejar "huecos"
        if(component!=null) 
            component.sayLocution();
    }
    if(this.repeatAllLocution!=null) {
        html_say(this.repeatAllLocution, true);
    }
}
// ------------------------------------------------------------------------------------
AccessibilityController.prototype.onkeydown = function(e){
    e = e || window.event;
    var keynum = e.keyCode || e.which;
    keychar = String.fromCharCode(keynum);
    
    if(keynum==8) {
        this.iterateForKeyCode("clear");
    }
    else if(keynum==13) {
        this.iterateForKeyCode("enter");
    }
    else if(keynum==27) {
        this.iterateForKeyCode("cancel");
    }
    else if(keynum>=48 && keynum<=57) {
        
        // Try first with ONE-DIGIT keycodes. If not handled, tries with TWO-DIGITS keycodes
        if(this.iterateForKeyCode(keychar)) {
            this.lastDigit = null;
        }
        else {
            if(this.lastDigit==null) {
                this.lastDigit = keychar;
            }
            else {
                var accessibleKeyCode = this.lastDigit + keychar;
                this.lastDigit = null;
                this.iterateForKeyCode(accessibleKeyCode);
            }
        }
    }
     
}
// ------------------------------------------------------------------------------------
AccessibilityController.prototype.iterateForKeyCode = function(keycode){
    
    var handled = false;
    
    // It's handled here the "99" keycode
    if(keycode==this.repeatKeyCode) {
        this.repeatAllAgain();
    }
    else{
        for(var n=0;n<this.components.length;n++) {
            var component = this.components[n];
            // El sistema de array puede dejar "huecos"
            if(component!=null) {
                if((handled=component.processKeycode(keycode))) {
                    break;
                }
            }
        }
    }
    
    return handled;
}
// ------------------------------------------------------------------------------------

// ------------------------------------------------------------------------------------
var theAccController = new AccessibilityController();
// ========================================================================================


// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************
// ************************************************************************************************************************************************



// ========================================================================================
// AccesibleButtonPanel
// ========================================================================================
function AccesibleButtonPanel(locIndex, optionsLocution) {

    this.optionsLocution = optionsLocution;
    this.locution2 = "";
    this.components = new Array();
    
    theAccController.addComponent(locIndex, this);
}
// ------------------------------------------------------------------------------------
AccesibleButtonPanel.prototype.addComponent = function(index, accessibleComponent){

    if(this.components[index]!=null)
        alert("Warning - AccesibleButtonPanel, there is already a component at index: "+index);
        
    this.components[index] = accessibleComponent;

    // Solo indica el numero de opciones si hay mas de 2
    if(this.components.length>2 && this.optionsLocution!=null) {
        this.locution2 = this.optionsLocution.format(this.components.length);
    }
    
}
// ------------------------------------------------------------------------------------
AccesibleButtonPanel.prototype.processKeycode = function(keycode) {

    for(var n=0;n<this.components.length;n++) {
        var component = this.components[n];
        if(component.processKeycode(keycode)) {
            return true;
        }
    }
    return false;
}
// ------------------------------------------------------------------------------------
AccesibleButtonPanel.prototype.sayLocution = function() {

    html_say(this.locution2,true);
    
    for(var n=0;n<this.components.length;n++) {
        var component = this.components[n];
        component.sayLocution();
    }
}
// ========================================================================================



// ========================================================================================
// AccesibleButton 
// ========================================================================================
function AccesibleButton(parentPanel, locIndex, elementID, keycode, locutionData, extraLocutionData, instructionsLocution, selectedLocution) {


    if(extraLocutionData==null) {
        extraLocutionData = "";
    }

    if(instructionsLocution==null) {
        instructionsLocution = "For {1}, press {0}. {2}";
    }
    
    if(selectedLocution==null) {
        selectedLocution = "Option {0} selected: {1}";
    }
    
    this.elementID = elementID;
    this.keycode = keycode;

    if(locutionData==null) {
        this.locution1 = "";
        this.locution2 = "";
    } else {
        var kcl = getKeycodeLocution(keycode);
        this.locution1 = instructionsLocution.format(kcl, locutionData, extraLocutionData);
        this.locution2 = selectedLocution.format(kcl, locutionData, extraLocutionData);
    }
    
    if(parentPanel!=null)
        parentPanel.addComponent(locIndex, this);
    else
        theAccController.addComponent(locIndex, this);
}
// ------------------------------------------------------------------------------------
function getKeycodeLocution(keycode) {
    if(keycode[0]=='0') {
        return keycode[0] + " " + keycode[1];
    }
    else {
        return keycode;
    }
}
// ------------------------------------------------------------------------------------
AccesibleButton.prototype.sayLocution = function() {
    html_say(this.locution1,true);
}
// ------------------------------------------------------------------------------------
AccesibleButton.prototype.processKeycode = function(keycode) {

    if(keycode != this.keycode) 
        return false;

    html_say(this.locution2,false);

    var element = document.getElementById(this.elementID);
    fireEvent(element,"click");
    
    var new_location = element.href;
    if(new_location!=null) {
        window.location = new_location;
    } 

    return true;
    
}
// ========================================================================================


 
// ========================================================================================
// AccesibleLabel
// ========================================================================================
function AccesibleLabel(locIndex, locution) {
    this.locution = locution;
    
    theAccController.addComponent(locIndex, this);
}
// ------------------------------------------------------------------------------------
AccesibleLabel.prototype.processKeycode = function() {
    return false;
}
// ------------------------------------------------------------------------------------
AccesibleLabel.prototype.sayLocution = function() {
    html_say(this.locution,true);
}
// ========================================================================================



// ========================================================================================
// AccesibleInputPIN
// ========================================================================================
function AccesibleInputPIN(locIndex) {
    
    theAccController.addComponent(locIndex, this);
}
// ------------------------------------------------------------------------------------
AccesibleInputPIN.prototype.processKeycode = function(keycode) {
    
    if(keycode.length!=1) {
        // "clear", "enter", "cancel", ...
        return false;
    }
    else {
        // "0"..."9"
        html_say("bip",false);
        return true;
    }
    
}
// ------------------------------------------------------------------------------------
AccesibleInputPIN.prototype.sayLocution = function() {
    // Nothing
}
// ========================================================================================
