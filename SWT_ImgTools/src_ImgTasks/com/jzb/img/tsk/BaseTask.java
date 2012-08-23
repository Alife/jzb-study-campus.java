/**
 * 
 */
package com.jzb.img.tsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public abstract class BaseTask {

    public static enum JustCheck {
        NO, YES
    }

    public static enum RecursiveProcessing {
        NO, YES
    }

    public static class TimeStampShift {

        public int years, months, days, hours, mins, secs;

        public TimeStampShift() {
        }

        public TimeStampShift(int shiftYears, int shiftMonths, int shiftDays, int shiftHours, int shiftMins, int shiftSecs) {
            this.years = shiftYears;
            this.months = shiftMonths;
            this.days = shiftDays;
            this.hours = shiftHours;
            this.mins = shiftMins;
            this.secs = shiftSecs;
        }
    }

    protected static class UndoInfo {

        public File newFile;
        public File origFile;
    }

    protected File                m_baseFolder;
    protected JustCheck           m_justChecking;

    protected NameComposer        m_nc = new NameComposer();
    protected RecursiveProcessing m_recursive;

    private boolean               m_dontUndo;

    private File                  m_undoFile;

    // --------------------------------------------------------------------------------------------------------
    protected BaseTask(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        this(justChecking, baseFolder, recursive, false);
    }

    // --------------------------------------------------------------------------------------------------------
    protected BaseTask(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive, boolean dontUndo) {

        m_justChecking = justChecking;
        m_recursive = recursive;
        m_baseFolder = baseFolder;
        m_dontUndo = dontUndo;
        _resetUndoFile();
    }

    // --------------------------------------------------------------------------------------------------------
    protected void _checkBaseFolder() throws IllegalArgumentException {
        if (m_baseFolder == null || !m_baseFolder.exists()) {
            throw new IllegalArgumentException("Base folder doesn't exist: '" + m_baseFolder + "'");
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected ArrayList<UndoInfo> _getUndoInfo() throws Exception {

        ArrayList<UndoInfo> info = new ArrayList<UndoInfo>();

        File undoFile = new File(m_baseFolder, "lastAction_undo.txt");
        if (undoFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(undoFile));
            while (reader.ready()) {
                String line = reader.readLine();
                String files[] = line.split("-->");
                UndoInfo ui = new UndoInfo();
                ui.origFile = new File(files[0]);
                ui.newFile = new File(files[1]);
                info.add(ui);
            }
            reader.close();
        }
        return info;
    }

    // --------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------
    protected void _renameFile(File oldFile, File newFile) throws Exception {

        if (oldFile.equals(newFile)) {
            Tracer._debug("File     '" + oldFile.getName() + "'     doesn't need to be processed");
            return;
        }

        if (newFile.exists()) {
            Tracer._error("File '" + newFile.getName() + "' already exists");
            return;
        }

        if (!oldFile.canWrite()) {
            Tracer._debug("File '" + oldFile.getName() + "' can't be written (maybe read-only)");
            return;
        }

        if (!newFile.getParentFile().exists()) {
            boolean done = m_justChecking == JustCheck.YES ? false : newFile.getParentFile().mkdirs();
            if (m_justChecking == JustCheck.NO && !done) {
                Tracer._error("Error creating file's folder '" + newFile.getParentFile() + "'");
                return;
            }
        }

        _updateUndoFile(oldFile, newFile);

        boolean done = m_justChecking == JustCheck.YES ? false : oldFile.renameTo(newFile);
        if (m_justChecking == JustCheck.YES || done) {
            if (oldFile.getParentFile().equals(newFile.getParentFile())) {
                Tracer._debug("File processed from     '" + oldFile.getName() + "'     to     '" + newFile.getName() + "'");
            } else {
                Tracer._debug("File processed from     '" + oldFile.getName() + "'     to     '" + newFile.getParentFile().getName() + File.separator + newFile.getName() + "'");
            }
        } else {
            if (oldFile.getParentFile().equals(newFile.getParentFile())) {
                Tracer._error("Error processing from     '" + oldFile.getName() + "'     to     '" + newFile.getName() + "'");
            } else {
                Tracer._error("Error processing from     '" + oldFile.getName() + "'     to     '" + newFile.getParentFile().getName() + File.separator + newFile.getName() + "'");
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    protected void _resetUndoFile() {

        if (m_dontUndo || m_justChecking == JustCheck.YES) {
            return;
        }

        try {
            File undoFile = new File(m_baseFolder, "lastAction_undo.txt");
            PrintWriter pw = new PrintWriter(new FileOutputStream(undoFile, false));
            pw.close();
            m_undoFile = undoFile;
        } catch (Exception ex) {
            Tracer._error("Error reseting Undo file", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected void _updateUndoFile(File origFile, File newFile) throws Exception {

        if (m_dontUndo || m_justChecking == JustCheck.YES) {
            return;
        }

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(m_undoFile, true));
            pw.print(origFile.getAbsolutePath());
            pw.print("-->");
            pw.print(newFile.getAbsolutePath());
            pw.println();
            pw.close();
        } catch (Exception ex) {
            throw new Exception("Error updating Undo file with '" + origFile + "' --> '" + newFile + "'", ex);
        }
    }
}
