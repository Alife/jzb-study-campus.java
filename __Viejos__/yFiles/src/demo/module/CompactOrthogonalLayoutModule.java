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

import y.layout.PartitionLayouter;
import y.layout.Layouter;
import y.layout.ComponentLayouter;
import y.layout.grouping.GroupNodeHider;
import y.layout.orthogonal.CompactOrthogonalLayouter;
import y.layout.orthogonal.OrthogonalLayouter;
import y.layout.router.ChannelEdgeRouter;
import y.module.LayoutModule;
import y.option.OptionHandler;
import y.option.ConstraintManager;
import y.option.OptionGroup;
import y.view.hierarchy.GroupLayoutConfigurator;

import java.awt.Dimension;

/**
 * This module represents an interactive configurator and launcher for
 * {@link y.layout.orthogonal.CompactOrthogonalLayouter}.
 *
 */
public class CompactOrthogonalLayoutModule extends LayoutModule {
  private static final String NAME = "COMPACT_ORTHOGONAL";

  private static final String ORTHOGONAL_LAYOUT_STYLE = "ORTHOGONAL_LAYOUT_STYLE";
  private static final String GRID = "GRID";

  private static final String NORMAL = "NORMAL";
  private static final String NORMAL_TREE = "NORMAL_TREE";
  private static final String FIXED_MIXED = "FIXED_MIXED";
  private static final String FIXED_BOX_NODES = "FIXED_BOX_NODES";

  private static final String ASPECT_RATIO = "ASPECT_RATIO";
  private static final String USE_VIEW_ASPECT_RATIO = "USE_VIEW_ASPECT_RATIO";

  private static final String PLACEMENT_STRATEGY = "PLACEMENT_STRATEGY";
  private static final String STYLE_ROWS = "STYLE_ROWS";
  private static final String STYLE_PACKED_COMPACT_RECTANGLE = "STYLE_PACKED_COMPACT_RECTANGLE";

  // ChannelInterEdgeRouter stuff
  private static final String INTER_EDGE_ROUTER = "INTER_EDGE_ROUTER";
  private static final String ROUTE_ALL_EDGES = "ROUTE_ALL_EDGES";

  // ChannelEdgeRouter stuff
  private static final String MINIMUM_DISTANCE = "MINIMUM_DISTANCE";
  private static final String CENTER_TO_SPACE_RATIO = "SPACE_DRIVEN_VS_CENTER_DRIVEN_SEARCH";
  private static final String EDGE_CROSSING_COST = "CROSSING_COST";

  // for the option handler
  private static final String[] COMPONENT_STYLE_ENUM = {
    STYLE_ROWS,
    STYLE_PACKED_COMPACT_RECTANGLE
  };
  private static final String[] STYLE_ENUM = {
          NORMAL, NORMAL_TREE, FIXED_MIXED, FIXED_BOX_NODES
  };


  public CompactOrthogonalLayoutModule() {
    super (NAME,"yFiles Layout Team",
           "Compact Orthogonal Layouter");
    setPortIntersectionCalculatorEnabled(true);
  }

  public OptionHandler createOptionHandler() {
    OptionHandler op = new OptionHandler(getModuleName());
    ConstraintManager cm =  new ConstraintManager(op);
    OptionGroup og;


    // use an instance of the layouter as a defaults provider
    CompactOrthogonalLayouter layouter = new CompactOrthogonalLayouter();
    prepare(layouter);

    OrthogonalLayouter cl = (OrthogonalLayouter)layouter.getCoreLayouter();
    op.addEnum(ORTHOGONAL_LAYOUT_STYLE, STYLE_ENUM, cl.getLayoutStyle());


    PartitionLayouter.ComponentPartitionPlacer cpp = (PartitionLayouter.ComponentPartitionPlacer)layouter.getPartitionPlacer();
    op.addEnum(PLACEMENT_STRATEGY, COMPONENT_STYLE_ENUM, cpp.getComponentLayouter().getStyle());

    op.addBool(USE_VIEW_ASPECT_RATIO, true);
    op.addDouble(ASPECT_RATIO, layouter.getAspectRatio());
    cm.setEnabledOnValueEquals(USE_VIEW_ASPECT_RATIO, Boolean.FALSE, ASPECT_RATIO);

    op.addInt(GRID, layouter.getGridSpacing());


    // ChannelInterEdgeRouter stuff
    og = new OptionGroup();
    og.setAttribute(OptionGroup.ATTRIBUTE_TITLE, INTER_EDGE_ROUTER);
    PartitionLayouter.ChannelInterEdgeRouter cier = (PartitionLayouter.ChannelInterEdgeRouter)layouter.getInterEdgeRouter();
    og.addItem(op.addBool(ROUTE_ALL_EDGES, !cier.isRouteInterEdgesOnly()));

    // ChannelEdgeRouter stuff
    ChannelEdgeRouter.OrthogonalShortestPathPathFinder osppf = (ChannelEdgeRouter.OrthogonalShortestPathPathFinder)cier.getChannelEdgeRouter().getPathFinderStrategy();

    // path finding strategy properties
    og.addItem(op.addInt(MINIMUM_DISTANCE, osppf.getMinimumDistance()));
    og.addItem(op.addDouble(EDGE_CROSSING_COST, osppf.getCrossingCost()));
    og.addItem(op.addDouble(CENTER_TO_SPACE_RATIO, osppf.getCenterToSpaceRatio(), 0, 1));

    return op;
  }


