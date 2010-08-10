/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/menus/EntryListMenu.java,v $
 * $Revision: 1.6 $
 * $Date: 2010/08/10 12:13:58 $
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
import de.willuhn.jameica.ca.gui.action.EntryCreate;
import de.willuhn.jameica.ca.gui.action.EntryDelete;
import de.willuhn.jameica.ca.gui.action.EntryExport;
import de.willuhn.jameica.ca.gui.action.EntryImport;
import de.willuhn.jameica.ca.gui.action.EntryView;
import de.willuhn.jameica.gui.parts.CheckedSingleContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.jameica.system.Application;
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
    this.addItem(new CheckedSingleContextMenuItem(i18n.tr("Öffnen..."),new EntryView(),"key-pub.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Erstellen..."),new EntryCreate(),"key-new.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Importieren..."),new EntryImport(),"key-import.png"));
    this.addItem(new CheckedSingleContextMenuItem(i18n.tr("Exportieren..."),new EntryExport(),"key-export.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedSingleContextMenuItem(i18n.tr("Löschen..."),new EntryDelete(),"user-trash-full.png"));
  }

}


/**********************************************************************
 * $Log: EntryListMenu.java,v $
 * Revision 1.6  2010/08/10 12:13:58  willuhn
 * @C kuerzere Menu-Labels
 *
 * Revision 1.5  2009/10/15 16:01:28  willuhn
 * @N Schluessel-Export
 *
 * Revision 1.4  2009/10/15 11:50:43  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.3  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 * Revision 1.2  2009/10/07 17:09:11  willuhn
 * @N Schluessel loeschen
 *
 * Revision 1.1  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 **********************************************************************/
