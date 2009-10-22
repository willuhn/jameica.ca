/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/model/EntryListModel.java,v $
 * $Revision: 1.5 $
 * $Date: 2009/10/22 17:27:08 $
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
import java.util.Date;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
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
      if (Entry.CHECK_CA && !e.isCA())
        continue;
      
      // Kein Private-Key zum Unterschreiben da
      if (e.getPrivateKey() == null)
        continue;

      result.add(i);
    }
    return result;
  }
}


/**********************************************************************
 * $Log: EntryListModel.java,v $
 * Revision 1.5  2009/10/22 17:27:08  willuhn
 * @N Auswahl des Ausstellers via DialogInput
 *
 * Revision 1.4  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 * Revision 1.3  2009/10/07 17:09:11  willuhn
 * @N Schluessel loeschen
 *
 * Revision 1.2  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.1  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 **********************************************************************/
