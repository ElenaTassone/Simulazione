package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private SerieADAO dao ;
	private List<Season> stagioni;
	private List<Match> partite;
	private DefaultDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo ;
	LinkedHashSet<DefaultWeightedEdge> parziale ;
	LinkedHashSet<DefaultWeightedEdge> best; 
	
	
	
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
			 
			best = new  LinkedHashSet<DefaultWeightedEdge> () ; 
			parziale = new LinkedHashSet<DefaultWeightedEdge> () ;
			 
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



	public String getClassifica(){
		List<Team>result =new ArrayList<Team> () ;
		for(Team t : grafo.vertexSet()){
			result.add(t) ;
		}
		String stampa = "";
		Collections.sort(result);
		for(Team t : result)
			stampa+=t.getTeam()+":"+t.getPunti()+"\n";
		
			return stampa ;
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



	public String trovaCammino() {
//		LinkedHashMap<Team, Team> parziale = null ;
//		LinkedHashMap<Team, Team> best = new LinkedHashMap<Team, Team> () ; 
//		
//		LinkedHashMap<Team, Team> finale = this.recursive(parziale, best, null) ;					
		LinkedHashSet<DefaultWeightedEdge>  finale1 = new LinkedHashSet<DefaultWeightedEdge> () ;
		LinkedHashSet<DefaultWeightedEdge>  finale2 = new LinkedHashSet<DefaultWeightedEdge> () ;
		for(DefaultWeightedEdge d : grafo.edgeSet()){
			finale1 = this.recursive (d,parziale, best, null);
			if(finale1.size()>finale2.size())
				finale2.clear();
			finale2.addAll(finale1);
		}
			
//		LinkedHashSet<DefaultWeightedEdge>  finale = this.recursive(parziale, best, null) ;					
//			System.out.println(finale.size());
		String result = "" ;
		System.out.println(finale2.size());
		for(DefaultWeightedEdge d :finale2)
			result+=grafo.getEdgeSource(d).toString()+"-"+grafo.getEdgeTarget(d).toString()+"\n" ;
		return result ;
	}
	
	public LinkedHashSet<DefaultWeightedEdge> recursive (DefaultWeightedEdge primo ,LinkedHashSet<DefaultWeightedEdge> parziale,LinkedHashSet<DefaultWeightedEdge> best, Team ultimo){
		
		// condizione di terminazione
		boolean contiene = true;
		if(this.cercaPerdente(primo, parziale, ultimo)==null){
//			 se non ho trovato un perdente esco
			contiene = false ;
		}
		
		if(contiene==false){
			if(parziale.size()>best.size()){
				best.clear();
				best.addAll(parziale) ;
			}
		return best;
		}
		
//		if(this.cercaPerdente(parziale, ultimo)==null){
//			// se non ho trovato un perdente esco
//			contiene = false ;
//		}
		
	
		
//		for(DefaultWeightedEdge d : grafo.edgeSet()){
			//ultimo =  grafo.getEdgeSource(d) ;
			DefaultWeightedEdge perdente = this.cercaPerdente(primo, parziale, ultimo );
			if(perdente==null){
				contiene = false;
				//return best;
			}
			else{
				parziale.add(perdente) ;
				ultimo=grafo.getEdgeTarget(perdente) ;
//				System.out.println(perdente);
				this.recursive(primo, parziale, best,ultimo);
			//parziale.remove(d);
				parziale.remove(perdente);
		
		//	}
		}
		return best;
	}

	public DefaultWeightedEdge cercaPerdente (DefaultWeightedEdge primo , LinkedHashSet<DefaultWeightedEdge> parziale, Team ultimo){
		for(DefaultWeightedEdge d : this.grafo.edgeSet()){
			//PRIMA SQUADRA DA NSERIRE 
			if(parziale==null || ultimo==null){
				
				return primo;
				}
			if(grafo.getEdgeWeight(d)==1){
				if(grafo.getEdgeSource(d).compare(ultimo)==0){
				
				//se il parziale non contiene quest'arco
					if(!parziale.contains(d)){
					// glielo ritorno 
						return d ;
				//se la squadra in casa è uguale all'ultima mia squadara fuori casa
//				if(grafo.getEdgeSource(d).compareTo(vincente)==0){
//					//controllo che non abbia già inserito questa partita
//					if(grafo.getEdgeTarget(d).compareTo(parziale.get(vincente))!=0){
						//se non la contiene posso aggiungerla 
						//perdente = grafo.getEdgeTarget(d) ;
						//return perdente ;
					}
				}
			}
		}
		return null ;
	}
		
	
}
