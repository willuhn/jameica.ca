/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/part/EntryListTree.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/13 00:26:32 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.part;

import java.util.Date;

import org.eclipse.swt.widgets.TreeItem;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.menus.EntryListMenu;
import de.willuhn.jameica.ca.gui.model.EntryTreeModel;
import de.willuhn.jameica.ca.gui.model.ListItem;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.TreeFormatter;
import de.willuhn.jameica.gui.parts.TreePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.Font;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Implementiert einen vorkonfigurierten Tree mit den Schluesseln.
 */
public class EntryListTree extends TreePart
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * ct.
   * @param action
   * @throws Exception
   */
  public EntryListTree(Action action) throws Exception
  {
    super(new EntryTreeModel().getItems(),action);

    this.setContextMenu(new EntryListMenu());
    this.addColumn(i18n.tr("Ausgestellt für"),"subject");
    this.addColumn(i18n.tr("Organisation"),"organization");
    this.addColumn(i18n.tr("Gültig von"),"validFrom",new DateFormatter());
    this.addColumn(i18n.tr("Gültig bis"),"validTo",new DateFormatter());
    
    this.setRememberColWidths(true);
    this.setRememberOrder(true);
    this.setRememberState(true);
    
    this.setFormatter(new TreeFormatter()
    {
      /**
       * @see de.willuhn.jameica.gui.formatter.TreeFormatter#format(org.eclipse.swt.widgets.TreeItem)
       */
      public void format(TreeItem item)
      {
        try
        {
          // Abgelaufene Schluessel zeigen wir rot an.
          ListItem line = (ListItem) item.getData();
          Date validTo = line.getValidTo();
          if (validTo != null && validTo.before(new Date()))
            item.setForeground(Color.ERROR.getSWTColor());
          else
            item.setForeground(Color.WIDGET_FG.getSWTColor());
          
          // CA-Zertifikate zeigen wir fett gedruckt an.
          if (line.isCA())
            item.setFont(Font.BOLD.getSWTFont());
          else
            item.setFont(Font.DEFAULT.getSWTFont());
          
          // Wir zeigen unterschiedliche Icons an, wenn nur Public-Key vorhanden ist oder beides
          if (line.havePrivateKey())
            item.setImage(SWTUtil.getImage("key-pub+priv.png"));
          else
            item.setImage(SWTUtil.getImage("key-pub.png"));
        }
        catch (Exception e)
        {
          Logger.error("unable to format line",e);
        }
      }
    });
  }
}


/**********************************************************************
 * $Log: EntryListTree.java,v $
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/
