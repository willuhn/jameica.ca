/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/CertificateWizzardCA.java,v $
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
   * @see de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard#getName()
   */
  public String getName()
  {
    return i18n.tr("CA-Zertifikat");
  }
  
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
      this.cn.setValidChars("0123456789abcdefghijklmnopqrstuvwxyzüöäßABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄ.-+*");
    }
    return this.cn;
  }

}


/**********************************************************************
 * $Log: CertificateWizzardCA.java,v $
 * Revision 1.1  2009/10/22 17:27:08  willuhn
 * @N Auswahl des Ausstellers via DialogInput
 *
 **********************************************************************/
