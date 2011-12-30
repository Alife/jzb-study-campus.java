/**
 * 
 */
package com.jzb.mp3;

import java.io.File;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.swt.util.TabbedTracerImpl;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;
import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class MP3AppWnd {

    private class ProgressMonitor implements IProgressMonitor {

        /**
         * @see com.jzb.swt.util2.tools.IProgressMonitor#processingEnded(boolean)
         */
        public void processingEnded(boolean failed, Object result) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _executionEnded();
                }
            });
        }

    }
    private static final String   APP_NAME       = "MP3Tool";
    private static AppPreferences s_prefs        = new AppPreferences(APP_NAME);
    private Button      m_AcceptButton;
    private ListViewer  m_activeListView;
    private Button      m_btnLoadInfo;
    private Button      m_btnPause;
    private Button      m_btnPlay;
    private Button      m_btnStop;
    private Button      m_DismissButton;
    private boolean     m_dragging   = false;
    private long        m_duration   = 0;
    private long        m_fileLenght = 0;
    private InfoLoader            m_infoLoader   = null;
    private Label       m_lblSongName;
    private List        m_List;

    private List        m_List_2;
    private List        m_List_3;
    private ListViewer  m_lstvwAccepted;
    private ListViewer  m_lstvwDismissed;

    private ListViewer  m_lstvwPending;

    private ProgressMonitor       m_monitor;

    private Shell                 m_Mp3toolShell;

    private BasicPlayer m_player;
    private Scale       m_Scale;
    private TabbedTracerImpl      m_tabbedTracer = new TabbedTracerImpl();
    private Text        m_txtPath;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            s_prefs.load(true);
            MP3AppWnd window = new MP3AppWnd();
            window.open();
            s_prefs.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void _handleSelectionChanged(final SelectionChangedEvent ev) {
        m_activeListView = (ListViewer) ev.getSource();
        if (m_activeListView != m_lstvwPending)
            m_lstvwPending.getList().deselectAll();
        if (m_activeListView != m_lstvwDismissed)
            m_lstvwDismissed.getList().deselectAll();
        if (m_activeListView != m_lstvwAccepted)
            m_lstvwAccepted.getList().deselectAll();

        FileInfo fi = (FileInfo) ((StructuredSelection) m_activeListView.getSelection()).getFirstElement();
        if (fi != null) {
            m_lblSongName.setText(" " + fi.file.getAbsolutePath());
        } else {
            m_lblSongName.setText(" ");
        }
    }

    /**
     * Open the window
     */
    public void open() throws Exception {

        final Display display = Display.getDefault();
        createContents();
        m_Mp3toolShell.open();
        m_Mp3toolShell.layout();

        _setWndPosition();
        _initFields();

        m_Mp3toolShell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                _updatePrefs();
                System.exit(0);
            }
        });

        while (!m_Mp3toolShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_Mp3toolShell = new Shell();

        m_Mp3toolShell.setImage(SWTResourceManager.getImage(MP3AppWnd.class, "/Properties.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_Mp3toolShell.setLayout(borderLayout);
        m_Mp3toolShell.setSize(921, 400);
        m_Mp3toolShell.setMinimumSize(new Point(800, 400));
        m_Mp3toolShell.setText("MP3Tool");

        Composite composite;
        composite = new Composite(m_Mp3toolShell, SWT.NONE);
        composite.setLayoutData(BorderLayout.NORTH);

        m_txtPath = new Text(composite, SWT.BORDER);
        m_txtPath.setBounds(45, 5, 351, 25);

        final Label pathLabel = new Label(composite, SWT.NONE);
        pathLabel.setText("Path:");
        pathLabel.setBounds(10, 10, 41, 15);

        final Label lblX = new Label(composite, SWT.NONE);
        lblX.setBounds(10, 10, 14, 263);

        final Button button = new Button(composite, SWT.NONE);
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _selectFolder(m_txtPath);
            }
        });
        button.setText("...");
        button.setBounds(400, 5, 29, 25);

        m_btnLoadInfo = new Button(composite, SWT.NONE);
        m_btnLoadInfo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _loadInfo();
            }
        });
        m_btnLoadInfo.setText("Load Info");
        m_btnLoadInfo.setBounds(435, 5, 67, 25);

        final Label pendingLabel = new Label(composite, SWT.NONE);
        pendingLabel.setText("Pending:");
        pendingLabel.setBounds(45, 58, 100, 15);

        m_lstvwPending = new ListViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        m_lstvwPending.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(final DoubleClickEvent arg0) {
                _play();
            }
        });
        m_lstvwPending.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(final SelectionChangedEvent ev) {
                _handleSelectionChanged(ev);
            }
        });
        m_List = m_lstvwPending.getList();
        m_List.setBounds(45, 75, 262, 100);

        final Label pendingLabel_1 = new Label(composite, SWT.NONE);
        pendingLabel_1.setBounds(325, 58, 100, 15);
        pendingLabel_1.setText("Accepted:");

        m_lstvwAccepted = new ListViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        m_lstvwAccepted.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(final DoubleClickEvent arg0) {
                _play();
            }
        });
        m_lstvwAccepted.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(final SelectionChangedEvent ev) {
                _handleSelectionChanged(ev);
            }
        });
        m_List_2 = m_lstvwAccepted.getList();
        m_List_2.setBounds(325, 75, 262, 100);

        final Label pendingLabel_2 = new Label(composite, SWT.NONE);
        pendingLabel_2.setBounds(605, 58, 100, 15);
        pendingLabel_2.setText("Dismissed:");

        m_lstvwDismissed = new ListViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        m_lstvwDismissed.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(final DoubleClickEvent arg0) {
                _play();
            }
        });
        m_lstvwDismissed.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(final SelectionChangedEvent ev) {
                _handleSelectionChanged(ev);
            }
        });
        m_List_3 = m_lstvwDismissed.getList();
        m_List_3.setBounds(605, 75, 264, 100);

        m_btnPlay = new Button(composite, SWT.NONE);
        m_btnPlay.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _play();
            }
        });
        m_btnPlay.setText("Play");
        m_btnPlay.setBounds(45, 214, 48, 25);

        m_btnStop = new Button(composite, SWT.NONE);
        m_btnStop.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _stop();
            }
        });
        m_btnStop.setText("Stop");
        m_btnStop.setBounds(99, 214, 48, 25);

        m_btnPause = new Button(composite, SWT.NONE);
        m_btnPause.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _pause();
            }
        });
        m_btnPause.setText("Pause");
        m_btnPause.setBounds(154, 214, 48, 25);

        m_Scale = new Scale(composite, SWT.NONE);
        m_Scale.addMouseListener(new MouseAdapter() {

            public void mouseUp(final MouseEvent e) {
                try {
                    m_player.seek(m_fileLenght * m_Scale.getSelection() / 100);
                } catch (Exception ex) {
                    Tracer._error("Error seeking file", ex);
                }
                m_dragging = false;
            }
        });
        m_Scale.addDragDetectListener(new DragDetectListener() {

            public void dragDetected(final DragDetectEvent ev) {
                m_dragging = true;
            }
        });
        m_Scale.setBounds(210, 205, 170, 42);

        m_AcceptButton = new Button(composite, SWT.NONE);
        m_AcceptButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _accept();
            }
        });
        m_AcceptButton.setText("Accept");
        m_AcceptButton.setBounds(415, 214, 48, 25);

        m_DismissButton = new Button(composite, SWT.NONE);
        m_DismissButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                _dismiss();
            }
        });
        m_DismissButton.setText("Dismiss");
        m_DismissButton.setBounds(478, 214, 48, 25);

        m_lblSongName = new Label(composite, SWT.WRAP | SWT.BORDER);
        m_lblSongName.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        m_lblSongName.setForeground(SWTResourceManager.getColor(128, 128, 128));
        m_lblSongName.setBounds(45, 180, 824, 19);
        final TabFolder m_tabTraces = new TabFolder(m_Mp3toolShell, SWT.NONE);
        m_tabTraces.setLayoutData(BorderLayout.CENTER);

        final Composite composite_1 = new Composite(m_Mp3toolShell, SWT.NONE);
        composite_1.setLayout(new BorderLayout(0, 0));
        composite_1.setLayoutData(BorderLayout.CENTER);

        // ------------------------------------------------------------------------
        // ********** TabbedTracer ***********************************************
        final CTabFolder tabTraces = m_tabbedTracer.createTabFolder(composite_1);
        tabTraces.setLayoutData(BorderLayout.CENTER);
        Tracer.setTracer(m_tabbedTracer);

    }

    private void _accept() {
        if (m_activeListView != m_lstvwAccepted) {
            int index = m_activeListView.getList().getSelectionIndex();
            FileInfo fi = (FileInfo) ((StructuredSelection) m_activeListView.getSelection()).getFirstElement();
            m_activeListView.remove(fi);
            fi.state = FileInfoState.ACCEPTED;
            m_lstvwAccepted.add(fi);
            _setActiveListIndex(index);
            if (m_player.getStatus() == BasicPlayer.PLAYING) {
                _play();
            }

            try {
                m_infoLoader.saveInfo();
            } catch (Throwable th) {
                Tracer._error("Error saving info file", th);
            }

        }
    }

    private void _calcRatio(File mp3File) {

        try {
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(mp3File);

            Object v = audioFileFormat.properties().get("duration");
            if (v != null) {
                if (v instanceof Number) {
                    m_duration = ((Number) v).longValue();
                } else if (v instanceof String) {
                    m_duration = Long.parseLong((String) v);
                } else {
                    m_duration = -1;
                    Tracer._error("Error calculating duration ratio: Property 'duration' doesn't exist");
                    return;
                }
            }
            m_fileLenght = mp3File.length();

        } catch (Exception ex) {
            Tracer._error("Error calculating duration ratio", ex);
        }
    }

    private void _disableButtons() {
        m_btnLoadInfo.setEnabled(false);
    }

    private void _dismiss() {
        if (m_activeListView != m_lstvwDismissed) {
            int index = m_activeListView.getList().getSelectionIndex();
            FileInfo fi = (FileInfo) ((StructuredSelection) m_activeListView.getSelection()).getFirstElement();
            m_activeListView.remove(fi);
            fi.state = FileInfoState.DISMISSED;
            m_lstvwDismissed.add(fi);
            _setActiveListIndex(index);
            if (m_player.getStatus() == BasicPlayer.PLAYING) {
                _play();
            }

            try {
                m_infoLoader.saveInfo();
            } catch (Throwable th) {
                Tracer._error("Error saving info file", th);
            }

        }
    }

    private String _durationToStr() {
        if (m_duration > 0) {
            StringBuilder sb = new StringBuilder();
            long dur = m_duration / 1000000;
            long min = dur / 60;
            long sec = dur - min * 60;
            if (min < 10)
                sb.append('0');
            sb.append(min);
            sb.append(':');
            if (sec < 10)
                sb.append('0');
            sb.append(sec);
            return sb.toString();
        } else {
            return "00:00";
        }
    }

    private void _enableButtons() {
        m_btnLoadInfo.setEnabled(true);
    }

    private void _executionEnded() {
        _enableButtons();
    }

    private void _executionStarted() {
        Tracer.reset();
        _disableButtons();
    }

    private void _initFields() throws Exception {

        m_monitor = new ProgressMonitor();
        m_txtPath.setText(s_prefs.getPref("infoPath", ""));

        BasicPlayerListener listener = new BasicPlayerListener() {

            public void opened(Object stream, Map properties) {
                // System.out.println("opened: " + stream + ", " + properties);
            }

            public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
                long position = (Long) properties.get("mp3.position.microseconds");
                final int pos = (int) (position * 100 / m_duration);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        if (!m_dragging) {
                            m_Scale.setSelection(pos);
                        }
                    }
                });

            }

            public void setController(BasicController controller) {
                // System.out.println("setController: " + controller);
            }

            public void stateUpdated(BasicPlayerEvent event) {
                if (event.getCode() == BasicPlayerEvent.EOM) {
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            _next();
                        }
                    });
                }
            }
        };

        m_player = new BasicPlayer();
        m_player.addBasicPlayerListener(listener);
    }

    private void _loadInfo() {
        m_infoLoader = new InfoLoader(m_txtPath.getText());
        try {
            m_infoLoader.loadInfo();

            LabelProvider fileInfoLabelProvider = new LabelProvider() {

                /**
                 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
                 */
                @Override
                public String getText(Object element) {
                    return ((FileInfo) element).title;
                }
            };

            m_lstvwPending.getList().removeAll();
            m_lstvwPending.setLabelProvider(fileInfoLabelProvider);

            m_lstvwAccepted.getList().removeAll();
            m_lstvwAccepted.setLabelProvider(fileInfoLabelProvider);

            m_lstvwDismissed.getList().removeAll();
            m_lstvwDismissed.setLabelProvider(fileInfoLabelProvider);

            for (FileInfo fi : m_infoLoader.getFileInfo()) {
                switch (fi.state) {
                    case PENDING:
                        m_lstvwPending.add(fi);
                        break;
                    case ACCEPTED:
                        m_lstvwAccepted.add(fi);
                        break;
                    case DISMISSED:
                        m_lstvwDismissed.add(fi);
                        break;
                }
            }

            m_lstvwAccepted.getList().deselectAll();
            m_lstvwDismissed.getList().deselectAll();
            m_activeListView = m_lstvwPending;
            _setActiveListIndex(0);

        } catch (Exception ex) {
            Tracer._error("Error reading files info", ex);
        }
    }

    private void _next() {
        int index = m_activeListView.getList().getSelectionIndex() + 1;
        _setActiveListIndex(index);
        _play();
    }

    private void _pause() {
        try {
            m_player.pause();
        } catch (Exception ex) {
            Tracer._error("Error pausing file", ex);
        }
    }

    private void _play() {
        try {
            if (m_player.getStatus() != BasicPlayer.PAUSED) {
                FileInfo fi = (FileInfo) ((StructuredSelection) m_activeListView.getSelection()).getFirstElement();
                if (fi != null) {
                    _calcRatio(fi.file);
                    m_lblSongName.setText(_durationToStr() + " " + fi.file.getAbsolutePath());
                    m_player.stop();
                    m_player.open(fi.file);
                    m_player.play();
                    m_Scale.setSelection(0);
                }
            } else {
                m_player.resume();
            }
        } catch (Exception ex) {
            Tracer._error("Error playing file", ex);
        }
    }

    private void _selectFolder(Text control) {
        DirectoryDialog dd = new DirectoryDialog(m_Mp3toolShell);
        dd.setFilterPath(control.getText());
        String newValue = dd.open();
        if (newValue != null)
            control.setText(newValue);
    }

    private void _setActiveListIndex(int index) {
        m_activeListView.getList().setSelection(index);
        FileInfo fi = (FileInfo) ((StructuredSelection) m_activeListView.getSelection()).getFirstElement();
        if (fi != null) {
            m_lblSongName.setText(" " + fi.file.getAbsolutePath());
        } else {
            m_lblSongName.setText(" ");
        }
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_Mp3toolShell.setBounds(x, y, w, h);
    }

    private void _stop() {
        try {
            m_player.stop();
            m_Scale.setSelection(0);
        } catch (Exception ex) {
            Tracer._error("Error stopping file", ex);
        }
    }

    private void _updatePrefs() {
        try {
            s_prefs.setPref("infoPath", m_txtPath.getText());

            s_prefs.save();
        } catch (Exception ex) {
        }
    }
}