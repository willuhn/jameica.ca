/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/wizzard/Attic/CreateCertificateWizzard.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/14 17:20:50 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

/**
 * Interface, welches von allen Wizzards zur Erzeugung neuer
 * Zertifikate implementiert werden muss.
 */
public interface CreateCertificateWizzard extends Part
{
  /**
   * Liefert einen sprechenden Namen fuer den Wizzard bzw. das Zertifikat-Format.
   * @return sprechender Name fuer den Wizzard.
   */
  public String getName();
  
  /**
   * Wird aufgerufen, wenn der User den "Uebernehmen"-Button geklickt hat.
   * Die Implementierung muss dann das passende Zertifikatstemplate erzeugen
   * und zurueckliefern.
   * @return Das Template, aus dem das Zertifikat erstellt werden soll.
   * @throws ApplicationException
   */
  public Template create() throws ApplicationException;
  
}


/**********************************************************************
 * $Log: CreateCertificateWizzard.java,v $
 * Revision 1.1  2009/10/14 17:20:50  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
