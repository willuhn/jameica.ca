/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/controller/EntryTreeControl.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/15 11:50:43 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.controller;

import de.willuhn.jameica.ca.gui.action.EntryView;
import de.willuhn.jameica.ca.gui.part.EntryListTree;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;

/**
 * Controller fuer den Tree der Schluessel.
 */
public class EntryTreeControl extends AbstractControl
{
  private EntryListTree tree = null;

  /**
   * ct.
   * @param view
   */
  public EntryTreeControl(AbstractView view)
  {
    super(view);
  }
  
  /**
   * Liefert den Tree mit den Schluesseln.
   * @return Tree mit den Schluesseln.
   * @throws Exception
   */
  public EntryListTree getTree() throws Exception
  {
    if (this.tree == null)
      this.tree = new EntryListTree(new EntryView());
    return this.tree;
  }
}


/**********************************************************************
 * $Log: EntryTreeControl.java,v $
 * Revision 1.2  2009/10/15 11:50:43  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 * Revision 1.1  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 **********************************************************************/
