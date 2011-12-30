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


import y.base.DataMap;
import y.base.DataProvider;
import y.base.Edge;
import y.base.EdgeCursor;
import y.base.NodeCursor;
import y.layout.CanonicMultiStageLayouter;
import y.layout.LabelLayoutConstants;
import y.layout.LabelRanking;
import y.layout.LayoutOrientation;
import y.layout.OrientationLayouter;
import y.layout.PortConstraint;
import y.layout.PortConstraintKeys;
import y.layout.hierarchic.AsIsLayerer;
import y.layout.hierarchic.BFSLayerer;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.hierarchic.incremental.EdgeLayoutDescriptor;
import y.layout.hierarchic.incremental.IncrementalHintsFactory;
import y.layout.hierarchic.incremental.NodeLayoutDescriptor;
import y.layout.hierarchic.incremental.OldLayererWrapper;
import y.layout.hierarchic.incremental.SimplexNodePlacer;
import y.layout.labeling.GreedyMISLabeling;
import y.option.ConstraintManager;
import y.option.DefaultEditorFactory;
import y.option.EnumOptionItem;
import y.option.OptionGroup;
import y.module.LayoutModule;

import y.view.Graph2D;
import y.view.hierarchy.GroupLayoutConfigurator;

import y.option.OptionHandler;
import y.option.OptionItem;
import y.option.ConstraintManager.Condition;
import y.util.DataProviderAdapter;
import y.util.Maps;
import y.view.EdgeLabel;
import y.view.EdgeRealizer;
import y.view.Selections;


/**
 * This module represents an interactive configurator and launcher for
 * {@link y.layout.hierarchic.IncrementalHierarchicLayouter}.
 * It is similar to IncrementalHierarchicLayoutModule found in the yFiles
 * package y.module.
 *
 */
public class IncrementalHierarchicLayoutModule extends LayoutModule {
  private static final String INCREMENTAL_HIERARCHIC = "INCREMENTAL_HIERARCHIC";

  private static final String GENERAL = "GENERAL";
  private static final String INTERACTION = "INTERACTION";
  private static final String SELECTED_ELEMENTS_INCREMENTALLY = "SELECTED_ELEMENTS_INCREMENTALLY";
  private static final String USE_DRAWING_AS_SKETCH = "USE_DRAWING_AS_SKETCH";
  private static final String ORIENTATION = "ORIENTATION";
  private static final String RIGHT_TO_LEFT = "RIGHT_TO_LEFT";
  private static final String BOTTOM_TO_TOP = "BOTTOM_TO_TOP";
  private static final String LEFT_TO_RIGHT = "LEFT_TO_RIGHT";
  private static final String TOP_TO_BOTTOM = "TOP_TO_BOTTOM";
  private static final String LAYOUT_COMPONENTS_SEPARATELY = "LAYOUT_COMPONENTS_SEPARATELY";
  private static final String SYMMETRIC_PLACEMENT = "SYMMETRIC_PLACEMENT";
  private static final String MINIMUM_DISTANCES = "MINIMUM_DISTANCES";
  private static final String NODE_TO_NODE_DISTANCE = "NODE_TO_NODE_DISTANCE";
  private static final String NODE_TO_EDGE_DISTANCE = "NODE_TO_EDGE_DISTANCE";
  private static final String EDGE_TO_EDGE_DISTANCE = "EDGE_TO_EDGE_DISTANCE";
  private static final String MINIMUM_LAYER_DISTANCE = "MINIMUM_LAYER_DISTANCE";

