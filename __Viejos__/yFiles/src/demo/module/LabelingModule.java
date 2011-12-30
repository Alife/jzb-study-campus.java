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

import java.util.Map;

import y.base.DataProvider;
import y.base.Edge;
import y.base.EdgeCursor;

import y.layout.LabelRanking;
import y.layout.labeling.AbstractLabelingAlgorithm;
import y.layout.labeling.GreedyMISLabeling;
import y.layout.labeling.SALabeling;

import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.Graph2D;
import y.view.NodeLabel;
import y.view.YLabel;
import y.view.hierarchy.GroupLayoutConfigurator;

import y.option.EnumOptionItem;
import y.option.MappedListCellRenderer;
import y.option.OptionHandler;
import y.util.DataProviderAdapter;
import y.module.YModule;

/**
 * This module represents an interactive configurator and launcher for the
 * yFiles labeling algorithms. 
 * It is similar to LabelingModule found in the yFiles package y.module.
 *
 */
public class LabelingModule extends YModule
{

  private static final String ALLOW_NODE_OVERLAPS = "ALLOW_NODE_OVERLAPS";
  private static final String AS_IS = "AS_IS";
  private static final String INPUT = "INPUT";
  private static final String CONSIDER_INVISIBLE_LABELS = "CONSIDER_INVISIBLE_LABELS";
  private static final String SIDE_SLIDER = "SIDE_SLIDER";
  private static final String ALLOW_EDGE_OVERLAPS = "ALLOW_EDGE_OVERLAPS";
  private static final String DIVERSE_LABELING = "DIVERSE_LABELING";
  private static final String QUALITY = "QUALITY";
  private static final String USE_OPTIMIZATION = "USE_OPTIMIZATION";
  private static final String CONSIDER_SELECTED_FEATURES_ONLY = "CONSIDER_SELECTED_FEATURES_ONLY";
  private static final String THREE_POS = "THREE_POS";
  private static final String SCOPE = "SCOPE";
  private static final String PLACE_EDGE_LABELS = "PLACE_EDGE_LABELS";
  private static final String CENTER_SLIDER = "CENTER_SLIDER";
  private static final String MODEL = "MODEL";
  private static final String EDGE_LABEL_MODEL = "EDGE_LABEL_MODEL";
  private static final String UNKNOWN_MODEL_VALUE = "UNKNOWN_MODEL_VALUE";
  private static final String SIX_POS = "SIX_POS";
  private static final String FREE = "FREE";
  private static final String BEST = "BEST";
  private static final String PLACE_NODE_LABELS = "PLACE_NODE_LABELS";

  private static final String[] edgeLabelModel = {
    BEST,
    AS_IS,
    CENTER_SLIDER,
    SIDE_SLIDER,
    SIX_POS,
    THREE_POS,
    FREE,
  };
  
  public LabelingModule()
  {
    super(DIVERSE_LABELING,"yFiles Layout Team","Places Labels");
  }
  
  /** Creates an option handler for this layouter */ 
  public OptionHandler createOptionHandler()
  {
    OptionHandler op = new OptionHandler(getModuleName());
    op.useSection(SCOPE);
    op.addBool(PLACE_NODE_LABELS,true);
    op.addBool(PLACE_EDGE_LABELS,true);
    op.addBool(CONSIDER_SELECTED_FEATURES_ONLY,false);
    op.addBool(CONSIDER_INVISIBLE_LABELS,false);
    op.useSection(QUALITY);
    op.addBool(USE_OPTIMIZATION, false);
    op.addBool(ALLOW_NODE_OVERLAPS,false);
    op.addBool(ALLOW_EDGE_OVERLAPS,true);

    op.useSection(MODEL);
    Map map = EdgeLabel.modelToStringMap();
    Object asIs = AS_IS; 
    map.put(asIs, asIs);
    Object best = BEST;
    map.put(best, best);
    map.remove(new Byte(EdgeLabel. FREE));
    EnumOptionItem item = 
      op.addEnum(EDGE_LABEL_MODEL,
              map.keySet().toArray(),
              best,
              new MappedListCellRenderer(map));
    return op;
  }

