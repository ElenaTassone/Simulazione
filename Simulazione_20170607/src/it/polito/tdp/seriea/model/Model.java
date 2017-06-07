package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private SerieADAO dao ;
	private List<Season> stagioni;
	private List<Match> partite;
	private DefaultDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo ;
	
	
	
	public Model() {
		super();
		this.dao = new SerieADAO ();
		
	}



	public List<Season> setStagioni() {
		if(this.stagioni==null){
			stagioni = dao.listSeasons() ;
			dao.listTeams();
		}
		return stagioni;
	}



	public void caricaPartite(Season s) {
			grafo =  new DefaultDirectedWeightedGraph <Team, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			List<Team> vertici = this.trovaSquadre(s.getSeason());
			Graphs.addAllVertices(grafo, vertici) ;
			this.azzeraPunteggi(grafo.vertexSet());
			this.setArchi(grafo);
			for(DefaultWeightedEdge d : grafo.edgeSet()){
				if(grafo.getEdgeWeight(d)==1){
					//vince quella in casa
					grafo.getEdgeSource(d).addPunti(3);
				}
				if(grafo.getEdgeWeight(d)==0){
					//pareggia
					grafo.getEdgeSource(d).addPunti(1);
				}
				if(grafo.getEdgeWeight(d)==-1){
					//vince quella fuori casa
					grafo.getEdgeTarget(d).addPunti(3);
				}
				
			
		}
		 
	}
	
	private void azzeraPunteggi(Set<Team> vertexSet) {
		for(Team t : vertexSet){
			t.azzera();
		}
		
	}



	public List<Team> getClassifica(){
		List<Team>result =new ArrayList<Team> () ;
		for(Team t : grafo.vertexSet()){
			result.add(t) ;
		}
		
		Collections.sort(result);
			return result ;
	}



	private void setArchi(DefaultDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo) {
	
		for(Match m : partite){	
			if(m.getFtr().compareTo("H")==0){
			
				
				DefaultWeightedEdge d = grafo.addEdge(m.getHomeTeam(), m.getAwayTeam());
				grafo.setEdgeWeight(d, 1);
				System.out.println(grafo.edgeSet().size());
			}
			if(m.getFtr().compareTo("A")==0){
				
				DefaultWeightedEdge d = grafo.addEdge(m.getHomeTeam(), m.getAwayTeam()) ;
				grafo.setEdgeWeight(d, -1);
				System.out.println(grafo.edgeSet().size());
			}
			if(m.getFtr().compareTo("D")==0){
		
				DefaultWeightedEdge d = grafo.addEdge(m.getHomeTeam(), m.getAwayTeam()) ;
				grafo.setEdgeWeight(d, 0);
				System.out.println(grafo.edgeSet().size());
			}
		
		
		}
	}





	private List<Team> trovaSquadre(int s) {
		
			partite = dao.listMatches(s) ;
		List<Team> result = new ArrayList<Team> ();
		for(Match m:partite){
			if(!result.contains(m.getAwayTeam()))
				result.add(m.getAwayTeam());
			if(!result.contains(m.getHomeTeam()))
				result.add(m.getHomeTeam());
		}
		return result ;
	}



	public DirectedGraph<Team, DefaultWeightedEdge> getGrafo() {
		return grafo;
		
	}

}
