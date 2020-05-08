package Entities;

/*Todas as operações foram feitas com base nos circuitos ilustrados
nos slides do professor Valdinei da Silva */

public class Flutuantes extends Inteiros {
	
	public static char[] somar(char[] mantissaA, char[] expoenteA, char[] mantissaB, char[] expoenteB) {
		mantissaA = formatarMantissa(mantissaA);
		mantissaB = formatarMantissa(mantissaB);
		final char[] um8bits = preencher(8);	//usado para conversão dos expoentes
		expoenteA = converterParaC2(expoenteA, um8bits);
		expoenteB = converterParaC2(expoenteB, um8bits);
		char[] resultado = new char[32];
		char[] um26bits = preencher(26);		
		final char[] faixaDenormalizada = new char[] {'1','0','0','0','0','0','0','1'}; //menor expoente (-127), que necessita de tratamento
																						// especial na notação IEEE-754
		if(numerosIguais(faixaDenormalizada, expoenteA)) {
			mantissaA[2] = '0';	//remove o 1 implicito
			expoenteA = incrementarExpoente(expoenteA); //aumenta o expoente para (-126) para entrar de acordo com a notação
			if(tudoZero(mantissaA)) {
				mantissaB = converterParaIEE(mantissaB, um26bits);	// se for tudo zero retorna a outra parcela da adição
				return formatarResultado(mantissaB, expoenteB);
			}									
		}
		if(numerosIguais(faixaDenormalizada, expoenteB)) {
			mantissaB[2] = '0';
			expoenteB = incrementarExpoente(expoenteB);
			if(tudoZero(mantissaB)) {
				mantissaA = converterParaIEE(mantissaA, um26bits);
				return formatarResultado(mantissaA, expoenteA);
			}						
		}		
		char[] mantissaSoma = new char[26];
		char[] expoenteSoma =  new char[8];
		char menor = menorExpoente(expoenteA, expoenteB);	//essa função retorna qual é o menor expoente
		if(menor == 'A') {
			mantissaA = igualarExpoentes(menor, expoenteB, expoenteA, mantissaA); //aumenta o menor expoente e da shift right na mantissa
			if(mantissaA == null) {												//até os expoentes serem iguais ou a mantissa for igual a zero(null)
				resultado = converterParaIEE(mantissaB, um26bits);
				resultado = normalizarResultado(mantissaB, expoenteB);
				return resultado;
			}
			expoenteSoma = expoenteB;				
		}
		else if(menor == 'B') {
			mantissaB = igualarExpoentes(menor, expoenteA, expoenteB, mantissaB);
			if(mantissaB == null) {
				resultado = converterParaIEE(mantissaA, um26bits);
				resultado = normalizarResultado(mantissaA, expoenteA);
				return resultado;
			}
			expoenteSoma = expoenteA;
		}
		else if(menor == '0') expoenteSoma = expoenteA;	//expoentes iguais		
		mantissaA = converterParaC2(mantissaA, um26bits); //conversão das mantissas para facilitar a soma
		mantissaB = converterParaC2(mantissaB, um26bits);
		mantissaSoma = somaParcial(mantissaA, mantissaB, 26); //soma em si
		mantissaSoma = converterParaIEE(mantissaSoma, um26bits);
		resultado = normalizarResultado(mantissaSoma, expoenteSoma);		
		return resultado;
	}
	
	public static char[] subtrair(char[] mantissaA, char[] expoenteA, char[] mantissaB, char[] expoenteB) {
		if(mantissaB[0] == '+') mantissaB[0] = '-';
		else if(mantissaB[0] == '-') mantissaB[0] = '+'; //inverte o sinal do segundo e soma
		char[] resultado = somar(mantissaA, expoenteA, mantissaB, expoenteB);
		return resultado;
	}
	
