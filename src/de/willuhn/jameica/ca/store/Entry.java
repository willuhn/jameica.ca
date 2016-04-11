/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.Extension;

import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;

/**
 * Ein einzelner Eintrag aus dem StoreService.
 * In der Regel handelt es sich hier um ein Schluesselpaar.
 */
public class Entry implements Comparable
{
  /**
   * Legt fest, ob nur tatsaechliche CA-Zertifikate als solche benutzt werden duerfen
   */
  public final static boolean CHECK_CA = Boolean.valueOf(false);
  
  private Store store            = null;

  private String alias           = null;
  private X509Certificate cert   = null;
  private PrivateKey key         = null;
  
  private Entry issuer           = null;
  private boolean issuerChecked  = false;
  
  private List<Entry> clients    = null;
  
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
   */
  public X509Certificate getCertificate()
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
   * Common-Name des Inhabers.
   * @return Liefert den Common-Name des Inhabers.
   */
  public String getCommonName()
  {
    X509Certificate c = this.getCertificate();
    if (c == null)
      return null;
    return new Certificate(c).getSubject().getAttribute(Principal.COMMON_NAME);
  }
  
  /**
   * Organisation des Inhabers.
   * @return Liefert das O-Attribut des Inhabers.
   */
  public String getOrganization()
  {
    X509Certificate c = this.getCertificate();
    if (c == null)
      return null;
    return new Certificate(c).getSubject().getAttribute(Principal.ORGANIZATION);
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
    if (this.issuer != null || this.issuerChecked)
      return this.issuer;

    this.issuerChecked = true;
    
    X509Certificate x = this.getCertificate();
    X500Principal issuer = x.getIssuerX500Principal();

    // brauchmer gar nicht erst anfangen zu suchen
    if (issuer == null)
      return null;

    // selbstsigniert
    if (issuer.equals(x.getSubjectX500Principal()))
      return null;

    byte[] issuerSig = x.getExtensionValue(Extension.authorityKeyIdentifier.getId());

    List<Entry> all = this.store.getEntries();
    // wenn die Signatur des Ausstellers bekannt ist, suchen wir anhand
    // der. Das ist die zuverlaessigste Methode.
    if (issuerSig != null && issuerSig.length > 0)
    {
      for (Entry e:all)
      {
        if (CHECK_CA && !e.isCA())
          continue;

        
        // OK, wir haben die Signatur des Ausstellers. Mal schauen,
        // ob wir sie im Keystore finden.
        byte[] test = e.getCertificate().getPublicKey().getEncoded();
        if (Arrays.equals(issuerSig,test))
        {
          this.issuer = e;
          return e; // gefunden
        }
      }
    }

    // Wir haben die Signatur nicht, stimmt vielleicht einen passenden DN?
    for (Entry e:all)
    {
      if (CHECK_CA && !e.isCA())
        continue;

      X500Principal subject = e.getCertificate().getSubjectX500Principal();
      if (subject.equals(issuer))
      {
        this.issuer = e;
        return e;
      }
    }
    
    // nichts gefunden
    return null;
  }
  
  /**
   * Liefert true, wenn es sich um ein CA-Zertifikat handelt.
   * @return true, wenn es sich um ein CA-Zertifikat handelt.
   */
  public boolean isCA()
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
    if (this.clients != null)
      return this.clients;
    
    this.clients = new ArrayList<Entry>();

    if (CHECK_CA && !this.isCA())
      return this.clients;

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
      byte[] issuerSig = x.getExtensionValue(Extension.authorityKeyIdentifier.getId());
      if (issuerSig != null && issuerSig.length > 0)
      {
        // Issuer-Signatur angegeben. Mal checken, ob es unsere ist
        if (Arrays.equals(issuerSig,sig))
        {
          // jepp, passt
          this.clients.add(e);
          continue;
        }
      }
      
      // Checken, ob der DN uebereinstimmt.
      X500Principal p = c.getIssuerX500Principal();
      
      // passt, nehmen wir auch
      if (p != null && p.equals(self))
      {
        this.clients.add(e);
        continue;
      }
    }
    
    Collections.sort(this.clients);
    return this.clients;
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    if (o == null || !(o instanceof Entry))
      return -1;
    
    Entry e = (Entry) o;
    
    int i = compare(this.getCommonName(),e.getCommonName());
    if (i != 0)
      return i;
    
    // CN ist identisch, dann vergleichen wir stattdessen O
    return compare(this.getOrganization(),e.getOrganization());
  }
  
  /**
   * Vergleicht die beiden Strings NULL-sicher.
   * @param s1 String 1.
   * @param s2 String 2.
   * @return der Vergleichswert.
   */
  private int compare(String s1, String s2)
  {
    if (s1 == null || s1.length() == 0) return -1;
    if (s2 == null || s2.length() == 0) return 1;
    
    return s1.compareTo(s2);
  }

  /**
   * Generiert von Eclipse.
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.alias == null) ? 0 : this.alias.hashCode());
    result = prime * result + ((this.cert == null) ? 0 : this.cert.hashCode());
    result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
    return result;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    Entry other = (Entry) obj;
    if (this.alias == null)
    {
      if (other.alias != null)
        return false;
    }
    else if (!this.alias.equals(other.alias))
      return false;
    
    if (this.cert == null)
    {
      if (other.cert != null)
        return false;
    }
    else if (!this.cert.equals(other.cert))
      return false;

    if (this.key == null)
    {
      if (other.key != null)
        return false;
    }
    else if (!this.key.equals(other.key))
      return false;

    return true;
  }
  
  
}
