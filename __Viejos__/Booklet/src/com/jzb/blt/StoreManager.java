package com.jzb.blt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

public class StoreManager {

    public static final String RECORD_STORE_NAME_MENU  = "Booklet_Menu_RS";
    public static final String RECORD_STORE_NAME_BOOKS = "Booklet_Books_RS";
    public static final String RECORD_STORE_NAME_PWD   = "Booklet_PWD_RS";

    private static byte[]      s_pwdMD5;

    public static char[] byteToChar(byte data[]) throws Exception {
        char buffer[] = new char[data.length];
        for (int n = 0; n < buffer.length; n++) {
            if (data[n] >= 0)
                buffer[n] = (char) data[n];
            else
                buffer[n] = (char) (256 + data[n]);
        }
        return buffer;
    }

    public static byte[] charToByte(char data[]) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length * 2);
        DataOutputStream dos = new DataOutputStream(baos);
        for (int n = 0; n < data.length; n++) {
            dos.writeChar(data[n]);
        }
        dos.close();
        return baos.toByteArray();
    }

    public static void deleteFile(int id) throws Exception {

        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_BOOKS, true);
            rs.deleteRecord(id);
            rs.closeRecordStore();
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static void deleteStorages(boolean deletePWD) throws RecordStoreException {
        deleteRS(RECORD_STORE_NAME_MENU);
        deleteRS(RECORD_STORE_NAME_BOOKS);
        if (deletePWD) {
            deleteRS(RECORD_STORE_NAME_PWD);
            setPWDMD5(null);
        }
    }

    public static byte[] loadFile(int id) throws Exception {

        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_BOOKS, true);
            byte buffer[] = rs.getRecord(id);
            rs.closeRecordStore();

            return _cipher(buffer);
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static byte[] loadMenu() throws Exception {

        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_MENU, true);
            if (rs.getNumRecords() > 0) {
                RecordEnumeration enume = rs.enumerateRecords(null, null, true);
                byte buffer[] = enume.nextRecord();

                rs.closeRecordStore();

                return _cipher(buffer);
            } else
                return null;
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static byte[] loadPWD() throws Exception {

        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_PWD, true);
            if (rs.getNumRecords() > 0) {
                RecordEnumeration enume = rs.enumerateRecords(null, null, true);
                byte buffer[] = enume.nextRecord();

                rs.closeRecordStore();

                return buffer;
            } else
                return null;
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static int saveFile(byte data[]) throws Exception {

        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_BOOKS, true);
            int id = rs.addRecord(_cipher(data), 0, data.length);
            rs.closeRecordStore();
            return id;
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static void saveMenu(byte text[]) throws Exception {
        RecordStore rs = null;
        try {
            deleteRS(RECORD_STORE_NAME_MENU);
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_MENU, true);
            rs.addRecord(_cipher(text), 0, text.length);
            rs.closeRecordStore();
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static void savePWD(byte pwd[]) throws Exception {
        RecordStore rs = null;
        try {
            deleteRS(RECORD_STORE_NAME_PWD);
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_PWD, true);
            rs.addRecord(pwd, 0, pwd.length);
            rs.closeRecordStore();
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    public static void setPWDMD5(byte[] pwdMD5) {
        s_pwdMD5 = pwdMD5;
    }

    public static void updateFile(int index, byte data[]) throws Exception {

        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(RECORD_STORE_NAME_BOOKS, true);
            rs.setRecord(index, _cipher(data), 0, data.length);
            rs.closeRecordStore();
        } catch (Exception ex) {
            throw ex;
        } finally {
            safeCloseRS(rs);
        }
    }

    private static void deleteRS(String name) throws RecordStoreException {
        try {
            RecordStore.deleteRecordStore(name);
        } catch (RecordStoreNotFoundException e) {
        }
    }

    private static void safeCloseRS(RecordStore rs) {
        try {
            if (rs != null)
                rs.closeRecordStore();
        } catch (Exception ex) {
        }
    }

    private StoreManager() {
    }
    
    private static byte[] _cipher(byte buffer[]) {
        int pp=0;
        for(int n=0;n<buffer.length;n++) {
            buffer[n]^=s_pwdMD5[pp++];
            if(pp>=s_pwdMD5.length) pp=0;
        }
        return buffer;
                
    }

}