	public static char[] multiplicar(char[] mantissaA, char[] expoenteA, char[] mantissaB, char[] expoenteB) {
		mantissaA = formatarMantissa(mantissaA);
		mantissaB = formatarMantissa(mantissaB);		
		char[] resultado;
		char[] um8bits = preencher(8);
		expoenteA = converterParaC2(expoenteA, um8bits);
		expoenteB = converterParaC2(expoenteB, um8bits);
		final char[] faixaDenormalizada = new char[] {'1','0','0','0','0','0','0','1'};
		
		if(numerosIguais(faixaDenormalizada, expoenteA)) {
			mantissaA[2] = '0';
			expoenteA = incrementarExpoente(expoenteA);
			if(tudoZero(mantissaA)) return resultadoIgualAZero(32);	
							
		}
		
		if(numerosIguais(faixaDenormalizada, expoenteB)) {
			mantissaB[2] = '0';
			expoenteB = incrementarExpoente(expoenteB);
			if(tudoZero(mantissaB)) return resultadoIgualAZero(32);							
		}		
				
		char[] expoenteProduto = somarExpoentes(expoenteA, expoenteB, true);	//soma os expoentes
		if(expoenteProduto == null) return null;	//verifica overflow/underflow
		char[] um26bits = preencher(26);
		mantissaA = converterParaC2(mantissaA, um26bits);
		mantissaB = converterParaC2(mantissaB, um26bits);
		char[] mantissaProduto;
		mantissaProduto = multiplicacaoParcial(mantissaA, mantissaB, 26, um26bits); //multiplicacao dos inteiros
		mantissaProduto = filtrarResultado(mantissaProduto); //recolhe o bit de sinal o '1' implicito e os primeiros 23 digitos fracionarios
		mantissaProduto = converterParaIEE(mantissaProduto, um26bits);				
		resultado = normalizarResultado(mantissaProduto, expoenteProduto);			
		
		return resultado;
	}
	
	public static char[] dividir(char[] mantissaA, char[] expoenteA, char[] mantissaB, char[] expoenteB) {
		mantissaA = formatarMantissa(mantissaA);
		mantissaB = formatarMantissa(mantissaB);		
		char[] resultado;
		char[] um8bits = preencher(8);
		expoenteA = converterParaC2(expoenteA, um8bits);
		expoenteB = converterParaC2(expoenteB, um8bits);
		final char[] faixaDenormalizada = new char[] {'1','0','0','0','0','0','0','1'};
		if(numerosIguais(faixaDenormalizada, expoenteB)) {
			mantissaB[2] = '0';
			expoenteB = incrementarExpoente(expoenteB);
			if(tudoZero(mantissaB)) {
				System.out.println("Impossível dividir por Zero");
				return null;
			}						
		}
		if(numerosIguais(faixaDenormalizada, expoenteA)) {
			mantissaA[2] = '0';
			expoenteA = incrementarExpoente(expoenteA);
			if(tudoZero(mantissaA)) return resultadoIgualAZero(32);							
		}		
		
		expoenteB = complementoA2(expoenteB, um8bits);
		char[] expoenteQuociente = somarExpoentes(expoenteA, expoenteB, true);
		if(expoenteQuociente == null) return null;	//overflow/underflow
		char[] mantissaQuociente = divisaoParcial(mantissaA, mantissaB); //divisao		
		resultado = normalizarResultado(mantissaQuociente, expoenteQuociente);		
		return resultado;
	}
	
	protected static char[] formatarExpoente(char[] expoente) {
		final char[] offset = new char[] {'0', '1', '1', '1', '1', '1', '1', '1'}; //excesso (127)
		expoente = somarExpoentes(expoente, offset, false);
		return expoente;
	}
	
	private static char[] somarExpoentes(char[] expoenteA,char[] expoenteB, boolean complementoA2) {
		char [] expoenteC = new char[8];            //soma de expoentes
		boolean vaiUm = false;						// true é setado quando a soma será entre os dois vetores
		for(int i = 7; i >= 0; i--) {				// false é setado quando o excesso será somado (se não da overflow)
			if(complementoA2) {
				if(i == 0 && expoenteA[i] == '0' && expoenteB[i] == '0' && vaiUm) {
					System.out.println("Expoente Overflow!");
					return null;
				}
				if(i == 0 && expoenteA[i] == '1' && expoenteB[i] == '1' && !vaiUm) {
					System.out.println("Expoente Underflow!");
					return null;
				}
			}
			if(!vaiUm) {				
				if((expoenteA[i] == '1' && expoenteB[i] == '0') || (expoenteB[i] == '1' && expoenteA[i] == '0')) {					
					expoenteC[i] = '1';
					vaiUm = false;
				}else if(expoenteA[i] == '1' && expoenteB[i] == '1'){
					expoenteC[i] = '0';
					vaiUm = true;
				}else if(expoenteA[i] == '0' && expoenteB[i] == '0') {
					expoenteC[i] = '0';
					vaiUm = false;
				}				
			}else if(vaiUm) {
				if((expoenteA[i] == '1' && expoenteB[i] == '0') || (expoenteB[i] == '1' && expoenteA[i] == '0')) {
					expoenteC[i] = '0';
					vaiUm = true;
				}else if(expoenteA[i] == '1' && expoenteB[i] == '1') {
					expoenteC[i] = '1';
					vaiUm = true;
				}else if(expoenteA[i] == '0' && expoenteB[i] == '0') {
					expoenteC[i] = '1';
					vaiUm = false;
				}
			}			
		}
		if(complementoA2) {
			final char[] excecao = new char[]{'1','0','0','0','0','0','0','0'};
			if(numerosIguais(expoenteC, excecao)) {
				if(expoenteA[0] == '0') System.out.println("Expoente overflow!");
				else System.out.println("Expoente underflow!");
				return null;
			}
		}
		return expoenteC;
	}
	
