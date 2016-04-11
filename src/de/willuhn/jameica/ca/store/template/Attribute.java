/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.template;

import java.io.Serializable;

import org.bouncycastle.asn1.x500.style.BCStyle;

/**
 * Ein einzelnes Attribut eines Zertifikates.
 */
public class Attribute implements Serializable
{
  /**
   * OID fuer: Common-Name (CN).
   */
  public final static String CN = BCStyle.CN.getId();
  
  /**
   * OID fuer: Organisation (O).
   */
  public final static String O  = BCStyle.O.getId();
  
  /**
   * OID fuer: Organizational Unit (OU).
   */
  public final static String OU = BCStyle.OU.getId();

  /**
   * OID fuer: Vorname (GIVENNAME).
   */
  public final static String GIVENNAME = BCStyle.GIVENNAME.getId();
  
  /**
   * OID fuer: Nachname (SURNAME).
   */
  public final static String SURNAME = BCStyle.SURNAME.getId();
  
  /**
   * OID fuer: Mail-Adresse (EmailAddress).
   */
  public final static String EmailAddress = BCStyle.EmailAddress.getId();
  
  /**
   * OID fuer: Country (C).
   */
  public final static String C = BCStyle.C.getId();

  /**
   * OID fuer: State or Province (ST).
   */
  public final static String ST = BCStyle.ST.getId();

  /**
   * OID fuer: Locality (L).
   */
  public final static String L = BCStyle.L.getId();
  
  /**
   * OID fuer: Business-Category.
   */
  public final static String BC = BCStyle.BUSINESS_CATEGORY.getId();

  
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
