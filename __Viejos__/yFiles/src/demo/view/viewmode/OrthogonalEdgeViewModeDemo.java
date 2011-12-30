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
package demo.view.viewmode;

import demo.view.DemoBase;
import y.base.Edge;
import y.base.EdgeMap;
import y.base.GraphEvent;
import y.base.GraphListener;
import y.view.CreateEdgeMode;
import y.view.EditMode;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * Demonstrates how to customize EditMode in order to simulate orthogonal edges. <br> This demo only allows
 * to switch between the creation of orthogonal and polyline edges.
 * Toggling the button in the toolbar switches the type of newly created edges.
 * This affects the behavior of {@link CreateEdgeMode} and {@link y.view.EditMode}, as well as implicitly the minor modes
 * of EditMode.
 */
public class OrthogonalEdgeViewModeDemo extends DemoBase {

  private boolean orthogonalRouting;
  private EditMode editMode;
  private EdgeMap orthogonalEdgeMap;
  private JToggleButton orthogonalButton;


  protected void initialize() {
    super.initialize();
    orthogonalEdgeMap = view.getGraph2D().createEdgeMap();
    view.getGraph2D().addGraphListener(new GraphListener() {
      public void onGraphEvent(GraphEvent e) {
        if (e.getType() == GraphEvent.EDGE_CREATION) {
          orthogonalEdgeMap.setBool(e.getData(), orthogonalRouting);
        }
      }
    });
  }


  protected JToolBar createToolBar() {
    JToolBar toolBar = super.createToolBar();
    orthogonalButton = new JToggleButton(new AbstractAction("Orthogonal") {
      public void actionPerformed(ActionEvent e) {
        setOrthogonalRouting(((JToggleButton) e.getSource()).isSelected());
      }
    });
    toolBar.add(orthogonalButton);
    setOrthogonalRouting(true);
    return toolBar;
  }

  protected void registerViewModes() {
    editMode = new EditMode() {
      protected boolean isOrthogonalRouting(Edge edge) {
        return orthogonalEdgeMap != null && orthogonalEdgeMap.getBool(edge);
      }
    };
    view.addViewMode(editMode);
    //set a custom CreateEdgeMode for the edge mode
  }


  public boolean isOrthogonalRouting() {
    return orthogonalRouting;
  }

  public void setOrthogonalRouting(boolean orthogonalRouting) {
    this.orthogonalRouting = orthogonalRouting;
    this.orthogonalButton.setSelected(orthogonalRouting);
    ((CreateEdgeMode) editMode.getCreateEdgeMode()).setOrthogonalEdgeCreation(orthogonalRouting);
    view.getGraph2D().getDefaultEdgeRealizer().setLineColor(orthogonalRouting ? Color.RED : Color.BLACK);
  }

  public static void main(String args[]) {
    OrthogonalEdgeViewModeDemo demo = new OrthogonalEdgeViewModeDemo();
    demo.start("Orthogonal Edge ViewMode Demo");
  }
}
