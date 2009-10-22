/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/template/CATemplate.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/22 17:27:08 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.template;

import java.util.List;
import java.util.Vector;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;

/**
 * Vorkonfiguriertes Template fuer ein CA-Zertifikat.
 */
public class CATemplate extends Template
{
  /**
   * Erstellt ein neues Template fuer ein CA-Zertifikat.
   */
  public CATemplate()
  {
    List<Extension> extensions = this.getExtensions();

    extensions.add(new Extension(X509Extensions.BasicConstraints.getId(),
                                 true,
                                 new BasicConstraints(true).getDEREncoded()));

    // Key-Usage
    extensions.add(new Extension(X509Extensions.KeyUsage.getId(),
                                 true,
                                 new KeyUsage(KeyUsage.cRLSign |
                                              KeyUsage.keyCertSign |
                                              KeyUsage.digitalSignature |
                                              KeyUsage.keyEncipherment | 
                                              KeyUsage.dataEncipherment).getDEREncoded()));

    // Server-Zertifikat
    Vector<DERObjectIdentifier> v = new Vector<DERObjectIdentifier>();
    v.add(KeyPurposeId.id_kp_serverAuth);
    extensions.add(new Extension(X509Extensions.ExtendedKeyUsage.getId(),
                                 false,
                                 new ExtendedKeyUsage(v).getDEREncoded()));

    // Netscape-Extension
    extensions.add(new Extension(MiscObjectIdentifiers.netscapeCertType.getId(),
                                 false,
                                 new NetscapeCertType(NetscapeCertType.sslCA).getDEREncoded()));
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.template.Template#getName()
   */
  public String getName()
  {
    return "CA-Zertifikat";
  }
}


/**********************************************************************
 * $Log: CATemplate.java,v $
 * Revision 1.1  2009/10/22 17:27:08  willuhn
 * @N Auswahl des Ausstellers via DialogInput
 *
 * Revision 1.2  2009/10/07 11:39:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 **********************************************************************/
