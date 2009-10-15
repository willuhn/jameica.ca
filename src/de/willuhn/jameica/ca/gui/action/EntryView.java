/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/action/EntryView.java,v $
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

package de.willuhn.jameica.ca.gui.action;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.model.ListItem;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.CertificateDetailDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Action zum Anzeigen eines Schluessels.
 */
public class EntryView implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null)
      return;
    
    Entry e = null;
    if (context instanceof ListItem)
      e = ((ListItem)context).getEntry();
    else
      e = (Entry) context;

    try
    {
      CertificateDetailDialog d = new CertificateDetailDialog(CertificateDetailDialog.POSITION_CENTER,e.getCertificate());
      d.open();
    }
    catch (OperationCanceledException oce)
    {
      // ignore
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception ex)
    {
      Logger.error("error while displaying certificate",ex);
      throw new ApplicationException(i18n.tr("Fehler beim Anzeigen des Zertifikates"),ex);
    }
  }

}


/**********************************************************************
 * $Log: EntryView.java,v $
 * Revision 1.1  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 **********************************************************************/
