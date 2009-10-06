/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/template/Extension.java,v $
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

/**
 * Eine einzelne Extension eines Zertifikates.
 * Das sind typischerweise die Verwendungszwecke (Key-Usage).
 */
public class Extension implements Serializable
{
  private String oid       = null;
  private boolean critical = false;
  private byte[] value     = null;

  /**
   * ct.
   * Bean-Konstruktor.
   */
  public Extension()
  {
  }
  
  /**
   * ct.
   * @param oid OID.
   * @param critical true, wenn die Extension critical ist.
   * Sollte true sein, wenn das Zertifikat nur genau dann fuer den angegebenen Zweck
   * genutzt werden koennen soll, wenn es diese Extension besitzt.
   * @param value Wert.
   */
  public Extension(String oid, boolean critical, byte[] value)
  {
    this.oid      = oid;
    this.critical = critical;
    this.value    = value;
  }
  
  /**
   * Liefert die OID der Extension.
   * @return OID der Extension.
   */
  public String getOid()
  {
    return this.oid;
  }

  /**
   * Speichert die OID der Extension.
   * @param oid die OID der Extension,
   */
  public void setOid(String oid)
  {
    this.oid = oid;
  }
  
  /**
   * Liefert true, wenn die Extension critical ist.
   * @return true, wenn die Extension critical ist.
   * Sollte true sein, wenn das Zertifikat nur genau dann fuer den angegebenen Zweck
   * genutzt werden koennen soll, wenn es diese Extension besitzt.
   */
  public boolean isCritical()
  {
    return this.critical;
  }

  /**
   * Speichert, ob die Extension critical ist.
   * @param critical true, wenn die Extension critical ist.
   * Sollte true sein, wenn das Zertifikat nur genau dann fuer den angegebenen Zweck
   * genutzt werden koennen soll, wenn es diese Extension besitzt.
   */
  public void setCritical(boolean critical)
  {
    this.critical = critical;
  }

  /**
   * Liefert den Wert der Extension.
   * @return Wert der Extension.
   */
  public byte[] getValue()
  {
    return this.value;
  }

  /**
   * Speichert den Wert der Extension.
   * @param value Wert der Extension.
   */
  public void setValue(byte[] value)
  {
    this.value = value;
  }
}


/**********************************************************************
 * $Log: Extension.java,v $
 * Revision 1.1  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 **********************************************************************/