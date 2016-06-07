/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import de.willuhn.jameica.ca.store.template.ClientAuthTemplate;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines SSL-Zertifikates fuer
 * einen Client-Authentifizierung.
 */
public class CertificateWizzardClientAuth extends AbstractCertificateWizzard
{
  private TextInput cn = null;
  
  /**
   * Ueberschrieben, um das Label und die zulaessigen Zeichen zu aendern.
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getCN()
   */
  TextInput getCN()
  {
    if (this.cn == null)
    {
      this.cn = super.getCN();
      this.cn.setName(i18n.tr("Common-Name"));
      this.cn.setValidChars("0123456789abcdefghijklmnopqrstuvwxyzüöäßABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄ.-+* ");
    }
    return this.cn;
  }


  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#_create()
   */
  Template _create() throws ApplicationException
  {
    return new ClientAuthTemplate();
  }
}
