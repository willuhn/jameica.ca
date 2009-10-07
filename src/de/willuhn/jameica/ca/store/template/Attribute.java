/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/template/Attribute.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/07 11:39:27 $
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
   * OID fuer: Common-Name (CN).
   */
  public final static String CN = X509Name.CN.getId();
  
  /**
   * OID fuer: Organisation (O).
   */
  public final static String O  = X509Name.O.getId();
  
  /**
   * OID fuer: Organizational Unit (OU).
   */
  public final static String OU = X509Name.OU.getId();

  /**
   * OID fuer: Vorname (GIVENNAME).
   */
  public final static String GIVENNAME = X509Name.GIVENNAME.getId();
  
  /**
   * OID fuer: Nachname (SURNAME).
   */
  public final static String SURNAME = X509Name.SURNAME.getId();
  
  /**
   * OID fuer: Mail-Adresse (EmailAddress).
   */
  public final static String EmailAddress = X509Name.EmailAddress.getId();
  
  /**
   * OID fuer: Country (C).
   */
  public final static String C = X509Name.C.getId();

  /**
   * OID fuer: State or Province (ST).
   */
  public final static String ST = X509Name.ST.getId();

  /**
   * OID fuer: Locality (L).
   */
  public final static String L = X509Name.L.getId();

  
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
 * Revision 1.2  2009/10/07 11:39:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
