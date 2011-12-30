/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

import com.jzb.ipa.ssh.IInstMonitor;
import com.jzb.ipa.ssh.SSHIPAInstaller;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class InstallBundlesWorker extends BaseWorker {

    public static class T_BundleInstallInfo {

        public Button chkFeedback;
        public File   ipaFile;
    }

    private static enum E_INST_STATUS {
        BUNDLE_INSTALLING, PROCESS_BEGUN, PROCESS_DONE, PROCESS_FAILED, SENDING_BUNDLE
    }

    public InstallBundlesWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void installBundles(final ArrayList<T_BundleInstallInfo> chkList) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("** Installing selected IPA files");
                installBundles_(chkList);
                Tracer._info("** Installation of selected IPA files done.");
                return null;
            }
        };

        _makeCall(callable);
    }

    private Image _createSendProgressImg(int percentage) {

        if (percentage < 0 || percentage >= 100) {
            return null;
        }

        int px = 140 * percentage / 100;

        Image img = new Image(Display.getDefault(), 140, 17);
        GC gc = new GC(img);

        Color c1;
        c1 = new Color(Display.getDefault(), 38, 83, 166);
        gc.setBackground(c1);
        gc.fillRectangle(0, 0, 140, 17);
        c1.dispose();

        Color c2;
        c2 = new Color(Display.getDefault(), 186, 205, 239);
        gc.setBackground(c2);
        gc.fillRectangle(px, 0, 140, 17);
        c2.dispose();

        gc.dispose();

        return img;
    }

    private Image _createStatusImg(final E_INST_STATUS status, final int percentage) {

        Color c1 = null;

        switch (status) {
            case SENDING_BUNDLE:
                return _createSendProgressImg(percentage);

            case PROCESS_BEGUN:
            case PROCESS_DONE:
                return null;

            case PROCESS_FAILED:
                c1 = new Color(Display.getDefault(), 255, 72, 72);
                break;

            case BUNDLE_INSTALLING:
                c1 = new Color(Display.getDefault(), 185, 255, 185);
                break;
        }

        Image img = new Image(Display.getDefault(), 140, 17);
        GC gc = new GC(img);
        gc.setBackground(c1);
        gc.fillRectangle(0, 0, 140, 17);
        c1.dispose();

        gc.dispose();

        return img;
    }

    @SuppressWarnings("synthetic-access")
    private void _processBundle(final SSHIPAInstaller ipaInst, final T_BundleInstallInfo info) throws Exception {

        IInstMonitor instMonitor = new IInstMonitor() {

            public void installingBundle() {
                _updateStatuProgress(info.chkFeedback, false, E_INST_STATUS.BUNDLE_INSTALLING, 0);
            }

            public void processBegin() {
                _updateStatuProgress(info.chkFeedback, false, E_INST_STATUS.PROCESS_BEGUN, 0);
            }

            public void processEnd(boolean failed) {
                E_INST_STATUS status = failed ? E_INST_STATUS.PROCESS_FAILED : E_INST_STATUS.PROCESS_DONE;
                _updateStatuProgress(info.chkFeedback, !failed, status, 0);
            }

            public void sendFileBegin() {
                // Nothing
            }

            public void sendFileEnd() {
                // Nothing
            }

            public void sendFileProgress(int percentage) {
                _updateStatuProgress(info.chkFeedback, false, E_INST_STATUS.SENDING_BUNDLE, percentage);
            }
        };

        ipaInst.installIPABundle(info.ipaFile, instMonitor);

    };

    private void _updateStatuProgress(final Button btn, final boolean forceDeselect, final E_INST_STATUS status, final int percentage) {

        final Image img = _createStatusImg(status, percentage);

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                Image bgImg = btn.getBackgroundImage();
                if (bgImg != null)
                    bgImg.dispose();
                btn.setBackgroundImage(img);
                if (forceDeselect)
                    btn.setSelection(false);
            }
        });
    }

    private void installBundles_(final ArrayList<T_BundleInstallInfo> chkList) throws Exception {

        SSHIPAInstaller ipaInst = new SSHIPAInstaller();

        ipaInst.connect("127.0.0.1", Des3Encrypter.decryptStr("i84ommBJaBQ="), Des3Encrypter.decryptStr("WvO3H3yRW2nPsNpQjcGPFA=="));

        for (T_BundleInstallInfo info : chkList) {
            _processBundle(ipaInst, info);
        }

        ipaInst.resetSpringBoard();
        ipaInst.disconnect();
    }
}
