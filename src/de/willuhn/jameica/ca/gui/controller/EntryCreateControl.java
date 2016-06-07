/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/controller/EntryCreateControl.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/10/15 22:55:29 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.controller;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.action.EntryTree;
import de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.EntryFactory;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import de.willuhn.util.ProgressMonitor;

/**
 * Controller zum Erstellen eines neuen Schluessels.
 */
public class EntryCreateControl extends AbstractControl
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private Template template = null;
  
  /**
   * ct.
   * @param view
   */
  public EntryCreateControl(AbstractView view)
  {
    super(view);
  }
  
  /**
   * Liefert den vom User ausgewaehlten Wizzard.
   * @return der vom User ausgewaehlte Wizzard.
   */
  public CertificateWizzard getWizzard()
  {
    return (CertificateWizzard) this.getCurrentObject();
  }
  
  /**
   * Startet die Erstellung des Schluessels.
   */
  public void handleCreate()
  {
    try
    {
      Logger.info("creating key template from wizzard " + getWizzard().getName());
      this.template = getWizzard().create();
    }
    catch (ApplicationException ae)
    {
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(ae.getMessage(),StatusBarMessage.TYPE_ERROR));
      return;
    }

    // Da die Erzeugung abhaengig von der Schluessellaenge etwas dauern kann,
    // machen wir das in einem Hintergrund-Prozess.
    Application.getController().start(new BackgroundTask()
    {
      /**
       * @see de.willuhn.jameica.system.BackgroundTask#run(de.willuhn.util.ProgressMonitor)
       */
      public void run(ProgressMonitor monitor) throws ApplicationException
      {
        try
        {
          StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
          Store store = service.getStore();
          EntryFactory factory = store.getEntryFactory();
          
          Logger.info("creating new key");
          Entry entry = factory.create(template,monitor);
          
          Logger.info("store created key");
          store.store(entry);

          Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.create").sendMessage(new QueryMessage(entry));
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Schlüssel erstellt"),StatusBarMessage.TYPE_SUCCESS));

          monitor.setStatus(ProgressMonitor.STATUS_DONE);
          monitor.setPercentComplete(100);
          monitor.setStatusText(i18n.tr("Schlüssel erstellt"));

          // Wir wechseln zurueck zur Liste der Schluessel
          new EntryTree().handleAction(null);
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (OperationCanceledException oce)
        {
          throw oce;
        }
        catch (Exception e)
        {
          Logger.error("error while creating new key",e);
          throw new ApplicationException(i18n.tr("Fehler beim Erstellen des neuen Schlüssels: {0}",e.getMessage()),e);
        }
      }
    
      /**
       * Nicht unterbrechbar.
       * @see de.willuhn.jameica.system.BackgroundTask#isInterrupted()
       */
      public boolean isInterrupted()
      {
        return false;
      }
    
      /**
       * Nicht unterbrechbar.
       * @see de.willuhn.jameica.system.BackgroundTask#interrupt()
       */
      public void interrupt()
      {
      }
    });
    
    
  }

}


/**********************************************************************
 * $Log: EntryCreateControl.java,v $
 * Revision 1.3  2009/10/15 22:55:29  willuhn
 * @N Wizzard zum Erstellen von Hibiscus Payment-Server Lizenzen
 *
 * Revision 1.2  2009/10/15 15:25:25  willuhn
 * @N Reload des Tree nach Erstellen/Loeschen eines Schluessels
 *
 * Revision 1.1  2009/10/15 11:50:43  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 **********************************************************************/