  private static final String EDGE_SETTINGS = "EDGE_SETTINGS";
  private static final String EDGE_ROUTING = "EDGE_ROUTING";
  private static final String EDGE_ROUTING_ORTHOGONAL = "EDGE_ROUTING_ORTHOGONAL";
  private static final String EDGE_ROUTING_POLYLINE = "EDGE_ROUTING_POLYLINE";
  private static final String BACKLOOP_ROUTING = "BACKLOOP_ROUTING";
  private static final String MINIMUM_FIRST_SEGMENT_LENGTH = "MINIMUM_FIRST_SEGMENT_LENGTH";
  private static final String MINIMUM_LAST_SEGMENT_LENGTH = "MINIMUM_LAST_SEGMENT_LENGTH";
  private static final String MINIMUM_EDGE_LENGTH = "MINIMUM_EDGE_LENGTH";
  private static final String MINIMUM_EDGE_DISTANCE = "MINIMUM_EDGE_DISTANCE";
  private static final String MINIMUM_SLOPE = "MINIMUM_SLOPE";
  private static final String PC_OPTIMIZATION_ENABLED = "PC_OPTIMIZATION_ENABLED";

  private static final String RANKS = "RANKS";
  private static final String RANKING_POLICY = "RANKING_POLICY";
  private static final String HIERARCHICAL_OPTIMAL = "HIERARCHICAL_OPTIMAL";
  private static final String HIERARCHICAL_TIGHT_TREE_HEURISTIC = "HIERARCHICAL_TIGHT_TREE_HEURISTIC";
  private static final String HIERARCHICAL_TOPMOST = "HIERARCHICAL_TOPMOST";
  private static final String BFS_LAYERS = "BFS_LAYERS";
  private static final String FROM_SKETCH = "FROM_SKETCH";
  private static final String LAYER_ALIGNMENT = "LAYER_ALIGNMENT";
  private static final String TOP = "TOP";
  private static final String CENTER = "CENTER";
  private static final String BOTTOM = "BOTTOM";
  private static final String FROM_SKETCH_PROPERTIES = "FROM_SKETCH_PROPERTIES";
  private static final String SCALE = "SCALE";
  private static final String HALO = "HALO";
  private static final String MINIMUM_SIZE = "MINIMUM_SIZE";
  private static final String MAXIMUM_SIZE = "MAXIMUM_SIZE";


  private static final String LABELING = "LABELING";
  private static final String NODE_PROPERTIES = "NODE_PROPERTIES";
  private static final String CONSIDER_NODE_LABELS = "CONSIDER_NODE_LABELS";
  private static final String EDGE_PROPERTIES = "EDGE_PROPERTIES";
  private static final String EDGE_LABELING = "EDGE_LABELING";
  private static final String EDGE_LABELING_NONE = "EDGE_LABELING_NONE";
  private static final String EDGE_LABELING_HIERARCHIC = "EDGE_LABELING_HIERARCHIC";
  private static final String EDGE_LABELING_GENERIC = "EDGE_LABELING_GENERIC";
  private static final String EDGE_LABEL_MODEL = "EDGE_LABEL_MODEL";
  private static final String EDGE_LABEL_MODEL_FREE = "EDGE_LABEL_MODEL_FREE";
  private static final String EDGE_LABEL_MODEL_BEST = "EDGE_LABEL_MODEL_BEST";
  private static final String EDGE_LABEL_MODEL_AS_IS = "EDGE_LABEL_MODEL_AS_IS";
  private static final String EDGE_LABEL_MODEL_SIDE_SLIDER = "EDGE_LABEL_MODEL_SIDE_SLIDER";
  private static final String EDGE_LABEL_MODEL_CENTER_SLIDER = "EDGE_LABEL_MODEL_CENTER_SLIDER";

  private static final String GROUPING = "GROUPING";
  private static final String GROUP_LAYERING_STRATEGY = "GROUP_LAYERING_STRATEGY";
  private static final String GLOBAL_LAYERING = "GLOBAL_LAYERING";
  private static final String RECURSIVE_LAYERING = "RECURSIVE_LAYERING";
  private static final String GROUP_ALIGNMENT = "GROUP_ALIGNMENT";
  private static final String GROUP_ALIGN_TOP = "GROUP_ALIGN_TOP";
  private static final String GROUP_ALIGN_CENTER = "GROUP_ALIGN_CENTER";
  private static final String GROUP_ALIGN_BOTTOM = "GROUP_ALIGN_BOTTOM";

