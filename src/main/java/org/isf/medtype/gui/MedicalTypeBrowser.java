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
package org.isf.medtype.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.medtype.gui.MedicalTypeBrowserEdit.MedicalTypeListener;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.ModalJFrame;

/**
 * Browsing of table MedicalDsrType
 *
 * @author Furlanetto, Zoia, Finotto
 */
public class MedicalTypeBrowser extends ModalJFrame implements MedicalTypeListener{

	private static final long serialVersionUID = 1L;
	private ArrayList<MedicalType> pMedicalType;
	private String[] pColumns = {
			MessageBundle.getMessage("angal.common.codem"),
			MessageBundle.getMessage("angal.common.descriptionm")
	};
	private int[] pColumnWidth = {80, 200 };
	private JPanel jContainPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jNewButton = null;
	private JButton jEditButton = null;
	private JButton jCloseButton = null;
	private JButton jDeleteButton = null;
	private JTable jTable = null;
	private MedicalTypeBrowserModel model;
	private int selectedrow;
	private MedicalTypeBrowserManager manager = Context.getApplicationContext().getBean(MedicalTypeBrowserManager.class);
	private MedicalType medicalType = null;
	private final JFrame myFrame;
	
	/**
	 * This method initializes
	 */
	public MedicalTypeBrowser() {
		super();
		myFrame=this;
		initialize();
		setVisible(true);
	}
	
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 10;
        final int pfrmWidth = 5;
        final int pfrmHeight =4;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                screensize.width * pfrmWidth / pfrmBase, screensize.height * pfrmHeight / pfrmBase);
		this.setTitle(MessageBundle.getMessage("angal.medtype.medicaltypebrowsing"));
		this.setContentPane(getJContainPanel());
		//pack();	
	}
	
	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel();
			jContainPanel.setLayout(new BorderLayout());
			jContainPanel.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContainPanel.add(new JScrollPane(getJTable()),
					java.awt.BorderLayout.CENTER);
			validate();
		}
		return jContainPanel;
	}
	
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.add(getJNewButton(), null);
			jButtonPanel.add(getJEditButton(), null);
			jButtonPanel.add(getJDeleteButton(), null);
			jButtonPanel.add(getJCloseButton(), null);
		}
		return jButtonPanel;
	}
	
	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton();
			jNewButton.setText(MessageBundle.getMessage("angal.common.new"));
			jNewButton.setMnemonic(KeyEvent.VK_N);
			jNewButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					medicalType = new MedicalType("","");
					MedicalTypeBrowserEdit newrecord = new MedicalTypeBrowserEdit(myFrame,medicalType, true);
					newrecord.addMedicalTypeListener(MedicalTypeBrowser.this);
					newrecord.setVisible(true);
				}
			});
		}
		return jNewButton;
	}
	
	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton();
			jEditButton.setText(MessageBundle.getMessage("angal.common.edit"));
			jEditButton.setMnemonic(KeyEvent.VK_E);
			jEditButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(MedicalTypeBrowser.this,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						selectedrow = jTable.getSelectedRow();
						medicalType = (MedicalType) (model.getValueAt(selectedrow, -1));
						MedicalTypeBrowserEdit newrecord = new MedicalTypeBrowserEdit(myFrame,medicalType, false);
						newrecord.addMedicalTypeListener(MedicalTypeBrowser.this);
						newrecord.setVisible(true);
					}
				}
			});
		}
		return jEditButton;
	}
	
	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(MessageBundle.getMessage("angal.common.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}
	
	/**
	 * This method initializes jDeleteButton
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton();
			jDeleteButton.setText(MessageBundle.getMessage("angal.common.delete"));
			jDeleteButton.setMnemonic(KeyEvent.VK_D);
			jDeleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(
								MedicalTypeBrowser.this,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), 
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						MedicalType dis = (MedicalType) (model.getValueAt(jTable.getSelectedRow(), -1));
						int n = JOptionPane.showConfirmDialog(null,
								MessageBundle.getMessage("angal.medtype.deletemedicaltype")+" \" "+dis.getDescription() + "\" ?",
								MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
						
						try {
							if ((n == JOptionPane.YES_OPTION)
									&& (manager.deleteMedicalType(dis))) {
								pMedicalType.remove(jTable.getSelectedRow());
								model.fireTableDataChanged();
								jTable.updateUI();
							}
						} catch (OHServiceException e) {
							OHServiceExceptionUtil.showMessages(e);
						}
					}
				}
				
			});
		}
		return jDeleteButton;
	}
	
	public JTable getJTable() {
		if (jTable == null) {
			model = new MedicalTypeBrowserModel();
			jTable = new JTable(model);
			jTable.getColumnModel().getColumn(0).setMinWidth(pColumnWidth[0]);
			jTable.getColumnModel().getColumn(1).setMinWidth(pColumnWidth[1]);
		}return jTable;
	}
	
	
	class MedicalTypeBrowserModel extends DefaultTableModel {
		
		private static final long serialVersionUID = 1L;

		public MedicalTypeBrowserModel() {
			try {
				pMedicalType = manager.getMedicalType();
			} catch (OHServiceException e) {
				pMedicalType = null;
				OHServiceExceptionUtil.showMessages(e);
			}
		}
		
		public int getRowCount() {
			if (pMedicalType == null)
				return 0;
			return pMedicalType.size();
		}
		
		public String getColumnName(int c) {
			return pColumns[c];
		}

		public int getColumnCount() {
			return pColumns.length;
		}

		public Object getValueAt(int r, int c) {
			MedicalType medType = pMedicalType.get(r);
			if (c == -1) {
				return medType;
			} else if (c == 0) {
				return medType.getCode();
			} else if (c == 1) {
				return medType.getDescription();
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			//return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	public void medicalTypeUpdated(AWTEvent e) {
		pMedicalType.set(selectedrow, medicalType);
		((MedicalTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
		jTable.updateUI();
		if ((jTable.getRowCount() > 0) && selectedrow > -1)
			jTable.setRowSelectionInterval(selectedrow, selectedrow);
	}
	
	
	public void medicalTypeInserted(AWTEvent e) {
		pMedicalType.add(0, medicalType);
		((MedicalTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
		if (jTable.getRowCount() > 0)
			jTable.setRowSelectionInterval(0, 0);
	}
}