  public void init()
  {
    OptionHandler op = getOptionHandler();
    DataProvider labelSet = new LabelSetDP(
      getGraph2D(),
      op.getBool(CONSIDER_SELECTED_FEATURES_ONLY),
      op.getBool(PLACE_NODE_LABELS),
      op.getBool(PLACE_EDGE_LABELS),
      op.getBool(CONSIDER_INVISIBLE_LABELS));
    getGraph2D().addDataProvider(INPUT,labelSet);

    setupEdgeLabelModels(op.get(EDGE_LABEL_MODEL), labelSet);
  }
  
  public void mainrun()
  {
    AbstractLabelingAlgorithm al;
    OptionHandler op = getOptionHandler();
    if (op.getBool(USE_OPTIMIZATION)){
      al = new SALabeling();
    } else {
      al = new GreedyMISLabeling();
    }
    al.setProfitModel(new LabelRanking());
    al.setRemoveNodeOverlaps(!op.getBool(ALLOW_NODE_OVERLAPS));
    al.setRemoveEdgeOverlaps(!op.getBool(ALLOW_EDGE_OVERLAPS));
    // initialize potential grouping information
    GroupLayoutConfigurator glc = new GroupLayoutConfigurator(getGraph2D());
    try {
      // register grouping relevant DataProviders
      glc.prepareGroupDataProviders();
      // launch layouter in buffered mode
      al.label(getGraph2D(),INPUT);
    } finally {
      // make sure the DataProviders will always be unregistered
      glc.restoreGroupDataProviders();
    }

    getGraph2D().removeDataProvider(INPUT);
    getGraph2D().updateViews();
  }
  

  /**
   * Selects the labels we want to set.
   */
  class LabelSetDP extends DataProviderAdapter
  {
    private boolean considerOnlySelected;
    private Graph2D graph;
    private boolean nodes;
    private boolean edges;
    private boolean unvisible;

    LabelSetDP(Graph2D g,boolean sel,boolean n,boolean e,boolean uv)
    {
      considerOnlySelected = sel;
      graph = g;
      nodes = n;
      edges = e;
      unvisible = uv;
    }

    public boolean getBool(Object o)
    {
      YLabel ylabel = (YLabel)o;
      if (!ylabel.isVisible() && !unvisible)
        return false;
      if (o instanceof NodeLabel)
      {
        NodeLabel l = (NodeLabel)o;
        if (l.getModel() == NodeLabel.INTERNAL) return false;
      }
      if (considerOnlySelected)
      {
        if ((o instanceof NodeLabel) && nodes)
        {
          NodeLabel l = (NodeLabel)o;
          if (graph.isSelected(l.getNode()))
            return true;
          else
            return false;
        }
        if ((o instanceof EdgeLabel) && edges)
        {
          EdgeLabel l = (EdgeLabel)o;
          if (graph.isSelected(l.getEdge()))
            return true;
          else
            return false;        
        }
        return false;
      }
      else
      {
        if ((o instanceof NodeLabel) && nodes) return true;
        if ((o instanceof EdgeLabel) && edges) return true;
        return false;
      }     
    }
  }
  
  void setupEdgeLabelModels(Object modelValue, DataProvider labelFilter)
  {
    if(AS_IS.equals(modelValue))
    {
      return;
    }
    
    byte model = 0;
    if(BEST.equals(modelValue))
    {
      model = EdgeLabel.SIDE_SLIDER;
    }
    else if(modelValue instanceof Byte)
    {
      model = ((Byte)modelValue).byteValue();
    }
    else
    {
      throw new IllegalArgumentException(UNKNOWN_MODEL_VALUE + modelValue);
    }
    
    Graph2D graph = getGraph2D();
    for(EdgeCursor ec = graph.edges(); ec.ok(); ec.next())
    {
      Edge e = ec.edge();
      EdgeRealizer er = graph.getRealizer(e);
      for(int i = 0; i < er.labelCount(); i++)
      {
        EdgeLabel label = er.getLabel(i);
        if(labelFilter.getBool(label))
        {
          label.setModel(model);
        }
      }
    }
  }
}

