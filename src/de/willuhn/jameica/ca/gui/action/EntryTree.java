/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/action/EntryTree.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/15 11:50:42 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

/**
 * Aktion zum Anzeigen des Tree mit den Schluesseln.
 */
public class EntryTree implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    GUI.startView(de.willuhn.jameica.ca.gui.view.EntryTree.class,null);
  }

}


/**********************************************************************
 * $Log: EntryTree.java,v $
 * Revision 1.2  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/