  private void prepare( CompactOrthogonalLayouter layouter ) {
    PartitionLayouter.InterEdgeRouter ier = layouter.getInterEdgeRouter();
    if (!(ier instanceof PartitionLayouter.ChannelInterEdgeRouter)) {
      ier = new PartitionLayouter.ChannelInterEdgeRouter();
      layouter.setInterEdgeRouter(ier);
    }
    Layouter pfs = ((PartitionLayouter.ChannelInterEdgeRouter)layouter.getInterEdgeRouter()).getChannelEdgeRouter().getPathFinderStrategy();
    if (!(pfs instanceof ChannelEdgeRouter.OrthogonalShortestPathPathFinder)) {
      pfs = new ChannelEdgeRouter.OrthogonalShortestPathPathFinder();
      ((PartitionLayouter.ChannelInterEdgeRouter)layouter.getInterEdgeRouter()).getChannelEdgeRouter().setPathFinderStrategy(pfs);
    }

    PartitionLayouter.PartitionPlacer pp = layouter.getPartitionPlacer();
    if (!(pp instanceof PartitionLayouter.ComponentPartitionPlacer)) {
      pp = new PartitionLayouter.ComponentPartitionPlacer();
      layouter.setPartitionPlacer(pp);
    }
    Layouter cl = layouter.getCoreLayouter();
    if (!(cl instanceof OrthogonalLayouter)) {
      cl = new OrthogonalLayouter();
      layouter.setCoreLayouter(cl);
    }
  }

  private void applyOptions( OptionHandler oh, PartitionLayouter.ChannelInterEdgeRouter router ) {
    router.setRouteInterEdgesOnly(!oh.getBool(ROUTE_ALL_EDGES));
  }

  private void applyOptions( OptionHandler oh, PartitionLayouter.ComponentPartitionPlacer placer ) {
    if (STYLE_PACKED_COMPACT_RECTANGLE.equals(oh.get(PLACEMENT_STRATEGY))) {
      placer.getComponentLayouter().setStyle(ComponentLayouter.STYLE_PACKED_COMPACT_RECTANGLE);
    }
    else if (STYLE_ROWS.equals(oh.get(PLACEMENT_STRATEGY))) {
      placer.getComponentLayouter().setStyle(ComponentLayouter.STYLE_ROWS);
    }
  }

  private void applyOptions( OptionHandler oh, OrthogonalLayouter layouter ) {
    switch (OptionHandler.getIndex(STYLE_ENUM, oh.getString(ORTHOGONAL_LAYOUT_STYLE))) {
      default:
      case 0:
        layouter.setLayoutStyle(OrthogonalLayouter.NORMAL_STYLE);
        break;
      case 1:
        layouter.setLayoutStyle(OrthogonalLayouter.NORMAL_TREE_STYLE);
        break;
      case 2:
        layouter.setLayoutStyle(OrthogonalLayouter.FIXED_MIXED_STYLE);
        break;
      case 3:
        layouter.setLayoutStyle(OrthogonalLayouter.FIXED_BOX_STYLE);
        break;
    }
  }

  private void applyOptions( OptionHandler oh, CompactOrthogonalLayouter layouter ) {
    layouter.setGridSpacing(oh.getInt(GRID));

    final double ar;
    if (oh.getBool(USE_VIEW_ASPECT_RATIO) && getGraph2DView() != null) {
      final Dimension dim = getGraph2DView().getSize();
      ar = dim.getWidth()/dim.getHeight();
    } else {
      ar = oh.getDouble(ASPECT_RATIO);
    }

    // this needs to be done as a final step since it will reconfigure
    // layout stages which support aspect ratio accordingly
    layouter.setAspectRatio(ar);
  }

  public void mainrun() {
    final OptionHandler op = getOptionHandler();

    CompactOrthogonalLayouter compactOrthogonal = new CompactOrthogonalLayouter();
    prepare(compactOrthogonal);

    PartitionLayouter.ChannelInterEdgeRouter router = (PartitionLayouter.ChannelInterEdgeRouter)compactOrthogonal.getInterEdgeRouter();
    applyOptions(op, router);

    PartitionLayouter.ComponentPartitionPlacer placer = (PartitionLayouter.ComponentPartitionPlacer) compactOrthogonal.getPartitionPlacer();
    applyOptions(op, placer);

    OrthogonalLayouter orthogonalCore = (OrthogonalLayouter) compactOrthogonal.getCoreLayouter();
    applyOptions(op, orthogonalCore);

    applyOptions(op, compactOrthogonal);


    // initialize potential grouping information
    GroupLayoutConfigurator glc = new GroupLayoutConfigurator(getGraph2D());
    try {
      // register grouping relevant DataProviders
      glc.prepareAll();
      // launch layouter in buffered mode
      launchLayouter(new GroupNodeHider(compactOrthogonal));
    } finally {
      // make sure the DataProviders will always be unregistered
      glc.restoreAll();
    }
  }
}
