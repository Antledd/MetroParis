package it.polito.tdp.metroparis.model;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	
	public void creaGrafo() {
		
		//Crea l'oggetto grafo
		this.grafo = new SimpleDirectedGraph<> (DefaultEdge.class);
	
		//Aggiungi i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
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
			List<Fermata> arrivi = dao.stazioniArrivo(partenza);
			
			for(Fermata arrivo : arrivi)
				this.grafo.addEdge(partenza, arrivo);
		}
		
		// Aggiungi gli archi (opzione 3)
		
	}

	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}
}
