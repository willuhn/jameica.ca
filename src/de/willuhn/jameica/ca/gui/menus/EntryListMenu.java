/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
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
    this.addItem(new CheckedSingleContextMenuItem(i18n.tr("Exportieren..."),new EntryExport(),"key-export.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new CheckedSingleContextMenuItem(i18n.tr("Löschen..."),new EntryDelete(),"user-trash-full.png"));
    this.addItem(ContextMenuItem.SEPARATOR);
    this.addItem(new ContextMenuItem(i18n.tr("Schlüssel erstellen..."),new EntryCreate(),"key-new.png"));
    this.addItem(new ContextMenuItem(i18n.tr("Schlüssel importieren..."),new EntryImport(),"key-import.png"));
  }

}
