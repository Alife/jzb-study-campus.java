/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM
 * ./ICardReader.idl
 */

package org.mozilla.interfaces;

public interface ICardReader extends nsISupports {

  String ICARDREADER_IID =
    "{6fde3824-7665-11dc-8314-0800200c9a69}";

  boolean isReady();

  void readCard(ICardReaderCallBack callback, String cardCode);

}