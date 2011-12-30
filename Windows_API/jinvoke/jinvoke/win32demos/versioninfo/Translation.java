// Translation.java
// Gayathri Singh, March 2008, gayathri@byteblend.com

/* Struct used by VersionInfo class to obtain the language and code-page ID
 */
package jinvoke.win32demos.versioninfo;

import com.jinvoke.NativeStruct;

@NativeStruct
public class Translation {
	public short languageID;
	public short codepageID;
}
