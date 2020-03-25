package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraversedGraphListener implements TraversalListener<Fermata, DefaultEdge> {

	Graph<Fermata,DefaultEdge> grafo;
	Map<Fermata, Fermata> back;
	
		
	public EdgeTraversedGraphListener(Graph<Fermata,DefaultEdge> grafo,Map<Fermata, Fermata> back) {
		super();
		this.grafo = grafo;
		this.back = back;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
	}
	
	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {
		/*
		 * back codifica relazioni del tipo child -> parent (dal nodo figlio al nodo padre)
		 * 
		 *  per un nuovo vertice "child" scoperto
		 *  devo avere che:
		 *  - child è ancora sconosciuto (non ancora trovato)
		 *  - parent è già stato visitato
		 */
		
		
		Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge());
		Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge());
		
		/* se il grafo è orientato, allora source == parent, target==child*/ 
		/* se il grafo non è orientato, potrebbe esseere al contrario... */
		
		if(!back.containsKey(targetVertex) && back.containsKey(sourceVertex)) {
			back.put(targetVertex,  sourceVertex);
		}else if(!back.containsKey(sourceVertex) && back.containsKey(targetVertex)) {
			back.put(sourceVertex,  targetVertex);
		}	
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
	}

	
}
