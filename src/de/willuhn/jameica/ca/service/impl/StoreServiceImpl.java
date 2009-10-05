/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/service/impl/StoreServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/05 16:02:38 $
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
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Implementierung des Keystore-Services.
 */
public class StoreServiceImpl implements StoreService
{
  private Store store = null;

  /**
   * @see de.willuhn.jameica.ca.service.StoreService#getEntries()
   */
  public List<Entry> getEntries() throws RemoteException
  {
    try
    {
      return this.store.getEntries();
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to load entries from keystore",e);
    }
  }

  /**
   * @see de.willuhn.jameica.ca.service.StoreService#store(de.willuhn.jameica.ca.store.Entry)
   */
  public void store(Entry entry) throws RemoteException
  {
    try
    {
      this.store.store(entry);
    }
    catch (Exception re)
    {
      throw new RemoteException("unable to store certificate",re);
    }
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "keystore";
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
      File file = new File(dir,"jameica.ca.keystore");
      this.store = new Store(file,new Callback()
      {
        /**
         * @see de.willuhn.jameica.ca.store.Callback#getStorePassword()
         */
        public char[] getStorePassword() throws Exception
        {
          return Application.getCallback().getPassword().toCharArray();
        }
        /**
         * @see de.willuhn.jameica.ca.store.Callback#getPassword(de.willuhn.jameica.ca.store.Entry)
         */
        public char[] getPassword(Entry entry) throws Exception
        {
          return getStorePassword();
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
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
