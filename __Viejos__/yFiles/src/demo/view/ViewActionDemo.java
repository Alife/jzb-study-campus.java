/****************************************************************************
 **
 ** This file is part of yFiles-2.5. 
 ** 
 ** yWorks proprietary/confidential. Use is subject to license terms.
 **
 ** Redistribution of this file or of an unauthorized byte-code version
 ** of this file is strictly forbidden.
 **
 ** Copyright (c) 2000-2007 by yWorks GmbH, Vor dem Kreuzberg 28, 
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ***************************************************************************/
package demo.view;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import y.base.DataProvider;
import y.io.GMLIOHandler;
import y.io.YGFIOHandler;
import y.option.OptionHandler;
import y.util.D;
import y.view.AreaZoomMode;
import y.view.EditMode;
import y.view.Graph2DPrinter;
import y.view.Graph2DView;
import y.view.Graph2DViewActions;
import y.view.Graph2DViewMouseWheelZoomListener;
import y.view.Selections;
import y.view.ViewMode;

/**
 *  Demonstrates basic usage of the Graph2DView.
 *
 *  Demonstrates how some actions can be performed on the view.
 *  The actions are:
 *  <ul>
 *    <li>Remove selected parts of the view content</li>
 *    <li>Zoom out of the view</li>
 *    <li>Zoom in on the view</li>
 *    <li>Zoom to the selected parts of the view content</li>
 *    <li>Fit view content to the size of the the view</li>
 *    <li>Print a graph</li>
 *    <li>Load a graph in GML and YGF format</li>
 *    <li>Save a graph in GML and YGF format</li>
 *  </ul>
 *
 *  Additionally this demo shows how to set up the default edit mode
 *  to display tool tips over nodes.
 *
 */
public class ViewActionDemo extends JPanel {

  /**
   * The view component of this demo.
   */
  protected Graph2DView view;
  /**
   * The view mode to be used with the view.
   */
  protected EditMode    editMode;


  public ViewActionDemo() {
    setLayout(new BorderLayout());

    view = new Graph2DView();
    view.setAntialiasedPainting(true);
    view.getCanvasComponent().addMouseWheelListener( new Graph2DViewMouseWheelZoomListener() );

    editMode = createEditMode();
    if (editMode != null) {
      view.addViewMode(editMode);
    }

    Graph2DViewActions actions = new Graph2DViewActions(view);
    ActionMap amap = actions.createActionMap();
    amap.remove(Graph2DViewActions.DELETE_SELECTION);
    InputMap imap = actions.createDefaultInputMap(amap);
    view.getCanvasComponent().setActionMap(amap);
    view.getCanvasComponent().setInputMap(JComponent.WHEN_FOCUSED, imap);

    add(view, BorderLayout.CENTER);
    add(createToolBar(), BorderLayout.NORTH);
  }

  protected EditMode createEditMode() {
    final EditMode editMode = new EditMode();
    editMode.showNodeTips(true);
    return editMode;
  }

  /**
   * Creates a toolbar for this demo.
   */
  protected JToolBar createToolBar()
  {
    JToolBar bar = new JToolBar();
    bar.add( new AbstractAction( "Clear",new ImageIcon( ClassLoader.getSystemResource( "demo/view/resource/New16.gif" ) ) ) {
      public void actionPerformed( ActionEvent e ) {
        view.getGraph2D().clear();
        view.updateView();
      }
    });
    bar.add( new DeleteSelection() );
    bar.add(new Zoom(1.2));
    bar.add(new Zoom(0.8));
    bar.add(new ZoomArea());
    bar.add(new FitContent());

    return bar;
  }

  /**
   * Create a menu bar for this demo.
   */
  protected JMenuBar createMenuBar()
  {
    JMenuBar bar = new JMenuBar();
    JMenu menu = new JMenu("File");
    menu.add(createLoadAction());
    menu.add(createSaveAction());
    menu.add(new SaveSubsetAction());
    menu.addSeparator();
    menu.add(new PrintAction());
    menu.addSeparator();
    menu.add(new ExitAction());
    bar.add(menu);
    return bar;
  }

  protected Action createLoadAction()
  {
    return new LoadAction();
  }

  protected Action createSaveAction()
  {
    return new SaveAction();
  }

