/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/dialogs/EntryExportDialog.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/05/06 12:31:32 $
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
import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.input.SelectFormatInput;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.EntryFactory;
import de.willuhn.jameica.ca.store.format.Format;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.DirectoryInput;
import de.willuhn.jameica.gui.internal.buttons.Cancel;
import de.willuhn.jameica.gui.util.ButtonArea;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.Settings;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Dialog zum Export eines X.509-Zertifikates zusammen mit einem Private-Key.
 */
public class EntryExportDialog extends AbstractDialog
{
  private final static Settings settings = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private DirectoryInput dir       = null;
  private SelectFormatInput format = null;
  
  private Entry entry              = null;
  
  /**
   * ct.
   * @param entry der zu exportierende Schluessel.
   * @param position
   * @throws ApplicationException
   */
  public EntryExportDialog(Entry entry, int position) throws ApplicationException
  {
    super(position);
    
    if (entry == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie den zu exportierenden Schlüssel aus."));

    this.entry = entry;
    this.setSize(550,SWT.DEFAULT);
    this.setTitle(i18n.tr("Schlüssel-Export"));
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#getData()
   */
  protected Object getData() throws Exception
  {
    return null;
  }

  /**
   * @see de.willuhn.jameica.gui.dialogs.AbstractDialog#paint(org.eclipse.swt.widgets.Composite)
   */
  protected void paint(Composite parent) throws Exception
  {
    SimpleContainer container = new SimpleContainer(parent);
    container.addText(i18n.tr("Bitte wählen Sie das Verzeichnis, in dem die Schlüssel gespeichert werden sollen"),true);
    container.addInput(this.getFormatInput());
    container.addInput(this.getDirInput());
    
    ButtonArea buttons = container.createButtonArea(2);
    buttons.addButton(i18n.tr("Übernehmen"),new Action()
    {
      /**
       * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
       */
      public void handleAction(Object context) throws ApplicationException
      {
        Format format = (Format) getFormatInput().getValue();
        String s      = (String) getDirInput().getValue();

        if (s == null || s.length() == 0 || format == null)
          return;

        settings.setAttribute("dir.last",s);
        
        try
        {
          File dir = new File(s);
          if (!dir.exists())
          {
            if (!dir.mkdirs())
              throw new ApplicationException(i18n.tr("Ziel-Ordner kann nicht erstellt werden"));
          }
          
          if (!dir.isDirectory() || !dir.canWrite())
            throw new ApplicationException(i18n.tr("Ziel-Ordner existiert nicht oder nicht beschreibbar"));

          StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
          EntryFactory ef = service.getStore().getEntryFactory();
          ef.write(entry,dir,format);
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
   * Liefert ein Eingabefeld fuer das Zielverzeichnis.
   * @return Eingabefeld.
   */
  private DirectoryInput getDirInput()
  {
    if (this.dir != null)
      return this.dir;
    
    String lastdir = settings.getString("dir.last",System.getProperty("user.home"));

    this.dir = new DirectoryInput(lastdir)
    {
      /**
       * @see de.willuhn.jameica.gui.input.DirectoryInput#setValue(java.lang.Object)
       */
      public void setValue(Object value)
      {
        super.setValue(value);
        // Damit der Dialog nach der Auswahl wieder angezeigt wird
        getShell().forceActive();
      }
    };
    this.dir.setName(i18n.tr("Zielverzeichnis"));
    this.dir.setMandatory(true);
    return this.dir;
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
 * $Log: EntryExportDialog.java,v $
 * Revision 1.3  2011/05/06 12:31:32  willuhn
 * @R Nicht mehr noetig - macht AbstractDialog jetzt selbst
 *
 * Revision 1.2  2009/10/15 23:15:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/15 16:01:28  willuhn
 * @N Schluessel-Export
 *
 **********************************************************************/
