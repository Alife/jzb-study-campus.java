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

import y.layout.circular.CircularLayouter;
import y.layout.circular.SingleCycleLayouter;
import y.layout.grouping.GroupingKeys;
import y.layout.tree.BalloonLayouter;
import y.option.OptionHandler;
import y.view.Graph2D;
import y.view.hierarchy.GroupLayoutConfigurator;
import y.module.LayoutModule;

/**
 * This module represents an interactive configurator and launcher for
 * {@link y.layout.circular.CircularLayouter}.
 * It is similar to CircularLayoutModule found in the yFiles package y.module.
 *
 */
public class CircularLayoutModule extends LayoutModule
{
  private static final String CIRCULAR = "CIRCULAR";
  private static final String ALLOW_OVERLAPS = "ALLOW_OVERLAPS";
  private static final String COMPACTNESS_FACTOR = "COMPACTNESS_FACTOR";
  private static final String MAXIMAL_DEVIATION_ANGLE = "MAXIMAL_DEVIATION_ANGLE";
  private static final String MINIMAL_EDGE_LENGTH = "MINIMAL_EDGE_LENGTH";
  private static final String PREFERRED_CHILD_WEDGE = "PREFERRED_CHILD_WEDGE";
  private static final String TREE = "TREE";
  private static final String FIXED_RADIUS = "FIXED_RADIUS";
  private static final String CHOOSE_RADIUS_AUTOMATICALLY = "CHOOSE_RADIUS_AUTOMATICALLY";
  private static final String MINIMAL_NODE_DISTANCE = "MINIMAL_NODE_DISTANCE";
  private static final String CYCLE = "CYCLE";
  private static final String ACT_ON_SELECTION_ONLY = "ACT_ON_SELECTION_ONLY";
  private static final String LAYOUT_STYLE = "LAYOUT_STYLE";
  private static final String GENERAL = "GENERAL";
  private static final String SINGLE_CYCLE = "SINGLE_CYCLE";
  private static final String BCC_ISOLATED = "BCC_ISOLATED";
  private static final String BCC_COMPACT = "BCC_COMPACT";
  private static final String CIRCULAR_CUSTOM_GROUPS = "CIRCULAR_CUSTOM_GROUPS";

  private final static String layoutStyles[] = {BCC_COMPACT, BCC_ISOLATED, CIRCULAR_CUSTOM_GROUPS, SINGLE_CYCLE};
  private final static String PARTITION_LAYOUT_STYLE = "PARTITION_LAYOUT_STYLE";
  private final static String PARTITION_LAYOUTSTYLE_CYCLIC = "PARTITION_LAYOUTSTYLE_CYCLIC";
  private final static String PARTITION_LAYOUTSTYLE_DISK = "PARTITION_LAYOUTSTYLE_DISK";
  private final static String PARTITION_LAYOUTSTYLE_ORGANIC = "PARTITION_LAYOUTSTYLE_ORGANIC";

  private final static String partitionLayoutStyles[] = {PARTITION_LAYOUTSTYLE_CYCLIC, PARTITION_LAYOUTSTYLE_DISK, PARTITION_LAYOUTSTYLE_ORGANIC};

  public CircularLayoutModule() {
    super( CIRCULAR, "yFiles Layout Team",
        "Circular Layout" );
  }

  public OptionHandler createOptionHandler() {
    CircularLayouter layouter = new CircularLayouter();
    SingleCycleLayouter cycleLayouter = layouter.getSingleCycleLayouter();
    BalloonLayouter treeLayouter = layouter.getBalloonLayouter();


    OptionHandler op = new OptionHandler( getModuleName() );

    op.useSection( GENERAL );
    op.addEnum( LAYOUT_STYLE, layoutStyles, layouter.getLayoutStyle() );
    op.addBool( ACT_ON_SELECTION_ONLY, false );

    op.useSection( CYCLE );
    op.addEnum( PARTITION_LAYOUT_STYLE, partitionLayoutStyles, layouter.getPartitionLayoutStyle() );
    op.addInt( MINIMAL_NODE_DISTANCE, ( int ) cycleLayouter.getMinimalNodeDistance(), 0, 999 );
    op.addBool( CHOOSE_RADIUS_AUTOMATICALLY, cycleLayouter.getAutomaticRadius() );
    op.addInt( FIXED_RADIUS, ( int ) cycleLayouter.getFixedRadius(), 50, 800 );


    op.useSection( TREE );
    op.addInt( PREFERRED_CHILD_WEDGE, treeLayouter.getPreferredChildWedge(), 1, 359 );
    op.addInt( MINIMAL_EDGE_LENGTH, treeLayouter.getMinimalEdgeLength(), 5, 400 );
    op.addInt( MAXIMAL_DEVIATION_ANGLE, layouter.getMaximalDeviationAngle(), 10, 360 );
    op.addDouble( COMPACTNESS_FACTOR, treeLayouter.getCompactnessFactor(), 0.1, 0.9 );
    op.addBool( ALLOW_OVERLAPS, treeLayouter.getAllowOverlaps() );

    return op;
  }


