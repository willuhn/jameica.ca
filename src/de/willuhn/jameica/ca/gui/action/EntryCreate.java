/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/action/EntryCreate.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/15 22:55:30 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.action;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.dialogs.SelectCreateWizzardDialog;
import de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Startet den Wizzard zum Erstellen eines neuen Zertifikates.
 */
public class EntryCreate implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    try
    {
      // Wenn wir als Parameter bereits einen Wizzard uebergeben gekriegt haben,
      // nehmen wir gleich den. Ansonsten fragen wir den User.
      CertificateWizzard wizzard = null;
      if (context != null && (context instanceof CertificateWizzard))
      {
        wizzard = (CertificateWizzard) context;
      }
      else
      {
        SelectCreateWizzardDialog d = new SelectCreateWizzardDialog(SelectCreateWizzardDialog.POSITION_CENTER);
        wizzard = (CertificateWizzard) d.open();
      }
      
      if (wizzard == null)
        return;
      
      GUI.startView(de.willuhn.jameica.ca.gui.view.EntryCreate.class,wizzard);
    }
    catch (OperationCanceledException oce)
    {
      Logger.info(oce.getMessage());
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen des neuen Schlüssels"),e);
    }
  }

}


/**********************************************************************
 * $Log: EntryCreate.java,v $
 * Revision 1.2  2009/10/15 22:55:30  willuhn
 * @N Wizzard zum Erstellen von Hibiscus Payment-Server Lizenzen
 *
 * Revision 1.1  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 **********************************************************************/
