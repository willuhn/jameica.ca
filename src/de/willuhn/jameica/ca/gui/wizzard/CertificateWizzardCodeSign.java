/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/CertificateWizzardCodeSign.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/22 17:28:16 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import de.willuhn.jameica.ca.store.template.CodeSignTemplate;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines SSL-Zertifikates fuer
 * einen Code-Signierung.
 */
public class CertificateWizzardCodeSign extends AbstractCertificateWizzard
{
  private TextInput cn = null;
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard#getName()
   */
  public String getName()
  {
    return i18n.tr("Zertifikat für Code-Signierung");
  }
  
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
    return new CodeSignTemplate();
  }
}


/**********************************************************************
 * $Log: CertificateWizzardCodeSign.java,v $
 * Revision 1.2  2009/10/22 17:28:16  willuhn
 * @N Leerzeichen in CN erlauben
 *
 * Revision 1.1  2009/10/15 22:55:29  willuhn
 * @N Wizzard zum Erstellen von Hibiscus Payment-Server Lizenzen
 *
 **********************************************************************/