  private static final String GROUP_ENABLE_COMPACTION = "GROUP_ENABLE_COMPACTION";

  private static final Object[] edgeRoutingEnum = new Object[]{EDGE_ROUTING_ORTHOGONAL, EDGE_ROUTING_POLYLINE};

  private static final Object[] orientEnum = {TOP_TO_BOTTOM, LEFT_TO_RIGHT, BOTTOM_TO_TOP, RIGHT_TO_LEFT};

  private static final Object[] alignmentEnum = {TOP, CENTER, BOTTOM};
  private static final String[] rankingPolicies = {HIERARCHICAL_OPTIMAL, HIERARCHICAL_TIGHT_TREE_HEURISTIC, BFS_LAYERS, FROM_SKETCH, HIERARCHICAL_TOPMOST};

  private static final String[] edgeLabeling = {EDGE_LABELING_NONE, EDGE_LABELING_GENERIC, EDGE_LABELING_HIERARCHIC};

  private static final String[] edgeLabelModel = {
      EDGE_LABEL_MODEL_BEST,
      EDGE_LABEL_MODEL_AS_IS,
      EDGE_LABEL_MODEL_CENTER_SLIDER,
      EDGE_LABEL_MODEL_SIDE_SLIDER,
      EDGE_LABEL_MODEL_FREE,
  };

  private static final Object[] groupStrategyEnum = {GLOBAL_LAYERING, RECURSIVE_LAYERING};
  private static final Object[] groupAlignmentEnum = {GROUP_ALIGN_TOP, GROUP_ALIGN_CENTER, GROUP_ALIGN_BOTTOM};

  public IncrementalHierarchicLayoutModule() {
    super( INCREMENTAL_HIERARCHIC, "yFiles Layout Team", "A sophisticated hierarchic layout algorithm" );
    setPortIntersectionCalculatorEnabled( true );
  }

