/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Store.java,v $
 * $Revision: 1.8 $
 * $Date: 2010/08/10 13:57:18 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;

/**
 * Kapselt Zugriff auf den Keystore ohne Jameica-Abhaengigkeiten.
 * Damit kann der Code auch unabhaengig von Jameica genutzt werden.
 */
public class Store
{
  private File file            = null;
  private KeyStore keystore    = null;
  private Callback callback    = null;
  private EntryFactory factory = null;
  
  private List<Entry> entries  = null;

  /**
   * ct.
   * @param file die Keystore-Datei.
   * @param callback Callback fuer die Passwort-Rueckfragen.
   * @throws Exception
   */
  public Store(File file, Callback callback) throws Exception
  {
    this.file = file;
    this.callback = callback;

    InputStream is = null;
    try
    {
      if (file.exists())
      {
        Logger.info("loading keystore " + file);
        is = new BufferedInputStream(new FileInputStream(file));
      }
      else
      {
        Logger.info("creating new keystore " + file);
      }
      
      this.keystore = KeyStore.getInstance("JKS");
      this.keystore.load(is,this.callback.getPassword(file));
    }
    finally
    {
      if (is != null)
      {
        try
        {
          is.close();
        }
        catch (Exception e)
        {
          // Werfen wir nicht weiter, weil es ggf. den urspruenglichen Fehler ueberdecken wuerde
          Logger.error("error while closing " + file,e);
        }
      }
    }
  }
  
  /**
   * Loescht einen Schluessel aus dem Keystore.
   * @param entry der zu loeschende Schluessel.
   * @throws Exception
   */
  public synchronized void delete(Entry entry) throws Exception
  {
    X509Certificate cert = entry.getCertificate();
    if (cert == null)
      throw new Exception("entry contains no certificate");

    String name = entry.getAlias();
    if (name == null)
      name = cert.getSubjectDN().getName();
    this.keystore.deleteEntry(name);
    this.store();
    Logger.info("deleted entry " + name);
  }
  
  /**
   * Speichert einen Eintrag im Keystore.
   * @param entry der zu speichernde Eintrag.
   * @throws Exception
   */
  public synchronized void store(Entry entry) throws Exception
  {
    X509Certificate cert = entry.getCertificate();
    if (cert == null)
      throw new Exception("entry contains no certificate");

    String name = entry.getAlias();
    if (name == null)
      name = cert.getSubjectDN().getName().toLowerCase(); // der keystore macht intern ein tolower
    
    // Checken, ob wir mit diesem Alias schon einen Eintrag haben
    boolean overwrite = true;
    
    List<Entry> list = this.getEntries();
    for (Entry e:list)
    {
      if (e.getAlias().equals(name))
      {
        try
        {
          Logger.info("entry " + name + " allready exists");
          overwrite = this.callback.overwrite(entry,e);
          Logger.info("overwrite: " + overwrite);
        }
        catch (OperationCanceledException oce)
        {
          Logger.info("operation cancelled");
          return;
        }
        break;
      }
    }
    
    if (!overwrite)
      name += ("-" + String.valueOf(System.currentTimeMillis())); // sollte eindeutig genug sein

    PrivateKey key = entry.getPrivateKey();
    if (key == null)
    {
      // Entry enthaelt keinen Private-Key. Wir speichern nur das Zertifikat
      this.keystore.setCertificateEntry(name,cert);
      Logger.info("storing certificate " + name);
    }
    else
    {
      // Wir speichern beides als Bundle.
      this.keystore.setKeyEntry(name,key,this.callback.getPassword(entry),new X509Certificate[]{cert});
      Logger.info("storing certificate+key " + name);
    }
    

    // Wir schreiben die Aenderungen sofort weg
    this.store();
  }

  
  /**
   * Liefert eine Liste der Eintraege im Keystore.
   * @return Liste der Eintraege.
   * @throws Exception
   */
  public List<Entry> getEntries() throws Exception
  {
    if (this.entries != null)
      return this.entries;

    this.entries = new ArrayList<Entry>();

    Enumeration<String> e = this.keystore.aliases();
    while (e.hasMoreElements())
    {
      String alias = e.nextElement();
      
      Entry entry = new Entry(this);
      entry.setAlias(alias);
      
      if (this.keystore.containsAlias(alias))
        entry.setCertificate((X509Certificate) this.keystore.getCertificate(alias));

      this.entries.add(entry);
    }
    Collections.sort(this.entries);
    return this.entries;
  }
  
  /**
   * Speichert den Keystore.
   * @throws Exception
   */
  private synchronized void store() throws Exception
  {
    OutputStream os = null;
    try
    {
      Logger.info("storing keystore " + file);
      os = new BufferedOutputStream(new FileOutputStream(file));
      this.keystore.store(os,this.callback.getPassword(file));
    }
    finally
    {
      if (os != null)
      {
        try
        {
          os.close();
        }
        catch (Exception e)
        {
          // Werfen wir nicht weiter, weil es ggf. den urspruenglichen Fehler ueberdecken wuerde
          Logger.error("error while closing " + file,e);
        }
      }
      this.entries = null; // forciert das Neu-Laden
    }
  }
  
  /**
   * Liefert die EntryFactory zum Erstellen, Importieren und Exportieren von Entries.
   * @return EntryFactory.
   */
  public EntryFactory getEntryFactory()
  {
    if (this.factory == null)
      this.factory = new EntryFactory(this.callback);
    return this.factory;
  }
  
  /**
   * Laedt den Private-Key des Entry on-demand.
   * @param entry der Entry.
   */
  void unlock(Entry entry) throws Exception
  {
    String alias = entry.getAlias();
    if (alias == null)
      return;
    
    if (!this.keystore.containsAlias(alias))
      return;
    
    Logger.debug("unlock private key " + alias);
    PrivateKey key = (PrivateKey) this.keystore.getKey(alias,this.callback.getPassword(entry));
    entry.setPrivateKey(key);
  }
}


/**********************************************************************
 * $Log: Store.java,v $
 * Revision 1.8  2010/08/10 13:57:18  willuhn
 * @N Cachen der Zertifikate - beschleunigt die Ladezeiten enorm
 *
 * Revision 1.7  2010/06/14 08:32:17  willuhn
 * @N Zertifikate alphabetisch sortieren
 *
 * Revision 1.6  2009/10/27 16:47:20  willuhn
 * @N Support zum Ueberschreiben/als Kopie anlegen beim Import
 * @N Integration in Jameica-Suche
 *
 * Revision 1.5  2009/10/14 17:20:50  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2009/10/07 17:09:11  willuhn
 * @N Schluessel loeschen
 *
 * Revision 1.3  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.2  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
