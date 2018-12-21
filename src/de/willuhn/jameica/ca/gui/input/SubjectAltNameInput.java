/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.input;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.store.template.SubjectAltName;
import de.willuhn.jameica.ca.store.template.SubjectAltNameType;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.ButtonInput;
import de.willuhn.jameica.gui.input.MultiInput;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Auswahl-/Eingabefeld fuer den SubjectAltName.
 */
public class SubjectAltNameInput extends MultiInput
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  private Listener listener = null;
  private SelectInput type = null;
  private ButtonInput text = null;
  
  /**
   * ct.
   * @param listener der Listener, der beim Klick auf den "+"-Button ausgeloest werden soll.
   */
  public SubjectAltNameInput(Listener listener)
  {
    this.listener = listener;
    this.setName(i18n.tr("Alternativer Name"));
    this.add(this.getTypeInput());
    this.add(this.getTextInput());
  }
  
  /**
   * Liefert das Auswahlfeld fuer die Art des SAN.
   * @return Auswahlfeld fuer die Art des SAN.
   */
  private SelectInput getTypeInput()
  {
    if (this.type != null)
      return this.type;
    
    this.type = new SelectInput(SubjectAltNameType.values(),SubjectAltNameType.DEFAULT);
    this.type.setData(MultiInput.DATA_WEIGHT,1);
    this.type.setAttribute("name");
    this.type.setName("");
    return this.type;
  }
  
  /**
   * Liefert das Eingabefeld fuer den Text.
   * @return das Eingabefeld fuer den Text.
   */
  private ButtonInput getTextInput()
  {
    if (this.text != null)
      return this.text;
    
    this.text = new ValueInput();
    return this.text;
  }
  
  /**
   * @see de.willuhn.jameica.gui.input.MultiInput#getValue()
   */
  @Override
  public Object getValue()
  {
    SubjectAltName result = new SubjectAltName();
    result.setType((SubjectAltNameType) this.getTypeInput().getValue());
    result.setValue((String)this.getTextInput().getValue());
    return result;
  }
  
  /**
   * Entfernt den Plus-Button.
   */
  public void removeButton()
  {
    Composite comp = (Composite) this.getTextInput().getControl();
    for (Control child:comp.getChildren())
    {
      if ((child instanceof Button) && !child.isDisposed())
      {
        child.dispose();
        
        GridLayout l = (GridLayout) comp.getLayout();
        l.numColumns = 1;
        comp.layout();
        break;
      }
    }
  }
  
  /**
   * Eingabefeld fuer den Wert mit einem Button hinten dran.
   */
  private class ValueInput extends ButtonInput
  {
    private Text widget = null;
    
    /**
     * ct.
     */
    private ValueInput()
    {
      this.setData(MultiInput.DATA_WEIGHT,5);
      this.setButtonImage(SWTUtil.getImage("list-add.png"));
      this.addButtonListener(listener);
    }
    
    /**
     * @see de.willuhn.jameica.gui.input.Input#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object value)
    {
      if (this.widget == null || this.widget.isDisposed())
        return;
      
      this.widget.setText(value != null ? value.toString() : "");
    }
    
    /**
     * @see de.willuhn.jameica.gui.input.Input#getValue()
     */
    @Override
    public Object getValue()
    {
      if (this.widget == null || this.widget.isDisposed())
        return "";
      return this.widget.getText();
    }
    
    /**
     * @see de.willuhn.jameica.gui.input.ButtonInput#getClientControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Control getClientControl(Composite parent)
    {
      if (this.widget != null && !this.widget.isDisposed())
        return this.widget;
      
      this.widget = GUI.getStyleFactory().createText(parent);
      this.widget.setText("");
      return this.widget;
    }
  }
}


