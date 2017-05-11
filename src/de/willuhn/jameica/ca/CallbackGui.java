/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.ca;

import java.io.File;

import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.gui.dialogs.PasswordDialog;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Implementierung des Store-Callback fuer die GUI.
 */
public class CallbackGui implements Callback
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  /**
   * @see de.willuhn.jameica.ca.store.Callback#getPassword(java.lang.Object)
   */
  public char[] getPassword(Object context) throws Exception
  {
    PasswordDialog d = new PasswordDialog(PasswordDialog.POSITION_CENTER)
    {
      /**
       * @see de.willuhn.jameica.gui.dialogs.PasswordDialog#checkPassword(java.lang.String)
       */
      protected boolean checkPassword(String password)
      {
        return true; // koennen wir nicht verifizieren
      }
    };
    
    if (context != null)
      d.setText(i18n.tr("Bitte geben Sie das Passwort ein für: {0}",context.toString()));
    else
      d.setText(i18n.tr("Bitte geben Sie das Passwort ein."));
    
    String pw = (String) d.open();
    return pw == null ? new char[0] : pw.toCharArray();
  }

  /**
   * @see de.willuhn.jameica.ca.store.Callback#overwrite(de.willuhn.jameica.ca.store.Entry, de.willuhn.jameica.ca.store.Entry)
   */
  public boolean overwrite(Entry newEntry, Entry oldEntry) throws OperationCanceledException
  {
    String q = i18n.tr("Ein Schlüssel mit diesem Alias-Namen existiert bereits.\nÜberschreiben?");
    try
    {
      return Application.getCallback().askUser(q);
    }
    catch (OperationCanceledException oce)
    {
      throw oce;
    }
    catch (Exception e)
    {
      Logger.error("error while asking user, cancel operation",e);
      throw new OperationCanceledException("operation cancelled");
    }
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.Callback#overwrite(java.io.File)
   */
  @Override
  public boolean overwrite(File file) throws OperationCanceledException
  {
    String q = i18n.tr("Datei {0} existiert bereits.\nÜberschreiben?",file.getAbsolutePath());
    try
    {
      return Application.getCallback().askUser(q);
    }
    catch (OperationCanceledException oce)
    {
      throw oce;
    }
    catch (Exception e)
    {
      Logger.error("error while asking user, cancel operation",e);
      throw new OperationCanceledException("operation cancelled");
    }
  }

}
