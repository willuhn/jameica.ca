/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/Format.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/10/07 11:47:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.format;

import java.io.File;

import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;

/**
 * Interface fuer Schluesselformate.
 */
public interface Format
{
  /**
   * Schreibt den Eintrag in den angegebenen Ordner.
   * @param e der Eintrag.
   * @param dir der Ziel-Ordner.
   * @param callback fuer die Abfrage von Passwoertern.
   * @throws Exception
   */
  public void write(Entry e, File dir, Callback callback) throws Exception;

  /**
   * Liest ein Zertifikat und Private-Key ein.
   * @param cert Datei mit dem Zertifikat.
   * @param key Datei mit dem Private-Key.
   * @param callback fuer die Abfrage von Passwoertern.
   * @return das eingelesene Zertifikat.
   * @throws Exception
   */
  public Entry read(File cert, File key, Callback callback) throws Exception;

  /**
   * Liefert einen sprechenden Namen fuer das Format.
   * @return sprechender Name fuer das Format.
   */
  public String getName();
}