  public OptionHandler createOptionHandler() {
    OptionHandler op = new OptionHandler( getModuleName() );

    OptionGroup og;

    op.useSection( GENERAL );

    og = new OptionGroup();
    og.setAttribute( OptionGroup.ATTRIBUTE_TITLE, INTERACTION );

    og.addItem( op.addBool( SELECTED_ELEMENTS_INCREMENTALLY, false ) );
    og.addItem( op.addBool( USE_DRAWING_AS_SKETCH, false ) );

    op.addEnum( ORIENTATION, orientEnum, 0 );

    op.addBool( LAYOUT_COMPONENTS_SEPARATELY, false );
    op.addBool( SYMMETRIC_PLACEMENT, true );


    og = new OptionGroup();
    og.setAttribute( OptionGroup.ATTRIBUTE_TITLE, MINIMUM_DISTANCES );
    og.addItem( op.addDouble( NODE_TO_NODE_DISTANCE, 30.0d ) );
    og.addItem( op.addDouble( NODE_TO_EDGE_DISTANCE, 15.0d ) );
    og.addItem( op.addDouble( EDGE_TO_EDGE_DISTANCE, 15.0d ) );
    og.addItem( op.addDouble( MINIMUM_LAYER_DISTANCE, 10.0d ) );

    op.useSection( EDGE_SETTINGS );

    EnumOptionItem eoi = op.addEnum( EDGE_ROUTING, edgeRoutingEnum, 0 );
    eoi.setAttribute( DefaultEditorFactory.ATTRIBUTE_ENUM_STYLE,
        DefaultEditorFactory.STYLE_RADIO_BUTTONS );
    eoi.setAttribute( DefaultEditorFactory.ATTRIBUTE_ENUM_ALIGNMENT,
        DefaultEditorFactory.ALIGNMENT_VERTICAL );

    op.addBool( BACKLOOP_ROUTING, false );
    op.addDouble( MINIMUM_FIRST_SEGMENT_LENGTH, 10.0d );
    op.addDouble( MINIMUM_LAST_SEGMENT_LENGTH, 15.0d );
    op.addDouble( MINIMUM_EDGE_LENGTH, 20.0d );
    op.addDouble( MINIMUM_EDGE_DISTANCE, 15.0d );

    ConstraintManager cm = new ConstraintManager( op );
    cm.setEnabledOnValueEquals( eoi, EDGE_ROUTING_POLYLINE,
        op.addDouble( MINIMUM_SLOPE, 0.25d, 0.0d, 5.0d, 2 ) );

    op.addBool( PC_OPTIMIZATION_ENABLED, false );

    op.useSection( RANKS );
    op.addEnum( RANKING_POLICY, rankingPolicies, 0 );
    op.addEnum( LAYER_ALIGNMENT, alignmentEnum, 1 );

    og = new OptionGroup();
    og.setAttribute( OptionGroup.ATTRIBUTE_TITLE, FROM_SKETCH_PROPERTIES );
    og.addItem( op.addDouble( SCALE, 1.0d, 0.0d, 5.0d, 1 ) );
    og.addItem( op.addDouble( HALO, 0.0d ) );
    og.addItem( op.addDouble( MINIMUM_SIZE, 0.0d ) );
    og.addItem( op.addDouble( MAXIMUM_SIZE, 1000.0d ) );

    Condition c =
        cm.createConditionValueEquals( USE_DRAWING_AS_SKETCH, Boolean.FALSE ).and(
            cm.createConditionValueEquals( SELECTED_ELEMENTS_INCREMENTALLY, Boolean.FALSE ) );
    cm.setEnabledOnCondition( c, op.getItem( RANKING_POLICY ) );

    c = c.inverse().or( cm.createConditionValueEquals( RANKING_POLICY, FROM_SKETCH ) );
    cm.setEnabledOnCondition( c, og );

    op.useSection( LABELING );
    og = new OptionGroup();
    og.setAttribute( OptionGroup.ATTRIBUTE_TITLE, NODE_PROPERTIES );
    og.addItem( op.addBool( CONSIDER_NODE_LABELS, true ) );
    og = new OptionGroup();
    og.setAttribute( OptionGroup.ATTRIBUTE_TITLE, EDGE_PROPERTIES );
    og.addItem( op.addEnum( EDGE_LABELING, edgeLabeling, 0 ) );
    cm.setEnabledOnValueEquals( op.getItem( EDGE_LABELING ), EDGE_LABELING_NONE,
        og.addItem( op.addEnum( EDGE_LABEL_MODEL, edgeLabelModel, 0 ) ), true );

    op.useSection( GROUPING );
    OptionItem gsi = op.addEnum( GROUP_LAYERING_STRATEGY, groupStrategyEnum, 0 );
    OptionItem eci = op.addBool( GROUP_ENABLE_COMPACTION, true);
    OptionItem gai = op.addEnum( GROUP_ALIGNMENT, groupAlignmentEnum, 0 );
    cm.setEnabledOnValueEquals( gsi, RECURSIVE_LAYERING, eci );
    cm.setEnabledOnValueEquals( gsi, RECURSIVE_LAYERING, gai );
    cm.setEnabledOnCondition( cm.createConditionValueEquals( gsi, RECURSIVE_LAYERING ).and( cm.createConditionValueEquals( eci, Boolean.TRUE ).inverse() ), gai );
    return op;
  }

