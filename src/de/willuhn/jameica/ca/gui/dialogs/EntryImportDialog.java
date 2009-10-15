/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/dialogs/EntryImportDialog.java,v $
 * $Revision: 1.5 $
 * $Date: 2009/10/15 23:15:04 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.dialogs;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.input.SelectFormatInput;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.EntryFactory;
import de.willuhn.jameica.ca.store.format.Format;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.FileInput;
import de.willuhn.jameica.gui.internal.buttons.Cancel;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Dialog zum Importieren eines X.509-Zertifikates zusammen mit einem Private-Key.
 */
public class EntryImportDialog extends AbstractDialog
{
  private final static Settings settings = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private FileInput cert           = null;
  private FileInput key            = null;
  private SelectFormatInput format = null;
  
  private Entry entry = null;

  /**
   * ct.
   * @param position
   */
  public EntryImportDialog(int position)
  {
    super(position);
    this.setSize(550,SWT.DEFAULT);
    this.setTitle(i18n.tr("Schlüssel-Import"));
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
    // Dialog bei Druck auf ESC automatisch schliessen
    parent.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.keyCode == SWT.ESC)
          throw new OperationCanceledException("import cancelled");
      }
    });
    
    SimpleContainer container = new SimpleContainer(parent);
    container.addText(i18n.tr("Bitte wählen die zu importierenden Schlüsseldateien aus,"),true);
    container.addInput(this.getFormatInput());
    container.addInput(this.getCertInput());
    container.addInput(this.getKeyInput());
    
    ButtonArea buttons = container.createButtonArea(2);
    buttons.addButton(i18n.tr("Übernehmen"),new Action()
    {
      /**
       * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
       */
      public void handleAction(Object context) throws ApplicationException
      {
        String cert   = (String) getCertInput().getValue();
        String key    = (String) getKeyInput().getValue();
        Format format = (Format) getFormatInput().getValue();
        
        if (cert == null || cert.length() == 0 || format == null)
          return;

        settings.setAttribute("import.cert.last",cert);
        settings.setAttribute("import.key.last",key);

        try
        {
          StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
          EntryFactory ef = service.getStore().getEntryFactory();
          entry = ef.read(new File(cert),key != null && key.length() > 0 ? new File(key) : null,format);
          close();
        }
        catch (ApplicationException ae)
        {
          throw ae;
        }
        catch (Exception e)
        {
          Logger.error("error while importing keys",e);
          throw new ApplicationException(i18n.tr("Fehler beim Import der Schlüssel"),e);
        }
      }
    },null,true,"ok.png");
    
    buttons.addButton(new Cancel());
  }
  
  /**
   * Liefert ein Eingabefeld fuer die Datei des Zertifikates.
   * @return Eingabefeld.
   */
  private FileInput getCertInput()
  {
    if (this.cert != null)
      return this.cert;
    
    String s = settings.getString("import.cert.last",System.getProperty("user.home"));
    this.cert = new FileInput(s)
    {
      /**
       * @see de.willuhn.jameica.gui.input.FileInput#setValue(java.lang.Object)
       */
      public void setValue(Object value)
      {
        super.setValue(value);
        // Damit der Dialog nach der Dateiauswahl wieder angezeigt wird
        getShell().forceActive();
      }
    };
    this.cert.setName(i18n.tr("Zertifikat"));
    this.cert.setMandatory(true);
    return this.cert;
  }

  /**
   * Liefert ein Eingabefeld fuer die Datei des Keys.
   * @return Eingabefeld.
   */
  private FileInput getKeyInput()
  {
    if (this.key != null)
      return this.key;
    
    String s = settings.getString("import.key.last",System.getProperty("user.home"));
    this.key = new FileInput(s)
    {
      /**
       * @see de.willuhn.jameica.gui.input.FileInput#setValue(java.lang.Object)
       */
      public void setValue(Object value)
      {
        super.setValue(value);
        // Damit der Dialog nach der Dateiauswahl wieder angezeigt wird
        getShell().forceActive();
      }
    };
    this.key.setName(i18n.tr("Private-Key"));
    return this.key;
  }
  
  /**
   * Liefert ein Auswahlfeld fuer das Schluesselformat.
   * @return Auswahlfeld.
   */
  private SelectFormatInput getFormatInput()
  {
    if (this.format != null)
      return this.format;
    
    this.format = new SelectFormatInput();
    this.format.setMandatory(true);
    return this.format;
  }

}


/**********************************************************************
 * $Log: EntryImportDialog.java,v $
 * Revision 1.5  2009/10/15 23:15:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2009/10/15 17:04:48  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2009/10/15 16:01:28  willuhn
 * @N Schluessel-Export
 *
 * Revision 1.2  2009/10/15 11:50:43  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.1  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 **********************************************************************/