	private static char[] formatarMantissa(char[] mantissa) {
		char[] novaMantissa = new char[26];
		novaMantissa[0] = mantissa[0];	//essa funcao armazena o sinal da mantissa na posicao 0
		novaMantissa[1] = '0';			// um zero que servirá de margem na soma
		novaMantissa[2] = mantissa[1];	// o 1 implicito da ieee-754 (que foi digitada pelo usuario)
		for(int i = 3; i < 26; i++) {
			novaMantissa[i] = mantissa[i]; //os 23 digitos da parte fracionaria
		}
		return novaMantissa;		
	}
	
	private static char menorExpoente(char[] expoenteA, char[] expoenteB) {
		char[] um = preencher(8);
		char menor = '0';
		if(expoenteA[0] == '1' && expoenteB[0] == '0') menor = 'A';
		else if(expoenteA[0] == '0' && expoenteB[0] == '1') menor = 'B';
		else if(expoenteA[0] == expoenteB[0]) {
			if(numerosIguais(expoenteA, expoenteB)) menor = '0';
			else {
				char[] soma = new char[8];
				char[] copia = copiar(expoenteA);
				copia = complementoA2(copia, um);
				soma = somarExpoentes(copia, expoenteB, true);							
				if(soma[0] == '1') menor = 'B';
				else menor = 'A';
			}
		}
		return menor;
	}
	
	private static boolean numerosIguais(char[] bitsA, char[] bitsB) {
		boolean igual = true;	//verifica se dois números são iguais
		for(int i = 0; i < bitsA.length; i++) {
			if(bitsA[i] != bitsB[i]) {
				igual = false;
				break;
			}
		}
		return igual;
	}
	
	private static char[] incrementarExpoente(char[] expoente) {
		final char[] maisUm = {'0','0','0','0','0','0','0','1'};
		expoente = somarExpoentes(expoente, maisUm, true);		
		return expoente;
	}
	
	private static char[] decrementarExpoente(char[] expoente) {
		final char[] menosUm = {'1','1','1','1','1','1','1','1'};
		expoente = somarExpoentes(expoente, menosUm, true);		
		return expoente;
	}	
	
	protected static char[] shiftRight(char[] mantissa) {
		for(int i = 25; i >= 2; i--) {
			mantissa[i] = mantissa[i - 1];
		}
		return mantissa;
	}
	
	protected static char[] shiftLeft(char[] mantissa) {
		for(int i = 2; i < 25; i++) {
			mantissa[i] = mantissa[i + 1];
		}
		return mantissa;
	}
	
	private static char[] formatarResultado(char[] mantissa, char[] expoente){
		if(tudoZero(mantissa)) return resultadoIgualAZero(32);
		char[] resultado = new char[32];
		resultado[0] = mantissa[0];
		expoente = formatarExpoente(expoente);
		for(int i = 1; i <= 8; i++) {
			resultado[i] = expoente[i - 1];
		}
		for(int i = 9; i < 32; i++) {
			resultado[i] = mantissa[i - 6];
		}
		return resultado;		
	}
	
	private static char[] igualarExpoentes(char menor, char[] expoenteMaior, char[] expoenteMenor, char[] mantissaDoMenor) {
		while(menor != '0') {
			expoenteMenor = incrementarExpoente(expoenteMenor);
			mantissaDoMenor = shiftRight(mantissaDoMenor);
			if(tudoZero(mantissaDoMenor)) return null;
			menor = menorExpoente(expoenteMenor, expoenteMaior);
		}
		return mantissaDoMenor;
	}
	