  public void mainrun() {
    CanonicMultiStageLayouter layouter = null;
    Graph2D graph = getGraph2D();

    OptionHandler op = getOptionHandler();

    final IncrementalHierarchicLayouter ihl = new IncrementalHierarchicLayouter();
    layouter = ihl;

    //  mark incremental elements if required
    DataMap incrementalElements = null;
    boolean fromSketch = op.getBool( USE_DRAWING_AS_SKETCH );
    boolean incrementalLayout = op.getBool( SELECTED_ELEMENTS_INCREMENTALLY );
    boolean selectedElements = !Selections.isEdgeSelectionEmpty( graph ) || !Selections.isNodeSelectionEmpty( graph );

    if ( incrementalLayout && selectedElements ) {
      // create storage for both nodes and edges
      incrementalElements = Maps.createHashedDataMap();
      // configure the mode
      ihl.setLayoutMode( IncrementalHierarchicLayouter.LAYOUT_MODE_INCREMENTAL );
      final IncrementalHintsFactory ihf = ihl.createIncrementalHintsFactory();

      for ( NodeCursor nc = graph.selectedNodes(); nc.ok(); nc.next() ) {
        incrementalElements.set( nc.node(), ihf.createLayerIncrementallyHint( nc.node() ) );
      }

      for ( EdgeCursor ec = graph.selectedEdges(); ec.ok(); ec.next() ) {
        incrementalElements.set( ec.edge(), ihf.createSequenceIncrementallyHint( ec.edge() ) );
      }
      graph.addDataProvider( IncrementalHierarchicLayouter.INCREMENTAL_HINTS_DPKEY, incrementalElements );
    } else if ( fromSketch ) {
      ihl.setLayoutMode( IncrementalHierarchicLayouter.LAYOUT_MODE_INCREMENTAL );
    } else {
      ihl.setLayoutMode( IncrementalHierarchicLayouter.LAYOUT_MODE_FROM_SCRATCH );
    }

    // cast to implementation simplex
    ( ( SimplexNodePlacer ) ihl.getNodePlacer() ).setBaryCenterModeEnabled( op.getBool( SYMMETRIC_PLACEMENT ) );

    ihl.setComponentLayouterEnabled( op.getBool( LAYOUT_COMPONENTS_SEPARATELY ) );

    ihl.setMinimumLayerDistance( op.getDouble( MINIMUM_LAYER_DISTANCE ) );
    ihl.setNodeToEdgeDistance( op.getDouble( NODE_TO_EDGE_DISTANCE ) );
    ihl.setNodeToNodeDistance( op.getDouble( NODE_TO_NODE_DISTANCE ) );
    ihl.setEdgeToEdgeDistance( op.getDouble( EDGE_TO_EDGE_DISTANCE ) );

    final NodeLayoutDescriptor nld = ihl.getNodeLayoutDescriptor();
    final EdgeLayoutDescriptor eld = ihl.getEdgeLayoutDescriptor();

    eld.setOrthogonallyRouted( op.getEnum( EDGE_ROUTING ) == 0 );
    eld.setMinimumFirstSegmentLength( op.getDouble( MINIMUM_FIRST_SEGMENT_LENGTH ) );
    eld.setMinimumLastSegmentLength( op.getDouble( MINIMUM_LAST_SEGMENT_LENGTH ) );

    eld.setMinimumDistance( op.getDouble( MINIMUM_EDGE_DISTANCE ) );
    eld.setMinimumLength( op.getDouble( MINIMUM_EDGE_LENGTH ) );

    eld.setMinimumSlope( op.getDouble( MINIMUM_SLOPE ) );

    eld.setSourcePortOptimizationEnabled( op.getBool( PC_OPTIMIZATION_ENABLED ) );
    eld.setTargetPortOptimizationEnabled( op.getBool( PC_OPTIMIZATION_ENABLED ) );

    nld.setMinimumDistance( Math.min( ihl.getNodeToNodeDistance(), ihl.getNodeToEdgeDistance() ) );
    nld.setMinimumLayerHeight( 0 );

    if ( op.get( LAYER_ALIGNMENT ).equals( TOP ) )
      nld.setLayerAlignment( 0.0 );
    else if ( op.get( LAYER_ALIGNMENT ).equals( CENTER ) )
      nld.setLayerAlignment( 0.5 );
    else if ( op.get( LAYER_ALIGNMENT ).equals( BOTTOM ) )
      nld.setLayerAlignment( 1.0 );

    final OrientationLayouter ol = ( OrientationLayouter ) ihl.getOrientationLayouter();
    if ( op.get( ORIENTATION ).equals( TOP_TO_BOTTOM ) )
      ol.setOrientation( OrientationLayouter.TOP_TO_BOTTOM );
    else if ( op.get( ORIENTATION ).equals( LEFT_TO_RIGHT ) )
      ol.setOrientation( OrientationLayouter.LEFT_TO_RIGHT );
    else if ( op.get( ORIENTATION ).equals( BOTTOM_TO_TOP ) )
      ol.setOrientation( OrientationLayouter.BOTTOM_TO_TOP );
    else if ( op.get( ORIENTATION ).equals( RIGHT_TO_LEFT ) )
      ol.setOrientation( OrientationLayouter.RIGHT_TO_LEFT );

    final String el = op.getString( EDGE_LABELING );
    if ( !el.equals( EDGE_LABELING_NONE ) ) {
      setupEdgeLabelModel( el, op.getString( EDGE_LABEL_MODEL ) );
      if ( el.equals( EDGE_LABELING_GENERIC ) ) {
        GreedyMISLabeling la = new GreedyMISLabeling();
        la.setPlaceNodeLabels( false );
        la.setPlaceEdgeLabels( true );
        la.setProfitModel( new LabelRanking() );
        ihl.setLabelLayouter( la );
        ihl.setLabelLayouterEnabled( true );
      } else if ( el.equals( EDGE_LABELING_HIERARCHIC ) ) {
        ihl.setIntegratedEdgeLabelingEnabled( true );
      }
    } else {
      ihl.setIntegratedEdgeLabelingEnabled( false );
    }

    if ( op.getBool( CONSIDER_NODE_LABELS ) ) {
      ihl.setConsiderNodeLabelsEnabled( true );
      ihl.getNodeLayoutDescriptor().setNodeLabelMode( NodeLayoutDescriptor.NODE_LABEL_MODE_CONSIDER_FOR_DRAWING );
    } else {
      ihl.setConsiderNodeLabelsEnabled( false );
    }

    DataProvider oldSdp = null;
    DataProvider oldTdp = null;

    if ( op.getBool( BACKLOOP_ROUTING ) ) {
      PortConstraint spc = null, tpc = null;
      switch ( ol.getOrientation() ) {
        case LayoutOrientation.TOP_TO_BOTTOM:
          spc = PortConstraint.create( PortConstraint.SOUTH );
          tpc = PortConstraint.create( PortConstraint.NORTH );
          break;
        case LayoutOrientation.LEFT_TO_RIGHT:
          spc = PortConstraint.create( PortConstraint.EAST );
          tpc = PortConstraint.create( PortConstraint.WEST );
          break;
        case LayoutOrientation.RIGHT_TO_LEFT:
          spc = PortConstraint.create( PortConstraint.WEST );
          tpc = PortConstraint.create( PortConstraint.EAST );
          break;
        case LayoutOrientation.BOTTOM_TO_TOP:
          spc = PortConstraint.create( PortConstraint.NORTH );
          tpc = PortConstraint.create( PortConstraint.SOUTH );
          break;
      }

      oldSdp = graph.getDataProvider( PortConstraintKeys.SOURCE_PORT_CONSTRAINT_KEY );
      oldTdp = graph.getDataProvider( PortConstraintKeys.TARGET_PORT_CONSTRAINT_KEY );

      DataProvider sdp = new BackloopConstraintDP( spc, oldSdp );
      DataProvider tdp = new BackloopConstraintDP( tpc, oldTdp );

      if ( oldSdp != null ) {
        graph.removeDataProvider( PortConstraintKeys.SOURCE_PORT_CONSTRAINT_KEY );
      }
      if ( oldTdp != null ) {
        graph.removeDataProvider( PortConstraintKeys.TARGET_PORT_CONSTRAINT_KEY );
      }

      graph.addDataProvider( PortConstraintKeys.SOURCE_PORT_CONSTRAINT_KEY, sdp );
      graph.addDataProvider( PortConstraintKeys.TARGET_PORT_CONSTRAINT_KEY, tdp );
    }


    final String rp = op.getString( RANKING_POLICY );

    if ( rp.equals( FROM_SKETCH ) ) {
      ihl.setFromScratchLayeringStrategy( IncrementalHierarchicLayouter.LAYERING_STRATEGY_FROM_SKETCH );
    } else if ( rp.equals( HIERARCHICAL_OPTIMAL ) )
      ihl.setFromScratchLayeringStrategy( IncrementalHierarchicLayouter.LAYERING_STRATEGY_HIERARCHICAL_OPTIMAL );
    else if ( rp.equals( HIERARCHICAL_TIGHT_TREE_HEURISTIC ) )
      ihl.setFromScratchLayeringStrategy( IncrementalHierarchicLayouter.LAYERING_STRATEGY_HIERARCHICAL_TIGHT_TREE );
    else if ( rp.equals( HIERARCHICAL_TOPMOST ) )
      ihl.setFromScratchLayeringStrategy( IncrementalHierarchicLayouter.LAYERING_STRATEGY_HIERARCHICAL_TOPMOST );
    else if ( rp.equals( BFS_LAYERS ) ) {
      ihl.setFromScratchLayeringStrategy( IncrementalHierarchicLayouter.LAYERING_STRATEGY_BFS );
      getGraph2D().addDataProvider( BFSLayerer.CORE_NODES, Selections.createSelectionNodeMap( getGraph2D() ) );
    }

    //configure AsIsLayerer
    Object layerer = ( ihl.getLayoutMode() == IncrementalHierarchicLayouter.LAYOUT_MODE_FROM_SCRATCH ) ?
        ihl.getFromScratchLayerer() : ihl.getFixedElementsLayerer();

    if ( layerer instanceof OldLayererWrapper ) {
      y.layout.hierarchic.Layerer coreLayerer = ( ( OldLayererWrapper ) layerer ).getOldLayerer();
      if ( coreLayerer instanceof AsIsLayerer ) {
        AsIsLayerer ail = ( AsIsLayerer ) coreLayerer;
        ail.setNodeHalo( op.getDouble( HALO ) );
        ail.setNodeScalingFactor( op.getDouble( SCALE ) );
        ail.setMinimumNodeSize( op.getDouble( MINIMUM_SIZE ) );
        ail.setMaximumNodeSize( op.getDouble( MAXIMUM_SIZE ) );
      }
    }

    if ( op.getString( GROUP_LAYERING_STRATEGY ).equals( RECURSIVE_LAYERING ) ) {
      byte alignmentPolicy = IncrementalHierarchicLayouter.POLICY_ALIGN_GROUPS_TOP;
      if ( op.getString( GROUP_ALIGNMENT ).equals( GROUP_ALIGN_CENTER ) ) {
        alignmentPolicy = IncrementalHierarchicLayouter.POLICY_ALIGN_GROUPS_CENTER;
      } else if ( op.getString( GROUP_ALIGNMENT ).equals( GROUP_ALIGN_BOTTOM ) ) {
        alignmentPolicy = IncrementalHierarchicLayouter.POLICY_ALIGN_GROUPS_BOTTOM;
      }
      ihl.setGroupCompactionEnabled( op.getBool( GROUP_ENABLE_COMPACTION));
      ihl.setGroupAlignmentPolicy( alignmentPolicy );
      ihl.setRecursiveGroupLayeringEnabled( true );
    } else {
      ihl.setRecursiveGroupLayeringEnabled( false );
    }

    // initialize potential grouping information
    GroupLayoutConfigurator glc = new GroupLayoutConfigurator( graph );
    try {
      // register grouping relevant DataProviders
      glc.prepareAll();
      // launch layouter in buffered mode
      launchLayouter( layouter );
    } finally {
      // make sure the DataProviders will always be unregistered
      glc.restoreAll();

      // remove the registered DataProvider instances
      if ( incrementalElements != null ) {
        graph.removeDataProvider( IncrementalHierarchicLayouter.INCREMENTAL_HINTS_DPKEY );
        incrementalElements = null;
      }

      if ( op.getBool( BACKLOOP_ROUTING ) ) {
        graph.removeDataProvider( PortConstraintKeys.SOURCE_PORT_CONSTRAINT_KEY );
        if ( oldSdp != null ) {
          graph.addDataProvider( PortConstraintKeys.SOURCE_PORT_CONSTRAINT_KEY, oldSdp );
        }
        graph.removeDataProvider( PortConstraintKeys.TARGET_PORT_CONSTRAINT_KEY );
        if ( oldTdp != null ) {
          graph.addDataProvider( PortConstraintKeys.TARGET_PORT_CONSTRAINT_KEY, oldTdp );
        }
      }
    }
  }

