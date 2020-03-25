package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	Map<Fermata, Fermata> backVisit;
	
	
	public void creaGrafo() {
		
		//Crea l'oggetto grafo
		this.grafo = new SimpleDirectedGraph<> (DefaultEdge.class);
	
		//Aggiungi i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
	  	
		//crea idMap
		this.fermateIdMap = new HashMap<>();
		for(Fermata f : this.fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		
		Graphs.addAllVertices(this.grafo, this.fermate);//per Graphs, v. Tempo 1:09:30 della lez. 23

		
		//Aggiungi gli archi (opzione 1)
	/*	for(Fermata partenza : this.grafo.vertexSet()) {
			for(Fermata arrivo : this.grafo.vertexSet()) {
				
				if(dao.esisteConnessione(partenza, arrivo)) {
					this.grafo.addEdge(partenza,  arrivo);
				}
				
			}
		}
	*/	
		//Aggiungi gli archi (opzione 2)
		for(Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza,fermateIdMap);
			
			for(Fermata arrivo : arrivi)
				this.grafo.addEdge(partenza, arrivo);
		}
		
		// Aggiungi gli archi (opzione 3)
		
	}
	
	public List<Fermata>  fermateRaggiungibili(Fermata source){ // lista di stazioni a partire da una certa fermata
		
		List<Fermata> result = new ArrayList<Fermata>();
	    backVisit = new HashMap<>(); //back è l'arco all'indietro
		
 		GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<> (this.grafo);
		
		//Elementi (fermate) in ordine diverso ma nello stesso numero
 //		GraphIterator<Fermata, DefaultEdge> it = new DepthFirstIterator<> (this.grafo);
		
 		it.addTraversalListener(new EdgeTraversedGraphListener(grafo, backVisit));
		backVisit.put(source, null);
 		
 		while(it.hasNext()) {
			result.add(it.next());
		}
		
 //	    System.out.println(backVisit);
 		
		return result;
		
	}
	
	public List<Fermata> percorsoFinoA(Fermata target){
		if(!backVisit.containsKey(target)) {
			// il target non è raggiungibile dalla source
			return null;
		}
		
		List<Fermata> percorso = new LinkedList<>();
		
		Fermata f = target;
		
		while(f != null) {
			percorso.add(f);
			f = backVisit.get(f);
		}
		
		return percorso;
	}
	
	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}
}
