/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/view/EntryCreate.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/15 11:50:42 $
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
import de.willuhn.jameica.ca.gui.controller.EntryCreateControl;
import de.willuhn.jameica.ca.gui.wizzard.CreateCertificateWizzard;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.buttons.Back;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * View zum Erstellen eines neuen Zertifikates.
 */
public class EntryCreate extends AbstractView
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    final EntryCreateControl control = new EntryCreateControl(this);
    CreateCertificateWizzard wizzard = control.getWizzard();

    GUI.getView().setTitle(wizzard.getName());
    
    // Wir rufen die paint()-Funktion des Wizzards auf
    wizzard.paint(this.getParent());
    
    ButtonArea buttons = new ButtonArea(this.getParent(),2);
    buttons.addButton(new Back(false));
    buttons.addButton(i18n.tr("Schlüssel erstellen"),new Action()
    {
      /**
       * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
       */
      public void handleAction(Object context) throws ApplicationException
      {
        control.handleCreate();
      }
    },null,true,"key-new.png");
  }

}


/**********************************************************************
 * $Log: EntryCreate.java,v $
 * Revision 1.1  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 **********************************************************************/
