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
package demo.module;

import y.base.Edge;
import y.base.EdgeCursor;

import y.layout.LabelLayoutConstants;
import y.layout.LabelLayoutTranslator;
import y.layout.LabelRanking;
import y.layout.LayoutStage;
import y.layout.Layouter;
import y.layout.grouping.FixedGroupLayoutStage;
import y.layout.labeling.GreedyMISLabeling;
import y.layout.orthogonal.OrthogonalGroupLayouter;
import y.layout.orthogonal.OrthogonalLayouter;

import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.hierarchy.GroupLayoutConfigurator;
import y.view.hierarchy.HierarchyManager;

import y.option.OptionHandler;
import y.module.LayoutModule;


/**
 * This module represents an interactive configurator and launcher for
 * {@link y.layout.orthogonal.OrthogonalLayouter}
 * and {@link y.layout.orthogonal.OrthogonalGroupLayouter} respectively.
 * It is similar to OrthogonalLayoutModule found in the yFiles package y.module.
 *
 */
public class OrthogonalLayoutModule extends LayoutModule
{
  private static final String ORTHOGONAL = "ORTHOGONAL_LAYOUTER";
  private static final String GROUPING      = "GROUPING";
  private static final String GROUP_POLICY  = "GROUP_LAYOUT_POLICY";
  private static final String IGNORE_GROUPS = "IGNORE_GROUPS";
  private static final String LAYOUT_GROUPS = "LAYOUT_GROUPS";
  private static final String FIX_GROUPS    = "FIX_GROUPS";
  private static final String GROUP_LAYOUT_QUALITY = "GROUP_LAYOUT_QUALITY";

  private static final String LENGTH_REDUCTION = "LENGTH_REDUCTION";
  private static final String STYLE = "STYLE";
  private static final String USE_RANDOMIZATION = "USE_RANDOMIZATION";
  private static final String USE_EXISTING_DRAWING_AS_SKETCH = "USE_EXISTING_DRAWING_AS_SKETCH";
  private static final String CROSSING_POSTPROCESSING = "CROSSING_POSTPROCESSING";
  private static final String GRID = "GRID";
  private static final String NORMAL = "NORMAL";
  private static final String NORMAL_TREE = "NORMAL_TREE";
  private static final String UNIFORM_NODES = "UNIFORM_NODES";
  private static final String BOX_NODES = "BOX_NODES";
  private static final String MIXED = "MIXED";

  private static final String LAYOUT = "LAYOUT";
  private static final String EDGE_LABEL_MODEL = "EDGE_LABEL_MODEL";
  private static final String EDGE_LABELING = "EDGE_LABELING";
  private static final String LABELING = "LABELING";
  private static final String GENERIC = "GENERIC";
  private static final String NONE = "NONE";
  private static final String INTEGRATED = "INTEGRATED";
  private static final String FREE = "FREE";
  private static final String SIDE_SLIDER = "SIDE_SLIDER";
  private static final String CENTER_SLIDER = "CENTER_SLIDER";
  private static final String AS_IS = "AS_IS";
  private static final String BEST = "BEST";

  private final String styleEnum[] = {NORMAL, NORMAL_TREE, UNIFORM_NODES, BOX_NODES, MIXED};

  private static final String[] edgeLabeling = {
    NONE,
    INTEGRATED,
    GENERIC
  };

  private static final String[] edgeLabelModel = {
    BEST,
    AS_IS,
    CENTER_SLIDER,
    SIDE_SLIDER,
    FREE,
  };

  public OrthogonalLayoutModule()
  {
    super (ORTHOGONAL,"yFiles Layout Team",
           "Orthogonal Layouter");
  }

  public OptionHandler createOptionHandler()
  {
    OptionHandler op = new OptionHandler(getModuleName());
    op.useSection(LAYOUT);
    op.addEnum(STYLE,styleEnum,0);
    op.addInt(GRID,25);
    op.addBool(LENGTH_REDUCTION, true);
    op.addBool(USE_EXISTING_DRAWING_AS_SKETCH,false);
    op.addBool(CROSSING_POSTPROCESSING,true);
    op.addBool(USE_RANDOMIZATION,true);
    op.useSection(LABELING);
    op.addEnum(EDGE_LABELING, edgeLabeling, 0);
    op.addEnum(EDGE_LABEL_MODEL, edgeLabelModel, 0);
    
    op.useSection(GROUPING);
    String[] gEnum = { LAYOUT_GROUPS, FIX_GROUPS, IGNORE_GROUPS };
    op.addEnum(GROUP_POLICY, gEnum, 0);
    op.addDouble(GROUP_LAYOUT_QUALITY, 1.0, 0.0, 1.0);
    
    return op;
  }

