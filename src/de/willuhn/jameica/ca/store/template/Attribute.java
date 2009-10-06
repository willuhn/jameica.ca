/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/template/Attribute.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/06 16:36:00 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.template;

import java.io.Serializable;

import org.bouncycastle.asn1.x509.X509Name;

/**
 * Ein einzelnes Attribut eines Zertifikates.
 */
public class Attribute implements Serializable
{
  /**
   * Template-Attribute fuer: Common-Name (CN).
   */
  public final static Attribute CN = new Attribute(X509Name.CN.getId(),null);
  
  /**
   * Template-Attribute fuer: Organisation (O).
   */
  public final static Attribute O  = new Attribute(X509Name.O.getId(),null);
  
  /**
   * Template-Attribute fuer: Organizational Unit (OU).
   */
  public final static Attribute OU = new Attribute(X509Name.OU.getId(),null);

  /**
   * Template-Attribute fuer: Vorname (GIVENNAME).
   */
  public final static Attribute GIVENNAME = new Attribute(X509Name.GIVENNAME.getId(),null);
  
  /**
   * Template-Attribute fuer: Nachname (SURNAME).
   */
  public final static Attribute SURNAME = new Attribute(X509Name.SURNAME.getId(),null);
  
  /**
   * Template-Attribute fuer: Mail-Adresse (EmailAddress).
   */
  public final static Attribute EmailAddress = new Attribute(X509Name.EmailAddress.getId(),null);
  
  
  private String oid = null;
  private String value = null;

  /**
   * ct.
   * Bean-Konstruktor.
   */
  public Attribute()
  {
  }
  
  /**
   * ct.
   * @param oid OID.
   * @param value Wert.
   */
  public Attribute(String oid, String value)
  {
    this.oid   = oid;
    this.value = value;
  }
  
  /**
   * Liefert die OID des Attributes.
   * @return OID des Attributes.
   */
  public String getOid()
  {
    return this.oid;
  }

  /**
   * Speichert die OID des Attributes.
   * @param oid die OID des Attributes,
   */
  public void setOid(String oid)
  {
    this.oid = oid;
  }

  /**
   * Liefert den Wert des Attributes.
   * @return Wert des Attributes.
   */
  public String getValue()
  {
    return this.value;
  }

  /**
   * Speichert den Wert des Attributes.
   * @param value Wert des Attributes.
   */
  public void setValue(String value)
  {
    this.value = value;
  }
}


/**********************************************************************
 * $Log: Attribute.java,v $
 * Revision 1.1  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
