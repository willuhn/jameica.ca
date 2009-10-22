/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/input/SelectIssuerInput.java,v $
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

package de.willuhn.jameica.ca.gui.input;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.dialogs.SelectIssuerDialog;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.gui.input.DialogInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Input-Feld zur Auswahl des Aussteller-Zertifikates
 */
public class SelectIssuerInput extends DialogInput
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  private final static String NONE = i18n.tr("kein Aussteller (selbstsigniertes Zertifikat)");
  
  /**
   * ct.
   */
  public SelectIssuerInput()
  {
    super(NONE);
    this.disableClientControl();
    this.setName(i18n.tr("Aussteller"));
    SelectIssuerDialog d = new SelectIssuerDialog(SelectIssuerDialog.POSITION_MOUSE);
    
    d.addCloseListener(new Listener()
    {
      public void handleEvent(Event event)
      {
        if (event.data == null || !(event.data instanceof Entry))
        {
          setText(NONE);
          return;
        }

        try
        {
          Entry e = (Entry) event.data;
          Certificate cert = new Certificate(e.getCertificate());
          Principal p = cert.getSubject();
          String cn = p.getAttribute(Principal.COMMON_NAME);
          if (cn == null || cn.length() == 0)
            cn = e.getAlias();
          setText(cn);
        }
        catch (Exception e)
        {
          Logger.error("error while getting common name",e);
          Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Fehler bei der Auswahl des Aussteller-Zertifikates: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
        }
      }
    });

    this.setDialog(d);
  }

}


/**********************************************************************
 * $Log: SelectIssuerInput.java,v $
 * Revision 1.1  2009/10/22 17:27:08  willuhn
 * @N Auswahl des Ausstellers via DialogInput
 *
 **********************************************************************/
