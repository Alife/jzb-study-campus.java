/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM
 * ./ITextToSpeech.idl
 */

package org.mozilla.interfaces;

public interface ITextToSpeech extends nsISupports {

  String ITEXTTOSPEECH_IID =
    "{6fde3824-7665-11dc-8314-0900200c9a69}";

  void say(String text, boolean queue);

}