/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM
 * ./ICardReader.idl
 */

package org.mozilla.interfaces;

public interface ICardReaderCallBack extends nsISupports {

  String ICARDREADERCALLBACK_IID =
    "{6fde3824-7665-11dc-8314-0800200c9a6d}";

  void call(String js_this, String cardData, ICardReaderError error);

}