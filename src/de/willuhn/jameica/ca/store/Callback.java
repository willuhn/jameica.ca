/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Callback.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/06 16:36:00 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

/**
 * Callback-Interface fuer die Benutzer-Rueckfragen.
 */
public interface Callback
{
  /**
   * Fragt das Passwort des uebergebenen Objektes ab.
   * Hierbei kann es sich z.Bsp. um einen Keystore oder einen Privatekey handeln.
   * @param context das Context-Objekt.
   * @return das Passwort.
   * @throws Exception
   */
  public char[] getPassword(Object context) throws Exception;
}


/**********************************************************************
 * $Log: Callback.java,v $
 * Revision 1.2  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