  public void mainrun() {
    OptionHandler op = getOptionHandler();

    CircularLayouter layouter = new CircularLayouter();

    BalloonLayouter treeLayouter = layouter.getBalloonLayouter();

    if ( op.getString( LAYOUT_STYLE ).equals( BCC_COMPACT ) ) {
      layouter.setLayoutStyle( CircularLayouter.BCC_COMPACT );
    } else if ( op.getString( LAYOUT_STYLE ).equals( BCC_ISOLATED ) ) {
      layouter.setLayoutStyle( CircularLayouter.BCC_ISOLATED );
    } else if ( op.getString( LAYOUT_STYLE ).equals( CIRCULAR_CUSTOM_GROUPS ) ) {
      layouter.setLayoutStyle( CircularLayouter.CIRCULAR_CUSTOM_GROUPS );
    } else {
      layouter.setLayoutStyle( CircularLayouter.SINGLE_CYCLE );
    }

    layouter.setSubgraphLayouterEnabled( op.getBool( ACT_ON_SELECTION_ONLY ) );
    layouter.setMaximalDeviationAngle( op.getInt( MAXIMAL_DEVIATION_ANGLE ) );


    if ( op.getString( PARTITION_LAYOUT_STYLE ).equals( PARTITION_LAYOUTSTYLE_CYCLIC ) ) {
      layouter.setPartitionLayoutStyle( CircularLayouter.PARTITION_LAYOUTSTYLE_CYCLIC );
    } else if ( op.getString( PARTITION_LAYOUT_STYLE ).equals( PARTITION_LAYOUTSTYLE_DISK ) ) {
      layouter.setPartitionLayoutStyle( CircularLayouter.PARTITION_LAYOUTSTYLE_DISK );
    }
    else if ( op.getString( PARTITION_LAYOUT_STYLE ).equals( PARTITION_LAYOUTSTYLE_ORGANIC ) ) {
      layouter.setPartitionLayoutStyle( CircularLayouter.PARTITION_LAYOUTSTYLE_ORGANIC );
    }

    SingleCycleLayouter cycleLayouter = layouter.getSingleCycleLayouter();
    cycleLayouter.setMinimalNodeDistance( op.getInt( MINIMAL_NODE_DISTANCE ) );
    cycleLayouter.setAutomaticRadius( op.getBool( CHOOSE_RADIUS_AUTOMATICALLY ) );
    cycleLayouter.setFixedRadius( op.getInt( FIXED_RADIUS ) );

    treeLayouter.setPreferredChildWedge( op.getInt( PREFERRED_CHILD_WEDGE ) );
    treeLayouter.setMinimalEdgeLength( op.getInt( MINIMAL_EDGE_LENGTH ) );
    treeLayouter.setCompactnessFactor( op.getDouble( COMPACTNESS_FACTOR ) );
    treeLayouter.setAllowOverlaps( op.getBool( ALLOW_OVERLAPS ) );

    // initialize potential grouping information
    GroupLayoutConfigurator glc = new GroupLayoutConfigurator( getGraph2D() );
    Graph2D graph = getGraph2D();
    try {
      // register grouping relevant DataProviders
      glc.prepareAll();

      if ( op.getString( LAYOUT_STYLE ).equals( CIRCULAR_CUSTOM_GROUPS ) ) {
        //Set up grouping key for custom layout style
        //This acts as an adapter for grouping structure to circular grouping keys
        graph.addDataProvider( CircularLayouter.CIRCULAR_CUSTOM_GROUPS_DPKEY,
            graph.getDataProvider( GroupingKeys.PARENT_NODE_ID_DPKEY ) );
      }

      launchLayouter( layouter );
    } finally {
      // make sure the DataProviders will always be unregistered
      if ( op.getString( LAYOUT_STYLE ).equals( CIRCULAR_CUSTOM_GROUPS ) ) {
        //Remove temporary set up data providers from graph
        graph.removeDataProvider( CircularLayouter.CIRCULAR_CUSTOM_GROUPS_DPKEY );
      }
      glc.restoreAll();
    }
  }
}
