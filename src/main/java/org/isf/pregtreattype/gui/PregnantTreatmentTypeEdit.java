/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.pregtreattype.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.VoLimitedTextField;

public class PregnantTreatmentTypeEdit extends JDialog{

	private static final long serialVersionUID = 1L;
	private EventListenerList pregnantTreatmentTypeListeners = new EventListenerList();

    public interface PregnantTreatmentTypeListener extends EventListener {
        void pregnantTreatmentTypeUpdated(AWTEvent e);
        void pregnantTreatmentTypeInserted(AWTEvent e);
    }

    public void addPregnantTreatmentTypeListener(PregnantTreatmentTypeListener l) {
    	pregnantTreatmentTypeListeners.add(PregnantTreatmentTypeListener.class, l);
    }

    public void removePregnantTreatmentTypeListener(PregnantTreatmentTypeListener listener) {
    	pregnantTreatmentTypeListeners.remove(PregnantTreatmentTypeListener.class, listener);
    }

    private void firePregnantTreatmentInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = pregnantTreatmentTypeListeners.getListeners(PregnantTreatmentTypeListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((PregnantTreatmentTypeListener)listeners[i]).pregnantTreatmentTypeInserted(event);
    }
    private void firePregnantTreatmentUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = pregnantTreatmentTypeListeners.getListeners(PregnantTreatmentTypeListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((PregnantTreatmentTypeListener)listeners[i]).pregnantTreatmentTypeUpdated(event);
    }
    
	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JTextField descriptionTextField = null;
	private VoLimitedTextField codeTextField = null;
	private String lastdescription;
	private PregnantTreatmentType pregnantTreatmentType = null;
	private boolean insert;
	private JPanel jDataPanel = null;
	private JLabel jCodeLabel = null;
	private JPanel jCodeLabelPanel = null;
	private JPanel jDescriptionLabelPanel = null;
	private JLabel jDescriptionLabel = null;

	/**
	 * This is the default constructor; we pass the arraylist and the selectedrow
     * because we need to update them
	 */
	public PregnantTreatmentTypeEdit(JFrame owner,PregnantTreatmentType old,boolean inserting) {
		super(owner,true);
		insert = inserting;
		pregnantTreatmentType = old;//disease will be used for every operation
		lastdescription= pregnantTreatmentType.getDescription();
		initialize();
	}


	/**
	 * This method initializes this
	 */
	private void initialize() {
		
		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.preagtreattype.newpregnanttreatmenttyperecord"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.preagtreattype.editingpregnanttreatmenttyperecord"));
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.NORTH);  // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);  // Generated
		}
		return jContentPane;
	}

	/**
	 * This method initializes dataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			//dataPanel.setLayout(new BoxLayout(getDataPanel(), BoxLayout.Y_AXIS));  // Generated
			dataPanel.add(getJDataPanel(), null);
		}
		return dataPanel;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);  // Generated
			buttonPanel.add(getCancelButton(), null);  // Generated
		}
		return buttonPanel;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));  // Generated
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.common.ok"));  // Generated
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String key = codeTextField.getText();
					PregnantTreatmentTypeBrowserManager manager = Context.getApplicationContext().getBean(PregnantTreatmentTypeBrowserManager.class);

					try{
							if (descriptionTextField.getText().equals(lastdescription)){
								dispose();	
							}
							pregnantTreatmentType.setDescription(descriptionTextField.getText());
							pregnantTreatmentType.setCode(codeTextField.getText());
							boolean result = false;
							if (insert) {      // inserting
								result = manager.newPregnantTreatmentType(pregnantTreatmentType);
								if (result) {
									firePregnantTreatmentInserted();
								}
								if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.sql.thedatacouldnotbesaved"));
								else  dispose();
							}
							else {                          // updating
								if (descriptionTextField.getText().equals(lastdescription)){
									dispose();	
								}else{
									result = manager.updatePregnantTreatmentType(pregnantTreatmentType);
									if (result) {
										firePregnantTreatmentUpdated();
									}
									if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.sql.thedatacouldnotbesaved"));
									else  dispose();
								}

							}
					}catch(OHServiceException ex){
						OHServiceExceptionUtil.showMessages(ex);
					}
                }
			});
		}
		return okButton;
	}

	/**
	 * This method initializes descriptionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextField(20);
			if (!insert) {
				descriptionTextField.setText(pregnantTreatmentType.getDescription());
				lastdescription=pregnantTreatmentType.getDescription();
			} 
		}
		return descriptionTextField;
	}
	
	/**
	 * This method initializes codeTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new VoLimitedTextField(10);
			if (!insert) {
				codeTextField.setText(pregnantTreatmentType.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}

	/**
	 * This method initializes jDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDataPanel() {
		if (jDataPanel == null) {
			jDataPanel = new JPanel();
			jDataPanel.setLayout(new BoxLayout(getJDataPanel(),BoxLayout.Y_AXIS));
			jDataPanel.add(getJCodeLabelPanel(), null);
			jDataPanel.add(getCodeTextField(), null);
			jDataPanel.add(getJDescriptionLabelPanel(), null);
			jDataPanel.add(getDescriptionTextField(), null);
		}
		return jDataPanel;
	}

	/**
	 * This method initializes jCodeLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getJCodeLabel() {
		if (jCodeLabel == null) {
			jCodeLabel = new JLabel();
			jCodeLabel.setText(MessageBundle.getMessage("angal.preagtreattype.codemaxlength"));
		}
		return jCodeLabel;
	}

	/**
	 * This method initializes jCodeLabelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJCodeLabelPanel() {
		if (jCodeLabelPanel == null) {
			jCodeLabelPanel = new JPanel();
			//jCodeLabelPanel.setLayout(new BorderLayout());
			jCodeLabelPanel.add(getJCodeLabel(), BorderLayout.CENTER);
		}
		return jCodeLabelPanel;
	}

	/**
	 * This method initializes jDescriptionLabelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDescriptionLabelPanel() {
		if (jDescriptionLabelPanel == null) {
			jDescriptionLabel = new JLabel();
			jDescriptionLabel.setText(MessageBundle.getMessage("angal.preagtreattype.description2"));
			jDescriptionLabelPanel = new JPanel();
			jDescriptionLabelPanel.add(jDescriptionLabel, null);
		}
		return jDescriptionLabelPanel;
	}

}
