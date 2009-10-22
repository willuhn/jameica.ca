/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/dialogs/SelectIssuerDialog.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/22 17:27:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.model.EntryListModel;
import de.willuhn.jameica.ca.gui.model.ListItem;
import de.willuhn.jameica.ca.gui.part.EntryListTable;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.internal.buttons.Cancel;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Dialog zur Auswahl eines Aussteller-Zertifikates.
 */
public class SelectIssuerDialog extends AbstractDialog
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  private Entry entry = null;
  
  /**
   * ct.
   * @param position
   */
  public SelectIssuerDialog(int position)
  {
    super(position);
    super.setTitle(i18n.tr("Aussteller-Zertifikat"));
    super.setSize(500,SWT.DEFAULT);
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return this.entry;
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    SimpleContainer container = new SimpleContainer(parent.getParent());
    container.addText(i18n.tr("Bitte wählen Sie das Aussteller-Zertifikate aus, mit dem der neue " +
    		                      "Schlüssel signiert werden soll.\nKlicken Sie alternativ auf \"Kein Aussteller\" wenn" +
    		                      "Sie ein selbstsigniertes Zertifikate erstellen möchten."),true);
    
    final EntryListTable table = new EntryListTable(new EntryListModel().getIssuer(),new Action()
    {
      /**
       * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
       */
      public void handleAction(Object context) throws ApplicationException
      {
        if (context == null || !(context instanceof ListItem))
          return;
        entry = ((ListItem)context).getEntry();
        close(); // Dialog schliessen
      }
    });
    table.setSummary(false);
    table.setContextMenu(null); // Contextmenu entfernen - ist im Dialog nicht erlaubt
    
    table.paint(container.getComposite());
    
    ButtonArea buttons = container.createButtonArea(3);
    buttons.addButton(i18n.tr("Übernehmen"),new Action()
    {
      /**
       * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
       */
      public void handleAction(Object context) throws ApplicationException
      {
        Object sel = table.getSelection();
        if (sel == null || !(sel instanceof ListItem))
          return;
        entry = ((ListItem)sel).getEntry();
        close(); // Dialog schliessen
      }
    },null,true,"ok.png");
    buttons.addButton(i18n.tr("Kein Aussteller"),new Action()
    {
      /**
       * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
       */
      public void handleAction(Object context) throws ApplicationException
      {
        entry = null;
        close();
      }
    },null,false,"edit-undo.png");
    buttons.addButton(new Cancel());
  }

}


/**********************************************************************
 * $Log: SelectIssuerDialog.java,v $
 * Revision 1.1  2009/10/22 17:27:08  willuhn
 * @N Auswahl des Ausstellers via DialogInput
 *
 **********************************************************************/
