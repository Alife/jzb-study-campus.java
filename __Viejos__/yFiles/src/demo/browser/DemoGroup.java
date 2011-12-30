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
package demo.browser;

import java.net.URL;

/**
 * TODO: add documentation
 *
 */
public class DemoGroup implements Displayable
{
  String qualifiedName;
  String displayName;
  String summary;
  String description;
  URL base;

  DemoGroup()
  {
    this.qualifiedName = "";
    this.displayName = "";
    this.description = "";
  }

  public boolean isDemo()
  {
    return false;
  }

  public boolean isExecutable()
  {
    return false;
  }

  public String getQualifiedName()
  {
    return qualifiedName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public String getSummary()
  {
    return summary;
  }

  public String getDescription()
  {
    return description;
  }

  public URL getBase()
  {
    return base;
  }

  public String toString()
  {
    return "DemoGroup[qualifiedName" + getQualifiedName() +
           "; displayname=" + getDisplayName() +
           "; summary=" + getSummary() +
           "; base=" + getBase() +
           "; description=" + getDescription() + "]";
  }
}
