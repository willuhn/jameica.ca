/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/part/EntryListTree.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/15 15:25:25 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.part;

import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.menus.EntryListMenu;
import de.willuhn.jameica.ca.gui.model.EntryTreeModel;
import de.willuhn.jameica.ca.gui.model.ListItem;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.TreeFormatter;
import de.willuhn.jameica.gui.parts.TreePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.Font;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Implementiert einen vorkonfigurierten Tree mit den Schluesseln.
 */
public class EntryListTree extends TreePart
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private Composite comp = null;
  
  /**
   * ct.
   * @param action
   * @throws Exception
   */
  public EntryListTree(Action action) throws Exception
  {
    super(new EntryTreeModel().getItems(),action);

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

  /**
   * @see de.willuhn.jameica.gui.parts.TreePart#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    this.setContextMenu(new EntryListMenu());

    if (this.comp == null)
    {
      // Hilfs-Composite, damit wir dessen Inhalt sauber disposen koennen.
      this.comp = new Composite(parent, SWT.NONE);
      this.comp.setLayoutData(new GridData(GridData.FILL_BOTH));
      this.comp.setLayout(new GridLayout());

      final MessageConsumer mc = new ReloadMessageConsumer();
      Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.import").registerMessageConsumer(mc);
      this.comp.addDisposeListener(new DisposeListener() {
        public void widgetDisposed(DisposeEvent e)
        {
          Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.import").unRegisterMessageConsumer(mc);
        }
      });
      Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.delete").registerMessageConsumer(mc);
      this.comp.addDisposeListener(new DisposeListener() {
        public void widgetDisposed(DisposeEvent e)
        {
          Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.delete").unRegisterMessageConsumer(mc);
        }
      });
    }


    super.paint(this.comp);
  }
  
  
  /**
   * Laedt die aktuelle Ansicht neu.
   */
  private class ReloadMessageConsumer implements MessageConsumer
  {
    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
     */
    public boolean autoRegister()
    {
      return false;
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
     */
    public Class[] getExpectedMessageTypes()
    {
      return new Class[]{QueryMessage.class};
    }

    /**
     * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
     */
    public void handleMessage(Message message) throws Exception
    {
      GUI.getDisplay().syncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            // Tree wegwerfen und neu zeichnen
            SWTUtil.disposeChildren(comp);

            setList(new EntryTreeModel().getItems());
            paint(comp);
            comp.layout(true);
            comp.redraw();
          }
          catch (Exception e)
          {
            Logger.error("unable to redraw tree",e);
            Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler beim Aktualisieren"), StatusBarMessage.TYPE_ERROR));
          }
        }
      });
    }
    
  }
  
}


/**********************************************************************
 * $Log: EntryListTree.java,v $
 * Revision 1.2  2009/10/15 15:25:25  willuhn
 * @N Reload des Tree nach Erstellen/Loeschen eines Schluessels
 *
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/