  public void mainrun()
  {
    OptionHandler op = getOptionHandler();
    
    OrthogonalLayouter orthogonal = new OrthogonalLayouter();
    
    ////////////////////////////////////////////////////////////////////////////
    // Layout
    ////////////////////////////////////////////////////////////////////////////

    switch (OptionHandler.getIndex(styleEnum, op.getString(STYLE))){
      default:
      case 0:
        orthogonal.setLayoutStyle(OrthogonalLayouter.NORMAL_STYLE);
        break;
      case 1:
        orthogonal.setLayoutStyle(OrthogonalLayouter.NORMAL_TREE_STYLE);
        break;
      case 2:
        orthogonal.setLayoutStyle(OrthogonalLayouter.UNIFORM_STYLE);
        break;
      case 3:
        orthogonal.setLayoutStyle(OrthogonalLayouter.BOX_STYLE);
        break;
      case 4:
        orthogonal.setLayoutStyle(OrthogonalLayouter.MIXED_STYLE);
        break;
    }
    orthogonal.setGrid(op.getInt(GRID));
    orthogonal.setUseLengthReduction(
      op.getBool(LENGTH_REDUCTION));
    orthogonal.setUseCrossingPostprocessing(
      op.getBool(CROSSING_POSTPROCESSING));
    orthogonal.setUseRandomization(
      op.getBool(USE_RANDOMIZATION));
    orthogonal.setUseSketchDrawing(op.getBool(USE_EXISTING_DRAWING_AS_SKETCH));


    ////////////////////////////////////////////////////////////////////////////
    // Labels
    ////////////////////////////////////////////////////////////////////////////

    String el = op.getString(EDGE_LABELING);
    if(!el.equals(NONE))
    {
      setupEdgeLabelModel(el, op.getString(EDGE_LABEL_MODEL));
      if(el.equals(GENERIC))
      {
        GreedyMISLabeling la = new GreedyMISLabeling();
        la.setPlaceNodeLabels(false);
        la.setPlaceEdgeLabels(true);
        la.setProfitModel(new LabelRanking());
        orthogonal.setLabelLayouter(la);
        orthogonal.setLabelLayouterEnabled(true);
      }
      else if(el.equals(INTEGRATED))
      {
        orthogonal.setLabelLayouter(new LabelLayoutTranslator());
        orthogonal.setLabelLayouterEnabled(true);
      }
    }
    else
    {
      orthogonal.setLabelLayouterEnabled(false);
    }
    
    Graph2D graph = getGraph2D();
    Layouter layouter = orthogonal;
    LayoutStage preStage = null;
    
    if(HierarchyManager.containsGroupNodes(graph) && !op.get(GROUP_POLICY).equals(IGNORE_GROUPS))
    {
      GroupLayoutConfigurator glc = new GroupLayoutConfigurator(graph);
      glc.prepareAutoBoundsFeatures();
      glc.prepareGroupDataProviders();
      glc.prepareGroupNodeInsets();
      
      FixedGroupLayoutStage fgl = null;
      if(op.get(GROUP_POLICY).equals(FIX_GROUPS))
      {
        fgl = new FixedGroupLayoutStage();
        fgl.setInterEdgeRoutingStyle(FixedGroupLayoutStage.ROUTING_STYLE_ORTHOGONAL);
        orthogonal.prependStage(fgl);
        preStage = fgl;
      }  
      else
      {
        OrthogonalGroupLayouter ogl = new OrthogonalGroupLayouter();
        ogl.setGrid(op.getInt(GRID));
        ogl.setLayoutQuality(op.getDouble(GROUP_LAYOUT_QUALITY));
        layouter = ogl;
      } 
    
      try
      {
        launchLayouter(layouter);
      }
      finally
      {
        glc.restoreAutoBoundsFeatures();
        glc.restoreGroupDataProviders();
        glc.restoreGroupNodeInsets();
        if(preStage != null) orthogonal.removeStage(preStage);
      }
    }
    else
    {
      launchLayouter(layouter);
    }
  }

  void setupEdgeLabelModel(String edgeLabeling, String edgeLabelModel)
  {
    if(edgeLabeling.equals(NONE) || edgeLabelModel.equals(AS_IS))
    {
      return; //nothing to do
    }

    if(edgeLabelModel.equals(BEST))
    {
      if(edgeLabeling.equals(GENERIC))
        edgeLabelModel = SIDE_SLIDER;
      else if(edgeLabeling.equals(INTEGRATED))
        edgeLabelModel = FREE;
    }

    byte model = EdgeLabel.SIDE_SLIDER;
    int preferredSide = LabelLayoutConstants.PLACE_RIGHT_OF_EDGE;
    if(edgeLabelModel.equals(CENTER_SLIDER))
    {
      model = EdgeLabel.CENTER_SLIDER;
      preferredSide = LabelLayoutConstants.PLACE_ON_EDGE;
    }
    else if(edgeLabelModel.equals(FREE))
    {
      model = EdgeLabel.FREE;
      preferredSide = LabelLayoutConstants.PLACE_ON_EDGE;
    }

    Graph2D graph = getGraph2D();
    for(EdgeCursor ec = graph.edges(); ec.ok(); ec.next())
    {
      Edge e = ec.edge();
      EdgeRealizer er = graph.getRealizer(e);
      for(int i = 0; i < er.labelCount(); i++)
      {
        EdgeLabel el = er.getLabel(i);
        el.setModel(model);
        int prefAlongEdge = el.getPreferredPlacement() & LabelLayoutConstants.PLACEMENT_ALONG_EDGE_MASK;
        el.setPreferredPlacement((byte)(preferredSide | prefAlongEdge));
      }
    }
  }
}
