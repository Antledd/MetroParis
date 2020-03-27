package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private class EdgeTraversedGraphListener implements TraversalListener<Fermata, DefaultWeightedEdge>{

		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {
						
			Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge());
			Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge());
					
			if(!backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
				backVisit.put(targetVertex,  sourceVertex);
			}else if(!backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
				backVisit.put(sourceVertex,  targetVertex);
			}	
			
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
		}
		
	}
	
	private Graph<Fermata, DefaultWeightedEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	Map<Fermata, Fermata> backVisit;
	
	
	public void creaGrafo() {
		
		//Crea l'oggetto grafo semplice e orientato
//		this.grafo = new SimpleDirectedGraph<> (DefaultEdge.class);
	
		//Crea l'oggetto grafo semplice, orientato e pesato
		this.grafo = new SimpleDirectedWeightedGraph<> (DefaultWeightedEdge.class);
		
		//Aggiungi i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
	  	
		//crea idMap
		this.fermateIdMap = new HashMap<>();
		for(Fermata f : this.fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		
		Graphs.addAllVertices(this.grafo, this.fermate);//per Graphs, v. Tempo 1:09:30 della lez. 23

		//Aggiungi gli archi 
		for(Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza,fermateIdMap);
			
			for(Fermata arrivo : arrivi)
				this.grafo.addEdge(partenza, arrivo);
		}
		
		// Aggiungi i pesi agli archi
		
		List<ConnessioneVelocita> archipesati = dao.getConnessionieVelocita();
		for(ConnessioneVelocita cp : archipesati) {
			Fermata partenza = fermateIdMap.get(cp.getStazP());
			Fermata	arrivo = fermateIdMap.get(cp.getStazA());	
			double distanza = LatLngTool.distance(partenza.getCoords(), arrivo.getCoords(),LengthUnit.KILOMETER);
			double peso = distanza / cp.getVelocita() * 3600; /* tempo in secondi */
			
			grafo.setEdgeWeight(partenza, arrivo, peso); // peso = spazio/velocità (il tempo di percorrenza, non solo la velocità)
		
			// OPPURE (aggiungo archi e vertici insieme): Graphs.addEdgeWithVertices(grafo, partenza, arrivo, peso);
		}
	}
	
	public List<Fermata>  fermateRaggiungibili(Fermata source){ // lista di stazioni a partire da una certa fermata
	
		
		List<Fermata> result = new ArrayList<Fermata>();
	    backVisit = new HashMap<>(); //back è l'arco all'indietro
		
	    //Elementi (fermate) considerati in ampiezza
       GraphIterator<Fermata, DefaultWeightedEdge> it = new BreadthFirstIterator<> (this.grafo, source);
		
		//Elementi (fermate) in ordine diverso (in profondità) ma nello stesso numero
 	//	GraphIterator<Fermata, DefaultEdge> it = new DepthFirstIterator<> (this.grafo, source);
		
 		it.addTraversalListener(new Model.EdgeTraversedGraphListener());
		
 		//Java permette di creare new, passandogli il nome dell'interfaccia
 		//certo non si può creare un oggetto partendo daun'interfaccia, ma...
 		//notare, la parentesi graffa finale della classe anonima (che lasciamo qua per
 		//"promemoria" commentata) è seguita da parentesi toda e ; Ha troppi metodi, questa
 		//classe "usa e getta" e l'utilità si riscontrerebbe più per altra interfaccia come per es.
 		//Comparable che ha pochi metodi e quindi meno invasiva...
 	/*	
 		it.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}
 			
 		});
 	*/	
 		
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
		//	percorso.add(f); //fa vedere prima il target (stazione finale) 
							 //e infine il source (stazione iniziale) 
			
			percorso.add(0,f); //0 come primo parametro fa vedere lo stesso percorso 
							   //ma in ordine corretto, cioè partendo da source 
			                   //per arrivare al target	(così va meglio) 
			f = backVisit.get(f);
		}
		
		return percorso;
	}
	
	public Graph<Fermata, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}
	
	public List<Fermata> trovaCamminoMinimo(Fermata partenza, Fermata arrivo){
		DijkstraShortestPath<Fermata,DefaultWeightedEdge> dijstra = new DijkstraShortestPath(this.grafo);//vedi Tempo 24:25 della lezione
		GraphPath<Fermata, DefaultWeightedEdge> path = dijstra.getPath(partenza,  arrivo);
		return path.getVertexList();
	}
	
}
