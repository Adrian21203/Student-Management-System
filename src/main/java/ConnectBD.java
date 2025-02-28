import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ConnectBD {
    private static PrinterJob printJob = PrinterJob.getPrinterJob();
    private static String URL = "jdbc:ucanaccess://C:\\Users\\win\\OneDrive\\Documente\\NetBeansProjects\\Practica\\Studenti\\Studenti.accdb";
    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    public static void loadDataFromDatabase(DefaultTableModel modeltabel) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Studenti ORDER BY IdStudent");

            modeltabel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("IdStudent"),
                    rs.getString("Nume"),
                    rs.getString("Prenume"),
                    rs.getString("Adresa"),
                    rs.getString("Codpostal"),
                    rs.getString("Telefon")
                };
                modeltabel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la conectarea cu baza de date", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Închide resursele
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void insertDataToDatabase(int idStudent, String nume, String prenume, String adresa, String codpostal, String telefon) {
        String query = "INSERT INTO Studenti (IdStudent, Nume, Prenume, Adresa, Codpostal, Telefon) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idStudent);
            pstmt.setString(2, nume);
            pstmt.setString(3, prenume);
            pstmt.setString(4, adresa);
            pstmt.setString(5, codpostal);
            pstmt.setString(6, telefon);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Datele au fost inserate cu succes.", "Succes", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la inserarea datelor în baza de date: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   public static int getNextIdStudent() {
    int nextId = 1; // În cazul în care tabela este goală
    String query = "SELECT IdStudent FROM Studenti ORDER BY IdStudent";
    
    try (Connection conn = connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        // Caută ID-ul disponibil cel mai mic
        while (rs.next()) {
            if (rs.getInt("IdStudent") != nextId) {
                return nextId; // Returnează ID-ul găsit care nu este folosit
            }
            nextId++;
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Eroare la obținerea ID-ului următor: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
    }
    
    return nextId; // Returnează ID-ul maxim + 1
}

       public static void deleteDataFromDatabase(int idStudent) {
        String deleteQuery = "DELETE FROM Studenti WHERE IdStudent = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, idStudent);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Studentul a fost șters cu succes.", "Succes", JOptionPane.INFORMATION_MESSAGE);
                reorderStudentIDs(); // Reordonează ID-urile după ștergere
            } else {
                JOptionPane.showMessageDialog(null, "Nu s-au găsit date pentru ID-ul specificat.", "Eroare", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la ștergerea datelor în baza de date: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

// Metodă pentru reordonarea ID-urilor
private static void reorderStudentIDs() {
    String selectQuery = "SELECT IdStudent FROM Studenti ORDER BY IdStudent";
    String updateQuery = "UPDATE Studenti SET IdStudent = ? WHERE IdStudent = ?";

    try (Connection conn = connect();
         PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
         PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
         ResultSet resultSet = selectStmt.executeQuery()) {

        int newId = 1;

        while (resultSet.next()) {
            int currentId = resultSet.getInt("IdStudent");

            if (currentId != newId) {
                // Reasignați ID-ul dacă este necesar
                updateStmt.setInt(1, newId);
                updateStmt.setInt(2, currentId);
                updateStmt.executeUpdate();
            }

            newId++;
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Eroare la reordonarea ID-urilor: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
    }
}


    public static boolean studentExists(int idStudent) {
        String query = "SELECT COUNT(*) FROM Studenti WHERE IdStudent = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idStudent);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la verificarea existenței studentului.", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
public static void updateDataFromDatabase(int idStudent, String nume, String prenume, String adresa, String codpostal, String telefon) {
    StringBuilder query = new StringBuilder("UPDATE Studenti SET ");
    List<Object> parameters = new ArrayList<>();
    boolean hasUpdate = false;

    if (nume != null && !nume.isEmpty()) {
        query.append("Nume = ?, ");
        parameters.add(nume);
        hasUpdate = true;
    }
    if (prenume != null && !prenume.isEmpty()) {
        query.append("Prenume = ?, ");
        parameters.add(prenume);
        hasUpdate = true;
    }
    if (adresa != null && !adresa.isEmpty()) {
        query.append("Adresa = ?, ");
        parameters.add(adresa);
        hasUpdate = true;
    }
    if (codpostal != null && !codpostal.isEmpty()) {
        query.append("Codpostal = ?, ");
        parameters.add(codpostal);
        hasUpdate = true;
    }
    if (telefon != null && !telefon.isEmpty()) {
        query.append("Telefon = ?, ");
        parameters.add(telefon);
        hasUpdate = true;
    }

    if (hasUpdate) {
        // Elimina ultima virgulă și spațiu
        query.setLength(query.length() - 2);
        query.append(" WHERE IdStudent = ?");
        parameters.add(idStudent);

        try (Connection conn = connect(); // Asigură-te că metoda connect() este corect implementată
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Datele au fost actualizate cu succes.", "Succes", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la actualizarea datelor în baza de date: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Niciun câmp nu a fost selectat pentru actualizare.", "Eroare", JOptionPane.ERROR_MESSAGE);
    }
}

public static void printTable(JTable table) { 
        try {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            
            printJob.setPrintable(new Printable() {
                @Override
                public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }
                    
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    
                    // Adjust scale if necessary
                    double scale = 0.5; // scale down to fit on a page, adjust as needed
                    g2d.scale(scale, scale);
                    
                    table.printAll(g2d); // print the table
                    
                    return PAGE_EXISTS;
                }
            });
            
            boolean printAccepted = printJob.printDialog();
            if (printAccepted) {
                printJob.print();
            }
        } catch (PrinterException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la printarea tabelului: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }
}