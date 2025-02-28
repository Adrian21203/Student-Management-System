import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FereastraPrincipala extends JFrame {
    private JTextField IdStudent, Nume, Prenume, Adresa, Telefon, Codpostal;
    private JTable tabel;
    private DefaultTableModel modeltabel;

    public FereastraPrincipala() {
        setTitle("Gestionare Baza de Date Studenti");
        setSize(1000, 600); // Dimensiunea ferestrei
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titlu = new JLabel("Studenti Management", JLabel.CENTER);
        titlu.setFont(new Font("Serif", Font.BOLD, 24));
        add(titlu, BorderLayout.NORTH);

        // Panel pentru câmpurile de text
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajustare font etichete
        Font labelFont = new Font("Serif", Font.BOLD, 20);

        JLabel lblIdStudent = new JLabel("ID Student");
        lblIdStudent.setFont(labelFont);
        inputPanel.add(lblIdStudent);

        IdStudent = new JTextField();
        IdStudent.setFont(new Font("Serif", Font.PLAIN, 18));
        inputPanel.add(IdStudent);

        JLabel lblNume = new JLabel("Nume");
        lblNume.setFont(labelFont);
        inputPanel.add(lblNume);

        Nume = new JTextField();
        Nume.setFont(new Font("Serif", Font.PLAIN, 18));
        inputPanel.add(Nume);

        JLabel lblPrenume = new JLabel("Prenume");
        lblPrenume.setFont(labelFont);
        inputPanel.add(lblPrenume);

        Prenume = new JTextField();
        Prenume.setFont(new Font("Serif", Font.PLAIN, 18));
        inputPanel.add(Prenume);

        JLabel lblAdresa = new JLabel("Adresa");
        lblAdresa.setFont(labelFont);
        inputPanel.add(lblAdresa);

        Adresa = new JTextField();

        Adresa.setFont(new Font("Serif", Font.PLAIN, 18));
        Adresa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Adresa.getText().trim().isEmpty()) {
                    Adresa.setText("Str. ");
                }
            }
        });
        inputPanel.add(Adresa);

        JLabel lblCodpostal = new JLabel("Cod postal");
        lblCodpostal.setFont(labelFont);
        inputPanel.add(lblCodpostal);

        Codpostal = new JTextField();
        Codpostal.setFont(new Font("Serif", Font.PLAIN, 18));
        inputPanel.add(Codpostal);

        JLabel lblTelefon = new JLabel("Telefon");
        lblTelefon.setFont(labelFont);
        inputPanel.add(lblTelefon);

        Telefon = new JTextField();
        Telefon.setFont(new Font("Serif", Font.PLAIN, 18));
        inputPanel.add(Telefon);

        JPanel inputPanelContainer = new JPanel();
        inputPanelContainer.setLayout(new BorderLayout());
        inputPanelContainer.add(inputPanel, BorderLayout.CENTER);
        inputPanelContainer.setPreferredSize(new Dimension(400, getHeight())); // Lățime fixă de 300 pixeli

        // Tabel
        String[] columns = {"Id Student", "Nume", "Prenume", "Adresa", "Cod Postal", "Telefon"};
        modeltabel = new DefaultTableModel(new Object[][] {}, columns);
        tabel = new JTable(modeltabel);

        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setPreferredSize(new Dimension(600, 300)); // Lățime fixă de 700 pixeli

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(inputPanelContainer, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Butoane
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 6, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton dbConnect = new JButton("BD Connect");
        JButton insert = new JButton("Insert");
        JButton reset = new JButton("Reset");
        JButton delete = new JButton("Delete");
        JButton update = new JButton("Update");
        JButton print = new JButton("Print");

        // Ajustare dimensiune butoane
        dbConnect.setPreferredSize(new Dimension(200, 80));
        insert.setPreferredSize(new Dimension(200, 80));
        reset.setPreferredSize(new Dimension(200, 80));
        delete.setPreferredSize(new Dimension(200, 80));
        update.setPreferredSize(new Dimension(200, 80));
        print.setPreferredSize(new Dimension(200, 80));

        bottomPanel.add(dbConnect);
        bottomPanel.add(insert);
        bottomPanel.add(reset);
        bottomPanel.add(delete);
        bottomPanel.add(update);
        bottomPanel.add(print);

        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        dbConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectBD.loadDataFromDatabase(modeltabel);
            }
        });

      insert.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String idText = IdStudent.getText().trim();
        String nume = Nume.getText().trim();
        String prenume = Prenume.getText().trim();
        String adresa = Adresa.getText().trim();
        String telefon = Telefon.getText().trim();
        String codpostal = Codpostal.getText().trim();

        if (idText.isEmpty() || nume.isEmpty() || prenume.isEmpty() || adresa.isEmpty() || telefon.isEmpty() || codpostal.isEmpty()) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "Toate câmpurile trebuie completate.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int maxId = ConnectBD.getNextIdStudent(); // Obține ID-ul maxim existent
        int idStudent;
        try {
            idStudent = Integer.parseInt(idText);
            if (idStudent <= 0) {
                // ID-ul introdus nu este mai mare decât 0
                JOptionPane.showMessageDialog(FereastraPrincipala.this, "ID-ul trebuie să fie un număr pozitiv.", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (idStudent != maxId) {
                JOptionPane.showMessageDialog(FereastraPrincipala.this, 
                    "ID-ul introdus trebuie să fie " + maxId + ". Folosiți acest ID pentru a adăuga studentul.", 
                    "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "ID-ul trebuie să fie un număr întreg.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!codpostal.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "Codul postal trebuie să conțină exact 6 cifre.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!telefon.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "Numărul de telefon trebuie să conțină exact 10 cifre.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (ConnectBD.studentExists(idStudent)) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "Un student cu acest ID există deja.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Inserarea în baza de date
        ConnectBD.insertDataToDatabase(idStudent, nume, prenume, adresa, codpostal, telefon);
        ConnectBD.loadDataFromDatabase(modeltabel);
        
            //resetare campuri text 
                IdStudent.setText("");
                Nume.setText("");
                Prenume.setText("");
                Adresa.setText("");
                Telefon.setText("");
                Codpostal.setText("");
    }
});

       
 reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IdStudent.setText("");
                Nume.setText("");
                Prenume.setText("");
                Adresa.setText("");
                Telefon.setText("");
                Codpostal.setText("");
            }
        });
       
       delete.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String idText = IdStudent.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "ID-ul studentului trebuie completat.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idStudent;
        try {
            idStudent = Integer.parseInt(idText);
            if (idStudent <= 0) {
                JOptionPane.showMessageDialog(FereastraPrincipala.this, "ID-ul studentului trebuie să fie mai mare decât 0.", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "ID-ul trebuie să fie un număr întreg.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ConnectBD.studentExists(idStudent)) {
                    JOptionPane.showMessageDialog(FereastraPrincipala.this, "Nu s-au găsit date pentru ID-ul specificat.", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }

        ConnectBD.deleteDataFromDatabase(idStudent);
        ConnectBD.loadDataFromDatabase(modeltabel);
    }
});
    
   update.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String idText = IdStudent.getText().trim();
        String nume = Nume.getText().trim();
        String prenume = Prenume.getText().trim();
        String adresa = Adresa.getText().trim();
        String telefon = Telefon.getText().trim();
        String codpostal = Codpostal.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this,"ID-ul studentului trebuie completat.", "Eroare",JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idStudent;
        try {
            idStudent = Integer.parseInt(idText);
            if (idStudent <= 0) {
                JOptionPane.showMessageDialog(FereastraPrincipala.this, "ID-ul studentului trebuie să fie mai mare decât 0.","Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this,"ID-ul trebuie să fie un număr întreg.","Eroare",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
          if (!codpostal.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "Codul postal trebuie să conțină exact 6 cifre.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!telefon.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(FereastraPrincipala.this, "Numărul de telefon trebuie să conțină exact 10 cifre.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Actualizarea în baza de date
        ConnectBD.updateDataFromDatabase(idStudent, 
                                         nume.isEmpty() ? null : nume, 
                                         prenume.isEmpty() ? null : prenume,
                                         adresa.isEmpty() ? null : adresa, 
                                         codpostal.isEmpty() ? null : codpostal, 
                                         telefon.isEmpty() ? null : telefon);

        // Reîncărcarea tabelului după actualizare
        ConnectBD.loadDataFromDatabase(modeltabel);
    }
});
    print.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e){
        ConnectBD.printTable(tabel);
    }
    });
}
}

