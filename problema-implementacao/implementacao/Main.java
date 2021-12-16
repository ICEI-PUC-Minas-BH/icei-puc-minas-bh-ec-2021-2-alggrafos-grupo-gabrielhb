package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner; // Import the Scanner class to read text files

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.Prim;


public class Main {
	
	public static void geraEntrada() {
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("entrada.txt"));
			fw.write("4 5 3");
			fw.newLine();
			fw.write("1 2 30");
			fw.newLine();
			fw.write("1 1 40");
			fw.newLine();
			fw.write("1 3");
			fw.newLine();
			fw.write("1 2");
			
			fw.close();
		}
		catch(IOException ioerro) {
			ioerro.printStackTrace();
		}
		
	}
	
	public static void testaGrafo(Graph grafo) {
		for(Node n:grafo) {
			System.out.println(n.getId());
		}
	}
	
	public static int explorar(Node source, String t) {
		int pesoMaximo = Integer.MAX_VALUE; //maior peso transportavel representa a menor aresta
		//Liga iterator para busca em profundidade na MST
		Iterator<? extends Node> it =  source.getDepthFirstIterator();
		if(it.hasNext()) {
			Node anterior = it.next();
			do {
				Node proximo = it.next();
				
				//System.out.println("ATUAL: "+anterior.getId() + " NEXT:" + proximo.getId());
				//System.out.println("ESPERADO: " + t);
				
				//Se o id for o no final retornar total
				if(proximo.getId().equals(t) && anterior.hasEdgeBetween(proximo.getId())) {
					if(pesoMaximo < (int)anterior.getEdgeToward(proximo.getId()).getAttribute("W"))
						return pesoMaximo;
					else {
						pesoMaximo = (int)anterior.getEdgeToward(proximo.getId()).getAttribute("W");
						return pesoMaximo;
					}
				}
				else {
					if((int)anterior.getEdgeToward(proximo.getId()).getAttribute("W") < pesoMaximo)
						pesoMaximo = (int)anterior.getEdgeToward(proximo.getId()).getAttribute("W");
					anterior = proximo;
				}
			}while(it.hasNext());
			
			//Se nao encontrar o t sair com -1
			return -1; 
		}
		else {
			return -1;
		}
		
	}

	public static void main(String[] args) {
		//Avisa pra API qual modulo grafico usar
		System.setProperty("org.graphstream.ui", "swing");
		
		//Folha de estilo para o grafo
	    String styleSheet =
	            "node {" +
	            "	fill-color: black;" +
	            "}" +
	            "node.marcado {" +
	            "	fill-color: blue;" +
	            "}";
		
		//Variaveis
		int N = 0, M = 0, S = 0; //ilhas, pontes, consultas
		int A, B, W; //indices e weight W
		Graph grafo = new SingleGraph("Grafo");
		String leitura;
		String[] consultas;
		
		//geraEntrada();
		
		BufferedReader rw;
		try {
			//rw = new BufferedReader(new FileReader("entrada.txt"));
			rw = new BufferedReader(new FileReader("entrada2.txt"));
			leitura = rw.readLine();
			if(leitura != null) {
				N = Integer.parseInt(leitura.split(" ")[0]);
				M = Integer.parseInt(leitura.split(" ")[1]);
				S = Integer.parseInt(leitura.split(" ")[2]);
				consultas = new String[S];
				
				System.out.println("N ilhas = " + N + " M  pontes = " + M);
				
				for(int i = 0; i < N; i++) {
					//adiciona ao grafo os N vértices rotulados
					grafo.addNode(""+ (1+i));
				}
				
				//testaGrafo(grafo);
				
				for(int i = 0; i < M; i++) {
					//le as M linhas
					leitura = rw.readLine();
					if(leitura != null) {
						//adiciona arestas que correspondem as pontes
						A = Integer.parseInt(leitura.split(" ")[0]);
						B = Integer.parseInt(leitura.split(" ")[1]);
						W = Integer.parseInt(leitura.split(" ")[2]);
						String tempName = " " + A + B;
						grafo.addEdge(tempName,""+A,""+B);
						//adiciona peso a aresta
						grafo.getEdge(tempName).setAttribute("W", W);
						//System.out.println(grafo.getEdge(tempName).getAttribute("W"));
					}
				}
				
				for(int i = 0; i < S; i++) {
					leitura = rw.readLine();
					if(leitura != null) {
						consultas[i] = leitura;
						System.out.println(consultas[i]);
					}
				}
				
				rw.close();
				
				//Algoritmo PRIMM alterado para gerar Spanning-tree Maxima
				PrimmGG algo_primm = new PrimmGG();
				algo_primm.init(grafo);
				algo_primm.compute();
				
				//Gerar novo grafo apenas com os da MST
				Graph grafoFim = new SingleGraph("grafoFim");
				
				grafo.nodes().forEach(n -> {
					if(n.hasAttribute("naMST")) {
						if((boolean)n.getAttribute("naMST") == true)
							grafoFim.addNode(n.getId());
					}
				});
				grafo.edges().forEach(a -> {
					if(a.hasAttribute("naMST")) {
						if((boolean)a.getAttribute("naMST") == true) {
							grafoFim.addEdge(a.getId(), a.getNode0().getId(), a.getNode1().getId());
							grafoFim.getEdge(a.getId()).setAttribute("W", (int)a.getAttribute("W"));
						}
					}
				});
				
				//Realizar consultas do texto
				String tempA;
				String tempB;
				int avalia = -1;
				
				for(int i = 0; i < S; i++) {
					tempA = consultas[i].split(" ")[0];
					tempB = consultas[i].split(" ")[1];
					
					//Realiza busca em profundidade para encontrar o valor desejado
					avalia = explorar(grafoFim.getNode(tempA), tempB);
					if(avalia != -1) {
						System.out.println("CONSULTA N " + i);
						System.out.println("DO " + tempA + " ATÉ O " + tempB);
						System.out.println("O PESO MÁXIMO É: " + avalia);
						System.out.println("============================");
					}
					else {
						System.out.println("ALGUM ERRO OCORREU E VALOR NAO FOI ENCONTRADO");
					}
				}
				
				
				
				
				for(Node n:grafo) {
					//Avisa para mostrar o label
					n.setAttribute("ui.label", n.getId());
				}
				for(Node n:grafoFim) {
					//Avisa para mostrar o label
					n.setAttribute("ui.label", n.getId());
				}
				
				//Mostra o grafo original
				grafo.setAttribute("ui.stylesheet", styleSheet);
				grafo.display();
				//Mostra o grafo da MST
				grafoFim.display();
			}
			else {
				rw.close();
				System.out.println("Erro detectado");
			}
		}catch(IOException ioerro) {
			ioerro.printStackTrace();
		}
		
		
	}

}
