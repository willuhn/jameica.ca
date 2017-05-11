/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import de.willuhn.jameica.ca.store.template.EmailTemplate;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines S/MIME-Zertifikates fuer Email.
 */
public class CertificateWizzardEmail extends AbstractCertificateWizzard
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
      this.cn.setName(i18n.tr("Mail-Adresse"));
      this.cn.setValidChars(null);
    }
    return this.cn;
  }


  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#_create()
   */
  Template _create() throws ApplicationException
  {
    return new EmailTemplate();
  }
}