	private static char[] copiar(char[] vetor) {
		char[] copia = new char[vetor.length];
		for(int i = 0; i < vetor.length; i++) {
			copia[i] = vetor[i];
		}
		return copia;
	}
	
	private static char[] converterParaIEE(char[] mantissa, char[] um) {
		if(mantissa[0] == '1') {
			mantissa = complementoA2(mantissa, um);
			mantissa[0] = '1';
		}
		else mantissa[0] = '0';
		return mantissa;
	}	
	
	private static char[] filtrarResultado(char[] resultado) {
		char[] novoResultado = new char[26];
		novoResultado[0] = resultado[0];
		for(int i = 1; i < 26; i++) {
			novoResultado[i] = resultado[i + 3];
		}
		return novoResultado;
	}
	
	private static char[] normalizarResultado(char[] resultadoM, char[] resultadoE) {
		char[] normalizado;
		final char[] faixaDenormalizada = new char[] {'1','0','0','0','0','0','0','1'};
		if(numerosIguais(faixaDenormalizada, resultadoE) && (resultadoM[1] == '0')) resultadoM = shiftRight(resultadoM); //01,000...* 2^(-127)
		if(tudoZero(resultadoM)) {																						 // vira 0,1..*2^(-126)
			normalizado = resultadoIgualAZero(32);				
		}
		else if(resultadoM[1] == '1') {
			resultadoM = shiftRight(resultadoM);	//mantissa overflow (10,00001100...00)
			resultadoM[1] = '0';
			resultadoE = incrementarExpoente(resultadoE);
			if(resultadoE == null) return null; //expoente overflow
		}
		else if(resultadoM[2] == '0' && !numerosIguais(faixaDenormalizada, resultadoE)) { //mantissa underflow (0,...1....00)			
			while(resultadoM[2] == '0') {				
				resultadoE = decrementarExpoente(resultadoE);
				if(numerosIguais(faixaDenormalizada, resultadoE)) break;	//caso o expoente chegue a (-127)
				resultadoM = shiftLeft(resultadoM);							
				resultadoM[25] = '0';				
			}
		}
		normalizado = formatarResultado(resultadoM, resultadoE);
		return normalizado;
	}
	
	private static char[] divisaoParcial(char[] dividendo, char[] divisor) {
		char[] quociente = new char[26];
		if(dividendo[0] != divisor[0]) quociente[0] = '1';	//determina o sinal do quociente
		else quociente[0] = '0';
		quociente[1] = '0'; //bit de margem
		dividendo[0] = '0'; 
		divisor[0] = '0';			//dividendo e divisor positivos	
		char[] auxDividendo = inicializarAux(dividendo);
		char[] auxDivisor = inicializarAux(divisor);	//converte ambos pra um vetor 49 bits(24 de margem e 1 de sinal)		
		char[] um49bits = preencher(49);
		for(int i = 1; i < 25; i++) {
			auxDivisor = complementoA2(auxDivisor, um49bits);
			auxDividendo = somaParcial(auxDividendo, auxDivisor, 49);	//subtrai dividendo do divisor
			auxDivisor = complementoA2(auxDivisor, um49bits);
			if(auxDividendo[0] == '1') {
				quociente[i + 1] = '0';
				auxDividendo = somaParcial(auxDividendo, auxDivisor, 49);	//preenche o quociente com 0 ou 1			
			}
			else {
				quociente[i + 1] = '1';
			}
			auxDividendo = shiftLeft2(auxDividendo); //dobra o Dividendo (adiciona um zero)
			auxDividendo[48] = '0';
		}
		return quociente;
		
	}
	
	private static char[] inicializarAux(char[] dividendo) {
		char[] aux = new char[49];
		aux[0] = dividendo[0];
		for(int i = 1; i < 25; i++) {
			aux[i] = '0';
		}
		for(int i = 25; i < 49; i++) {
			aux[i] = dividendo[i - 23];
		}
		return aux;
	}
	
	private static char[] shiftLeft2(char[] mantissa) {
		for(int i = 1; i < 48; i++) {
			mantissa[i] = mantissa[i + 1];
		}
		return mantissa;
	}	
	
	
 }
