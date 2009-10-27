/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/search/EntrySearchProvider.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/27 16:47:20 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.search;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.action.EntryView;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.search.Result;
import de.willuhn.jameica.search.SearchProvider;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung eines Search-Providers fuer die Suche im Keystore.
 */
public class EntrySearchProvider implements SearchProvider
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * @see de.willuhn.jameica.search.SearchProvider#getName()
   */
  public String getName()
  {
    return i18n.tr("Zertifikate");
  }

  /**
   * @see de.willuhn.jameica.search.SearchProvider#search(java.lang.String)
   */
  public List search(String search) throws RemoteException, ApplicationException
  {
    if (search == null || search.length() == 0)
      return null;
    
    search = search.toLowerCase();
    try
    {
      List<Result> results = new ArrayList<Result>();
      
      StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
      Store store = service.getStore();
      List<Entry> entries = store.getEntries();
      for (Entry e:entries)
      {
        StringBuffer sb = new StringBuffer();
        Principal subject = new Certificate(e.getCertificate()).getSubject();
        sb.append(" ");
        sb.append(subject.getAttribute(Principal.LOCALITY));
        sb.append(" ");
        sb.append(subject.getAttribute(Principal.ORGANIZATION));
        sb.append(" ");
        sb.append(subject.getAttribute(Principal.DISTINGUISHED_NAME));
        sb.append(" ");
        sb.append(subject.getAttribute(Principal.COMMON_NAME));
        sb.append(" ");
        sb.append(subject.getAttribute(Principal.ORGANIZATIONAL_UNIT));
        sb.append(" ");
        sb.append(subject.getAttribute(Principal.STATE));
        
        String s = sb.toString().toLowerCase();
        if (s.indexOf(search) != -1)
          results.add(new MyResult(e));
      }
      
      return results;
    }
    catch (Exception e)
    {
      Logger.error("error while searching in certificates",e);
    }
    return null;
  }
  
  /**
   * Hilfsklasse mit einem Suchergebnis.
   */
  private class MyResult implements Result
  {
    private Entry entry = null;
    
    /**
     * ct.
     * @param e
     */
    private MyResult(Entry e)
    {
      this.entry = e;
    }

    /**
     * @see de.willuhn.jameica.search.Result#execute()
     */
    public void execute() throws RemoteException, ApplicationException
    {
      new EntryView().handleAction(this.entry);
    }

    /**
     * @see de.willuhn.jameica.search.Result#getName()
     */
    public String getName()
    {
      return this.entry.getCommonName();
    }
  }

}


/**********************************************************************
 * $Log: EntrySearchProvider.java,v $
 * Revision 1.1  2009/10/27 16:47:20  willuhn
 * @N Support zum Ueberschreiben/als Kopie anlegen beim Import
 * @N Integration in Jameica-Suche
 *
 **********************************************************************/
