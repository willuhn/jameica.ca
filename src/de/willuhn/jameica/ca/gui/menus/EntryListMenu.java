/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/menus/EntryListMenu.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/07 17:09:11 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.menus;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.action.EntryDelete;
import de.willuhn.jameica.ca.gui.action.EntryImport;
import de.willuhn.jameica.ca.gui.model.EntryListModel.Line;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedSingleContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Contextmenu fuer die Liste der Schluessel.
 */
public class EntryListMenu extends ContextMenu
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * ct.
   */
  public EntryListMenu()
  {
    this.addItem(new ContextMenuItem(i18n.tr("Schlüssel importieren..."),new EntryImport(),"key-import.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedSingleContextMenuItem(i18n.tr("Schlüssel löschen..."),new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof Line))
          return;
        new EntryDelete().handleAction(((Line)context).getEntry());
      }
    },"user-trash-full.png"));
  }

}


/**********************************************************************
 * $Log: EntryListMenu.java,v $
 * Revision 1.2  2009/10/07 17:09:11  willuhn
 * @N Schluessel loeschen
 *
 * Revision 1.1  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 **********************************************************************/
