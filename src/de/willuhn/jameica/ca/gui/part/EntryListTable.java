/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.part;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.menus.EntryListMenu;
import de.willuhn.jameica.ca.gui.model.EntryListModel;
import de.willuhn.jameica.ca.gui.model.ListItem;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.Font;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Implementiert eine vorkonfigurierte Liste mit den Schluesseln.
 */
public class EntryListTable extends TablePart
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * ct.
   * @param action Auszufuehrende Aktion bei Doppelklick.
   * @throws Exception
   */
  public EntryListTable(Action action) throws Exception
  {
    this(new EntryListModel().getItems(),action);
  }

  /**
   * ct.
   * @param items Liste der Elemente.
   * @param action
   * @throws Exception
   */
  public EntryListTable(List<ListItem> items, Action action) throws Exception
  {
    super(items,action);
    
    this.setContextMenu(new EntryListMenu());
    this.addColumn(i18n.tr("Ausgestellt für"),"subject");
    this.addColumn(i18n.tr("Organisation"),"organization");
    this.addColumn(i18n.tr("Aussteller"),"issuer");
    this.addColumn(i18n.tr("Gültig von"),"validFrom",new DateFormatter());
    this.addColumn(i18n.tr("Gültig bis"),"validTo",new DateFormatter());
    
    this.setRememberColWidths(true);
    this.setRememberOrder(true);
    this.setRememberState(true);
    this.setSummary(true);
    
    this.setFormatter(new TableFormatter()
    {
      /**
       * @see de.willuhn.jameica.gui.formatter.TableFormatter#format(org.eclipse.swt.widgets.TableItem)
       */
      public void format(TableItem item)
      {
        try
        {
          // Abgelaufene Schluessel zeigen wir rot an.
          ListItem line = (ListItem) item.getData();
          Date validTo = line.getValidTo();
          if (validTo != null && validTo.before(new Date()))
            item.setForeground(Color.ERROR.getSWTColor());
          else
            item.setForeground(Color.FOREGROUND.getSWTColor());
          
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
   * @see de.willuhn.jameica.gui.parts.TablePart#paint(org.eclipse.swt.widgets.Composite)
   */
  public synchronized void paint(Composite parent) throws RemoteException
  {
    final MessageConsumer add = new MyMessageConsumer(true);
    Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.import").registerMessageConsumer(add);
    parent.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e)
      {
        Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.import").unRegisterMessageConsumer(add);
      }
    });
    final MessageConsumer del = new MyMessageConsumer(false);
    Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.delete").registerMessageConsumer(del);
    parent.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e)
      {
        Application.getMessagingFactory().getMessagingQueue("jameica.ca.entry.delete").unRegisterMessageConsumer(del);
      }
    });

    super.paint(parent);
    this.sort();
  }

  /**
   * Damit werden wir benachrichtigt, wenn neue Schluessel importiert wurden.
   * Wir koennen sie dann live zur Tabelle hinzufuegen.
   */
  private class MyMessageConsumer implements MessageConsumer
  {
    boolean add = true;
    
    /**
     * ct.
     * @param add true fuer Hinzufuegen, false fuer Entfernen.
     */
    private MyMessageConsumer(boolean add)
    {
      this.add = add;
    }
    
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
      QueryMessage msg = (QueryMessage) message;
      final Object data = msg.getData();
      if (data == null || !(data instanceof Entry))
        return;
      
      GUI.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          try
          {
            if (add)
              addItem(new ListItem((Entry)data));
            else
              removeItem(new ListItem((Entry)data));
            sort();
          }
          catch (Exception e)
          {
            Logger.error("error while adding item",e);
          }
        }
      });
    }
    
  }
  
}
