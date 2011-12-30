			secureSendToJava = function( id ){
				var response = "";
				try{ 
					//netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect"); 

					var componentClass = Components.classes['@com.isb.mira.atm/MiraXPCOM;1'];
					var MiraXPCOM = componentClass.createInstance(Components.interfaces.IMiraXPCOMInterface);
					if (MiraXPCOM == null){
						alert("MiraXPCOM is null");
						return; 
					}
					
					if( id == "enable" )
						response = MiraXPCOM.enableButton("aa");
					else if (id == "disable")
						response = MiraXPCOM.disableButton("bb");
					else if( id == "print" )
						response = MiraXPCOM.print("cc");
					
				} catch(e) { 
					alert(e); 
				}
				return response;