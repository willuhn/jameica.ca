/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/action/EntryImport.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/07 16:38:59 $
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
import de.willuhn.jameica.ca.gui.dialogs.EntryImportDialog;
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
 * Aktion zum Importieren eines Schluesselpaares.
 */
public class EntryImport implements Action
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    try
    {
      EntryImportDialog d = new EntryImportDialog(EntryImportDialog.POSITION_CENTER);
      Entry e = (Entry) d.open();
      StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
      service.getStore().store(e);

      Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.import").sendMessage(new QueryMessage(e));
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Schlüsselpaar importiert"),StatusBarMessage.TYPE_SUCCESS));
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
      Logger.error("error while importing keys",e);
      throw new ApplicationException(i18n.tr("Fehler beim Import der Schlüssel"),e);
    }
  }

}


/**********************************************************************
 * $Log: EntryImport.java,v $
 * Revision 1.1  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 **********************************************************************/
