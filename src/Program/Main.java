package Program;

import java.util.Scanner;

import Entities.Flutuantes;
import Entities.Inteiros;

public class Main {
	
	static Scanner sc = new Scanner(System.in);
	
	//a main apenas recebe as entradas e chama a função que o usuario escolher
	public static void main(String[] args) {
		char controle = 'o';		
		
		 do {
			System.out.println("Digite:\ni - para realizar operações com inteiros;\nf - para operações com pontos flutuantes;\ns - para sair do programa;\n");
			controle = sc.next().charAt(0);
			System.out.println();
			
			if(controle == 'i') {
				System.out.print("Com quantos bits você deseja realizar a operação (incluindo o bit de sinal)?: ");				
				int tamanho = sc.nextInt();				
				sc.nextLine();
				System.out.println();
				System.out.print("Digite o primeiro número no formato +XXXXX (ou -XXXXX): ");
				char [] a = input(tamanho);
				System.out.print("Digite o segundo número no formato +XXXXX (ou -XXXXX): ");
				char [] b = input(tamanho);				
				char[] resultado = null;				
				
				System.out.println("Qual operação você deseja realizar? Digite:");
				System.out.println("0 - para soma;\n1 - para subtração;\n2 - para multiplicação;\n3 - para divisão;");
				System.out.println();
				int opcao = sc.nextInt();				
				
				switch (opcao) {
					case 0:
						resultado = Inteiros.somar(a, b, tamanho);
						break;
					case 1:
						resultado = Inteiros.subtrair(a, b, tamanho);
						break;
					case 2:
						resultado = Inteiros.multiplicar(a, b, tamanho);
						break;
					case 3:
						resultado = Inteiros.dividir(a, b, tamanho);
						break;
					default:
						System.out.println("Valor inválido");
						break;
				}				
								
				System.out.println();				
				exibir(resultado);
				System.out.println();
			}
			else if(controle == 'f') {
				sc.nextLine();
				System.out.println("Para representar o 0 basta escrever +1,000...0 na matissa e +0000000 no expoente.");
				System.out.println();
				System.out.print("Digite a primeira mantissa no formato +1,XXXXX...XX (ou -1,XXXXX...XX): ");
				char[] mantissaA = input(26);
				System.out.print("Digite um expoente entre -1111111 e +1111111: ");
				char[] expoenteA = input(8);
				System.out.print("Digite a segunda mantissa no formato +1,XXXXX...XX (ou -1,XXXXX...XX): ");
				char[] mantissaB = input(26);
				System.out.print("Digite um expoente entre -1111111 e +1111111: ");
				char[] expoenteB = input(8);
				char[] resultado = null;				
				
				System.out.println("Qual operação você deseja realizar? Digite:");
				System.out.println("0 - para soma;\n1 - para subtração;\n2 - para multiplicação;\n3 - para divisão;");
				System.out.println();
				int opcao = sc.nextInt();
				
				switch (opcao) {
				case 0:
					resultado = Flutuantes.somar(mantissaA, expoenteA, mantissaB, expoenteB);
					break;
				case 1:
					resultado = Flutuantes.subtrair(mantissaA, expoenteA, mantissaB, expoenteB);
					break;
				case 2:
					resultado = Flutuantes.multiplicar(mantissaA, expoenteA, mantissaB, expoenteB);
					break;
				case 3:
					resultado = Flutuantes.dividir(mantissaA, expoenteA, mantissaB, expoenteB);
					break;
				default:
					System.out.println("Valor inválido");
					break;
			}				
							
			System.out.println();				
			exibir(resultado);
			System.out.println();
			}
			
		}while(controle != 's');
		
		
		/*char[] mantissaA = new char[] {'-','1',',','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
		char[] mantissaB = new char[] {'+','1',',','1','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
		char[] expoenteA = new char[] {'+','0','0','0','0','0','1','1'};
		char[] expoenteB = new char[] {'+','0','0','0','0','0','0','1'};
		
		char[] resultado = Flutuantes.subtrair(mantissaA, expoenteA, mantissaB, expoenteB);
		//char[] resultado = Inteiros.subtrair(expoenteA, expoenteB, 8);
		exibir(resultado);*/

	}
	
	//essa função recolhe os dados inseridos pelo usuário
	public static char[] input(int tamanho) {		
		char [] bitsA = new char[tamanho];		
		String aux = sc.nextLine();
		for(int i = 0; i < tamanho; i++) {
			bitsA[i] = aux.charAt(i);
		}
		System.out.println();
		return bitsA;		
	}	
	
	public static void exibir(char[] resultado) {
		if(resultado != null) {
			System.out.print("Resultado: ");
			for(int i = 0; i < resultado.length; i++) {
				System.out.print(resultado[i]);
			}
			System.out.println();
		}		
	}
}