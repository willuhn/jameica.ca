/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/Attic/CreateWebserverCertificateWizzard.java,v $
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
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.ca.store.template.Attribute;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.ca.store.template.WebserverTemplate;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines SSL-Zertifikates fuer
 * einen Webserver.
 */
public class CreateWebserverCertificateWizzard extends AbstractCreateCertificateWizzard
{
  
  private TextInput cn = null;
  private TextInput o  = null;
  private TextInput ou = null;
  private TextInput c  = null;
  private TextInput l  = null;
  private TextInput st = null;
  
  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CreateCertificateWizzard#getName()
   */
  public String getName()
  {
    return i18n.tr("Webserver-Zertifikat");
  }
  
  /**
   * Liefert ein Eingabefeld fuer den Hostnamen des Zertifikates.
   * @return Eingabefeld.
   */
  private TextInput getCN()
  {
    if (this.cn == null)
    {
      this.cn = createTemplate();
      this.cn.setName(i18n.tr("Hostname"));
      this.cn.setComment(i18n.tr("Name, auf den das Zertifikat ausgestellt wird (Common Name)"));
      this.cn.setValidChars("0123456789abcdefghijklmnopqrstuvwxyzüöäß.-+*");
      this.cn.setMandatory(true);
    }
    return this.cn;
  }

  /**
   * Liefert ein Eingabefeld fuer die Organisation.
   * @return Eingabefeld.
   */
  private TextInput getO()
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
  private TextInput getOU()
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
  private TextInput getC()
  {
    if (this.c == null)
    {
      this.c = createTemplate();
      this.c.setValue(Application.getConfig().getLocale().getCountry());
      this.c.setName(i18n.tr("Land"));
      this.c.setComment(i18n.tr("Kürzel des Landes (Country)"));
    }
    return this.c;
  }

  /**
   * Liefert ein Eingabefeld fuer das Bundesland.
   * @return Eingabefeld.
   */
  private TextInput getST()
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
  private TextInput getL()
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
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    super.paint(parent);
    
    SimpleContainer container = new SimpleContainer(parent);
    container.addHeadline(i18n.tr("Eigenschaften des Webserver-Zertifikates"));
    container.addInput(this.getCN());
    container.addInput(this.getO());
    container.addInput(this.getOU());
    container.addInput(this.getC());
    container.addInput(this.getST());
    container.addInput(this.getL());
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CreateCertificateWizzard#create()
   */
  public Template create() throws ApplicationException
  {
    Template t = super.create();
    
    String hostname = (String) this.getCN().getValue();
    if (hostname == null || hostname.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Hostnamen an"));

    List<Attribute> attributes = t.getAttributes();
    attributes.add(new Attribute(Attribute.CN,hostname));
    attributes.add(new Attribute(Attribute.O,(String) getO().getValue()));
    attributes.add(new Attribute(Attribute.OU,(String) getOU().getValue()));
    attributes.add(new Attribute(Attribute.C,(String) getC().getValue()));
    attributes.add(new Attribute(Attribute.ST,(String) getST().getValue()));
    attributes.add(new Attribute(Attribute.L,(String) getL().getValue()));

    return t;
  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.AbstractCreateCertificateWizzard#_create()
   */
  Template _create() throws ApplicationException
  {
    return new WebserverTemplate();
  }

}


/**********************************************************************
 * $Log: CreateWebserverCertificateWizzard.java,v $
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
