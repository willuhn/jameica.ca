/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.input.SelectIssuerInput;
import de.willuhn.jameica.ca.gui.input.SubjectAltNameInput;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.template.Attribute;
import de.willuhn.jameica.ca.store.template.SubjectAltName;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Implementierung, die von Implementierungen des Interfaces
 * {@link CertificateWizzard} genutzt werden kann.
 */
public abstract class AbstractCertificateWizzard implements CertificateWizzard, Comparable
{
  final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  private DateInput validFrom      = null;
  private DateInput validTo        = null;
  private SelectInput keySize      = null;
  private SelectInput signatureAlg = null;
  private SelectIssuerInput issuer = null;

  private TextInput cn = null;
  private TextInput o  = null;
  private TextInput ou = null;
  private TextInput c  = null;
  private TextInput l  = null;
  private TextInput st = null;
  private TextInput bc = null;
  private TextInput zip = null;
  private TextInput street = null;
  
  private Container altNameContainer = null;
  private List<SubjectAltNameInput> altNames = null;
  
  /**
   * Liefert ein Eingabefeld fuer das Beginn-Datum der Gueltigkeit.
   * @return Eingabefeld.
   */
  DateInput getValidFrom()
  {
    if (this.validFrom == null)
    {
      this.validFrom = new DateInput(new Date());
      this.validFrom.setName(i18n.tr("G�ltig ab"));
      this.validFrom.setTitle(i18n.tr("Zertifikat g�ltig ab"));
      this.validFrom.setMandatory(true);
    }
    return this.validFrom;
  }
  
