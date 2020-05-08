package Entities;

/*Todas as operações foram feitas com base nos circuitos ilustrados
   nos slides do professor Valdinei da Silva */

public class Inteiros {
			
	public static char[] somar(char[] bitsA, char[] bitsB, int tamanho) {		
		char[] um = preencher(tamanho); //cria o vetor "000...01" a partir do tamanho dos numeros
		bitsA = converterParaC2(bitsA, um);
		bitsB = converterParaC2(bitsB, um); //faz a conversão de bit de sinal para complemento de 2
		char[] resultado = somaParcial(bitsA, bitsB, tamanho); //faz a soma em si
		if(resultado == null) {
			System.out.println("Overflow!"); //em caso de overflow o resultado é null
		}
		else resultado = converterParaBitSinal(resultado, um);		
		return resultado;		
	}
	
	public static char[] subtrair(char[] bitsA, char[] bitsB, int tamanho) {		
		if(bitsB[0] == '+') bitsB[0] = '-';
		else if(bitsB[0] == '-') bitsB[0] = '+'; //apenas inverte o sinal e soma;
		return somar(bitsA, bitsB, tamanho);		
	}
	
	public static char[] multiplicar(char[] bitsA, char[] bitsB, int tamanho) {
		char[] resultado;
		if(tudoZero(bitsA) || tudoZero(bitsB)) resultado = resultadoIgualAZero(tamanho); //multiplicação por zero		
		else {
			char[] um = preencher(tamanho);
			bitsA = converterParaC2(bitsA, um);
			bitsB = converterParaC2(bitsB, um);		
			resultado = multiplicacaoParcial(bitsA, bitsB, tamanho, um); //multiplicação em si	
			um = preencher(2 * tamanho);
			resultado = converterParaBitSinal(resultado, um);
		}
		return resultado;		
	}
	
	public static char[] dividir(char[] dividendo, char[] divisor, int tamanho) {
		if(tudoZero(divisor)) {
			System.out.println("Impossível dividir por 0!");
			return null;
		}
		boolean negativo = false;
		if(dividendo[0] != divisor[0]) negativo = true; //determina o sinal do quociente 		
		divisor[0] = '0';								//esse algoritmo é de divisao sem sinal
		dividendo[0] = '0';
		char[] um = preencher(tamanho);		
		char[] operacao = new char[2 * tamanho];
		inicializarDivisao(operacao, dividendo, tamanho);	//cria o vetor auxiliar para realizar a divisao
		for(int i = tamanho; i > 0; i--) {
			operacao = shiftLeft(operacao);		//desloca o vetor para esquerda	
			divisor = complementoA2(divisor, um);
			operacao = somaBooth(operacao, divisor, tamanho);	//subtrai o divisor do dividendo
			divisor = complementoA2(divisor, um); //(des)inverte o divisor
			if(operacao[0] == '1') {	//se o dividendo for menor que zero é colocado um 0 no quociente
				operacao[(2 * tamanho) - 1] = '0';
				operacao = somaBooth(operacao, divisor, tamanho); //e o divisor é somado de volta
			}
			else operacao[(2 * tamanho) - 1] = '1'; //se for positivo é colocado 1
		}
		char[] resultado = new char[tamanho];
		resultado = resultadoDivisao(operacao, resultado, negativo, tamanho);	//recolhe o quociente do vetor operacao
		if(tudoZero(resultado)) resultado[0] = '0';
		return resultado;
	}
	
	protected static char[] somaParcial(char[] bitA, char[] bitB, int tamanho) {		
		char [] bitC = new char[tamanho];
		boolean vaiUm = false;
		for(int i = tamanho - 1; i >= 0; i--) {	
			if(i == 0 && bitA[i] == '0' && bitB[i] == '0' && vaiUm) {
				return null; //Overflow
			}
			if(i == 0 && bitA[i] == '1' && bitB[i] == '1' && !vaiUm) {
				return null; //Underflow
			}
			if(!vaiUm) {			//todos os casos da soma bit a bit	
				if((bitA[i] == '1' && bitB[i] == '0') || (bitB[i] == '1' && bitA[i] == '0')) {					
					bitC[i] = '1';
					vaiUm = false;
				}else if(bitA[i] == '1' && bitB[i] == '1'){
					bitC[i] = '0';
					vaiUm = true;
				}else if(bitA[i] == '0' && bitB[i] == '0') {
					bitC[i] = '0';
					vaiUm = false;
				}				
			}else if(vaiUm) {
				if((bitA[i] == '1' && bitB[i] == '0') || (bitB[i] == '1' && bitA[i] == '0')) {
					bitC[i] = '0';
					vaiUm = true;
				}else if(bitA[i] == '1' && bitB[i] == '1') {
					bitC[i] = '1';
					vaiUm = true;
				}else if(bitA[i] == '0' && bitB[i] == '0') {
					bitC[i] = '1';
					vaiUm = false;
				}
			}			
		}		
		return bitC;
	}

	protected static char [] complementoA2(char[] numero, char[] um) {
		char[] resultado = new char[numero.length];
		resultado = numero;	//inverte todos os numeros
		for(int i = 0; i < resultado.length; i++) {
			if(resultado[i] == '1') resultado[i] = '0';
			else if(resultado[i] == '0') resultado[i] = '1';			
		}			//soma um no final
		resultado = somaParcial(resultado, um, resultado.length);		
		return resultado;
	}
	
	protected static char[] converterParaC2(char[] numero, char[] um) {
		if(numero[0] == '+') numero[0] = '0';
		else if(numero[0] == '-') {
			numero[0] = '0';	//troca o bit de sinal por 0 ou 1
			numero = complementoA2(numero, um);
		}		
		return numero;		
	}
	  