  /**
   * Creates an application  frame for this demo
   * and displays it. The given string is the title of
   * the displayed frame.
   */
  public void start( final String title )
  {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    addContentTo(frame.getRootPane());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public final void addContentTo( final JRootPane rootPane )
  {
    rootPane.setJMenuBar(createMenuBar());
    rootPane.setContentPane(this);
  }

  /**
   * Initializes to a "nice" look and feel.
   */
  public static void initLnF(){
    try
    {
      if(!UIManager.getSystemLookAndFeelClassName().equals(
        "com.sun.java.swing.plaf.motif.MotifLookAndFeel") &&
        !UIManager.getSystemLookAndFeelClassName().equals(
        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel") &&
         !UIManager.getSystemLookAndFeelClassName().equals(
           UIManager.getLookAndFeel().getClass().getName()))
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Launches this demo.
   */
  public static void main(String args[])
  {
    initLnF();
    ViewActionDemo demo = new ViewActionDemo();
    demo.start(demo.getClass().getName());
  }


  /**
   * Action that prints the contents of the view
   */
  protected class PrintAction extends AbstractAction
  {
    PageFormat pageFormat;
    OptionHandler printOptions;

    public PrintAction()
    {
      super("Print");

      //setup option handler
      printOptions = new OptionHandler("Print Options");
      printOptions.addInt("Poster Rows",1);
      printOptions.addInt("Poster Columns",1);
      printOptions.addBool("Add Poster Coords",false);
      final String[] area = {"View","Graph"};
      printOptions.addEnum("Clip Area",area,1);
    }

    public void actionPerformed( ActionEvent e)
    {
      Graph2DPrinter gprinter = new Graph2DPrinter(view);

      //show custom print dialog and adopt values
      if(!printOptions.showEditor()) return;
      gprinter.setPosterRows(printOptions.getInt("Poster Rows"));
      gprinter.setPosterColumns(printOptions.getInt("Poster Columns"));
      gprinter.setPrintPosterCoords(
        printOptions.getBool("Add Poster Coords"));
      if(printOptions.get("Clip Area").equals("Graph"))
        gprinter.setClipType(Graph2DPrinter.CLIP_GRAPH);
      else
        gprinter.setClipType(Graph2DPrinter.CLIP_VIEW);

      //show default print dialogs
      PrinterJob printJob = PrinterJob.getPrinterJob();
      if(pageFormat == null) pageFormat = printJob.defaultPage();
      PageFormat pf = printJob.pageDialog( pageFormat );
      if(pf == pageFormat) return;
      else pageFormat = pf;

      //setup printjob.
      //Graph2DPrinter is of type Printable
      printJob.setPrintable(gprinter, pageFormat);

      if (printJob.printDialog()) {
        try {
          printJob.print();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  /**
   * Action that terminates the application
   */
  protected class ExitAction extends AbstractAction
  {
    ExitAction()
    {
      super("Exit");
    }

    public void actionPerformed(ActionEvent e)
    {

      System.exit(0);
    }
  }

  /**
   * Action that saves the current graph to a file in YGF format.
   */
  protected class SaveAction extends AbstractAction
  {
    JFileChooser chooser;
    public SaveAction()
    {
      super("Save...");
      chooser = null;
    }

    public void actionPerformed(ActionEvent e)
    {
      if (chooser == null)
      {
        chooser = new JFileChooser();
      }
      if(chooser.showSaveDialog(ViewActionDemo.this) == JFileChooser.APPROVE_OPTION)
      {
        String name = chooser.getSelectedFile().toString();
        if (name.endsWith(".gml")){
          GMLIOHandler ioh = new GMLIOHandler();
          try
          {
            ioh.write(view.getGraph2D(),name);
          } catch ( IOException ioe){
            D.show(ioe);
          }
        } else {
          if(!name.endsWith(".ygf")) name = name + ".ygf";
          YGFIOHandler ioh = new YGFIOHandler();
          try
          {
            ioh.write(view.getGraph2D(),name);
          } catch (IOException ioe){
            D.show(ioe);
          }
        }
      }
    }
  }

  /**
   * Action that saves the current subset of the graph to a file in YGF format.
   */
  protected class SaveSubsetAction extends AbstractAction
  {
    JFileChooser chooser;
    public SaveSubsetAction()
    {
      super("Save selection...");
      chooser = null;
    }

    public void actionPerformed(ActionEvent e)
    {
      if (chooser == null)
      {
        chooser = new JFileChooser();
      }
      if(chooser.showSaveDialog(ViewActionDemo.this) == JFileChooser.APPROVE_OPTION)
      {
        String name = chooser.getSelectedFile().toString();
        if(!name.endsWith(".ygf")) name = name + ".ygf";
        YGFIOHandler ioh = new YGFIOHandler();
        try
        {
          DataProvider dp = Selections.createSelectionDataProvider(view.getGraph2D());
          ioh.writeSubset(view.getGraph2D(), dp, name);
        } catch (IOException ioe){
          D.show(ioe);
        }
      }
    }
  }

  /**
   * Action that loads the current graph from a file in YGF format.
   */
  protected class LoadAction extends AbstractAction
  {
    JFileChooser chooser;
    public LoadAction()
    {
      super("Load...");
      chooser = null;
    }

    public void actionPerformed(ActionEvent e)
    {
      if (chooser == null)
      {
        chooser = new JFileChooser();
      }
      if(chooser.showOpenDialog(ViewActionDemo.this) == JFileChooser.APPROVE_OPTION)
      {
        String name = chooser.getSelectedFile().toString();
        if (name.endsWith(".gml")){
          GMLIOHandler ioh = new GMLIOHandler();
          try
          {
            view.getGraph2D().clear();
            ioh.read(view.getGraph2D(),name);
          } catch (IOException ioe)
          {
            D.show(ioe);
          }
        } else {
          if(!name.endsWith(".ygf")) name = name + ".ygf";
          YGFIOHandler ioh = new YGFIOHandler();
          try
          {
            view.getGraph2D().clear();
            ioh.read(view.getGraph2D(),name);
          } catch (IOException ioe)
          {
            D.show(ioe);
          }
        }
        //force redisplay of view contents
        view.fitContent();
        view.getGraph2D().updateViews();
      }
    }
  }

  /**
   * Action that deletes the selected parts of the graph.
   */
  protected class DeleteSelection extends AbstractAction
  {
    public DeleteSelection()
    {
      super("Delete Selection");
      URL imageURL = ClassLoader.getSystemResource("demo/view/resource/Delete16.gif");
      if (imageURL != null){
        this.putValue( Action.SMALL_ICON, new ImageIcon(imageURL));
      }
    }

    public void actionPerformed(ActionEvent e)
    {
      view.getGraph2D().removeSelection();
      view.getGraph2D().updateViews();
    }
  }

  /**
   * Action that applies a specified zoom level to the view.
   */
  protected class Zoom extends AbstractAction
  {
    double factor;

    public Zoom(double factor)
    {
      super("Zoom " + (factor > 1.0 ? "In" : "Out"));
      URL imageURL;
      if (factor > 1.0d){
        imageURL = ClassLoader.getSystemResource("demo/view/resource/ZoomIn16.gif");
      } else {
        imageURL = ClassLoader.getSystemResource("demo/view/resource/ZoomOut16.gif");
      }
      if (imageURL != null){
        this.putValue(Action.SMALL_ICON, new ImageIcon(imageURL));
      }
      this.factor = factor;
    }

    public void actionPerformed(ActionEvent e)
    {
      view.setZoom(view.getZoom()*factor);
      //optional code that adjusts the size of the
      //view's world rectangle. The world rectangle
      //defines the region of the canvas that is
      //accessible by using the scrollbars of the view.
      Rectangle box = view.getGraph2D().getBoundingBox();
      view.setWorldRect(box.x-20,box.y-20,box.width+40,box.height+40);

      view.updateView();
    }
  }

  /**
   * Action that fits the content nicely inside the view.
   */
  protected class FitContent extends AbstractAction
  {
    public FitContent()
    {
      super("Fit Content");
      URL imageURL = ClassLoader.getSystemResource("demo/view/resource/FitContent16.gif");
      if (imageURL != null){
        this.putValue(Action.SMALL_ICON, new ImageIcon(imageURL));
      }
    }

    public void actionPerformed(ActionEvent e)
    {
      view.fitContent();
      view.updateView();
    }
  }

  /**
   * Action that zooms the view to the selected box.
   */
  public class ZoomArea extends AbstractAction {
    public ZoomArea() {
      super( "Zoom Area" );
      URL imageURL = ClassLoader.getSystemResource( "demo/view/resource/Zoom16.gif" );
      if ( imageURL != null ) {
        this.putValue( Action.SMALL_ICON, new ImageIcon( imageURL ) );
      }
      this.putValue( Action.SHORT_DESCRIPTION, "Zoom Area");
    }

    public void actionPerformed( ActionEvent e ) {
      Iterator viewModes = view.getViewModes();
      while ( viewModes.hasNext() ) {
        ViewMode viewMode = ( ViewMode ) viewModes.next();
        if (viewMode instanceof EditMode) {
          EditMode editMode = ( EditMode ) viewMode;
          editMode.setChild( new AreaZoomMode(), null, null );
        }
      }
    }
  }
}
