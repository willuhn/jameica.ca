/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/template/Template.java,v $
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
import java.util.Date;


/**
 * Template fuer ein zu erstellendes Zertifikat.
 */
public class Template implements Serializable
{
  private int keysize         = 1024;
  private String signatureAlg = "SHA1withRSA";
  
  private Attributes subject  = new Attributes();
  private Attributes issuer   = new Attributes();
  
  private Date validFrom      = new Date();
  private Date validTo        = new Date(System.currentTimeMillis() + (1000l*60*60*24*365*10)); // per Default 10 Jahre
  
  /**
   * Liefert die Schluessel-Laenge.
   * @return die Schluessel-Laenge.
   */
  public int getKeySize()
  {
    return this.keysize;
  }
  
  /**
   * Liefert die Attribute auf die das Zertifikat ausgestellt werden soll.
   * @return Attribute des Subjects.
   */
  public Attributes getSubject()
  {
    return this.subject;
  }
  
  /**
   * Liefert die Attributes des Ausstellers.
   * @return Attributes des Ausstellers.
   */
  public Attributes getIssuer()
  {
    return this.issuer;
  }
  
  /**
   * Liefert das Beginn-Datum der Gueligkeit.
   * @return Gueltig ab.
   */
  public Date getValidFrom()
  {
    return this.validFrom;
  }
  
  /**
   * Liefert das End-Datum der Gueltigkeit.
   * @return Gueltig bis.
   */
  public Date getValidTo()
  {
    return this.validTo;
  }
  
  /**
   * Liefert den zu verwendenden Signatur-Algorithmus.
   * @return den zu verwendenden Signatur-Algorithmus.
   */
  public String getSignatureAlgorithm()
  {
    return this.signatureAlg;
  }

}


/**********************************************************************
 * $Log: Template.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