	protected static char[] converterParaBitSinal(char[] numero, char[] um) {
		if(numero[0] == '0') numero[0] = '+';	
		else if(numero[0] == '1') {			//troca o 0 por + e o 1 por -
			numero = complementoA2(numero, um);
			if(numero == null) System.out.println("Overflow!");			
			else numero[0] = '-';
		}		
		return numero;
	}
	
	protected static char[] preencher(int tamanho) {
		char[] um = new char[tamanho];		
		for(int i = 0; i < tamanho - 1; i++) {			
			um[i] = '0';
		}						
		um[tamanho - 1] = '1';
		return um;
	}
	
	protected static char[] inicializarMulti(char[] operacao, char[] bitsA, int tamanho) {
		for(int i = 0; i < tamanho; i++) {
			operacao[i] = '0';
		}
		for(int i = tamanho; i < 2 * tamanho; i++) {
			operacao[i] = bitsA[i - tamanho];
		}
		operacao[2 * tamanho] = '0';
		return operacao;
	}
	
	protected static char[] somaBooth(char[] bitA, char[] bitB, int tamanho) {
		char [] bitC = bitA;	//semelhante a soma parcial, mas permite o overflow
		boolean vaiUm = false;
		for(int i = tamanho - 1; i >= 0; i--) {				
			if(!vaiUm) {				
				if((bitA[i] == '1' && bitB[i] == '0') || (bitB[i] == '1' && bitA[i] == '0')) {					
					bitC[i] = '1';
					vaiUm = false;
				}else if(bitA[i] == '1' && bitB[i] == '1'){
					bitC[i] = '0';
					vaiUm = true;
				}else if(bitA[i] == '0' && bitB[i] == '0') {
					bitC[i] = '0';
					vaiUm = false;
				}				
			}else if(vaiUm) {
				if((bitA[i] == '1' && bitB[i] == '0') || (bitB[i] == '1' && bitA[i] == '0')) {
					bitC[i] = '0';
					vaiUm = true;
				}else if(bitA[i] == '1' && bitB[i] == '1') {
					bitC[i] = '1';
					vaiUm = true;
				}else if(bitA[i] == '0' && bitB[i] == '0') {
					bitC[i] = '1';
					vaiUm = false;
				}
			}			
		}		
		return bitC;
	}
	
	protected static char[] shiftRight(char[] operacao) {
		for(int i = operacao.length - 1; i >= 1; i--) {
			operacao[i] = operacao[i - 1];
		}
		return operacao;
	}
	
	protected static char[]	resultadoMulti(char[] operacao, char[] resultado) {
		for(int i = 0; i < (operacao.length - 1); i++) {
			resultado[i] = operacao[i];
		}
		return resultado;
	}
	
	protected static char[] shiftLeft(char[] numero) {
		for(int i = 0; i < numero.length - 1; i++) {
			numero[i] = numero[i + 1];
		}
		return numero;
	}
	
	protected static char[] inicializarDivisao(char[] bitsA, char[] dividendo, int tamanho) {
		for(int i = 0; i < tamanho; i++) {
			bitsA[i] = '0';				//a primeira metade do vetor é uma margem para que o shiftLeft não ultrapasse o vetor
		}								//ao final o resto da divisão ficará aqui
		for(int i = tamanho; i < 2 * tamanho; i++) {
			bitsA[i] = dividendo[i - tamanho]; //a segunda metade possui o dividendo
		}
		return bitsA;
	}

	protected static char[] resultadoDivisao(char[] operacao, char[] resultado, boolean negativo, int tamanho) {
		if(negativo) resultado[0] = '-';
		else resultado[0] = '+';
		for(int i = tamanho + 1; i < operacao.length; i++) {
			resultado[i - tamanho] = operacao[i];
		}
		return resultado;
	}
	
	protected static boolean tudoZero(char[] divisor) {
		boolean zero = true;	//verifica se é um array apenas de zeros
		for(int i = 1; i < divisor.length; i++) {
			if(divisor[i] != '0') {
				zero = false;
				break;
			}
		}
		return zero;
	}
	
	protected static char[] multiplicacaoParcial(char[] numeroA, char[] numeroB, int tamanho, char[] um) {
		char[] operacao = new char[(2 * tamanho) + 1];	//cria o vetor auxiliar utilizado pelo algoritmo de booth
		inicializarMulti(operacao, numeroA, tamanho);	//inicializa o vetor
		for(int i = tamanho; i > 0; i--) {
			if(operacao[(2 * tamanho) - 1] == '0' && operacao[(2 * tamanho)] == '1') {
				operacao = somaBooth(operacao, numeroB, tamanho);
			}
			if(operacao[(2 * tamanho) - 1] == '1' && operacao[(2 * tamanho)] == '0') {
				numeroB = complementoA2(numeroB, um);
				operacao = somaBooth(operacao, numeroB, tamanho);	//inverte o numero para subtrair e dps outra
				numeroB = complementoA2(numeroB, um);				// inversao para deixa-lo com o sinal correto
			}
			operacao = shiftRight(operacao); //desloca todos os valores para direita
		}
		char[] resultado = new char[2 * tamanho];
		resultado = resultadoMulti(operacao, resultado); //exclui a ultima posição do vetor
		return resultado;
	}
	
	protected static char[] resultadoIgualAZero(int tamanho) {
		char[] resultado = new char[tamanho];	//cria um vetor apenas com 0
		for(int i = 0; i < tamanho; i++) {
			resultado[i] = '0';
		}
		return resultado;
	}

}	