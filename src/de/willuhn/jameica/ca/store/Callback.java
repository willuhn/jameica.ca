/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Callback.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/10/27 16:47:20 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import de.willuhn.jameica.system.OperationCanceledException;


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
  
  /**
   * Wird aufgerufen, wenn versucht wird, einen Schluessel zu speichern, welcher
   * mit diesem Alias-Namen bereits existiert. Der User kann dann entscheiden,
   * ob der alte Eintrag ueberschrieben werden soll.
   * @param newEntry der neue hinzuzufuegende Schluessel.
   * @param oldEntry der gleichnamige bereits existierende Schluessel.
   * @return true, wenn der Schluessel ueberschrieben werden soll. false, wenn
   * der neue Schluessel zusaetzlich (unter einem neuen Alias) gespeichert werden soll.
   * @throws OperationCanceledException wenn der Import abgebrochen werden soll.
   */
  public boolean overwrite(Entry newEntry, Entry oldEntry) throws OperationCanceledException;
}


/**********************************************************************
 * $Log: Callback.java,v $
 * Revision 1.4  2009/10/27 16:47:20  willuhn
 * @N Support zum Ueberschreiben/als Kopie anlegen beim Import
 * @N Integration in Jameica-Suche
 *
 * Revision 1.3  2009/10/19 11:51:56  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
