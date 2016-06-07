/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import java.util.Calendar;
import java.util.Date;

import de.willuhn.jameica.ca.store.template.CATemplate;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines CA-Zertifikates.
 */
public class CertificateWizzardCA extends AbstractCertificateWizzard
{
  private DateInput validTo = null;
  private TextInput cn      = null;

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#_create()
   */
  Template _create() throws ApplicationException
  {
    return new CATemplate();
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getValidTo()
   */
  DateInput getValidTo()
  {
    // Wir verlaengern die Default-Gueltigkeit auf 10 Jahre
    if (this.validTo == null)
    {
      this.validTo = super.getValidTo();
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.YEAR,10);
      this.validTo.setValue(cal.getTime());
    }
    return this.validTo;
  }
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getCN()
   */
  TextInput getCN()
  {
    // Wir aendern das Label und erweitern die Liste der moeglichen Zeichen
    if (this.cn == null)
    {
      this.cn = super.getCN();
      this.cn.setName(i18n.tr("Common-Name"));
      this.cn.setValidChars("0123456789abcdefghijklmnopqrstuvwxyzüöäßABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄ.-+* ");
    }
    return this.cn;
  }

}
