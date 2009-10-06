/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Store.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/06 16:36:00 $
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
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import de.willuhn.logging.Logger;

/**
 * Kapselt Zugriff auf den Keystore ohne Jameica-Abhaengigkeiten.
 * Damit kann der Code auch unabhaengig von Jameica genutzt werden.
 */
public class Store
{
  private File file         = null;
  private KeyStore keystore = null;
  private Callback callback = null;

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
      name = cert.getSubjectDN().getName();

    try
    {
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
    catch (Exception re)
    {
      throw new RemoteException("unable to store certificate",re);
    }
  }

  
  /**
   * Liefert eine Liste der Eintraege im Keystore.
   * @return Liste der Eintraege.
   * @throws Exception
   */
  public List<Entry> getEntries() throws Exception
  {
    List<Entry> list = new ArrayList<Entry>();

    try
    {
      Enumeration<String> e = this.keystore.aliases();
      while (e.hasMoreElements())
      {
        String alias = e.nextElement();
        
        Entry entry = new Entry(this);
        entry.setAlias(alias);
        
        if (this.keystore.containsAlias(alias))
          entry.setCertificate((X509Certificate) this.keystore.getCertificate(alias));

        list.add(entry);
      }
      return list;
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to load entries from keystore",e);
    }
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
    }
  }
  
  /**
   * Laedt den Private-Key des Entry on-demand.
   * @param entry der Entry.
   */
  void unlock(Entry entry) throws Exception
  {
    String alias = entry.getAlias();
    
    if (!this.keystore.containsAlias(alias))
      return;
    
    Logger.info("unlock private key " + alias);
    PrivateKey key = (PrivateKey) this.keystore.getKey(alias,this.callback.getPassword(entry));
    entry.setPrivateKey(key);
  }
}


/**********************************************************************
 * $Log: Store.java,v $
 * Revision 1.2  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