  /**
   * Liefert ein Eingabefeld fuer das End-Datum der Gueltigkeit.
   * @return Eingabefeld.
   */
  DateInput getValidTo()
  {
    if (this.validTo == null)
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.add(Calendar.YEAR,2); // Per Default 2 Jahre Gueltigkeit
      this.validTo = new DateInput(cal.getTime());
      this.validTo.setName(i18n.tr("G�ltig bis"));
      this.validTo.setTitle(i18n.tr("Zertifikat g�ltig bis"));
      this.validTo.setMandatory(true);
    }
    return this.validTo;
  }
  
  /**
   * Liefert eine Auswahlliste fuer moegliche Schluessellaengen.
   * @return Auswahlliste.
   */
  SelectInput getKeySize()
  {
    if (this.keySize == null)
    {
      List<Integer> values = new ArrayList<Integer>();
      values.add(2048);
      values.add(3072);
      values.add(4096);
      values.add(5120);
      values.add(6144);
      values.add(8192);
      
      this.keySize = new SelectInput(values,Template.KEYSIZE_DEFAULT);
      this.keySize.setName(i18n.tr("Schl�ssell�nge"));
      this.keySize.setComment(i18n.tr("Bytes"));
      this.keySize.setMandatory(true);
    }
    return this.keySize;
  }
  
  /**
   * Liefert eine Auswahlliste fuer die moeglichen Signatur-Algorithmen.
   * @return Auswahlliste.
   */
  SelectInput getSignatureAlgorithm()
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
  Input getIssuer()
  {
    if (this.issuer == null)
      this.issuer = new SelectIssuerInput();
    return this.issuer;
  }
  
  /**
   * Liefert ein Eingabefeld fuer den Hostnamen des Zertifikates.
   * @return Eingabefeld.
   */
  TextInput getCN()
  {
    if (this.cn == null)
    {
      this.cn = createTemplate();
      this.cn.setName(i18n.tr("Hostname"));
      this.cn.setComment(i18n.tr("Name, auf den das Zertifikat ausgestellt wird (Common-Name)"));
      this.cn.setValidChars("0123456789abcdefghijklmnopqrstuvwxyz����.-+*");
      this.cn.setMandatory(true);
    }
    return this.cn;
  }

  /**
   * Liefert ein Eingabefeld fuer die Organisation.
   * @return Eingabefeld.
   */
  TextInput getO()
  {
    if (this.o == null)
    {
      this.o = createTemplate();
      this.o.setName(i18n.tr("Organisation"));
      this.o.setComment(i18n.tr("Name der Organisation (Organization)"));
    }
    return this.o;
  }

  /**
   * Liefert ein Eingabefeld fuer die Abteilung.
   * @return Eingabefeld.
   */
  TextInput getOU()
  {
    if (this.ou == null)
    {
      this.ou = createTemplate();
      this.ou.setName(i18n.tr("Abteilung"));
      this.ou.setComment(i18n.tr("Name der Abteilung (Organizational Unit)"));
    }
    return this.ou;
  }

  /**
   * Liefert ein Eingabefeld fuer das Land.
   * @return Eingabefeld.
   */
  TextInput getC()
  {
    if (this.c == null)
    {
      this.c = createTemplate();
      this.c.setValue(Application.getConfig().getLocale().getCountry());
      this.c.setName(i18n.tr("Land"));
      this.c.setComment(i18n.tr("K�rzel des Landes (Country)"));
    }
    return this.c;
  }

  /**
   * Liefert ein Eingabefeld fuer das Bundesland.
   * @return Eingabefeld.
   */
  TextInput getST()
  {
    if (this.st == null)
    {
      this.st = createTemplate();
      this.st.setName(i18n.tr("Bundesland"));
      this.st.setComment(i18n.tr("Name des Bundeslandes (State)"));
    }
    return this.st;
  }

  /**
   * Liefert ein Eingabefeld fuer die Stadt.
   * @return Eingabefeld.
   */
  TextInput getL()
  {
    if (this.l == null)
    {
      this.l = createTemplate();
      this.l.setName(i18n.tr("Stadt"));
      this.l.setComment(i18n.tr("Name der Stadt (Locality)"));
    }
    return this.l;
  }

  /**
   * Liefert ein Eingabefeld fuer die PLZ.
   * @return Eingabefeld.
   */
  TextInput getZip()
  {
    if (this.zip == null)
    {
      this.zip = createTemplate();
      this.zip.setName(i18n.tr("PLZ"));
      this.zip.setComment(i18n.tr("Postleitzahl (PostalCode)"));
    }
    return this.zip;
  }

  /**
   * Liefert ein Eingabefeld fuer die Strasse.
   * @return Eingabefeld.
   */
  TextInput getStreet()
  {
    if (this.street == null)
    {
      this.street = createTemplate();
      this.street.setName(i18n.tr("Stra�e"));
      this.street.setComment(i18n.tr("Stra�e und Hausnummer (Street)"));
    }
    return this.street;
  }

  /**
   * Liefert ein Eingabefeld fuer die Business-Kategorie.
   * @return Eingabefeld.
   */
  TextInput getBC()
  {
    if (this.bc == null)
    {
      this.bc = createTemplate();
      this.bc.setName(i18n.tr("Business-Kategorie"));
      this.bc.setComment("");
    }
    return this.bc;
  }

  /**
   * Erstellt ein Default-Eingabefeld.
   * @return Default-Eingabefeld.
   */
  private TextInput createTemplate()
  {
    TextInput t = new TextInput(null);
    t.setMaxLength(255);
    return t;
  }
  
  /**
   * Liefert die Liste der Eingabefelder mit den Alt-Names.
   * @return Liste der Eingabefelder mit den Alt-Names.
   */
  private List<SubjectAltNameInput> getAltNames()
  {
    if (this.altNames != null)
      return this.altNames;
    
    this.altNames = new ArrayList<SubjectAltNameInput>();
    
    final Listener l = new Listener() {
      
      @Override
      public void handleEvent(Event event)
      {
        // Alle existierenden Buttons deaktivieren
        for (SubjectAltNameInput i:altNames)
        {
          i.removeButton();
        }
        
        // Neues Eingabefeld unten anhaengen
        SubjectAltNameInput next = new SubjectAltNameInput(this);
        altNames.add(next);
        altNameContainer.addInput(next);
        
        Composite comp = altNameContainer.getComposite();
        comp.layout();
      }
    };
    this.altNames.add(new SubjectAltNameInput(l));
    return this.altNames;
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard#create()
   */
  public Template create() throws ApplicationException
  {
    Template t = _create();
    
    Date from = (Date) this.getValidFrom().getValue();
    if (from == null)
      throw new ApplicationException(i18n.tr("Bitte w�hlen Sie ein Datum f�r den Beginn der G�ltigkeit aus"));
    t.setValidFrom(from);

    Date to = (Date) this.getValidTo().getValue();
    if (to == null)
      throw new ApplicationException(i18n.tr("Bitte w�hlen Sie ein Datum f�r das Ende der G�ltigkeit aus"));
    t.setValidTo(to);
    
    Integer keysize = (Integer) this.getKeySize().getValue();
    if (keysize == null || keysize <= 0 || keysize > Integer.MAX_VALUE)
      throw new ApplicationException(i18n.tr("Bitte w�hlen Sie eine Schl�ssell�nge aus"));
    t.setKeySize(keysize.intValue());
    
    String alg = (String) this.getSignatureAlgorithm().getValue();
    if (alg == null || alg.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte w�hlen Sie einen Signatur-Algorithmus aus"));
    t.setSignatureAlgorithm(alg);
    
    t.setIssuer((Entry) this.getIssuer().getValue());

    String cn = (String) this.getCN().getValue();
    if (cn == null || cn.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Common-Name an"));

    List<Attribute> attributes = t.getAttributes();
    attributes.add(new Attribute(Attribute.CN,cn));
    attributes.add(new Attribute(Attribute.O,(String) getO().getValue()));
    attributes.add(new Attribute(Attribute.OU,(String) getOU().getValue()));
    attributes.add(new Attribute(Attribute.C,(String) getC().getValue()));
    attributes.add(new Attribute(Attribute.ST,(String) getST().getValue()));
    attributes.add(new Attribute(Attribute.L,(String) getL().getValue()));
    attributes.add(new Attribute(Attribute.STREET,(String) getStreet().getValue()));
    attributes.add(new Attribute(Attribute.ZIP,(String) getZip().getValue()));
    
    String bc = (String) getBC().getValue();
    if (bc != null && bc.length() > 0)
      attributes.add(new Attribute(Attribute.BC,bc));
    
    List<SubjectAltName> altNames = t.getAltNames();
    for (SubjectAltNameInput i:this.getAltNames())
    {
      SubjectAltName n = (SubjectAltName) i.getValue();
      
      // Wenn kein Text eingegeben wurde, ueberspringen wir ihn
      String s = StringUtils.trimToNull(n.getValue());
      if (s == null)
        continue;
      altNames.add(n);
    }
      
    return t;
  }
  
  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    ColumnLayout columns = new ColumnLayout(parent,2);

    SimpleContainer left = new SimpleContainer(columns.getComposite());
    left.addHeadline(i18n.tr("Schl�ssel und Aussteller"));
    left.addInput(this.getIssuer());
    left.addInput(this.getSignatureAlgorithm());
    left.addInput(this.getKeySize());

    left.addHeadline(i18n.tr("G�ltigkeit"));
    left.addInput(this.getValidFrom());
    left.addInput(this.getValidTo());

    SimpleContainer right = new SimpleContainer(columns.getComposite());
    right.addHeadline(i18n.tr("Adresse"));
    right.addInput(this.getL());
    right.addInput(this.getZip());
    right.addInput(this.getStreet());
    right.addInput(this.getST());
    right.addInput(this.getC());

    SimpleContainer container = new SimpleContainer(parent);
    container.addHeadline(i18n.tr("Eigenschaften des Zertifikates"));
    container.addInput(this.getCN());
    container.addInput(this.getO());
    container.addInput(this.getOU());
    container.addInput(this.getBC());
    
    this.altNameContainer = new SimpleContainer(parent,true);
    this.altNameContainer.addHeadline(i18n.tr("Alternative Namen (Subject Alt Names)"));
    this.altNameContainer.addText(i18n.tr("Hinweis: Der Common-Name (Hostname bei Webserver-Zertifikaten bzw. Mail-Adresse bei S/MIME-Zertifikaten) wird automatisch als alternativer Name hinzugef�gt und muss hier nicht separat erfasst werden."),true, Color.COMMENT);
    for (SubjectAltNameInput input:this.getAltNames())
    {
      this.altNameContainer.addInput(input);
    }
  }

  /**
   * Muss von abgeleiteten Klassen ueberschrieben werden, um initial die Instanz zu erzeugen.
   * @return initiale Instanz des Template-Objektes.
   * @throws ApplicationException
   */
  abstract Template _create() throws ApplicationException;
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CertificateWizzard#getName()
   */
  @Override
  public String getName() throws ApplicationException
  {
    return this._create().getName();
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (o == null || !(o instanceof CertificateWizzard))
      return -1;
    
    try
    {
      return this.getName().compareTo(((CertificateWizzard)o).getName());
    }
    catch (ApplicationException ae)
    {
      return this.getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
    }
  }
}
