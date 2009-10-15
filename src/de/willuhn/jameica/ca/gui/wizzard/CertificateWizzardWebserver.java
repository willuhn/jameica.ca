/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/CertificateWizzardWebserver.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/15 22:55:29 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.ca.store.template.WebserverTemplate;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines SSL-Zertifikates fuer
 * einen Webserver.
 */
public class CertificateWizzardWebserver extends AbstractCertificateWizzard
{
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard#getName()
   */
  public String getName()
  {
    return i18n.tr("Webserver-Zertifikat");
  }
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#_create()
   */
  Template _create() throws ApplicationException
  {
    return new WebserverTemplate();
  }

}


/**********************************************************************
 * $Log: CertificateWizzardWebserver.java,v $
 * Revision 1.1  2009/10/15 22:55:29  willuhn
 * @N Wizzard zum Erstellen von Hibiscus Payment-Server Lizenzen
 *
 * Revision 1.3  2009/10/15 15:25:25  willuhn
 * @N Reload des Tree nach Erstellen/Loeschen eines Schluessels
 *
 * Revision 1.2  2009/10/15 11:50:42  willuhn
 * @N Erste Schluessel-Erstellung via GUI und Wizzard funktioniert ;)
 *
 * Revision 1.1  2009/10/14 23:58:17  willuhn
 * @N Erster Code fuer die Wizzards zum Erstellen neuer Zertifikate
 *
 **********************************************************************/
