/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/model/EntryListModel.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/07 16:38:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.model;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.service.StoreService;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;
import de.willuhn.jameica.system.Application;

/**
 * Implementiert ein Model, welches die Schluessel des Keystore als Liste (Tabelle) liefert.
 */
public class EntryListModel
{
  private List<Line> lines = null;
  
  /**
   * Liefert eine Liste der Schluessel.
   * @return Liste der Schluessel.
   * @throws Exception
   */
  public synchronized List<Line> getItems() throws Exception
  {
    if (this.lines != null)
      return this.lines;
    
    this.lines = new ArrayList<Line>();
    StoreService service = (StoreService) Application.getServiceFactory().lookup(Plugin.class,"store");
    Store store = service.getStore();
    List<Entry> entries = store.getEntries();
    for (Entry e:entries)
    {
      this.lines.add(new Line(e));
    }
    
    return this.lines;
  }

  /**
   * Implementiert eine einzelne Zeile.
   */
  public static class Line
  {
    private Entry entry = null;
    
    /**
     * ct.
     * @param entry
     */
    public Line(Entry entry)
    {
      this.entry = entry;
    }
    
    /**
     * Liefert den Namen, auf den das Zertifikat ausgestellt ist.
     * @return Name, auf den das Zertifikat ausgestellt ist.
     * @throws Exception
     */
    public String getSubject() throws Exception
    {
      X509Certificate x = this.entry.getCertificate();
      Certificate c = new Certificate(x);
      String cn = c.getSubject().getAttribute(Principal.COMMON_NAME);
      return cn == null ? x.getSubjectDN().getName() : cn;
    }

    /**
     * Liefert den Namen der Organisation, auf die das Zertifikat ausgestellt ist.
     * @return Name der Organisation, auf die das Zertifikat ausgestellt ist.
     * @throws Exception
     */
    public String getOrganization() throws Exception
    {
      X509Certificate x = this.entry.getCertificate();
      Certificate c = new Certificate(x);
      return c.getSubject().getAttribute(Principal.ORGANIZATION);
    }

    /**
     * Liefert den Namen des Ausstellers.
     * @return Name des Ausstellers.
     * @throws Exception
     */
    public String getIssuer() throws Exception
    {
      X509Certificate x = this.entry.getCertificate();
      Certificate c = new Certificate(x);
      String cn = c.getIssuer().getAttribute(Principal.COMMON_NAME);
      return cn == null ? x.getIssuerDN().getName() : cn;
    }
    
    /**
     * Liefert das Beginn-Datum der Gueltigkeit.
     * @return Beginn-Datum der Gueltigkeit.
     * @throws Exception
     */
    public Date getValidFrom() throws Exception
    {
      return this.entry.getCertificate().getNotBefore();
    }

    /**
     * Liefert das End-Datum der Gueltigkeit.
     * @return End-Datum der Gueltigkeit.
     * @throws Exception
     */
    public Date getValidTo() throws Exception
    {
      return this.entry.getCertificate().getNotAfter();
    }

    /**
     * Liefert true, wenn ein Private-Key vorhanden ist.
     * @throws Exception
     */
    public boolean havePrivateKey() throws Exception
    {
      return this.entry.getPrivateKey() != null;
    }
  }
}


/**********************************************************************
 * $Log: EntryListModel.java,v $
 * Revision 1.2  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.1  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 **********************************************************************/
