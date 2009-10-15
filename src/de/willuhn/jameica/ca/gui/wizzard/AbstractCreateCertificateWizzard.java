/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/Attic/AbstractCreateCertificateWizzard.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/10/15 15:25:25 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Implementierung, die von Implementierungen des Interfaces
 * {@link CreateCertificateWizzard} genutzt werden kann.
 */
public abstract class AbstractCreateCertificateWizzard implements CreateCertificateWizzard, Comparable
{
  final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  private DateInput validFrom      = null;
  private DateInput validTo        = null;
  private SelectInput keySize      = null;
  private SelectInput signatureAlg = null;
  private SelectInput issuer       = null;
  
  /**
   * Liefert ein Eingabefeld fuer das Beginn-Datum der Gueltigkeit.
   * @return Eingabefeld.
   */
  private DateInput getValidFrom()
  {
    if (this.validFrom == null)
    {
      this.validFrom = new DateInput(new Date());
      this.validFrom.setName(i18n.tr("Gültig ab"));
      this.validFrom.setTitle(i18n.tr("Zertifikat gültig ab"));
      this.validFrom.setMandatory(true);
    }
    return this.validFrom;
  }
  
  /**
   * Liefert ein Eingabefeld fuer das End-Datum der Gueltigkeit.
   * @return Eingabefeld.
   */
  private DateInput getValidTo()
  {
    if (this.validTo == null)
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.YEAR,2); // Per Default 2 Jahre Gueltigkeit
      this.validTo = new DateInput(cal.getTime());
      this.validTo.setName(i18n.tr("Gültig bis"));
      this.validTo.setTitle(i18n.tr("Zertifikat gültig bis"));
      this.validTo.setMandatory(true);
    }
    return this.validTo;
  }
  
  /**
   * Liefert eine Auswahlliste fuer moegliche Schluessellaengen.
   * @return Auswahlliste.
   */
  private SelectInput getKeySize()
  {
    if (this.keySize == null)
    {
      List<Integer> values = new ArrayList<Integer>();
      values.add(512);
      values.add(768);
      values.add(1024);
      values.add(2048);
      
      this.keySize = new SelectInput(values,Template.KEYSIZE_DEFAULT);
      this.keySize.setName(i18n.tr("Schlüssellänge"));
      this.keySize.setComment(i18n.tr("Bytes"));
      this.keySize.setMandatory(true);
    }
    return this.keySize;
  }
  
  /**
   * Liefert eine Auswahlliste fuer die moeglichen Signatur-Algorithmen.
   * @return Auswahlliste.
   */
  private SelectInput getSignatureAlgorithm()
  {
    if (this.signatureAlg == null)
    {
      List<String> values = new ArrayList<String>();
      values.add("MD5WithRSAEncryption");
      values.add("SHA1WithRSAEncryption");
      values.add("SHA224WithRSAEncryption");
      values.add("SHA256WithRSAEncryption");
      values.add("SHA384WithRSAEncryption");
      values.add("SHA512WithRSAEncryption");
      values.add("RIPEMD160WithRSAEncryption");
      values.add("RIPEMD128WithRSAEncryption");
      values.add("RIPEMD256WithRSAEncryption");
      
      this.signatureAlg = new SelectInput(values,Template.SIGNATUREALG_DEFAULT);
      this.signatureAlg.setName(i18n.tr("Signatur-Algorithmus"));
      this.signatureAlg.setMandatory(true);
    }
    return this.signatureAlg;
  }
  
  /**
   * Liefert eine Auswahlliste mit moeglichen Aussteller-Zertifikaten.
   * @return Auswahlliste.
   */
  private SelectInput getIssuer()
  {
    if (this.issuer == null)
    {
      List<Entry> cacerts = new ArrayList<Entry>();

      try
      {
        // Wir holen uns die Liste der brauchbaren Aussteller-Zertifikate
        StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
        Store store = service.getStore();

        List<Entry> entries = store.getEntries();
        for (Entry e:entries)
        {
          // Nehmen wir nur, wenn es eine CA ist und wir den Private-Key zum
          // Unterschreiben haben.
          if (e.isCA() && e.getPrivateKey() != null)
            cacerts.add(e);
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to load ca certs",e);
      }

      this.issuer = new SelectInput(cacerts,null);
      this.issuer.setName(i18n.tr("Aussteller"));
      this.issuer.setPleaseChoose(i18n.tr("kein Aussteller (selbstsigniertes Zertifikat)"));
    }
    return this.issuer;
  }
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CreateCertificateWizzard#create()
   */
  public Template create() throws ApplicationException
  {
    Template t = _create();
    
    Date from = (Date) this.getValidFrom().getValue();
    if (from == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Datum für den Beginn der Gültigkeit aus"));
    t.setValidFrom(from);

    Date to = (Date) this.getValidTo().getValue();
    if (to == null)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie ein Datum für das Ende der Gültigkeit aus"));
    t.setValidTo(to);
    
    Integer keysize = (Integer) this.getKeySize().getValue();
    if (keysize == null || keysize <= 0 || keysize > Integer.MAX_VALUE)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie eine Schlüssellänge aus"));
    t.setKeySize(keysize.intValue());
    
    String alg = (String) this.getSignatureAlgorithm().getValue();
    if (alg == null || alg.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte wählen Sie einen Signatur-Algorithmus aus"));
    t.setSignatureAlgorithm(alg);
    
    t.setIssuer((Entry) this.getIssuer().getValue());
    return t;
  }
  
  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    ColumnLayout columns = new ColumnLayout(parent,2);

    SimpleContainer key = new SimpleContainer(columns.getComposite());
    key.addHeadline(i18n.tr("Schlüssel und Aussteller"));
    key.addInput(this.getIssuer());
    key.addInput(this.getSignatureAlgorithm());
    key.addInput(this.getKeySize());

    SimpleContainer dates = new SimpleContainer(columns.getComposite());
    dates.addHeadline(i18n.tr("Gültigkeit"));
    dates.addInput(this.getValidFrom());
    dates.addInput(this.getValidTo());
  }

  /**
   * Muss von abgeleiteten Klassen ueberschrieben werden, um initial die Instanz zu erzeugen.
   * @return initiale Instanz des Template-Objektes.
   * @throws ApplicationException
   */
  abstract Template _create() throws ApplicationException;

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (o == null || !(o instanceof CreateCertificateWizzard))
      return -1;
    return this.getName().compareTo(((CreateCertificateWizzard)o).getName());
  }
}


/**********************************************************************
 * $Log: AbstractCreateCertificateWizzard.java,v $
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
