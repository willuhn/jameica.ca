/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Entry.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/10/13 00:26:31 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.X509Extensions;

/**
 * Ein einzelner Eintrag aus dem StoreService.
 * In der Regel handelt es sich hier um ein Schluesselpaar.
 */
public class Entry
{
  private Store store          = null;

  private String alias         = null;
  private X509Certificate cert = null;
  private PrivateKey key       = null;
  
  /**
   * ct.
   * Oeffentlicher Konstruktor.
   */
  public Entry()
  {
    
  }
  
  /**
   * ct.
   * Interner Konstruktor.
   * @param store der Store, aus dem der Entry stammt.
   */
  Entry(Store store)
  {
    this.store = store;
  }
  
  /**
   * Liefert das Zertifikat.
   * @return das Zertifikat.
   * @throws Exception 
   */
  public X509Certificate getCertificate() throws Exception
  {
    return this.cert;
  }
  
  /**
   * Speichert das Zertifikat.
   * @param cert das Zertifikat.
   */
  public void setCertificate(X509Certificate cert)
  {
    this.cert = cert;
  }
  
  /**
   * Liefert den Private-Key - falls vorhanden.
   * @return der Private-Key.
   * @throws Exception
   */
  public PrivateKey getPrivateKey() throws Exception
  {
    if (this.key == null && this.store != null)
      this.store.unlock(this);
    return this.key;
  }
  
  /**
   * Speichert den Private-Key.
   * @param key der Private-Key.
   */
  public void setPrivateKey(PrivateKey key)
  {
    this.key = key;
  }

  /**
   * Liefert den Alias des Eintrages.
   * @return der Alias.
   */
  public String getAlias()
  {
    return this.alias;
  }
  
  /**
   * Speichert den Alias-Namen.
   * @param alias Alias-Name.
   */
  public void setAlias(String alias)
  {
    this.alias = alias;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return this.alias;
  }
  
  /**
   * Liefert den Aussteller, insofern ermittelbar.
   * @return der Aussteller.
   * Wenn es sich um ein selbstsigniertes Zertifikat handelt, liefert die Funktion NULL.
   * @throws Exception
   */
  public Entry getIssuer() throws Exception
  {
    X509Certificate x = this.getCertificate();
    X500Principal issuer = x.getIssuerX500Principal();

    // brauchmer gar nicht erst anfangen zu suchen
    if (issuer == null)
      return null;

    // selbstsigniert
    if (issuer.equals(x.getSubjectX500Principal()))
      return null;

    byte[] issuerSig = x.getExtensionValue(X509Extensions.AuthorityKeyIdentifier.getId());

    List<Entry> all = this.store.getEntries();
    // wenn die Signatur des Ausstellers bekannt ist, suchen wir anhand
    // der. Das ist die zuverlaessigste Methode.
    if (issuerSig != null && issuerSig.length > 0)
    {
      for (Entry e:all)
      {
        if (!e.isCA())
          continue;
        
        // OK, wir haben die Signatur des Ausstellers. Mal schauen,
        // ob wir sie im Keystore finden.
        byte[] test = e.getCertificate().getPublicKey().getEncoded();
        if (Arrays.equals(issuerSig,test))
          return e; // gefunden
      }
    }

    // Wir haben die Signatur nicht, stimmt vielleicht einen passenden DN?
    for (Entry e:all)
    {
      if (!e.isCA())
        continue;

      X500Principal subject = e.getCertificate().getSubjectX500Principal();
      if (subject.equals(issuer))
        return e;
    }
    
    // nichts gefunden
    return null;
  }
  
  /**
   * Liefert true, wenn es sich um ein CA-Zertifikat handelt.
   * @return true, wenn es sich um ein CA-Zertifikat handelt.
   * @throws Exception
   */
  public boolean isCA() throws Exception
  {
    X509Certificate x = this.getCertificate();
    return x.getBasicConstraints() > -1;
  }
  
  /**
   * Liefert alle Schluessel, die von diesem signiert wurden.
   * @return Liste aller Schluessel, die von diesem signiert wurden.
   * Die Funktion liefert nie NULL sondern hoechstens eine leere Liste.
   * @throws Exception
   */
  public List<Entry> getClients() throws Exception
  {
    List<Entry> children = new ArrayList<Entry>();

    // 1. Wir checken erstmal, ob wir ueberhaupt ein CA-Zertifikat sind
    if (!this.isCA())
      return children;

    X509Certificate x = this.getCertificate();
    
    byte[] sig = x.getPublicKey().getEncoded();
    X500Principal self = x.getSubjectX500Principal();
    
    // 2. Wir sind ein CA-Zertifikat, jetzt holen wir alle
    // Zertifikate, bei denen wir als CA eingetragen sind.
    List<Entry> all = this.store.getEntries();
    for (Entry e:all)
    {
      X509Certificate c = e.getCertificate();
      
      // sind wir selbst
      if (c.equals(x))
        continue;
      
      // Checken, ob die Aussteller-Signatur angegeben ist
      byte[] issuerSig = x.getExtensionValue(X509Extensions.AuthorityKeyIdentifier.getId());
      if (issuerSig != null && issuerSig.length > 0)
      {
        // Issuer-Signatur angegeben. Mal checken, ob es unsere ist
        if (Arrays.equals(issuerSig,sig))
        {
          // jepp, passt
          children.add(e);
          continue;
        }
      }
      
      // Checken, ob der DN uebereinstimmt.
      X500Principal p = c.getIssuerX500Principal();
      
      // passt, nehmen wir auch
      if (p != null && p.equals(self))
      {
        children.add(e);
        continue;
      }
    }
    
    return children;
  }
}


/**********************************************************************
 * $Log: Entry.java,v $
 * Revision 1.3  2009/10/13 00:26:31  willuhn
 * @N Tree-View fuer Zertifikate
 *
 * Revision 1.2  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
