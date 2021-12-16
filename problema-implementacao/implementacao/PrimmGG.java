package app;


import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.Algorithm;

public class PrimmGG
{
	public static int V;
	public Graph grafo;
	public int[][] adjacencyMatrix;
	
	public static void testeMatriz(int[][] matriz) {
		for(int i=0;i<V;i++) {
			for(int j=0;j < V; j++) {
				System.out.print(" " + matriz[i][j] + " ");
			}
			System.out.println(" ");
		}
	}
	
	public void init(Graph grafo_original)
	{
		grafo = grafo_original;
		int n = grafo.getNodeCount();
		V = grafo.getNodeCount();
		adjacencyMatrix = new int[n][n];
		
		/*
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				adjacencyMatrix[i][j] = grafo.getNode(i).hasEdgeBetween(j) ? 1 : 0;
		*/
		
		// Gera matriz de adjacencia com pesos
		// Duas alocacoes pois os dois lados representam nos se so um da erro
		grafo.edges().forEach(a -> {
			adjacencyMatrix[a.getNode0().getIndex()][a.getNode1().getIndex()] = (int) a.getAttribute("W");
			adjacencyMatrix[a.getNode1().getIndex()][a.getNode0().getIndex()] = (int) a.getAttribute("W");
		});
		
		System.out.println("numero de nos no grafo: " + V + " matriz resultante: ");
		
		testeMatriz(adjacencyMatrix);
		
	}
	
	public void compute() {
		maximumSpanningTree(adjacencyMatrix, grafo);
	}
	
	
	//encontrar o vertice de maior peso do conjunto nao visitado
	static int findMaxVertex(boolean visited[],
							int weights[])
	{
	
		// indice do no de maior peso da busca
		int index = -1;
	
		// valor comparativo de peso e guardara o maior peso dos nos
		int maxW = Integer.MIN_VALUE;
	
		// anda pelos nos do grafo no array de pesos e visitados
		for (int k = 0; k < V; k++)
		{
			
	
		    
		    // Se nao visitado e peso maior que o definido
			//System.out.println("visited = " + visited[k] + " weights = " + weights[k]);
			if (visited[k] == false && weights[k] > maxW)
			{
		
				// Update maxW
				maxW = weights[k];
		
				// Update index
				index = k;
			}
		}
		return index;
	}
	
	//Finaliza o algoritmo imprime MST
	static void printMaximumSpanningTree(int graph[][],int parent[], Graph grafo)
	{
	
		// O peso maximo da arvore
		int MST = 0;
		
	
		// Anda pelos nos da MST
		for (int i = 1; i < V; i++)
		{
	
		// Atualiza peso maximo da arvore
		MST += graph[i][parent[i]];
		}
	
		System.out.println("Peso total da Spanning-tree máxima:  "
						+ MST);
		System.out.println();
		System.out.println("Aresta \t Peso");
	
		// Imprime as arestas e pesos da
		// maximum spanning tree
		for (int i = 1; i < V; i++)
		{
		// soma 1 pois no grafo original vai de 1 a N
		System.out.println((parent[i] + 1) + " - " + (i+1) + " \t"
							+ graph[i][parent[i]]);
		
		//Seta os atributos indicando nos no grafo
		grafo.getNode(parent[i]).setAttribute("naMST", true);
		grafo.getNode(i).setAttribute("naMST", true);
		grafo.getNode(parent[i]).getEdgeToward(i).setAttribute("naMST", true);
		//Atributos para representacao em tela
		grafo.getNode(parent[i]).setAttribute("ui.class", "marcado");
		grafo.getNode(parent[i]).setAttribute("ui.class", "marcado");
		}
		
	}
	
	//maximum spanning tree
	static void maximumSpanningTree(int[][] graph, Graph grafo)
	{
	
		// visited[i]:Se vertice i
		// visitado ou nao
		boolean[] visited = new boolean[V];
	
		// weights[i]: Vetor de pesos das arestas
		int[] weights = new int[V];
	
		// parent[i]: Vetor de nos pais para montar a mst
		int[] parent = new int[V];
	
		// Inicia pesos como -INFINITE,
		// visitados como false
		for (int i = 0; i < V; i++) {
		visited[i] = false;
		weights[i] = Integer.MIN_VALUE;
		}
	
		// O primeiro vertice incluido na arvore com peso maximo
		weights[0] = Integer.MAX_VALUE;
		parent[0] = -1;
	
		// Analisar outros vertices
		for (int i = 0; i < V - 1; i++) {
	
		// Guarda indice do vertice de maior peso dos nao visitados
		int maxVertexIndex
			= findMaxVertex(visited, weights);
	
		// Agora foi visitado entao marcar
		visited[maxVertexIndex] = true;
	
		// Atualizar adjacentes do visitado
		// Se matriz adjacente nao correta gera erro
		for (int j = 0; j < V; j++) {
	
			// Analisa matriz se vertice adjacente e nao visitado
			if (graph[j][maxVertexIndex] != 0
				&& visited[j] == false) {
				
	
				// Se aresta[v][x] tem peso maior
				// que o peso weights[v]
				if (graph[j][maxVertexIndex]
					> weights[j]) {
					
		
					// Atualizar vetor de pesos weights[j]
					weights[j]
					= graph[j][maxVertexIndex];
		
					// Atualizar vetor de pais parent[j]
					parent[j] = maxVertexIndex;
				}
			}
		}
		}
	
		// Imprime a arvore gerada
		printMaximumSpanningTree(graph, parent, grafo);
	}
	
}