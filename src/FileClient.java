
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.awt.EventQueue;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileClient extends JFrame {

    private final JPanel contentPane;
    private List list = new List();
    private String filename;
    private String mode, to_edit;
    
    static FileInterface look_op;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        String name = "//localhost/FileServer";
                        look_op = (FileInterface) Naming.lookup(name);
                        
                        FileClient frame = new FileClient();
                        frame.setVisible(true);
                    } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                        System.out.println("FileClient err: " + ex);
                        System.exit(1);
                    }
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                }
            }
        });
    }

    /**
     * Refresh file list
     * @param filenames 
     */
    private void refresh_list(ArrayList<String> filenames) {
        list.removeAll();
        for (String name : filenames) {
            list.add(name);
        }
    }

    public FileClient() throws FileNotFoundException, UnsupportedEncodingException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 895, 560);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        final JButton btnCreateFile = new JButton("Create file");
        btnCreateFile.setBounds(224, 13, 111, 47);
        contentPane.add(btnCreateFile);

        JScrollPane scrollbar_2 = new JScrollPane(list);
        scrollbar_2.setBounds(12, 10, 200, 505);
        contentPane.add(scrollbar_2);

        final JButton btnEditFile = new JButton("Edit file");
        btnEditFile.setBounds(224, 73, 111, 47);
        contentPane.add(btnEditFile);

        final JButton btnViewFile = new JButton("Read file");
        btnViewFile.setBounds(224, 133, 111, 47);
        contentPane.add(btnViewFile);

        final JTextArea textArea_1 = new JTextArea();
        JScrollPane scrollbar_1 = new JScrollPane(textArea_1);
        scrollbar_1.setBounds(350, 10, 525, 505);
        textArea_1.setLineWrap(true);
        textArea_1.setWrapStyleWord(true);
        //textArea_1.setEditable(false);
        contentPane.add(scrollbar_1);
        textArea_1.setEditable(false);

        final JButton btnSaveFile = new JButton("Save file");
        btnSaveFile.setBounds(224, 190, 111, 47);
        contentPane.add(btnSaveFile);
        btnSaveFile.setEnabled(false);
        
        // Ananewsh ths listas arxeiwn pou uparxoun ston server
        try {
            refresh_list(look_op.getList());
        } catch (RemoteException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Katharizei h forma kai energopoieitai to koumpi Save to opoio tha xrhsimopoieitai kai sto Edit
        btnCreateFile.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gia na kseroume sto Save an prokeitai gia Create 'h Edit
                mode = "create";
                textArea_1.setEditable(true);
                textArea_1.setText("");
                // Mporoume na to swsoume
                btnSaveFile.setEnabled(true);
                // Hdh dhmiourgoume neo arxeio
                btnCreateFile.setEnabled(false);
                btnEditFile.setEnabled(false);
                btnViewFile.setEnabled(false);
            }
            
        });

		//edw pernei to arxeio me to onoma marios kai to emfanizei gia tropopoihsh
        btnEditFile.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (look_op.canEdit(list.getSelectedItem())) {
                        // Gia na kseroume sto Save an prokeitai gia Create 'h Edit
                        mode = "edit";
                        // Gia na eksasfalisoume oti tha tropopoihthei to epilegmeno arxeio se periptwsh
                        // pou ginei click se kapoio allo kata thn epeksergasia
                        to_edit = list.getSelectedItem();
                        // Emfanizoume to periexomeno pou uphrxe hdh sto arxeio
                        textArea_1.setText(look_op.viewFile(list.getSelectedItem()));
                        textArea_1.setEditable(true);
                        btnSaveFile.setEnabled(true);
                        // Hdh epeksergazomaste to arxeio
                        btnCreateFile.setEnabled(false);
                        btnEditFile.setEnabled(false);
                        btnViewFile.setEnabled(false);
                    } else {
                        System.out.println("Not the owner of file!");
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });

        // Pairnei to arxeio pou einai epilegmeno sth lista kai to anoigei mono gia anagnwsh
        btnViewFile.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    textArea_1.setText(look_op.viewFile(list.getSelectedItem()));
                    textArea_1.setEditable(false);
                } catch (RemoteException ex) {
                    Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });

        // Energopoieitai kata thn dhmiourgia 'h tropopoihsh arxeiou kai apothikeuei to arxeio ston server
        btnSaveFile.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (mode.equals("create")) {
                        if (!textArea_1.getText().equals("")) {
                            try {
                                do {
                                    filename = JOptionPane.showInputDialog(null, "Save as:");
                                    // Elegxos gia to an uparxei hdh to arxeio kai an to onoma pou dwthike einai keno
                                } while (look_op.exists(filename+".txt") || filename.isEmpty());
                                // Dhmiourgia arxeiou ston server
                                look_op.createFile(filename+".txt", textArea_1.getText());
                            } catch (RemoteException ex) {
                                Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else if (mode.equals("edit")) {
                        look_op.editFile(to_edit, textArea_1.getText());
                    }
                    textArea_1.setText("");
                    textArea_1.setEditable(false);
                    btnSaveFile.setEnabled(false);
                    // Mporoume na dhmiourghsoume neo arxeio
                    btnCreateFile.setEnabled(true);
                    // Mporoume na epeksergastoume kapoio arxeio
                    btnEditFile.setEnabled(true);
                    // Mporoume na diavasoume kapoio arxeio
                    btnViewFile.setEnabled(true);
                    // Ananewnoume th lista twn arxeiwn
                    refresh_list(look_op.getList());
                } catch (RemoteException ex) {
                    Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });

    }
}
