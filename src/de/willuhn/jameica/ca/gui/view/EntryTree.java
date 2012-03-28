/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/view/EntryTree.java,v $
 * $Revision: 1.3 $
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
import de.willuhn.jameica.ca.gui.controller.EntryTreeControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * View zum Anzeigen des Schluessel-Tree.
 */
public class EntryTree extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    EntryTreeControl control = new EntryTreeControl(this);
    GUI.getView().setTitle(i18n.tr("Installierte Zertifikate"));
    
    control.getTree().paint(this.getParent());

    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Schlüssel importieren..."),new EntryImport(),null,false,"key-import.png");
    buttons.addButton(i18n.tr("Schlüssel erstellen..."),new EntryCreate(),null,false,"key-new.png");
    buttons.paint(this.getParent());
  }
}


/**********************************************************************
 * $Log: EntryTree.java,v $
 * Revision 1.3  2012/03/28 22:19:22  willuhn
 * @R Back-Button entfernt
 * @C Umstellung auf neue ButtonArea
 *
 * Revision 1.2  2009/10/15 15:25:25  willuhn
 * @N Reload des Tree nach Erstellen/Loeschen eines Schluessels
 *
 * Revision 1.1  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/
