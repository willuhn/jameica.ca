/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/Plugin.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/07 16:38:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca;

import de.willuhn.jameica.ca.messaging.ConsolePasswordCallback;
import de.willuhn.jameica.ca.messaging.GuiPasswordCallback;
import de.willuhn.jameica.messaging.MessagingQueue;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

/**
 * Basis-Klasse des Plugins.
 */
public class Plugin extends AbstractPlugin
{

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  public void init() throws ApplicationException
  {
    MessagingQueue queue = Application.getMessagingFactory().getMessagingQueue("jameica.ca.callback.password");
    queue.registerMessageConsumer(Application.inServerMode() ? new ConsolePasswordCallback() : new GuiPasswordCallback());
  }
}


/**********************************************************************
 * $Log: Plugin.java,v $
 * Revision 1.2  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
