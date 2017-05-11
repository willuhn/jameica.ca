/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.model;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.Settings;
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
      if ((Settings.isCheckCA() && e.isCA()) || e.getIssuer() == null)
        list.add(new ListItem(e));
    }
    
    return PseudoIterator.fromArray(list.toArray(new ListItem[list.size()]));
  }
}
