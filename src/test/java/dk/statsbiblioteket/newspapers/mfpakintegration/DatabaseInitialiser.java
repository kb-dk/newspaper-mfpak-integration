package dk.statsbiblioteket.newspapers.mfpakintegration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is a command line application to create the mfpak database
 */
public class DatabaseInitialiser {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://achernar/mfpak-devel";
        String user = "mfpak";
        String password = "foobar";

        Connection con = DriverManager.getConnection(url, user, password);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT VERSION()");

        if (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }
}
