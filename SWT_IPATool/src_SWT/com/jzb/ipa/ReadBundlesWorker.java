/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.io.FileInputStream;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.jzb.futil.FileExtFilter;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class ReadBundlesWorker extends BaseWorker {

    private static final int NUM_COLS = 4;

    public ReadBundlesWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void readBundles(final String baseFolderStr, final Composite compBundleList) {

        try {
            File baseFolder = baseFolderStr == null ? null : new File(baseFolderStr);
            if (baseFolder != null && !baseFolder.isDirectory())
                throw new Exception("Indicated folder is not correct: '" + baseFolder + "'");

            Tracer._info("** Reading bundles list from folder: " + baseFolderStr);
            Tracer._info("");
            _readBundles(new File(baseFolderStr), compBundleList);
            Tracer._info("");
            Tracer._info("** Bundles reading done.");

            m_monitor.processingEnded(false, null);

        } catch (Throwable th) {
            Tracer._error("Error in processing execution", th);
            m_monitor.processingEnded(true, null);
        }

    }

    private Point _createEntry(File ipaFile, final int index, final Composite compBundleList) throws Exception {

        int x = 10 + 250 * (index % NUM_COLS);
        int y = 50 + 90 * (index / NUM_COLS);
        int x2 = x + 250;
        int y2 = y + 90;

        Image img = _loadIPAImg(ipaFile);

        Label lblImg = new Label(compBundleList, SWT.BORDER);
        lblImg.setBounds(x, y, 80, 80);
        lblImg.setText("");
        if (img != null)
            lblImg.setBackgroundImage(img);

        Button chkIPAFile = new Button(compBundleList, SWT.CHECK);
        chkIPAFile.setBounds(x + 90, y + 40, 140, 17);
        chkIPAFile.setSelection(false);
        chkIPAFile.setText(_getBNameElement(ipaFile.getName(), "N"));
        chkIPAFile.setData(ipaFile);

        return new Point(x2, y2);
    }

    private String _getBNameElement(String name, String part) {

        String startStr = "_" + part + "[";
        int p1 = name.indexOf(startStr);
        if (p1 < 0)
            return null;

        int p2 = name.indexOf("]", p1);
        if (p2 < 0)
            return null;

        return name.substring(p1 + startStr.length(), p2);
    }

    private Image _loadIPAImg(File ipaFile) {

        try {
            String imgFName = ipaFile.getAbsolutePath();
            imgFName = imgFName.substring(0, imgFName.length() - 3) + "jpg";

            File imgFile = new File(imgFName);
            if (!imgFile.exists())
                return null;

            Display display = Display.getCurrent();
            FileInputStream fis = new FileInputStream(imgFile);
            ImageData data = new ImageData(fis).scaledTo(80, 80);
            fis.close();
            if (data.transparentPixel > 0) {
                return new Image(display, data, data.getTransparencyMask());
            }
            return new Image(display, data);

        } catch (Throwable th) {
            Tracer._warn("Error reading IPA image for: " + ipaFile, th);
            return null;
        }

    }

    private void _readBundles(final File baseFolder, final Composite compBundleList) throws Exception {

        int mx = 0, my = 0;

        _realeasePrevList(compBundleList);

        TreeSet<File> sortedFiles = new TreeSet<File>();
        for (File ipaFile : baseFolder.listFiles(new FileExtFilter(false, "ipa"))) {
            sortedFiles.add(ipaFile);
        }

        int index = 0;
        for (File ipaFile : sortedFiles) {
            Point p = _createEntry(ipaFile, index, compBundleList);
            mx = p.x > mx ? p.x : mx;
            my = p.y > my ? p.y : my;
            index++;
        }

        ((ScrolledComposite) compBundleList.getParent()).setMinSize(mx, my);

    }

    private void _realeasePrevList(final Composite compBundleList) {
        Control clist[] = compBundleList.getChildren();
        if (clist != null) {
            for (Control c : clist) {
                Image img = c.getBackgroundImage();
                if (img != null)
                    img.dispose();
                if ((c.getStyle() & SWT.CHECK) != 0 || c instanceof Label)
                    c.dispose();
            }
        }
        compBundleList.update();
    }
}
