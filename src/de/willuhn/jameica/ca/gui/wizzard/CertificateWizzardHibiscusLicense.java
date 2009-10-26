/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/CertificateWizzardHibiscusLicense.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/26 23:48:49 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.ca.store.template.CodeSignTemplate;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines Lizenzschluessels fuer den Hibiscus Payment-Server.
 */
public class CertificateWizzardHibiscusLicense extends CertificateWizzardCodeSign
{
  private SelectInput issuer  = null;
  private SelectInput keySize = null;
  private SelectInput sigAlg  = null;
  private DateInput validTo   = null;

  private TextInput cn        = null;
  private TextInput o         = null;
  private TextInput ou        = null;
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CertificateWizzardCodeSign#getName()
   */
  public String getName()
  {
    return i18n.tr("Hibiscus Payment-Server Lizenz");
  }
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#isEnabled()
   */
  public boolean isEnabled()
  {
    return this.getIssuerEntry() != null;
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getCN()
   */
  TextInput getCN()
  {
    if (this.cn == null)
    {
      // fest vorgegeben
      this.cn = super.getCN();
      this.cn.setName(i18n.tr("Common-Name"));
      this.cn.setValue("hibiscus.payment.server");
      this.cn.setEnabled(false);
    }
    return this.cn;
  }
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getIssuer()
   */
  SelectInput getIssuer()
  {
    // Nur das CA-Zertifikat fuer Lizensierung auswaehlbar
    if (this.issuer == null)
    {
      List<Entry> certs = new ArrayList<Entry>();

      Entry e = getIssuerEntry();
      if (e != null)
        certs.add(e);
      else
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(i18n.tr("Aussteller-Zertifikat nicht gefunden"),StatusBarMessage.TYPE_ERROR));

      this.issuer = new SelectInput(certs,null);
      this.issuer.setAttribute("commonName");
      this.issuer.setEnabled(false);
      this.issuer.setName(i18n.tr("Aussteller"));
    }
    return this.issuer;
  }

  /**
   * Liefert das benoetigte Aussteller-Zertifikat.
   * @return das benoetigte Aussteller-Zertifikat.
   */
  private Entry getIssuerEntry()
  {
    try
    {
      StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
      Store store = service.getStore();

      List<Entry> entries = store.getEntries();
      for (Entry e:entries)
      {
        String cn = e.getCommonName();
        if (cn != null && cn.equals("willuhn.ca.licensing"))
          return e;
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to load ca certs",e);
    }
    return null;
  }
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getO()
   */
  TextInput getO()
  {
    if (this.o == null)
    {
      // Beschriftung aendern und um Pflichtfeld machen
      this.o = super.getO();
      this.o.setMandatory(true);
      this.o.setName(i18n.tr("Lizenznehmer"));
      this.o.setComment(i18n.tr("Name/Firma des Kunden"));
    }
    return this.o;
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getOU()
   */
  TextInput getOU()
  {
    if (this.ou == null)
    {
      // Wir nicht benoetigt
      this.ou = super.getOU();
      this.ou.setEnabled(false);
      this.ou.setComment("");
    }
    return this.ou;
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getValidTo()
   */
  DateInput getValidTo()
  {
    if (this.validTo == null)
    {
      // Wir verlaengern die Default-Gueltigkeit auf 10 Jahre
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.YEAR,10);

      this.validTo = super.getValidTo();
      this.validTo.setValue(cal.getTime());
    }
    return this.validTo;
  }
  
  

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getKeySize()
   */
  SelectInput getKeySize()
  {
    if (this.keySize == null)
    {
      // read only
      this.keySize = super.getKeySize();
      this.keySize.setEnabled(false);
    }
    return this.keySize;
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCertificateWizzard#getSignatureAlgorithm()
   */
  SelectInput getSignatureAlgorithm()
  {
    if (this.sigAlg == null)
    {
      // read only
      this.sigAlg = super.getSignatureAlgorithm();
      this.sigAlg.setEnabled(false);
    }
    return this.sigAlg;
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
 * $Log: CertificateWizzardHibiscusLicense.java,v $
 * Revision 1.2  2009/10/26 23:48:49  willuhn
 * @N Payment-Server-Wizzard ausblenden, wenn CA nicht vorhanden
 *
 * Revision 1.1  2009/10/15 22:55:29  willuhn
 * @N Wizzard zum Erstellen von Hibiscus Payment-Server Lizenzen
 *
 **********************************************************************/