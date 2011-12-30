package com.JWinAPI;


public class WinManager {

		private static JWinAPI wapi;

		/**
		 * @param args
		 */
		public static void main(String[] args) {
			Logger.setFilename( "demoLog.txt" );			
			wapi = new JWinAPI();
			new WinManagerUI().doUI( wapi );
		}
		
		
		

}
