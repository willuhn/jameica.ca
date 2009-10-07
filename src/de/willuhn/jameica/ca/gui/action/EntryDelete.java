/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/action/EntryDelete.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/07 17:09:11 $
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
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Loescht einen Entry aus dem Keystore.
 */
public class EntryDelete implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    if (context == null || !(context instanceof Entry))
      return;

    Entry e = (Entry) context;
    try
    {
      if (!Application.getCallback().askUser(i18n.tr("Möchten Sie diesen Schlüssel wirklich löschen?")))
        return;
      
      StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
      service.getStore().delete(e);

      Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.delete").sendMessage(new QueryMessage(e));
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Schlüssel gelöscht"),StatusBarMessage.TYPE_SUCCESS));
    }
    catch (OperationCanceledException oce)
    {
      Logger.info(oce.getMessage());
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception ex)
    {
      Logger.error("error while deleting key",ex);
      throw new ApplicationException(i18n.tr("Fehler beim Löschen des Schlüssels"),ex);
    }

  }

}


/**********************************************************************
 * $Log: EntryDelete.java,v $
 * Revision 1.1  2009/10/07 17:09:11  willuhn
 * @N Schluessel loeschen
 *
 **********************************************************************/
