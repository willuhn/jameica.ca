/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/view/Attic/EntryTreeView.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/13 00:26:32 $
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
import de.willuhn.jameica.ca.gui.action.EntryImport;
import de.willuhn.jameica.ca.gui.controller.EntryTreeControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * View zum Anzeigen des Schluessel-Tree.
 */
public class EntryTreeView extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final EntryTreeControl control = new EntryTreeControl(this);
    GUI.getView().setTitle(i18n.tr("Installierte Zertifikate"));
    
    control.getTree().paint(this.getParent());

    ButtonArea buttons = new ButtonArea(getParent(),2);
    buttons.addButton(new Back(true));
    buttons.addButton(i18n.tr("Schlüssel importieren..."),new EntryImport(),null,false,"key-import.png");
  }

}


/**********************************************************************
 * $Log: EntryTreeView.java,v $
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/
