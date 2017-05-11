/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/service/impl/StoreServiceImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2009/10/27 16:47:20 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.service.impl;

import java.io.File;
import java.rmi.RemoteException;

import de.willuhn.jameica.ca.CallbackConsole;
import de.willuhn.jameica.ca.CallbackGui;
import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;

/**
 * Implementierung des Keystore-Services.
 */
public class StoreServiceImpl implements StoreService
{
  private Store store = null;

  /**
   * @see de.willuhn.jameica.ca.service.StoreService#getStore()
   */
  public Store getStore()
  {
    return this.store;
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "Keystore service";
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    return !this.isStarted();
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    return this.store != null;
  }

  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    if (this.isStarted())
    {
      Logger.warn("service allready started, skipping request");
      return;
    }
    
    try
    {
      String dir = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getWorkPath();
      final File file = new File(dir,"jameica.ca.keystore");
      
      final Callback cb = Application.inServerMode() ? new CallbackConsole() : new CallbackGui();
      this.store = new Store(file,new Callback()
      {
        /**
         * @see de.willuhn.jameica.ca.store.Callback#getPassword(java.lang.Object)
         */
        public char[] getPassword(Object context) throws Exception
        {
          // Beim Keystore selbst oder bei Entry-Objekten nehmen wir immer das Master-Passwort.
          if (context != null && (context.equals(file) || (context instanceof Entry)))
            return Application.getCallback().getPassword().toCharArray();
          
          return cb.getPassword(context);
        }

        /**
         * @see de.willuhn.jameica.ca.store.Callback#overwrite(de.willuhn.jameica.ca.store.Entry, de.willuhn.jameica.ca.store.Entry)
         */
        public boolean overwrite(Entry newEntry, Entry oldEntry) throws OperationCanceledException
        {
          return cb.overwrite(newEntry,oldEntry);
        }
        
        /**
         * @see de.willuhn.jameica.ca.store.Callback#overwrite(java.io.File)
         */
        @Override
        public boolean overwrite(File file) throws OperationCanceledException
        {
          return cb.overwrite(file);
        }
      });
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to load keystore",e);
    }
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!this.isStarted())
    {
      Logger.warn("service not started, skipping request");
      return;
    }
    
    this.store = null;
  }

}


/**********************************************************************
 * $Log: StoreServiceImpl.java,v $
 * Revision 1.5  2009/10/27 16:47:20  willuhn
 * @N Support zum Ueberschreiben/als Kopie anlegen beim Import
 * @N Integration in Jameica-Suche
 *
 * Revision 1.4  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.3  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 * Revision 1.2  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
