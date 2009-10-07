/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/part/EntryListTable.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/07 12:24:04 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.part;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.model.EntryListModel;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Implementiert eine vorkonfigurierte Liste mit den Schluesseln.
 */
public class EntryListTable extends TablePart
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * ct.
   * @param action
   * @throws Exception
   */
  public EntryListTable(Action action) throws Exception
  {
    super(new EntryListModel().getItems(),action);
    
    this.addColumn(i18n.tr("Ausgestellt für"),"subject");
    this.addColumn(i18n.tr("Ausgestellt von"),"issuer");
    this.addColumn(i18n.tr("Gültig von"),"validFrom",new DateFormatter());
    this.addColumn(i18n.tr("Gültig bis"),"validTo",new DateFormatter());
    
    this.setRememberColWidths(true);
    this.setRememberOrder(true);
    this.setRememberState(true);
    this.setSummary(true);
  }
}


/**********************************************************************
 * $Log: EntryListTable.java,v $
 * Revision 1.1  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 **********************************************************************/
