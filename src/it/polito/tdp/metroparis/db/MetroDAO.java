package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}
	
	public boolean esisteConnessione(Fermata partenza, Fermata arrivo) {
		
		String sql = "SELECT COUNT(*) AS cnt" +
				" FROM connessione" +
				" WHERE id_stazP=?\n " +
				" AND id_stazA=?";
		
		Connection conn = DBConnect.getConnection();
		PreparedStatement st;
		try{
			st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			st.setInt(2, arrivo.getIdFermata());
			
			ResultSet rs = st.executeQuery();
			
			rs.next(); // mi posizione sulla prima (e unica) riga
			
			int numero = rs.getInt("cnt");
			
			conn.close(); // restituisco la connessione al pooling (vedi lez. precedente)
			
			return (numero > 0);
			
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	public List<Fermata> stazioniArrivo(Fermata partenza) {
		String sql = "SELECT id_stazA " +
				"FROM connessione " +
				"WHERE id_stazP=?" ; 
		
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs = st.executeQuery();
			
			List<Fermata> result = new ArrayList<>();
			
			while(rs.next()) {
				result.add(new Fermata(rs.getInt("id_stazA"), null, null));
			}
			
			conn.close();//ricordarsi sempre di chiudere connessione
			return result;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
		
	}


}
