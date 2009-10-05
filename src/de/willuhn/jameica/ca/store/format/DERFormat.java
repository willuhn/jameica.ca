/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/DERFormat.java,v $
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
 * Implementierung des DER-Format.
 */
public class DERFormat implements Format
{

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#write(de.willuhn.jameica.ca.store.Entry, java.io.OutputStream)
   */
  public void write(Entry entry, OutputStream os) throws Exception
  {
    os.write(entry.getCertificate().getEncoded());
    os.flush();
  }

}


/**********************************************************************
 * $Log: DERFormat.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
