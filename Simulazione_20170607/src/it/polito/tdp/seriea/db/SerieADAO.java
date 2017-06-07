package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;


public class SerieADAO {
	
	private Map<Integer, Season> seasonMap ;
	private Map<String, Team> teamMap ;
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		seasonMap = new TreeMap<Integer,Season>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				
				Season s =  new Season(res.getInt("season"), res.getString("description")) ;
				result.add(s) ;
				seasonMap.put(res.getInt("season"), s) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		teamMap = new TreeMap<String, Team>  ();
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				Team t = new Team(res.getString("team"));
				result.add(t) ;
				teamMap.put( res.getString("team"), t) ;
				
				
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

 // data una stagione trovare i match 
	
	public List<Match> listMatches(int id_s) {
		String sql = "SELECT * FROM matches WHERE Season=?" ;
		
		List<Match> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id_s);

			ResultSet rs = st.executeQuery();

			
			while(rs.next()) {
				LocalDate input = rs.getDate("Date").toLocalDate() ;
				
				
				result.add( new Match(rs.getInt("match_id"), seasonMap.get(rs.getInt("Season")), 
						rs.getString("Div"), input, teamMap.get(rs.getString("HomeTeam")), 
						teamMap.get(rs.getString("AwayTeam")),rs.getInt("FTHG"), 
						rs.getInt("FTAG"), rs.getString("FTR")) ) ;
				
			}
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

}
