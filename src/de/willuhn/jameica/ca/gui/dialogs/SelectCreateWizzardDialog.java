/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/dialogs/SelectCreateWizzardDialog.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/15 11:50:43 $
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.wizzard.CreateCertificateWizzard;
import de.willuhn.jameica.ca.gui.wizzard.WizzardUtil;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.internal.buttons.Cancel;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Dialog zur Auswahl des Wizzards fuer die Erstellung eines neuen Zertifikates.
 */
public class SelectCreateWizzardDialog extends AbstractDialog
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private CreateCertificateWizzard wizzard = null;
  
  /**
   * ct.
   * @param position
   */
  public SelectCreateWizzardDialog(int position)
  {
    super(position);
    this.setSize(400,SWT.DEFAULT);
    this.setTitle(i18n.tr("Assistent zur Erstellung eines Schlüssels"));
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return this.wizzard;
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    // Dialog bei Druck auf ESC automatisch schliessen
    parent.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.ESC)
          throw new OperationCanceledException("wizzard cancelled");
      }
    });

    SimpleContainer c = new SimpleContainer(parent);
    c.addText(i18n.tr("Bitte wählen Sie den Assistenten aus, den Sie zur Erstellung des " +
    		              "neuen Schlüssels nutzen möchten."),true);
    
    final SelectInput select = new SelectInput(WizzardUtil.getWizzards(),null);
    select.setAttribute("name");
    select.setName(i18n.tr("Assistent"));
    select.setPleaseChoose(i18n.tr("Bitte wählen..."));
    
    c.addInput(select);
    
    ButtonArea buttons = c.createButtonArea(2);
    buttons.addButton(i18n.tr("Übernehmen"),new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        CreateCertificateWizzard selected = (CreateCertificateWizzard) select.getValue();
        if (selected == null)
          return;
        
        wizzard = selected;
        close();
      }
    },null,true,"ok.png");
    buttons.addButton(new Cancel());
  }

}


/**********************************************************************
 * $Log: SelectCreateWizzardDialog.java,v $
 * Revision 1.1  2009/10/15 11:50:43  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 **********************************************************************/
