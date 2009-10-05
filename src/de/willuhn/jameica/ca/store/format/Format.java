/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/Format.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/05 16:02:38 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.format;

import java.io.OutputStream;

import de.willuhn.jameica.ca.store.Entry;

/**
 * Interface fuer Schluesselformate.
 */
public interface Format
{
  /**
   * Schreibt einen Schluessel in den OutputStream.
   * @param entry der Schluessel.
   * @param os der OutputStream.
   * @throws Exception
   */
  public void write(Entry entry, OutputStream os) throws Exception;
}


/**********************************************************************
 * $Log: Format.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
