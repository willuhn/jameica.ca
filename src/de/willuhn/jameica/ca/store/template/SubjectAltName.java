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

/**
 * Kapselt einen Subject Alt Name.
 */
public class SubjectAltName
{
  private SubjectAltNameType type = null;
  private String value = null;
  
  /**
   * Liefert den Typ.
   * @return der Typ.
   */
  public SubjectAltNameType getType()
  {
    return type;
  }
  
  /**
   * Speichert den Typ.
   * @param type der Typ.
   */
  public void setType(SubjectAltNameType type)
  {
    this.type = type;
  }
  
  /**
   * Liefert den Wert.
   * @return der Wert.
   */
  public String getValue()
  {
    return value;
  }
  
  /**
   * Speichert den Wert.
   * @param value der Wert.
   */
  public void setValue(String value)
  {
    this.value = value;
  }
}


