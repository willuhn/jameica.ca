/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
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
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#_create()
   */
  Template _create() throws ApplicationException
  {
    return new WebserverTemplate();
  }
}
