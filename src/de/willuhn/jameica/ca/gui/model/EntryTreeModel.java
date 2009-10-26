/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/model/EntryTreeModel.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/10/26 23:40:37 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.model;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.system.Application;

/**
 * Implementiert ein Model, welches die Schluessel des Keystore als Tree liefert.
 */
public class EntryTreeModel
{
  /**
   * Liefert eine Liste der Schluessel.
   * Jedoch nur die, welche kein Ausssteller-Zertifikat haben.
   * Das sind also nur CA- oder selfsigned Zertifikate.
   * @return Liste der Schluessel.
   * @throws Exception
   */
  public GenericIterator getItems() throws Exception
  {
    List<ListItem> list = new ArrayList<ListItem>();
    StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
    Store store = service.getStore();
    List<Entry> entries = store.getEntries();
    for (Entry e:entries)
    {
      if ((Entry.CHECK_CA && e.isCA()) || e.getIssuer() == null)
        list.add(new ListItem(e));
    }
    
    return PseudoIterator.fromArray(list.toArray(new ListItem[list.size()]));
  }
}


/**********************************************************************
 * $Log: EntryTreeModel.java,v $
 * Revision 1.3  2009/10/26 23:40:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/22 17:27:08  willuhn
 * @N Auswahl des Ausstellers via DialogInput
 *
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/
