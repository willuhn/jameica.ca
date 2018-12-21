/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.template;

import org.bouncycastle.asn1.x509.GeneralName;

/**
 * Kapselt die Arten von Werten fuer SubjectAltName.
 */
public enum SubjectAltNameType
{
  /**
   * DNS-Name.
   */
  DNS(GeneralName.dNSName,"DNS-Adresse"),
  
  /**
   * ID-Adresse.
   */
  IP(GeneralName.iPAddress,"IP-Adresse"),
  
  /**
   * Mail-Adresse.
   */
  MAIL(GeneralName.rfc822Name,"Mail-Adresse"),
  ;
  
  /**
   * Der Default-Wert.
   */
  public final static SubjectAltNameType DEFAULT = SubjectAltNameType.DNS;

  private int id;
  private String name;
  
  /**
   * ct.
   * @param id
   * @param name
   */
  private SubjectAltNameType(int id, String name)
  {
    this.id = id;
    this.name = name;
  }
  
  /**
   * Liefert die ID.
   * @return die ID.
   */
  public int getId()
  {
    return id;
  }
  
  /**
   * Liefert den Namen.
   * @return der Name.
   */
  public String getName()
  {
    return name;
  }

}


