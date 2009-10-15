/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/action/EntryExport.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/15 16:01:28 $
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
import de.willuhn.jameica.ca.gui.dialogs.EntryExportDialog;
import de.willuhn.jameica.ca.gui.model.ListItem;
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
 * Action zum Exportieren eines Schluessels.
 */
public class EntryExport implements Action
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
      EntryExportDialog d = new EntryExportDialog(e,EntryExportDialog.POSITION_CENTER);
      d.open();
      Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.export").sendMessage(new QueryMessage(e));
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Schlüssel exportiert"),StatusBarMessage.TYPE_SUCCESS));
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
      Logger.error("error while exporting key",ex);
      throw new ApplicationException(i18n.tr("Fehler beim Export des Schlüssels"),ex);
    }
  }

}


/**********************************************************************
 * $Log: EntryExport.java,v $
 * Revision 1.1  2009/10/15 16:01:28  willuhn
 * @N Schluessel-Export
 *
 **********************************************************************/
