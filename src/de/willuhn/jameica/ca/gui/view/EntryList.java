/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/view/EntryList.java,v $
 * $Revision: 1.2 $
 * $Date: 2012/03/28 22:19:22 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.view;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.action.EntryCreate;
import de.willuhn.jameica.ca.gui.action.EntryImport;
import de.willuhn.jameica.ca.gui.controller.EntryListControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * View zum Anzeigen der Schluesselliste.
 */
public class EntryList extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final EntryListControl control = new EntryListControl(this);
    GUI.getView().setTitle(i18n.tr("Installierte Zertifikate"));
    
    control.getTable().paint(this.getParent());

    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Schlüssel importieren..."),new EntryImport(),null,false,"key-import.png");
    buttons.addButton(i18n.tr("Schlüssel erstellen..."),new EntryCreate(),null,false,"key-new.png");
    buttons.paint(this.getParent());
  }

}


/**********************************************************************
 * $Log: EntryList.java,v $
 * Revision 1.2  2012/03/28 22:19:22  willuhn
 * @R Back-Button entfernt
 * @C Umstellung auf neue ButtonArea
 *
 * Revision 1.1  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.2  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.1  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 **********************************************************************/