  void setupEdgeLabelModel( String edgeLabeling, String edgeLabelModel ) {
    if ( edgeLabeling.equals( EDGE_LABELING_NONE ) || edgeLabelModel.equals( EDGE_LABEL_MODEL_AS_IS ) ) {
      return; //nothing to do
    }

    if ( edgeLabelModel.equals( EDGE_LABEL_MODEL_BEST ) ) {
      if ( edgeLabeling.equals( EDGE_LABELING_GENERIC ) )
        edgeLabelModel = EDGE_LABEL_MODEL_SIDE_SLIDER;
      else if ( edgeLabeling.equals( EDGE_LABELING_HIERARCHIC ) )
        edgeLabelModel = EDGE_LABEL_MODEL_FREE;
    }

    byte model = EdgeLabel.SIDE_SLIDER;
    int preferredSide = LabelLayoutConstants.PLACE_RIGHT_OF_EDGE;
    if ( edgeLabelModel.equals( EDGE_LABEL_MODEL_CENTER_SLIDER ) ) {
      model = EdgeLabel.CENTER_SLIDER;
      preferredSide = LabelLayoutConstants.PLACE_ON_EDGE;
    } else if ( edgeLabelModel.equals( EDGE_LABEL_MODEL_FREE ) ) {
      model = EdgeLabel.FREE;
      preferredSide = LabelLayoutConstants.PLACE_ON_EDGE;
    }

    Graph2D graph = getGraph2D();
    for ( EdgeCursor ec = graph.edges(); ec.ok(); ec.next() ) {
      Edge e = ec.edge();
      EdgeRealizer er = graph.getRealizer( e );
      for ( int i = 0; i < er.labelCount(); i++ ) {
        EdgeLabel el = er.getLabel( i );
        el.setModel( model );
        int prefAlongEdge = el.getPreferredPlacement() & LabelLayoutConstants.PLACEMENT_ALONG_EDGE_MASK;
        el.setPreferredPlacement( ( byte ) ( preferredSide | prefAlongEdge ) );
      }
    }
  }


  static final class BackloopConstraintDP extends DataProviderAdapter {
    private PortConstraint pc;
    private DataProvider delegate;
    private static final PortConstraint anySide = PortConstraint.create( PortConstraint.ANY_SIDE );

    BackloopConstraintDP( PortConstraint pc, DataProvider delegate ) {
      this.pc = pc;
      this.delegate = delegate;
    }

    public Object get( Object o ) {
      if ( delegate != null ) {
        Object delegateResult = delegate.get( o );
        if ( delegateResult != null ) {
          return delegateResult;
        }
      }
      Edge e = ( Edge ) o;
      if ( e.isSelfLoop() ) {
        return anySide;
      } else {
        return pc;
      }
    }
  }
}