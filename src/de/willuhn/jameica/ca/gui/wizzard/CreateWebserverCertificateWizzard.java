/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/Attic/CreateWebserverCertificateWizzard.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/14 23:58:17 $
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
import de.willuhn.util.ApplicationException;

/**
 * Implementierung eines Wizzards zur Erstellung eines SSL-Zertifikates fuer
 * einen Webserver.
 */
public class CreateWebserverCertificateWizzard extends AbstractCreateCertificateWizzard
{
  
  private TextInput hostname = null;
  
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
  TextInput getHostname()
  {
    if (this.hostname == null)
    {
      this.hostname = new TextInput(null);
      this.hostname.setName(i18n.tr("Hostname"));
      this.hostname.setComment(i18n.tr("Name auf den das Zertifikat ausgestellt wird (CN)"));
      this.hostname.setMandatory(true);
      this.hostname.setMaxLength(255);
      
      // Ich bin mir nicht sicher, ob hier noch Zeichen fehlen, sollten aber eigentlich alle erlaubten sein
      // Da Hostnamen nicht case sensitive sind, brauchen wir Grossbuchstaben auch nicht zulassen ;)
      this.hostname.setValidChars("0123456789abcdefghijklmnopqrstuvwxyzüöäß.-+*");
    }
    return this.hostname;
  }

  /**
   * @see de.willuhn.jameica.gui.Part#paint(org.eclipse.swt.widgets.Composite)
   */
  public void paint(Composite parent) throws RemoteException
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.willuhn.jameica.ca.gui.wizzard.CreateCertificateWizzard#create()
   */
  public Template create() throws ApplicationException
  {
    Template t = super.create();
    
    String hostname = (String) this.getHostname().getValue();
    if (hostname == null || hostname.length() == 0)
      throw new ApplicationException(i18n.tr("Bitte geben Sie einen Hostnamen an"));

    List<Attribute> attributes = t.getAttributes();
    attributes.add(new Attribute(Attribute.CN,hostname));

    // TODO
//    attributes.add(new Attribute(Attribute.O,"willuhn software & services"));
//    attributes.add(new Attribute(Attribute.OU,"Development"));
//    attributes.add(new Attribute(Attribute.C,"DE"));
//    attributes.add(new Attribute(Attribute.L,"Leipzig"));
//    attributes.add(new Attribute(Attribute.ST,"Saxony"));

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
 * Revision 1.1  2009/10/14 23:58:17  willuhn
 * @N Erster Code fuer die Wizzards zum Erstellen neuer Zertifikate
 *
 **********************************************************************/
