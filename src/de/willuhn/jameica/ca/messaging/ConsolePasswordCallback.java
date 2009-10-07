/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/messaging/Attic/ConsolePasswordCallback.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/07 16:38:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.messaging;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Fragt Passwoerter beim User via Console ab.
 */
public class ConsolePasswordCallback implements MessageConsumer
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
    QueryMessage msg = (QueryMessage) message;
    Object context = msg.getData();
    
    I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

    String q = null;
    
    if (context != null)
      q = i18n.tr("Bitte geben Sie das Passwort ein für: {0}",context.toString());
    else
      q = i18n.tr("Bitte geben Sie das Passwort ein.");
    
    String pw = Application.getCallback().askUser(q,i18n.tr("Passwort: "));
    msg.setData(pw == null ? null : pw.toCharArray());
  }

}


/**********************************************************************
 * $Log: ConsolePasswordCallback.java,v $
 * Revision 1.1  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 **********************************************************************/
