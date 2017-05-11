/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.io.File;

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

  /**
   * Wird aufgerufen, wenn versucht wird, eine Datei zu ueberschreiben, die schon existiert.
   * @param file die zu ueberschreibende Datei.
   * @return true, wenn die Datei ueberschrieben werden darf.
   * @throws OperationCanceledException
   */
  public boolean overwrite(File file) throws OperationCanceledException;
}
