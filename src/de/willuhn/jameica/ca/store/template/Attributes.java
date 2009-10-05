/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/template/Attic/Attributes.java,v $
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

package de.willuhn.jameica.ca.store.template;

import java.io.Serializable;

/**
 * Attribute des Zertifikates.
 */
public class Attributes implements Serializable
{
  /**
   * Common-Name (CN).
   */
  public String CN = null;
  
  /**
   * Organisation (O).
   */
  public String O  = null;
  
  /**
   * Organizational Unit (OU).
   */
  public String OU = null;

  /**
   * Vorname (GIVENNAME).
   */
  public String GIVENNAME = null;
  
  /**
   * Nachname (SURNAME).
   */
  public String SURNAME = null;
  
  /**
   * Mail-Adresse (EmailAddress).
   */
  public String EmailAddress = null;
  
  
}


/**********************************************************************
 * $Log: Attributes.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
