/**
 * 
 */
package com.jzb.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class ImgTagsAppWnd {

    private static final String                APP_NAME      = "ImgTags";

    private static Preferences                 s_prefs       = new Preferences(APP_NAME);
    private final static int                   NUM_TAGS      = 12;

    private Button                             m_checkboxs[] = new Button[NUM_TAGS];
    private File                               m_ImgBaseFolder;
    private ArrayList<File>                    m_imgFiles    = new ArrayList<File>();

    private TreeMap<String, ArrayList<String>> m_imgFileTags = new TreeMap<String, ArrayList<String>>();
    private Shell                              m_ImgtagsShell;
    private int                                m_index       = 0;
    private Label                              m_lblImage;
    private File                               m_tagsFile;

    private Text                               m_TagTexts[]  = new Text[NUM_TAGS];
    private Text                               m_txtExtraTags;
    private Label                              m_lblCount;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            ImgTagsAppWnd window = new ImgTagsAppWnd();
            window.open();
            s_prefs.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() throws Exception {

        final Display display = Display.getDefault();
        createContents();

        _setWndPosition();
        _initFields();

        m_ImgtagsShell.open();
        m_ImgtagsShell.layout();

        while (!m_ImgtagsShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_ImgtagsShell = new Shell();
        m_ImgtagsShell.setLayout(new FillLayout());
        m_ImgtagsShell.setImage(SWTResourceManager.getImage(ImgTagsAppWnd.class, "/Tools.ico"));
        m_ImgtagsShell.setSize(808, 501);
        m_ImgtagsShell.setMinimumSize(new Point(800, 400));
        m_ImgtagsShell.setText("ImgTags [<new>]");

        final Menu menu = new Menu(m_ImgtagsShell, SWT.BAR);
        m_ImgtagsShell.setMenuBar(menu);

        final MenuItem nmuTagsFile = new MenuItem(menu, SWT.CASCADE);
        nmuTagsFile.setText("Tags File");

        final Menu menu_1 = new Menu(nmuTagsFile);
        nmuTagsFile.setMenu(menu_1);

        final MenuItem mnuNew = new MenuItem(menu_1, SWT.NONE);
        mnuNew.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _mnu_New();
            }
        });
        mnuNew.setText("New");

        final MenuItem mnuOpen = new MenuItem(menu_1, SWT.NONE);
        mnuOpen.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _mnu_Open();
            }
        });
        mnuOpen.setText("Open...");

        final MenuItem mnuSave = new MenuItem(menu_1, SWT.NONE);
        mnuSave.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _mnu_Save();
            }
        });
        mnuSave.setText("Save");

        final MenuItem mnuSaveAs = new MenuItem(menu_1, SWT.NONE);
        mnuSaveAs.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _mnu_SaveAs();
            }
        });
        mnuSaveAs.setText("Save As...");

        final MenuItem mnuImgFolder = new MenuItem(menu, SWT.CASCADE);
        mnuImgFolder.setText("Img Folder");

        final Menu menu_2 = new Menu(mnuImgFolder);
        mnuImgFolder.setMenu(menu_2);

        final MenuItem mnuChooseFolder = new MenuItem(menu_2, SWT.NONE);
        mnuChooseFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _mnu_ChooseFolder();
            }
        });
        mnuChooseFolder.setText("Choose Folder...");

        final SashForm sashForm = new SashForm(m_ImgtagsShell, SWT.NONE);

        m_lblImage = new Label(sashForm, SWT.CENTER | SWT.BORDER);
        m_lblImage.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(final ControlEvent e) {
                _showImage();
            }
        });

        final TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
        tabFolder.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                if (tabFolder.getSelectionIndex() == 0) {
                    _setCurrentFileTags();
                    _setChkBoxImgTags();
                }
            }
        });

        final TabItem tagsTabItem = new TabItem(tabFolder, SWT.NONE);
        tagsTabItem.setText("Image Tags");

        final TabItem tagsDefinitionTabItem = new TabItem(tabFolder, SWT.NONE);
        tagsDefinitionTabItem.setText("Tags Definition");

        final Composite composite_1 = new Composite(tabFolder, SWT.NONE);
        tagsDefinitionTabItem.setControl(composite_1);

        final Table tagsTable = new Table(composite_1, SWT.CENTER);
        tagsTable.setBounds(0, 0, 425, 280);
        tagsTable.setLinesVisible(true);
        tagsTable.setHeaderVisible(true);

        final TableColumn column1 = new TableColumn(tagsTable, SWT.NONE);
        column1.setWidth(150);
        column1.setText("Tag CheckBox");
        final TableColumn column2 = new TableColumn(tagsTable, SWT.NONE);
        column2.setWidth(300);
        column2.setText("Tag Value");

        final Composite composite = new Composite(tabFolder, SWT.NONE);
        tagsTabItem.setControl(composite);

        final Group tagsGroup = new Group(composite, SWT.NONE);
        tagsGroup.setText("Tags");
        tagsGroup.setBounds(10, 38, 405, 169);

        final Button btnInitial = new Button(composite, SWT.NONE);
        btnInitial.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _moveToIndex(0);
            }
        });
        btnInitial.setBounds(10, 5, 71, 24);
        btnInitial.setText("<<");

        final Button btnPrev = new Button(composite, SWT.NONE);
        btnPrev.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_index > 0)
                    _moveToIndex(m_index - 1);
            }
        });
        btnPrev.setBounds(85, 5, 71, 24);
        btnPrev.setText("<");

        final Button btnNext = new Button(composite, SWT.NONE);
        btnNext.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (m_index < m_imgFiles.size() - 1)
                    _moveToIndex(m_index + 1);
            }
        });
        btnNext.setBounds(265, 5, 71, 24);
        btnNext.setText(">");

        final Button btnFinal = new Button(composite, SWT.NONE);
        btnFinal.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _moveToIndex(m_imgFiles.size() - 1);
            }
        });
        btnFinal.setBounds(342, 5, 71, 24);
        btnFinal.setText(">>");

        final Label extraTagsLabel = new Label(composite, SWT.NONE);
        extraTagsLabel.setText("Extra Tags:");
        extraTagsLabel.setBounds(10, 219, 71, 16);

        m_txtExtraTags = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL | SWT.BORDER);
        m_txtExtraTags.setBounds(85, 215, 330, 91);

        final Label label = new Label(composite, SWT.NONE);
        label.setText("Label");
        label.setBounds(695, 5, 17, 27);

        m_lblCount = new Label(composite, SWT.CENTER);
        m_lblCount.setText("[0 \\ 0]");
        m_lblCount.setBounds(175, 9, 71, 16);
        sashForm.setWeights(new int[] { 364, 433 });

        for (int i = 0; i < NUM_TAGS; i++) {
            TableItem item = new TableItem(tagsTable, SWT.NONE);
            final TableEditor editor = new TableEditor(tagsTable);
            editor.horizontalAlignment = SWT.LEFT;
            editor.grabHorizontal = true;
            m_TagTexts[i] = new Text(tagsTable, SWT.NONE);

            final int index = i;
            m_TagTexts[i].addModifyListener(new ModifyListener() {

                public void modifyText(final ModifyEvent e) {
                    String text = m_TagTexts[index].getText().trim();
                    m_checkboxs[index].setText(text);
                    m_checkboxs[index].setEnabled(text.length() > 0);
                }
            });

            editor.setEditor(m_TagTexts[i], item, 1);
            item.setText(0, "Tag CheckBox-" + i);
        }

        int index = 0;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 2; x++) {
                m_checkboxs[index] = new Button(tagsGroup, SWT.CHECK);
                m_checkboxs[index].setEnabled(false);
                m_checkboxs[index].setText("<undefined>");
                m_checkboxs[index].setBounds(10 + 195 * x, 20 + 24 * y, 190, 16);
                index++;
            }
        }

    }

    private void _deselectAllChecks() {
        for (Button btn : m_checkboxs) {
            if (btn != null)
                btn.setSelection(false);
        }
    }

    private Button _getCheckByName(String name) {
        for (Button btn : m_checkboxs) {
            if (btn.getText().equals(name))
                return btn;
        }
        return null;
    }

    private ArrayList<String> _getChkBoxImgTags() {

        ArrayList<String> tags = new ArrayList<String>();

        for (Button btn : m_checkboxs) {
            if (btn != null && btn.getSelection())
                tags.add(btn.getText());
        }

        if (m_txtExtraTags != null) {
            StringTokenizer st = new StringTokenizer(m_txtExtraTags.getText(), ",");
            while (st.hasMoreTokens()) {
                tags.add(st.nextToken().trim());
            }
        }
        return tags;
    }

    private ArrayList<String> _getCurrentFileTags() {
        String fname = _getJustFName();
        ArrayList<String> tags = m_imgFileTags.get(fname);
        if (tags == null) {
            tags = new ArrayList<String>();
            m_imgFileTags.put(fname, tags);
        }
        return tags;
    }

    private String _getJustFName() {
        if (m_imgFiles.size() == 0)
            return null;

        String name = m_imgFiles.get(m_index).getName();
        int pos = name.lastIndexOf('.');
        if (pos > 0)
            return name.substring(0, pos);
        else
            return name;
    }

    private void _initFields() throws Exception {
    }

    private void _mnu_ChooseFolder() {

        if (m_ImgBaseFolder == null) {
            m_ImgBaseFolder = new File(s_prefs.getPref("lastImgBaseFolder", ""));
        }

        DirectoryDialog dd = new DirectoryDialog(m_ImgtagsShell);
        if (m_ImgBaseFolder != null)
            dd.setFilterPath(m_ImgBaseFolder.getAbsolutePath());
        String newValue = dd.open();
        if (newValue != null) {
            File folder = new File(newValue);
            if (folder.exists()) {
                m_ImgBaseFolder = folder;

                _readImgFiles();
                s_prefs.setPref("lastImgBaseFolder", newValue);
                
                _updateImgInfo();
            }
        }
    }

    private void _mnu_New() {
        m_tagsFile = null;
        m_imgFileTags.clear();
        m_index = 0;
        _setTagsTableValues(null);
        _showImage();
        _updateWndTitle();
    }

    private void _mnu_Open() {

        if (m_tagsFile == null) {
            m_tagsFile = new File(s_prefs.getPref("lastTagsFile", ""));
        }

        FileDialog fd = new FileDialog(m_ImgtagsShell, SWT.OPEN);
        fd.setText("Open Tags txt file");
        if (m_tagsFile != null)
            fd.setFilterPath(m_tagsFile.getAbsolutePath());
        String[] filterExt = { "*.txt", "*.*" };
        fd.setFilterExtensions(filterExt);
        String newValue = fd.open();
        if (newValue != null) {
            File file = new File(newValue);
            if (file.exists()) {
                m_tagsFile = file;

                _readTagsFile();

                _updateWndTitle();

                m_index = 0;
                _updateImgInfo();
                
                s_prefs.setPref("lastTagsFile", newValue);
            }
        }
    }

    private void _mnu_Save() {

        if (m_tagsFile == null) {
            _mnu_SaveAs();
        } else {
            _setCurrentFileTags();
            _writeTagsFile();
            _updateWndTitle();
            s_prefs.setPref("lastTagsFile", m_tagsFile.getAbsolutePath());
        }
    }

    private void _mnu_SaveAs() {

        FileDialog fd = new FileDialog(m_ImgtagsShell, SWT.SAVE);
        fd.setText("Save Tags txt file");
        if (m_tagsFile != null)
            fd.setFilterPath(m_tagsFile.getAbsolutePath());
        String[] filterExt = { "*.txt", "*.*" };
        fd.setFilterExtensions(filterExt);
        String newValue = fd.open();
        if (newValue != null) {
            m_tagsFile = new File(newValue);
            _mnu_Save();
        }
    }

    private void _moveToIndex(int newIndex) {
        if (newIndex == m_index)
            return;

        _setCurrentFileTags();
        m_index = newIndex;
        _updateImgInfo();
    }

    private void _readImgFiles() {

        try {
            m_imgFiles.clear();
            m_index = 0;
            _readImgFiles2(m_ImgBaseFolder);
        } catch (Throwable th) {
            _showErrorMsg("Error reading image files", th);
        }

    }

    private void _updateImgInfo() {
        _showImage();
        _setChkBoxImgTags();
        m_lblCount.setText("[" + m_index + " \\ " + (m_imgFiles.size() - 1) + "]");
    }

    private void _readImgFiles2(File folder) throws Exception {

        // Separado para no mezclar las fotos
        for (File file : folder.listFiles(new ImgFileFilter())) {
            if (file.isFile()) {
                m_imgFiles.add(file);
            }
        }

        for (File file : folder.listFiles(new ImgFileFilter())) {
            if (file.isDirectory()) {
                _readImgFiles2(file);
            }
        }
    }

    private void _readTagsFile() {

        TreeSet<String> tagSet = new TreeSet<String>();

        m_imgFileTags.clear();

        try {
            BufferedReader br = new BufferedReader(new FileReader(m_tagsFile));
            while (br.ready()) {
                String line = br.readLine();
                StringTokenizer st = new StringTokenizer(line, ",");
                if (st.countTokens() > 1) {

                    String key = st.nextToken();
                    ArrayList<String> tags = new ArrayList<String>();
                    m_imgFileTags.put(key, tags);
                    while (st.hasMoreTokens()) {
                        String tag = st.nextToken().trim();
                        tags.add(tag);
                        tagSet.add(tag);
                    }
                }
            }
            br.close();

            _setTagsTableValues(tagSet);

        } catch (Throwable th) {
            _showErrorMsg("Error reading Tags file", th);
        }
    }

    private void _setChkBoxImgTags() {

        _deselectAllChecks();
        if (m_imgFiles.size() == 0)
            return;

        boolean first = true;
        String extraTags = "";
        for (String tag : _getCurrentFileTags()) {
            Button btn = _getCheckByName(tag);
            if (btn != null)
                btn.setSelection(true);
            else {
                if (!first)
                    extraTags += ", ";
                extraTags += tag;
                first = false;
            }
        }

        m_txtExtraTags.setText(extraTags);
    }

    private void _setCurrentFileTags() {
        String fname = _getJustFName();
        if (fname != null) {
            ArrayList<String> tags = _getChkBoxImgTags();
            m_imgFileTags.put(fname, tags);
        }
    }

    private void _setTagsTableValues(TreeSet<String> tagSet) {

        for (Text txt : m_TagTexts) {
            txt.setText("");
        }

        if (tagSet != null) {
            int n = 0;
            for (String tag : tagSet) {
                if (n >= NUM_TAGS)
                    break;
                m_TagTexts[n++].setText(tag);
            }
        }
    }

    private void _showErrorMsg(String msg, Throwable th) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);

        MessageBox mb = new MessageBox(m_ImgtagsShell, 0);
        mb.setText("Error executing application");
        mb.setMessage(msg + "\r\n\r\nStack Trace:\r\n" + sw.toString());
        mb.open();
    }

    private void _showImage() {
        if (m_imgFiles.size() == 0)
            return;
        File imgFile = m_imgFiles.get(m_index);
        ImageData imgData = new ImageData(imgFile.getAbsolutePath());
        int lw = m_lblImage.getBounds().width;
        int lh = m_lblImage.getBounds().height;
        int pw, ph;
        if (lw >= lh) {
            ph = lh;
            pw = (imgData.width * lh) / imgData.height;
        } else {
            pw = lw;
            ph = (imgData.height * lw) / imgData.width;
        }

        imgData = imgData.scaledTo(pw, ph);
        Image img = new Image(Display.getCurrent(), imgData);
        m_lblImage.setImage(img);

    }

    private void _updateWndTitle() {
        if (m_tagsFile == null)
            m_ImgtagsShell.setText("ImgTags [<new>]");
        else
            m_ImgtagsShell.setText("ImgTags [" + m_tagsFile.getAbsolutePath() + "]");
    }

    private void _writeTagsFile() {
        try {
            PrintWriter pw = new PrintWriter(m_tagsFile);
            for (Entry<String, ArrayList<String>> entry : m_imgFileTags.entrySet()) {
                if (entry.getValue().size() == 0)
                    continue;
                pw.print(entry.getKey());
                pw.print(", ");
                boolean first = true;
                for (String tag : entry.getValue()) {
                    if (!first)
                        pw.print(", ");
                    pw.print(tag);
                    first = false;
                }
                pw.println();
            }
            pw.close();
        } catch (Throwable th) {
            _showErrorMsg("Error writing Tags file", th);
        }
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_ImgtagsShell.setBounds(x, y, w, h);
    }
}