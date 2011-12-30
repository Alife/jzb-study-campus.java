#!/bin/bash

ZIP_FILE=rawBackup.zip


rm -f $ZIP_FILE

#-----------------------------------------------------------------------------------
#----- Contacts Database 

FOLDER=/private/var/mobile/Library/AddressBook
zip -r $ZIP_FILE  $FOLDER/AddressBook.sqlitedb  $FOLDER/AddressBookImages.sqlitedb


#-----------------------------------------------------------------------------------
#----- Messages Database 

FOLDER=/private/var/mobile/Library/SMS
zip -r $ZIP_FILE  $FOLDER/sms.db


#-----------------------------------------------------------------------------------
#----- Calendar

FOLDER=/private/var/mobile/Library/Calendar
zip -r $ZIP_FILE  $FOLDER/Calendar.sqlitedb


#-----------------------------------------------------------------------------------
#----- Notes

FOLDER=/private/var/mobile/Library/Notes
zip -r $ZIP_FILE  $FOLDER/notes.db  $FOLDER/notes.idx


