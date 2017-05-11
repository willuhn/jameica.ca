/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.Settings;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.system.Application;

/**
 * Implementiert ein Model, welches die Schluessel des Keystore als Liste (Tabelle) liefert.
 */
public class EntryListModel
{
  /**
   * Liefert eine Liste aller Schluessel.
   * @return Liste aller Schluessel.
   * @throws Exception
   */
  public List<ListItem> getItems() throws Exception
  {
    List<ListItem> list = new ArrayList<ListItem>();
    StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
    Store store = service.getStore();
    List<Entry> entries = store.getEntries();
    for (Entry e:entries)
    {
      list.add(new ListItem(e));
    }
    return list;
  }
  
  /**
   * Liefert eine Liste der Schluessel, die als Aussteller taugen.
   * @return Liste von aussteller-tauglichen Schluesseln.
   * @throws Exception
   */
  public List<ListItem> getIssuer() throws Exception
  {
    // Wir holen uns erstmal alle und schmeissen dann die unbrauchbaren raus
    List<ListItem> result = new ArrayList<ListItem>();
    List<ListItem> all = this.getItems();
    
    for (ListItem i:all)
    {
      // Abgelaufen
      Date validTo = i.getValidTo();
      if (validTo != null && validTo.before(new Date()))
        continue;

      Entry e = i.getEntry();
      
      // Keine CA unc CA-Pruefung aktiv
      if (Settings.isCheckCA() && !e.isCA())
        continue;
      
      // Kein Private-Key zum Unterschreiben da
      if (e.getPrivateKey() == null)
        continue;

      result.add(i);
    }
    return result;
  }
}
