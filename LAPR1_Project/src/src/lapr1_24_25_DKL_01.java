import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Random;

public class lapr1_24_25_DKL_01 {
    static Scanner scKeyboard = new Scanner(System.in);  // Cria um objeto Scanner para ler entrada do teclado
    static final int MAXSIZEMATRIX = 256;  // Define o tamanho máximo de uma matriz (256)
    static final int MINCOLORPIXEL = 0;  // Define o valor mínimo de pixel de cor (0)
    static final int MAXCOLORPIXEL = 255;  // Define o valor máximo de pixel de cor (255)

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {  // Verifica se não foram passados argumentos
            InteractiveMode();  // Chama o modo interativo se não houver argumentos
        } else {  // Caso contrário, se houver argumentos
            NonInteractiveMode(args);  // Chama o modo não interativo passando os argumentos
        }
    }

    public static void InteractiveMode() throws IOException {
        int option = -1;  // Inicia a variável “option” com um valor que não seja válido para começar o loop.
        while (option != 0) {  // Inicia um loop que continuará até que a opção seja 0 (encerrar programa).
            option = PrinterGUI_Menu();  // Chama a função PrinterGUI_Menu() para exibir o menu e capturar a opção escolhida.
            PrinterStartFunction(option);  // Chama a função PrinterStartFunction() para imprimir uma mensagem de início com base na opção escolhida.
            switch (option) {
                case 1: {
                    PrinterMenu1();  // Exibe o primeiro menu
                    System.out.print("-> Digite a localização do ficheiro CSV com a matriz: ");  // Solicita o caminho do ficheiro CSV
                    scKeyboard.nextLine();  // Lê a linha do teclado
                    String pathOfFileCSV = scKeyboard.nextLine();  // Lê o caminho do ficheiro CSV
                    pathOfFileCSV = pathOfFileCSV.trim();  // Remove espaços antes e depois do caminho
                    File fileCSV = new File(pathOfFileCSV);  // Atribui o caminho a uma variável ficheiro
                    if(fileCSV.isFile() && pathOfFileCSV.endsWith(".csv")) {  // Verifica se é um ficheiro válido
                        if (MatrixIsSquare(pathOfFileCSV)) {  // Verifica se a matriz é quadrada
                            int sizeOfMatrix = SizeOfMatrixComma(pathOfFileCSV);  // Obtém o tamanho da matriz
                            double[][] matrix = ReadMatrix(sizeOfMatrix, pathOfFileCSV);  // Lê a matriz do ficheiro
                            if (MatrixIsSymmetrical(matrix)) {  // Verifica se a matriz é simétrica
                                double[][] eigenvaluesOfMatrix = EigenvaluesMatrixDiagonal(matrix);  // Calcula os valores próprios da matriz
                                double[][] eigenvectorsOfMatrix = EigenvectorsMatrix(matrix);  // Calcula os vetores próprios da matriz
                                int numOfProperVectors = ValueInsideOfBounds(sizeOfMatrix);  // Obtém o número de vetores próprios
                                double[][] compressedMatrix = CompressorOfMatrix(eigenvectorsOfMatrix, eigenvaluesOfMatrix, numOfProperVectors);  // Comprime a matriz com os vetores próprios e valores próprios
                                double absoluteError = CalculateAbsoluteError(matrix, compressedMatrix);  // Calcula o erro absoluto entre a matriz original e a comprimida
                                System.out.println("\n --------> Resultado <--------");  // Exibe o resultado
                                System.out.printf("\n-> Nº valores e vetores próprios: %d\n", numOfProperVectors);  // Exibe o número de valores e vetores próprios usados
                                System.out.println("\n-> Valores próprios usados na decomposição:\n");  // Exibe os valores próprios usados na decomposição
                                PrintMatrixFilterNumColumns(eigenvaluesOfMatrix, numOfProperVectors, nDigitsMaxMatrixNum(eigenvaluesOfMatrix));  // Imprime os valores próprios
                                System.out.println("\n-> Vetores próprios usados na decomposição:\n");  // Exibe os vetores próprios usados na decomposição
                                PrintMatrixFilterNumColumns(eigenvectorsOfMatrix, numOfProperVectors, nDigitsMaxMatrixNum(eigenvectorsOfMatrix));  // Imprime os vetores próprios
                                System.out.printf("\n-> EAM = %s\n", ConvertNumberToScientificNotation(absoluteError));  // Exibe o erro absoluto em notação científica
                                System.out.printf("\n-> Matriz reconstruída com %d valores/vetores próprios:\n\n", numOfProperVectors);  // Exibe a matriz reconstruída
                                PrintMatrixFilterNumColumns(compressedMatrix, compressedMatrix.length, nDigitsMaxMatrixNum(compressedMatrix));  // Imprime a matriz comprimida
                                String outputDirectoryName = "Output";  // Define o nome da pasta de saída
                                CreateDirectory(outputDirectoryName, true, "");  // Cria a pasta de saída
                                String outputFileName = "\\MatrizReconstruidaComKVsProprios.csv";  // Define o nome do ficheiro de saída
                                String textCompressedMatrix = "Matriz reconstruída com " + numOfProperVectors + " valores/vetores próprios:";  // Define o título do ficheiro
                                PrintMatrixFilterFile(textCompressedMatrix, compressedMatrix, outputDirectoryName + outputFileName, -1, compressedMatrix[0].length, nDigitsMaxMatrixNum(compressedMatrix), true, true);  // Imprime a matriz comprimida para o ficheiro
                                System.out.printf("\nFicheiros adicionados/alterados em: %s\n", outputDirectoryName);  // Exibe o local onde os ficheiros foram adicionados
                            } else PrintErrorMessage("UnsymmetricalMatrix", true, null);  // Exibe mensagem de erro se a matriz não for simétrica
                        } else PrintErrorMessage("InvalidSize", true, null);  // Exibe mensagem de erro se o tamanho da matriz for inválido
                    } else PrintErrorMessage("InvalidFile",true,null);  // Exibe mensagem de erro se o ficheiro for inválido
                }
                break;
                case 2: {
                    PrinterMenu2();  // Exibe o segundo menu
                    System.out.print("-> Digite a localização da pasta: ");  // Solicita o caminho da pasta
                    scKeyboard.nextLine();  // Lê a linha do teclado
                    String pathOfDirectory = scKeyboard.nextLine();  // Lê o caminho da pasta
                    pathOfDirectory = pathOfDirectory.trim();  // Remove espaços antes e depois do caminho
                    int nFilesCSVDirectory = CounterFilesCSVOnDirectory(pathOfDirectory);  // Conta o número de arquivos CSV na pasta
                    if (nFilesCSVDirectory != 0) {  // Verifica se há arquivos CSV na pasta
                        String[] nameListValidFiles = new String[nFilesCSVDirectory];  // Cria uma lista para os nomes dos arquivos válidos
                        double[][] globalMatrix = ReadGlobalMatrixWithExceptions(pathOfDirectory, nFilesCSVDirectory, nameListValidFiles, null, true);  // Lê a matriz global com exceções
                        if (globalMatrix[0][0] != -1) {  // Verifica se a matriz foi lida corretamente
                            double[] meanVector = MeanVector(globalMatrix);  // Calcula o vetor médio
                            double[][] matrixA = ConstructionOfMatrixA(globalMatrix, meanVector);  // Constrói a matriz A
                            double[][] multiplicationTranposerA_A = MultiplicationMatrix(MatrixTransposer(matrixA), matrixA);  // Multiplica a transposta de A por A
                            double[][] properVectorsTransposerA_A = EigenvectorsMatrix(multiplicationTranposerA_A);  // Calcula os vetores próprios da matriz A^tA
                            double[][] normalizedProperVectors = NormalizedProperVectors(matrixA, properVectorsTransposerA_A);  // Normaliza os vetores próprios
                            System.out.printf("->Digite o número de eigenfaces que pretende usar para a reconstrução (K) (1 - %d): ", globalMatrix[0].length);  // Solicita o número de eigenfaces
                            int kValue = FindKValue(matrixA);  // Obtém o valor de K
                            double[][] globalWeightOfImages = globalWeight(kValue, matrixA, normalizedProperVectors);  // Calcula os pesos globais das imagens
                            double[][] imageRestorationK = ImageRestoration(kValue, meanVector, matrixA, normalizedProperVectors, globalWeightOfImages);  // Reconstrói a imagem com K eigenfaces
                            System.out.println("\n--------> Resultado <--------");  // Exibe o resultado
                            System.out.println("\n-> Vetor µ:\n");  // Exibe o vetor médio
                            double[][] horizontalMeanVector = MatrixTransposer(ConvertArrayToMatrixVertical(meanVector));  // Transforma o vetor médio para matriz horizontal
                            PrintMatrixFilterNumColumns(horizontalMeanVector, horizontalMeanVector[0].length, nDigitsMaxMatrixNum(horizontalMeanVector));  // Imprime o vetor médio
                            System.out.println("\n-> Matriz A^tA:\n");  // Exibe a matriz A^tA
                            PrintMatrixFilterNumColumns(multiplicationTranposerA_A, multiplicationTranposerA_A[0].length, nDigitsMaxMatrixNum(multiplicationTranposerA_A));  // Imprime a matriz A^tA
                            System.out.println("\n-> Nº eigenfaces utilizadas: " + kValue);  // Exibe o número de eigenfaces utilizadas
                            System.out.println("\n-> Vetores de pesos (w) usados na reconstrução das imagens:\n");  // Exibe os vetores de pesos
                            System.out.print("Imagem:  ");  // Exibe o título "Imagem"
                            for (int indexImagem = 1; indexImagem <= globalWeightOfImages[0].length; indexImagem++) {  // Imprime os índices das imagens
                                System.out.printf("%8d |", indexImagem);  // Formatação dos índices
                            }
                            System.out.print("\n         ");  // Espaçamento
                            for (int indexImagem = 1; indexImagem <= globalWeightOfImages[0].length; indexImagem++) {  // Imprime o separador de colunas
                                System.out.print("---------|");
                            }
                            for (int indexRow = 0; indexRow < globalWeightOfImages.length; indexRow++) {  // Imprime os pesos das imagens
                                System.out.print("\n         ");  // Espaçamento
                                for (int indexColumn = 0; indexColumn < globalWeightOfImages[0].length; indexColumn++) {  // Imprime os valores dos pesos
                                    System.out.printf("%8.2f |", globalWeightOfImages[indexRow][indexColumn]);
                                }
                            }
                            String directoryNameEigenFaces = "Eigenfaces";  // Define o nome do diretório para as eigenfaces
                            String directoryNameRestorationImages = "ImagensReconstruidas";  // Define o nome do diretório para as imagens reconstruídas
                            System.out.println();  // Quebra de linha
                            CreateDirectory(directoryNameEigenFaces, true, "");  // Cria o diretório de eigenfaces
                            CreateDirectory(directoryNameRestorationImages, true, "");  // Cria o diretório de imagens reconstruídas
                            for (int nImage = 0; nImage < globalMatrix[0].length; nImage++) {  // Reitera sobre as imagens
                                double[][] restorationMatrixK = ConvertArrayToSquareMatrix(ConvertColumnMatrixToArray(imageRestorationK, nImage));  // Converte a matriz de reconstrução para formato quadrado
                                double[][] matrixOriginalImageK = ConvertArrayToSquareMatrix(ConvertColumnMatrixToArray(globalMatrix, nImage));  // Converte a imagem original para formato quadrado
                                double absoluteErrorMatrixImage = CalculateAbsoluteError(matrixOriginalImageK, restorationMatrixK);  // Calcula o erro absoluto entre a imagem original e a reconstruída
                                String textTittleMatrixOriginal = "Matriz reconstruida original de " + nameListValidFiles[nImage] + " com " + kValue + " eigenfaces:";  // Título da matriz original
                                String filePathNameMatrixOriginal = directoryNameEigenFaces + "\\" + nameListValidFiles[nImage] + "_MatrizReconstruidaOriginal_K" + kValue + ".csv";  // Caminho para o arquivo da matriz original
                                String textTittleMatrixTransformed = "Matriz reconstruida transformada de " + nameListValidFiles[nImage] + " com " + kValue + " eigenfaces:";  // Título da matriz transformada
                                String filePathNameMatrixTransformed = directoryNameEigenFaces + "\\" + nameListValidFiles[nImage] + "_MatrizReconstruidaTransformada_K" + kValue + ".csv";  // Caminho para o arquivo da matriz transformada
                                String pathFileImage = directoryNameRestorationImages + "\\" + nameListValidFiles[nImage] + "_ImagemReconstruida_K" + kValue + ".jpg";  // Caminho para a imagem reconstruída
                                PrintMatrices_OT_Image_IfNecessary_Error(restorationMatrixK, textTittleMatrixOriginal, filePathNameMatrixOriginal, textTittleMatrixTransformed, filePathNameMatrixTransformed, pathFileImage, absoluteErrorMatrixImage);  // Imprime a matriz e a imagem, se necessário
                            }
                            System.out.printf("\nFicheiros adicionados em: %s & %s\n", directoryNameEigenFaces, directoryNameRestorationImages);  // Exibe os diretórios onde os arquivos foram adicionados
                        }
                    } else PrintErrorMessageFilterFile("InvalidDirectory", true, null, pathOfDirectory);  // Exibe mensagem de erro se o diretório for inválido
                }
                break;
                case 3: {
                    PrinterMenu3();  // Exibe o terceiro menu
                    System.out.print("-> Digite a localização da pasta: ");  // Solicita o caminho da pasta
                    scKeyboard.nextLine();  // Lê a linha do teclado
                    String pathOfDirectory = scKeyboard.nextLine();  // Lê o caminho da pasta
                    pathOfDirectory = pathOfDirectory.trim();  // Remove espaços antes e depois do caminho
                    int nFilesCSVDirectory = CounterFilesCSVOnDirectory(pathOfDirectory);  // Conta o número de arquivos CSV na pasta
                    if (nFilesCSVDirectory != 0) {  // Verifica se há arquivos CSV na pasta
                        String[] nameListValidFiles = new String[nFilesCSVDirectory];  // Cria uma lista para os nomes dos arquivos válidos
                        double[][] globalMatrix = ReadGlobalMatrixWithExceptions(pathOfDirectory, nFilesCSVDirectory, nameListValidFiles, null, true);  // Lê a matriz global com exceções
                        if (globalMatrix[0][0] != -1) {  // Verifica se a matriz foi lida corretamente
                            System.out.printf("-> Digite o número de eigenfaces que pretende usar para a reconstrução (K) (1 - %d): ", globalMatrix[0].length);  // Solicita o número de eigenfaces
                            int kValue = FindKValue(globalMatrix);  // Obtém o valor de K
                            System.out.print("-> Digite a localização da nova imagem: ");  // Solicita o caminho da nova imagem
                            scKeyboard.nextLine();  // Lê a linha do teclado
                            String pathOfFileCSV = scKeyboard.nextLine();  // Lê o caminho do arquivo CSV
                            pathOfFileCSV = pathOfFileCSV.trim();  // Remove espaços antes e depois do caminho
                            File pathOfFileName = new File(pathOfFileCSV);  // Cria um objeto File com o caminho fornecido
                            if (pathOfFileName.isFile() && (pathOfFileCSV.toLowerCase().endsWith(".csv"))) {  // Verifica se o caminho é de um arquivo CSV
                                if (MatrixIsSquare(pathOfFileCSV)) {  // Verifica se a matriz é quadrada
                                    int sizeNewImageMatrix = SizeOfMatrixComma(pathOfFileCSV);  // Obtém o tamanho da matriz da nova imagem
                                    if (sizeNewImageMatrix == (int) Math.sqrt(globalMatrix.length)) {  // Verifica se o tamanho da matriz é compatível
                                        double[][] matrixOfNewImage = ReadMatrix(sizeNewImageMatrix, pathOfFileCSV);  // Lê a matriz da nova imagem
                                        if (MatrixWithValuesWithinLimits(matrixOfNewImage, 0, 255)) {  // Verifica se os valores da matriz estão dentro dos limites
                                            double[] meanVector = MeanVector(globalMatrix);  // Calcula o vetor médio
                                            double[][] matrixA = ConstructionOfMatrixA(globalMatrix, meanVector);  // Constrói a matriz A
                                            double[][] multiplicationTranposerA_A = MultiplicationMatrix(MatrixTransposer(matrixA), matrixA);  // Multiplica a transposta de A por A
                                            double[][] properVectorsTransposerA_A = EigenvectorsMatrix(multiplicationTranposerA_A);  // Calcula os vetores próprios da matriz A^tA
                                            double[][] normalizedProperVectors = NormalizedProperVectors(matrixA, properVectorsTransposerA_A);  // Normaliza os vetores próprios
                                            double[][] matrixOhmNewImage = CalculateOhm(matrixOfNewImage, meanVector, kValue, normalizedProperVectors);  // Calcula o vetor omega da nova imagem
                                            System.out.println("\n --------> Resultado <--------");  // Exibe o resultado
                                            int closestImage = CalculateLessEuclideanDistance(matrixOhmNewImage, globalMatrix, meanVector, kValue, normalizedProperVectors, nameListValidFiles, "", true);  // Encontra a imagem mais próxima
                                            System.out.println("\n-> Nº eigenfaces utilizadas: " + kValue);  // Exibe o número de eigenfaces utilizadas
                                            String directoryImageIdentification = "Identificacao";  // Define o nome do diretório para identificação
                                            CreateDirectory(directoryImageIdentification, true, "");  // Cria o diretório de identificação
                                            String pathNameFileImage = directoryImageIdentification + "\\ImagemIdentificacao_K" + kValue + ".jpg";  // Caminho para o arquivo de imagem
                                            System.out.println("\n-> Vetor (omega)nova.:\n");  // Exibe o vetor omega da nova imagem
                                            PrintMatrixFilterNumColumns(matrixOhmNewImage, matrixOhmNewImage[0].length, nDigitsMaxMatrixNum(matrixOhmNewImage));  // Imprime o vetor omega
                                            CreateImageWithMatrixForFile(globalMatrix, closestImage, pathNameFileImage);  // Cria a imagem da identificação
                                            System.out.printf("\nFicheiros adicionados/alterados em: %s\n", directoryImageIdentification);  // Exibe a pasta onde os arquivos foram adicionados
                                        } else PrintErrorMessage("InvalidValue", true, null);  // Exibe mensagem de erro se os valores da matriz estiverem fora dos limites
                                    } else PrintErrorMessage("InvalidSize", true, null);  // Exibe mensagem de erro se o tamanho da matriz não for válido
                                } else PrintErrorMessage("InvalidSize", true, null);  // Exibe mensagem de erro se a matriz não for quadrada
                            } else PrintErrorMessage("InvalidFile", true, null);  // Exibe mensagem de erro se o arquivo não for válido
                        }
                    } else PrintErrorMessageFilterFile("InvalidDirectory", true, null, pathOfDirectory);  // Exibe mensagem de erro se o diretório for inválido
                }
                break;
                case 4: {
                    PrinterMenu4();  // Exibe o quarto menu
                    System.out.print("-> Digite a localização da pasta: ");  // Solicita o caminho da pasta
                    scKeyboard.nextLine();  // Lê a linha do teclado
                    String pathOfDirectory = scKeyboard.nextLine();  // Lê o caminho da pasta
                    pathOfDirectory = pathOfDirectory.trim();  // Remove espaços antes e depois do caminho
                    int nFilesCSVDirectory = CounterFilesCSVOnDirectory(pathOfDirectory);  // Conta o número de arquivos CSV na pasta
                    if (nFilesCSVDirectory != 0) {  // Verifica se há arquivos CSV na pasta
                        String[] nameListValidFiles = new String[nFilesCSVDirectory];  // Cria uma lista para os nomes dos arquivos válidos
                        double[][] globalMatrix = ReadGlobalMatrixWithExceptions(pathOfDirectory, nFilesCSVDirectory, nameListValidFiles, null, true);  // Lê a matriz global com exceções
                        if (globalMatrix[0][0] != -1) {  // Verifica se a matriz foi lida corretamente
                            double[] meanVector = MeanVector(globalMatrix);  // Calcula o vetor médio
                            double[][] matrixA = ConstructionOfMatrixA(globalMatrix, meanVector);  // Constrói a matriz A
                            double[][] multiplicationTranposerA_A = MultiplicationMatrix(MatrixTransposer(matrixA), matrixA);  // Multiplica a transposta de A por A
                            double[][] properVectorsTransposerA_A = EigenvectorsMatrix(multiplicationTranposerA_A);  // Calcula os vetores próprios da matriz A^tA
                            double[][] normalizedProperVectors = NormalizedProperVectors(matrixA, properVectorsTransposerA_A);  // Normaliza os vetores próprios
                            double[][] eigenvaluesOfMatrix = EigenvaluesMatrixDiagonal(multiplicationTranposerA_A);  // Calcula os valores próprios da matriz A^tA
                            System.out.printf("-> Digite o número de eigenfaces que pretende usar para a reconstrução (K) (1 - %d): ", globalMatrix[0].length);  // Solicita o número de eigenfaces
                            int kValue = FindKValue(matrixA);  // Obtém o valor de K
                            double[][] multiplicationMatrixI_K = MultiplicationMatrixFunction4I_K(kValue, eigenvaluesOfMatrix, normalizedProperVectors);  // Multiplica a matriz
                            double[] summationVector = SummationArrayWithColumnsMatrix(meanVector, multiplicationMatrixI_K, kValue);  // Calcula o vetor de soma
                            double[][] finalMatrix = ConvertArrayToSquareMatrix(summationVector);  // Converte o vetor de soma numa matriz quadrada
                            String directoryNameGeneratedImages = "OutPut";  // Define o nome do diretório para as imagens geradas
                            CreateDirectory(directoryNameGeneratedImages, true, "");  // Cria o diretório de imagens geradas
                            String textTittleMatrixOriginal = "Matriz gerada original com " + kValue + " eigenfaces:";  // Título para a matriz original
                            String filePathNameMatrixOriginal = directoryNameGeneratedImages + "\\MatrizGeradaOriginal_K" + kValue + ".csv";  // Caminho para a matriz original
                            String textTittleMatrixTransformed = "Matriz gerada transformada com " + kValue + " eigenfaces:";  // Título para a matriz transformada
                            String filePathNameMatrixTransformed = directoryNameGeneratedImages + "\\MatrizGeradaTransformada_K" + kValue + ".csv";  // Caminho para a matriz transformada
                            String pathFileImage = directoryNameGeneratedImages + "\\ImagemGerada_K" + kValue + ".jpg";  // Caminho para a imagem gerada
                            PrintMatrices_OT_Image_IfNecessary_Error(finalMatrix, textTittleMatrixOriginal, filePathNameMatrixOriginal, textTittleMatrixTransformed, filePathNameMatrixTransformed, pathFileImage, -1);  // Imprime a matriz e cria a imagem
                        }
                    } else PrintErrorMessageFilterFile("InvalidDirectory", true, null, pathOfDirectory);  // Exibe mensagem de erro se o diretório for inválido
                }
                break;
                case 0: {
                    System.out.println("\nFim do programa. ");  // Exibe mensagem de fim de programa
                }
                break;
            }
            PrinterFinishFunction(option);  // Exibe mensagem de fim de função
        }
    }

    private static void NonInteractiveMode(String[] args) throws IOException {
        // Inicializa as variáveis para armazenar os valores dos argumentos
        int option = -1;  // variável para armazenar a opção
        int kValue = -1;  // variável para armazenar o valor k
        String pathOfFileCSV = "";  // caminho para o arquivo CSV de entrada
        String pathOfDirectory = "";  // caminho para o diretório onde os arquivos estão localizados
        String outPutFileName = "";  // nome do arquivo de saída onde os resultados serão salvos
        // Loop para processar os argumentos da linha de comando
        for (int i = 0; i < args.length; i++) {  // reitera os argumentos passados ao programa
            switch (args[i]) {  // verifica cada argumento individualmente
                case "-f":  // caso o argumento seja "-f"
                    option = Integer.parseInt(args[++i]);  // a próxima posição contém o valor da opção, assume um inteiro
                    break;
                case "-k":  // caso o argumento seja "-k"
                    kValue = Integer.parseInt(args[++i]);  // a próxima posição contém o valor de k, assume um inteiro
                    break;
                case "-i":  // caso o argumento seja "-i"
                    pathOfFileCSV = args[++i];  // a próxima posição contém o caminho do arquivo CSV, atribui à variável
                    break;
                case "-d":  // caso o argumento seja "-d"
                    pathOfDirectory = args[++i];  // a próxima posição contém o caminho do diretório, atribui à variável
                    break;
                default:  // caso o argumento não seja nenhum dos casos anteriores
                    if (i == args.length - 1) {  // se for o último argumento
                        outPutFileName = args[i];  // esse argumento será o nome do arquivo de saída
                    }
                    break;
            }
        }
        // Criação do arquivo de saída
        File outputFile = new File(outPutFileName);  // cria o objeto de arquivo usando o nome fornecido
        // Verifica se o arquivo de saída já existe. Se existir, elimina e recria o arquivo
        if (outputFile.delete()) {
            outputFile.createNewFile();  // elimina o arquivo existente e cria um novo arquivo
        }
        // Cria um PrintWriter para escrever no arquivo de saída
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile, true));  // true significa que vai adicionar ao arquivo
        switch (option) {
            case 1: {
                pathOfFileCSV = pathOfFileCSV.trim();  // Remove espaços adicionais no caminho do arquivo CSV
                File fileCSV = new File(pathOfFileCSV);  // Atribui o caminho a uma variável ficheiro
                if(fileCSV.isFile() && pathOfFileCSV.endsWith(".csv")) {  // Verifica se é um ficheiro válido
                    if (MatrixIsSquare(pathOfFileCSV)) {  // Verifica se a matriz é quadrada
                        int sizeOfMatrix = SizeOfMatrixComma(pathOfFileCSV);  // Obtém o tamanho da matriz a partir do arquivo CSV
                        double[][] matrix = ReadMatrix(sizeOfMatrix, pathOfFileCSV);  // Lê a matriz a partir do arquivo CSV
                        if (MatrixIsSymmetrical(matrix)) {  // Verifica se a matriz é simétrica
                            double[][] eigenvaluesOfMatrix = EigenvaluesMatrixDiagonal(matrix);  // Calcula os valores próprios da matriz
                            double[][] eigenvectorsOfMatrix = EigenvectorsMatrix(matrix);  // Calcula os vetores próprios da matriz
                            kValue = kValue == -1 || kValue > sizeOfMatrix ? sizeOfMatrix : kValue;  // Define o valor de k (número de valores/vetores próprios a ser usado)
                            double[][] compressedMatrix = CompressorOfMatrix(eigenvectorsOfMatrix, eigenvaluesOfMatrix, kValue);  // Realiza a compressão da matriz
                            double absoluteError = CalculateAbsoluteError(matrix, compressedMatrix);  // Calcula o erro absoluto entre a matriz original e a matriz comprimida
                            String outputDirectoryName = "Output";  // Define o nome do diretório de saída
                            CreateDirectory(outputDirectoryName, false, outPutFileName);  // Cria o diretório de saída se não existir
                            String textEigenValuesMatrix = "-> Valores próprios usados na decomposição:";  // Define o título para os valores próprios
                            PrintMatrixFilterFile(textEigenValuesMatrix, eigenvaluesOfMatrix, outPutFileName, -1, kValue, nDigitsMaxMatrixNum(eigenvaluesOfMatrix), false, false);  // Imprime os valores próprios no arquivo de saída
                            String textEigenVectorsMatrix = "\n-> Vetores próprios usados na decomposição:";  // Define o título para os vetores próprios
                            PrintMatrixFilterFile(textEigenVectorsMatrix, eigenvectorsOfMatrix, outPutFileName, -1, kValue, nDigitsMaxMatrixNum(eigenvectorsOfMatrix), false, false);  // Imprime os vetores próprios no arquivo de saída
                            String textTittleCompressedMatrix = "\n-> Matriz reconstruída com " + kValue + " valores/vetores próprios:";  // Define o título para a matriz comprimida
                            PrintMatrixFilterFile(textTittleCompressedMatrix, compressedMatrix, outPutFileName, absoluteError, kValue, nDigitsMaxMatrixNum(compressedMatrix), false, false);  // Imprime a matriz comprimida no arquivo de saída
                            printWriter.printf("\n-> Nº valores e vetores próprios: %d\n", kValue);  // Imprime no terminal o número de valores e vetores próprios usados
                            String textCompressedMatrix = "Matriz reconstruída com " + kValue + " valores/vetores próprios:";  // Define o título para a matriz comprimida
                            String outputFileNameMatrixReconstructed = outputDirectoryName + "\\MatrizReconstruidaComKVsProprios.csv";  // Define o nome do arquivo CSV para a matriz comprimida
                            PrintMatrixFilterFile(textCompressedMatrix, compressedMatrix, outputFileNameMatrixReconstructed, -1, compressedMatrix[0].length, nDigitsMaxMatrixNum(compressedMatrix), true, true);  // Imprime a matriz comprimida no arquivo CSV
                            printWriter.printf("\nFicheiros adicionados/alterados em: %s", outputDirectoryName);  // Imprime o diretório onde os arquivos foram adicionados ou alterados
                        } else PrintErrorMessage("UnsymmetricalMatrix", false, outputFile);  // Exibe mensagem de erro se a matriz não for simétrica
                    } else PrintErrorMessage("InvalidSize", false, outputFile);  // Exibe mensagem de erro se a matriz não for quadrada
                } else PrintErrorMessage("InvalidFile",false,outputFile);  // Exibe mensagem de erro se o ficheiro for inválido
            }
            break;
            case 2: {
                pathOfDirectory = pathOfDirectory.trim();  // Remove espaços adicionais no caminho do diretório
                int nFilesCSVDirectory = CounterFilesCSVOnDirectory(pathOfDirectory);  // Conta o número de arquivos CSV no diretório especificado
                if (nFilesCSVDirectory != 0) {  // Verifica se há arquivos CSV no diretório
                    String[] nameListValidFiles = new String[nFilesCSVDirectory];  // Cria um array para armazenar os nomes dos arquivos CSV válidos
                    double[][] globalMatrix = ReadGlobalMatrixWithExceptions(pathOfDirectory, nFilesCSVDirectory, nameListValidFiles, outputFile, false);  // Lê a matriz global a partir dos arquivos CSV
                    if (globalMatrix[0][0] != -1) {  // Verifica se a matriz foi carregada corretamente
                        double[] meanVector = MeanVector(globalMatrix);  // Calcula o vetor médio (µ) da matriz global
                        double[][] matrixA = ConstructionOfMatrixA(globalMatrix, meanVector);  // Constrói a matriz A a partir da matriz global e do vetor médio
                        double[][] multiplicationTranposerA_A = MultiplicationMatrix(MatrixTransposer(matrixA), matrixA);  // Multiplica a transposta de A por A
                        double[][] properVectorsTransposerA_A = EigenvectorsMatrix(multiplicationTranposerA_A);  // Calcula os vetores próprios da matriz A^T * A
                        double[][] normalizedProperVectors = NormalizedProperVectors(matrixA, properVectorsTransposerA_A);  // Normaliza os vetores próprios
                        kValue = kValue == -1 || kValue > globalMatrix[0].length ? globalMatrix[0].length : kValue;  // Define o valor de k
                        double[][] globalWeightOfImages = globalWeight(kValue, matrixA, normalizedProperVectors);  // Calcula os pesos globais das imagens
                        double[][] imageRestorationK = ImageRestoration(kValue, meanVector, matrixA, normalizedProperVectors, globalWeightOfImages);  // Restaura a imagem usando os k valores/vetores próprios
                        String directoryNameEigenFaces = "Eigenfaces";  // Define o nome do diretório para as eigenfaces
                        String directoryNameRestorationImages = "ImagensReconstruidas";  // Define o nome do diretório para as imagens reconstruídas
                        CreateDirectory(directoryNameEigenFaces, false, outPutFileName);  // Cria o diretório para as eigenfaces
                        CreateDirectory(directoryNameRestorationImages, false, outPutFileName);  // Cria o diretório para as imagens reconstruídas
                        String textMeanVector = "-> Vetor µ:";  // Define o título para o vetor médio
                        double[][] horizontalMeanVector = MatrixTransposer(ConvertArrayToMatrixVertical(meanVector));  // Transforma o vetor médio  numa matriz horizontal
                        PrintMatrixFilterFile(textMeanVector, horizontalMeanVector, outPutFileName, -1, horizontalMeanVector[0].length, nDigitsMaxMatrixNum(ConvertArrayToMatrixVertical(meanVector)), false, false);  // Imprime o vetor médio no arquivo de saída
                        String textMultTA_A = "\n-> Matriz A^tA:";  // Define o título para a matriz A^T * A
                        PrintMatrixFilterFile(textMultTA_A, multiplicationTranposerA_A, outPutFileName, -1, multiplicationTranposerA_A[0].length, nDigitsMaxMatrixNum(multiplicationTranposerA_A), false, false);  // Imprime a matriz A^T * A no arquivo de saída
                        printWriter.println("\n-> Nº eigenfaces utilizadas: " + kValue);  // Imprime o número de eigenfaces utilizadas no arquivo de saída
                        printWriter.println("\n-> Vetores de pesos (w) usados na reconstrução das imagens:");  // Imprime a lista de vetores de pesos usados na reconstrução
                        printWriter.print("\nImagem:  ");  // Imprime o cabeçalho para os índices das imagens
                        for (int indexImagem = 1; indexImagem <= globalWeightOfImages[0].length; indexImagem++) {  // Percorre as imagens
                            printWriter.printf("%8d |", indexImagem);  // Imprime o índice da imagem
                        }
                        printWriter.print("\n         ");  // Cria espaço para o próximo cabeçalho
                        for (int indexImagem = 1; indexImagem <= globalWeightOfImages[0].length; indexImagem++) {  // Percorre novamente as imagens
                            printWriter.print("---------|");  // Imprime uma linha separadora
                        }
                        for (int indexRow = 0; indexRow < globalWeightOfImages.length; indexRow++) {  // Percorre as linhas da matriz de pesos das imagens
                            printWriter.print("\n         ");  // Cria espaço para os valores
                            for (int indexColumn = 0; indexColumn < globalWeightOfImages[0].length; indexColumn++) {  // Percorre as colunas da matriz
                                printWriter.printf("%8.2f |", globalWeightOfImages[indexRow][indexColumn]);  // Imprime o valor do peso
                            }
                        }
                        printWriter.println();  // Imprime uma linha nova após a tabela de pesos
                        for (int nImage = 0; nImage < globalMatrix[0].length; nImage++) {  // Percorre cada imagem
                            double[][] restorationMatrixK = ConvertArrayToSquareMatrix(ConvertColumnMatrixToArray(imageRestorationK, nImage));  // Converte a imagem restaurada para uma matriz quadrada
                            double[][] matrixOriginalImageK = ConvertArrayToSquareMatrix(ConvertColumnMatrixToArray(globalMatrix, nImage));  // Converte a imagem original para uma matriz quadrada
                            double absoluteErrorMatrixImage = CalculateAbsoluteError(matrixOriginalImageK, restorationMatrixK);  // Calcula o erro absoluto entre a imagem original e a restaurada
                            String textTittleMatrixOriginal = "Matriz reconstruída original de " + nameListValidFiles[nImage] + " com " + kValue + " eigenfaces:";  // Define o título para a matriz reconstruída original
                            String filePathNameMatrixOriginal = directoryNameEigenFaces + "\\" + nameListValidFiles[nImage] + "_MatrizReconstruidaOriginal_K" + kValue + ".csv";  // Define o caminho do arquivo CSV para a matriz original
                            String textTittleMatrixTransformed = "Matriz reconstruída transformada de " + nameListValidFiles[nImage] + " com " + kValue + " eigenfaces:";  // Define o título para a matriz transformada
                            String filePathNameMatrixTransformed = directoryNameEigenFaces + "\\" + nameListValidFiles[nImage] + "_MatrizReconstruidaTransformada_K" + kValue + ".csv";  // Define o caminho do arquivo CSV para a matriz transformada
                            String pathFileImage = directoryNameRestorationImages + "\\" + nameListValidFiles[nImage] + "_ImagemReconstruida_K" + kValue + ".jpg";  // Define o caminho do arquivo de imagem restaurada
                            PrintMatrices_OT_Image_IfNecessary_Error(restorationMatrixK, textTittleMatrixOriginal, filePathNameMatrixOriginal, textTittleMatrixTransformed, filePathNameMatrixTransformed, pathFileImage, absoluteErrorMatrixImage);  // Imprime as matrizes e a imagem restaurada
                        }
                        printWriter.printf("\nFicheiros adicionados em: %s & %s", directoryNameEigenFaces, directoryNameRestorationImages);  // Imprime os diretórios onde os arquivos foram adicionados
                    }
                } else PrintErrorMessageFilterFile("InvalidDirectory", false, outputFile, pathOfDirectory);  // Exibe uma mensagem de erro se o diretório não for válido
            }
            break;
            case 3: {
                pathOfDirectory = pathOfDirectory.trim();  // Remove espaços extras do caminho do diretório
                int nFilesCSVDirectory = CounterFilesCSVOnDirectory(pathOfDirectory);  // Conta o número de arquivos CSV no diretório especificado
                if (nFilesCSVDirectory != 0) {  // Verifica se há arquivos CSV no diretório
                    String[] nameListValidFiles = new String[nFilesCSVDirectory];  // Cria um array para armazenar os nomes dos arquivos CSV válidos
                    double[][] globalMatrix = ReadGlobalMatrixWithExceptions(pathOfDirectory, nFilesCSVDirectory, nameListValidFiles, outputFile, false);  // Lê a matriz global a partir dos arquivos CSV no diretório
                    if (globalMatrix[0][0] != -1) {  // Verifica se a matriz foi carregada corretamente
                        pathOfFileCSV = pathOfFileCSV.trim();  // Remove espaços adicionais do caminho do arquivo CSV
                        File pathOfFileName = new File(pathOfFileCSV);  // Cria um objeto File com o caminho do arquivo CSV
                        if (pathOfFileName.isFile() && (pathOfFileCSV.toLowerCase().endsWith(".csv"))) {  // Verifica se o caminho corresponde a um arquivo CSV válido
                            if (MatrixIsSquare(pathOfFileCSV)) {  // Verifica se a matriz do arquivo CSV é quadrada
                                int sizeNewImageMatrix = SizeOfMatrixComma(pathOfFileCSV);  // Obtém o tamanho da matriz da nova imagem a partir do arquivo CSV
                                if (sizeNewImageMatrix == (int) Math.sqrt(globalMatrix.length)) {  // Verifica se o tamanho da matriz da nova imagem é compatível com o tamanho da matriz global
                                    double[][] matrixOfNewImage = ReadMatrix(sizeNewImageMatrix, pathOfFileCSV);  // Lê a matriz da nova imagem
                                    if (MatrixWithValuesWithinLimits(matrixOfNewImage, 0, 255)) {  // Verifica se os valores da matriz da nova imagem estão dentro do intervalo [0, 255]
                                        double[] meanVector = MeanVector(globalMatrix);  // Calcula o vetor médio (µ) da matriz global
                                        double[][] matrixA = ConstructionOfMatrixA(globalMatrix, meanVector);  // Constrói a matriz A a partir da matriz global e do vetor médio
                                        double[][] multiplicationTranposerA_A = MultiplicationMatrix(MatrixTransposer(matrixA), matrixA);  // Multiplica a transposta de A por A
                                        double[][] properVectorsTransposerA_A = EigenvectorsMatrix(multiplicationTranposerA_A);  // Calcula os vetores próprios da matriz A^T * A
                                        double[][] normalizedProperVectors = NormalizedProperVectors(matrixA, properVectorsTransposerA_A);  // Normaliza os vetores próprios
                                        kValue = kValue == -1 || kValue > globalMatrix[0].length ? globalMatrix[0].length : kValue;  // Define o valor de k (número de eigenfaces a ser utilizado)
                                        double[][] matrixOhmNewImage = CalculateOhm(matrixOfNewImage, meanVector, kValue, normalizedProperVectors);  // Calcula o vetor ω (omega) da nova imagem
                                        String directoryImageIdentification = "Identificacao";  // Define o nome do diretório para a identificação da imagem
                                        CreateDirectory(directoryImageIdentification, false, outPutFileName);  // Cria o diretório para a identificação da imagem
                                        int closestImage = CalculateLessEuclideanDistance(matrixOhmNewImage, globalMatrix, meanVector, kValue, normalizedProperVectors, nameListValidFiles, outPutFileName, false);  // Calcula a imagem mais próxima usando distância euclidiana
                                        printWriter.println("\n-> Nº eigenfaces utilizadas: " + kValue);  // Imprime o número de eigenfaces utilizadas
                                        String pathNameFileImage = directoryImageIdentification + "\\ImagemIdentificacao_K" + kValue + ".jpg";  // Define o caminho do arquivo da imagem identificada
                                        String textMatrixOhmNewImage = "\n-> Vetor (omega) nova: ";  // Título para o vetor ω (omega) da nova imagem
                                        PrintMatrixFilterFile(textMatrixOhmNewImage, matrixOhmNewImage, outPutFileName, -1, matrixOhmNewImage[0].length, nDigitsMaxMatrixNum(matrixOhmNewImage), false, false);  // Imprime o vetor ω (omega) no arquivo de saída
                                        CreateImageWithMatrixForFile(globalMatrix, closestImage, pathNameFileImage);  // Cria a imagem identificada a partir da matriz
                                        printWriter.printf("\nFicheiros adicionados/alterados em: %s\n", directoryImageIdentification);  // Imprime a mensagem indicando onde os arquivos foram adicionados ou alterados
                                    } else PrintErrorMessage("InvalidValue", false, outputFile);  // Exibe mensagem de erro caso os valores na matriz da nova imagem estejam fora dos limites
                                } else PrintErrorMessage("InvalidSize", false, outputFile);  // Exibe mensagem de erro se o tamanho da matriz da nova imagem não for compatível
                            } else PrintErrorMessage("InvalidSize", false, outputFile);  // Exibe mensagem de erro se a matriz do arquivo CSV não for quadrada
                        } else PrintErrorMessage("InvalidFile", false, outputFile);  // Exibe mensagem de erro se o arquivo não for um arquivo CSV válido
                    }
                } else PrintErrorMessageFilterFile("InvalidDirectory", false, outputFile, pathOfDirectory);  // Exibe mensagem de erro se o diretório não tiver arquivos CSV válidos
            }
            break;
            case 4: {
                pathOfDirectory = pathOfDirectory.trim();  // Remove espaços extras do caminho do diretório
                int nFilesCSVDirectory = CounterFilesCSVOnDirectory(pathOfDirectory);  // Conta o número de arquivos CSV no diretório especificado
                if (nFilesCSVDirectory != 0) {  // Verifica se há arquivos CSV no diretório
                    String[] nameListValidFiles = new String[nFilesCSVDirectory];  // Cria um array para armazenar os nomes dos arquivos CSV válidos
                    double[][] globalMatrix = ReadGlobalMatrixWithExceptions(pathOfDirectory, nFilesCSVDirectory, nameListValidFiles, outputFile, false);  // Lê a matriz global a partir dos arquivos CSV no diretório
                    if (globalMatrix[0][0] != -1) {  // Verifica se a matriz foi carregada corretamente
                        double[] meanVector = MeanVector(globalMatrix);  // Calcula o vetor médio (µ) da matriz global
                        double[][] matrixA = ConstructionOfMatrixA(globalMatrix, meanVector);  // Constrói a matriz A a partir da matriz global e do vetor médio
                        double[][] multiplicationTranposerA_A = MultiplicationMatrix(MatrixTransposer(matrixA), matrixA);  // Multiplica a transposta de A por A
                        double[][] properVectorsTransposerA_A = EigenvectorsMatrix(multiplicationTranposerA_A);  // Calcula os vetores próprios da matriz A^T * A
                        double[][] normalizedProperVectors = NormalizedProperVectors(matrixA, properVectorsTransposerA_A);  // Normaliza os vetores próprios
                        double[][] eigenvaluesOfMatrix = EigenvaluesMatrixDiagonal(multiplicationTranposerA_A);  // Calcula os valores próprios da matriz A^T * A
                        String directoryNameGeneratedImages = "OutPut";  // Define o nome do diretório onde as imagens geradas serão salvas
                        CreateDirectory(directoryNameGeneratedImages, false, outPutFileName);  // Cria o diretório onde as imagens serão salvas
                        kValue = kValue == -1 || kValue > globalMatrix[0].length ? globalMatrix[0].length : kValue;  // Define o valor de k (número de eigenfaces a ser utilizado)
                        double[][] multiplicationMatrixI_K = MultiplicationMatrixFunction4I_K(kValue, eigenvaluesOfMatrix, normalizedProperVectors);  // Calcula a multiplicação de I * K
                        double[] summationVector = SummationArrayWithColumnsMatrix(meanVector, multiplicationMatrixI_K, kValue);  // Soma o vetor médio com a multiplicação
                        double[][] finalMatrix = ConvertArrayToSquareMatrix(summationVector);  // Converte o vetor somado para uma matriz quadrada
                        String textTittleMatrixOriginal = "Matriz gerada original com " + kValue + " eigenfaces:";  // Título para a matriz original gerada
                        String filePathNameMatrixOriginal = directoryNameGeneratedImages + "\\MatrizGeradaOriginal_K" + kValue + ".csv";  // Caminho do arquivo da matriz original gerada
                        String textTittleMatrixTransformed = "Matriz gerada transformada com " + kValue + " eigenfaces:";  // Título para a matriz transformada gerada
                        String filePathNameMatrixTransformed = directoryNameGeneratedImages + "\\MatrizGeradaTransformada_K" + kValue + ".csv";  // Caminho do arquivo da matriz transformada gerada
                        String pathFileImage = directoryNameGeneratedImages + "\\ImagemGerada_K" + kValue + ".jpg";  // Caminho do arquivo de imagem gerada
                        PrintMatrices_OT_Image_IfNecessary_Error(finalMatrix, textTittleMatrixOriginal, filePathNameMatrixOriginal, textTittleMatrixTransformed, filePathNameMatrixTransformed, pathFileImage, -1);  // Imprime a matriz e a imagem gerada no arquivo de saída
                        printWriter.printf("Ficheiros adicionados/alterados em: %s\n", directoryNameGeneratedImages);  // Imprime mensagem indicando onde os arquivos foram adicionados ou alterados
                    }
                } else PrintErrorMessageFilterFile("InvalidDirectory", false, outputFile, pathOfDirectory);  // Exibe mensagem de erro se o diretório não contiver arquivos CSV válidos
            }
            break;
            case 0: {
                printWriter.println("\nFim do programa. ");  // Imprime a mensagem "Fim do programa." no arquivo de saída indicando o término do programa
            }
            break;
        }
        printWriter.close();
    }

    private static double[][] ConvertArrayToMatrixVertical(double[] array) {  // Metodo que converte um array unidimensional em uma matriz vertical (coluna)
        double[][] matrix = new double[array.length][1];  // Cria uma nova matriz com o número de linhas igual ao tamanho do array e 1 coluna
        for (int index = 0; index < array.length; index++) {  // Percorre todos os elementos do array
            matrix[index][0] = array[index];  // Atribui o valor do array na posição correspondente da matriz
        }
        return matrix;  // Retorna a matriz vertical (coluna)
    }

    private static void PrintMatrices_OT_Image_IfNecessary_Error(double[][] matrixOriginal, String textMatrixOriginal, String filePathMatrixOriginal, String textMatrixTransformed, String filePathMatrixTransformed, String pathFileImage, double absoluteError) throws IOException {
        PrintMatrixFilterFile(textMatrixOriginal, matrixOriginal, filePathMatrixOriginal, absoluteError, matrixOriginal[0].length, nDigitsMaxMatrixNum(matrixOriginal), true, true);  // Imprime a matriz original em formato CSV
        BufferedImage generatedImage;  // Declara uma variável para armazenar a imagem gerada
        if (!MatrixWithValuesWithinLimits(matrixOriginal, MINCOLORPIXEL, MAXCOLORPIXEL)) {  // Se os valores estiverem fora do intervalo (0-255)
            double[][] finalMatrixTransformed = EigenfacesWithInPossibleLimits(matrixOriginal);  // Aplica a transformação para ajustar os valores da matriz
            PrintMatrixFilterFile(textMatrixTransformed, finalMatrixTransformed, filePathMatrixTransformed, absoluteError, finalMatrixTransformed[0].length, nDigitsMaxMatrixNum(finalMatrixTransformed), true, true);  // Imprime a matriz transformada em formato CSV
            generatedImage = ImagesReconstruction(finalMatrixTransformed);  // Reconstrói a imagem com a matriz transformada
        } else {
            generatedImage = ImagesReconstruction(matrixOriginal);  // Reconstrói a imagem com a matriz original se os valores estiverem dentro do intervalo
        }
        ImageIO.write(generatedImage, "jpg", new File(pathFileImage));  // Salva a imagem gerada no formato JPG
    }

    private static double[] ConvertColumnMatrixToArray(double[][] matrix, int nColumn) {  // Metodo para converter uma coluna de uma matriz para um array
        double[] array = new double[matrix.length];  // Cria um array com o mesmo número de linhas da matriz
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Reitera sobre cada linha da matriz
            array[indexRow] = matrix[indexRow][nColumn];  // Atribui o valor da coluna específica à posição correspondente do array
        }
        return array;  // Retorna o array contendo os valores da coluna
    }

    private static boolean MatrixWithValuesWithinLimits(double[][] matrix, int minLimit, int maxLimit) {  // Metodo que verifica se todos os valores de uma matriz estão dentro de um intervalo especificado
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Reitera sobre cada linha da matriz
            for (int indexColumn = 0; indexColumn < matrix[indexRow].length; indexColumn++) {  // Reitera sobre cada coluna da linha
                if (matrix[indexRow][indexColumn] < minLimit || matrix[indexRow][indexColumn] > maxLimit) {  // Verifica se o valor da célula está fora dos limites
                    return false;  // Se algum valor estiver fora do intervalo, retorna false
                }
            }
        }
        return true;  // Se todos os valores estiverem dentro dos limites, retorna true
    }

    private static double[][] EigenfacesWithInPossibleLimits(double[][] eigenfacesMatrix) {  // Metodo para ajustar os valores da matriz de eigenfaces dentro de limites possíveis
        double min = MinOrMaxValueOfMatrix(eigenfacesMatrix, "min");  // Obtém o valor mínimo da matriz de eigenfaces
        double max = MinOrMaxValueOfMatrix(eigenfacesMatrix, "max");  // Obtém o valor máximo da matriz de eigenfaces
        double subtractionAndMultiplication = MAXCOLORPIXEL / (max - min);  // Colocar a matriz em forma de escada
        for (int indexRow = 0; indexRow < eigenfacesMatrix.length; indexRow++) {  // Reitera sobre cada linha da matriz
            for (int indexColumn = 0; indexColumn < eigenfacesMatrix[0].length; indexColumn++) {  // Reitera sobre cada coluna da linha
                eigenfacesMatrix[indexRow][indexColumn] = subtractionAndMultiplication * (eigenfacesMatrix[indexRow][indexColumn] - min);  // Aplica o ajuste aos valores da matriz
            }
        }
        return eigenfacesMatrix;  // Retorna a matriz de eigenfaces com valores ajustados
    }

    private static double GenerateRandomValueInLimits(double minLimit, double maxLimit) {  // Metodo para gerar um valor aleatório dentro de limites especificados
        Random random = new Random();  // Cria um objeto aleatório para gerar números aleatórios
        return random.nextInt((int) maxLimit - (int) minLimit + 1) + minLimit;  // Gera um número aleatório dentro do intervalo e retorna-o
    }

    private static double[][] MultiplicationMatrixFunction4I_K(int kValue, double[][] eigenvaluesOfMatrix, double[][] normalizedProperVectors) {  // Metodo para multiplicar a matriz de vetores próprios normalizados com valores aleatórios ajustados pela raiz quadrada dos valores próprios
        for (int iValue = 1; iValue < kValue; iValue++) {  // Reitera sobre os valores de 1 até k-1
            double properValueI = eigenvaluesOfMatrix[iValue - 1][iValue - 1];  // Obtém o valor próprio da matriz
            double maxLimit = Math.sqrt(properValueI);  // Calcula o limite superior, a raiz quadrada do valor próprio
            double minLimit = -maxLimit;  // Define o limite inferior como o negativo do limite superior
            double randomNumIntervalW = GenerateRandomValueInLimits(minLimit, maxLimit);  // Gera um número aleatório dentro do intervalo calculado
            for (int indexRow = 0; indexRow < normalizedProperVectors.length; indexRow++) {  // Reitera sobre as linhas dos vetores próprios normalizados
                normalizedProperVectors[indexRow][iValue - 1] *= randomNumIntervalW;  // Multiplica o valor do vetor próprio normalizado por um número aleatório
            }
        }
        return normalizedProperVectors;  // Retorna a matriz modificada de vetores próprios normalizados
    }

    private static double[][] ConvertArrayToSquareMatrix(double[] array) {  // Metodo para converter um array unidimensional em uma matriz quadrada
        int sizeOfMatrix = (int) Math.sqrt(array.length);  // Calcula o tamanho da matriz quadrada, a raiz quadrada do comprimento do array
        double[][] squareMatrix = new double[sizeOfMatrix][sizeOfMatrix];  // Cria uma nova matriz quadrada com o tamanho calculado
        for (int indexRow = 0; indexRow < sizeOfMatrix; indexRow++) {  // Reitera sobre as linhas da matriz
            for (int indexColumn = 0; indexColumn < sizeOfMatrix; indexColumn++) {  // Reitera sobre as colunas da matriz
                squareMatrix[indexRow][indexColumn] = array[indexRow * sizeOfMatrix + indexColumn];  // Preenche a matriz quadrada com os valores do array, mapeando os índices corretamente
            }
        }
        return squareMatrix;  // Retorna a matriz quadrada preenchida
    }

    private static double[] SummationArrayWithColumnsMatrix(double[] array, double[][] matrix, int indexLastColumn) {  // Metodo que soma os elementos das colunas de uma matriz em um array
        for (int indexColumn = 0; indexColumn < indexLastColumn; indexColumn++) {  // Reitera sobre as colunas da matriz até ao índice especificado (indexLastColumn)
            for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Reitera sobre as linhas da matriz
                array[indexRow] += matrix[indexRow][indexColumn];  // Soma o valor da célula da matriz ao elemento correspondente no array
            }
        }
        return array;  // Retorna o array com os valores somados
    }

    private static boolean MatrixIsSquare(String pathOfFile) throws FileNotFoundException {  // Metodo que verifica se a matriz é quadrada a partir de um ficheiro CSV
        Scanner scFile = new Scanner(new File(pathOfFile));  // Cria um Scanner para ler o ficheiro CSV
        String[] firstLineOfMatrix;  // Variável para armazenar os elementos da primeira linha da matriz
        String lineOfMatrix;  // Variável para armazenar as linhas subsequentes da matriz
        if (scFile.hasNextLine()) {  // Verifica se existe pelo menos uma linha no ficheiro
            firstLineOfMatrix = scFile.nextLine().trim().split(",");  // Lê a primeira linha e divide os elementos por vírgulas
        } else return false;  // Se não houver linhas, retorna falso
        int counterNumberLines = 1;  // Contador para armazenar o número de linhas da matriz
        while (scFile.hasNextLine()) {  // Enquanto houver mais linhas no ficheiro
            lineOfMatrix = scFile.nextLine();  // Lê a linha atual
            if (lineOfMatrix.contains(",")) {  // Se a linha contiver vírgulas, conta como uma linha de matriz válida
                counterNumberLines++;
            }
        }
        return counterNumberLines < MAXSIZEMATRIX && firstLineOfMatrix.length == counterNumberLines;  // Verifica se o número de linhas é igual ao número de colunas e se é menor que o tamanho máximo da matriz
    }

    private static int SizeOfMatrixComma(String pathOfFile) throws FileNotFoundException {  // Metodo que retorna o número de colunas de uma matriz armazenada num ficheiro CSV
        Scanner scFile = new Scanner(new File(pathOfFile));  // Cria um Scanner para ler o ficheiro CSV
        String[] lineOfMatrix;  // Variável para armazenar os elementos de uma linha da matriz
        if (scFile.hasNextLine()) {  // Verifica se o ficheiro contém ao menos uma linha
            lineOfMatrix = scFile.nextLine().trim().split(",");  // Lê a primeira linha e divide os elementos por vírgulas
        } else return 0;  // Se não houver linhas, retorna 0, indicando que a matriz está vazia
        return lineOfMatrix.length;  // Retorna o número de colunas da primeira linha (que será o número de colunas de toda a matriz)
    }

    private static boolean MatrixIsSymmetrical(double[][] matrix) {  // Metodo que verifica se uma matriz é simétrica
        double[][] transposedMatrix = MatrixTransposer(matrix);  // Obtém a matriz transposta
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Reitera pelas linhas da matriz
            for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Reitera pelas colunas da matriz
                if (matrix[indexRow][indexColumn] != transposedMatrix[indexRow][indexColumn]) {  // Verifica se o elemento da matriz original é diferente do correspondente na transposta
                    return false;  // Se encontrar qualquer diferença, a matriz não é simétrica
                }
            }
        }
        return true;  // Se todos os elementos corresponderem, a matriz é simétrica
    }

    private static double[][] ReadMatrix(int sizeMatrix, String pathOfFile) throws FileNotFoundException {  // Metodo que lê uma matriz de um arquivo CSV
        Scanner scFile = new Scanner(new File(pathOfFile));  // Cria um scanner para ler o arquivo especificado pelo caminho
        double[][] matrix = new double[sizeMatrix][sizeMatrix];  // Cria uma matriz de tamanho especificado
        String[] temporaryArrayString;  // Array temporário para armazenar cada linha como String
        double[] temporaryArrayDouble;  // Array temporário para armazenar os valores numéricos da linha
        for (int indexRow = 0; indexRow < sizeMatrix; indexRow++) {  // Reitera pelas linhas da matriz
            temporaryArrayString = scFile.nextLine().split(",");  // Lê a linha atual e divide os valores por vírgula
            temporaryArrayDouble = ConvertStringArrayToDoubleArray(temporaryArrayString);  // Converte a linha de String para double
            for (int indexColumn = 0; indexColumn < temporaryArrayDouble.length; indexColumn++) {  // Reitera pelas colunas da matriz
                matrix[indexRow][indexColumn] = temporaryArrayDouble[indexColumn];  // Atribui os valores à matriz
            }
        }
        return matrix;  // Retorna a matriz lida do arquivo
    }

    private static double[][] ReadMatrixLimits(int sizeMatrix, String pathOfFile, int minLimit, int maxLimit) throws FileNotFoundException {  // Metodo para ler uma matriz com valores limitados
        Scanner scFile = new Scanner(new File(pathOfFile));  // Cria um scanner para ler o arquivo especificado
        double[][] matrix = new double[sizeMatrix][sizeMatrix];  // Cria uma matriz de tamanho especificado
        String[] temporaryArrayString;  // Array temporário para armazenar cada linha como String
        double[] temporaryArrayDouble;  // Array temporário para armazenar os valores numéricos da linha
        for (int indexRow = 0; indexRow < sizeMatrix; indexRow++) {  // Reitera pelas linhas da matriz
            temporaryArrayString = scFile.nextLine().split(",");  // Lê a linha atual e divide os valores por vírgula
            temporaryArrayDouble = ConvertStringArrayToDoubleArray(temporaryArrayString);  // Converte a linha de String para double
            for (int indexColumn = 0; indexColumn < temporaryArrayDouble.length; indexColumn++) {  // Reitera pelas colunas da matriz
                double actualValue = temporaryArrayDouble[indexColumn];  // Obtém o valor atual da célula
                if (actualValue < minLimit || actualValue > maxLimit) {  // Verifica se o valor está fora dos limites
                    matrix[0][0] = -1;  // Se o valor estiver fora do intervalo, define a primeira célula da matriz como -1
                    return matrix;  // Retorna a matriz com erro (-1)
                }
                matrix[indexRow][indexColumn] = actualValue;  // Atribui o valor à célula da matriz
            }
        }
        return matrix;  // Retorna a matriz lida do arquivo
    }

    private static int CounterFilesCSVOnDirectory(String pathOfDirectory) {  // Metodo para contar arquivos CSV em um diretório
        File folderPath = new File(pathOfDirectory);  // Cria um objeto File com o caminho do diretório
        File[] listOfFiles = folderPath.listFiles();  // Lista todos os arquivos no diretório
        int counterFilesCSV = 0;  // Inicia o contador de arquivos CSV
        if (folderPath.exists() && folderPath.isDirectory()) {  // Verifica se o caminho existe e é um diretório
            for (int indexFile = 0; indexFile < listOfFiles.length; indexFile++) {  // Reitera por todos os arquivos no diretório
                if (listOfFiles[indexFile].exists() && listOfFiles[indexFile].isFile() && listOfFiles[indexFile].getName().toLowerCase().endsWith(".csv")) {  // Verifica se é um arquivo CSV
                    counterFilesCSV++;  // Se for um arquivo CSV, incrementa o contador
                }
            }
        }
        return counterFilesCSV;  // Retorna o número total de arquivos CSV encontrados no diretório
    }

    private static double[][] ReadGlobalMatrixWithExceptions(String pathOfDirectory, int counterFilesCSV, String[] nameListValidFiles, File outputFile, boolean interactiveMode) throws FileNotFoundException {
        File folderPath = new File(pathOfDirectory);  // Cria um objeto File para o diretório especificado
        File[] listOfFiles = folderPath.listFiles();  // Lista todos os arquivos no diretório
        int indexFileCSV = 0;
        for (int indexFile = 0; indexFile < listOfFiles.length; indexFile++) {
            if (listOfFiles[indexFile].exists() && listOfFiles[indexFile].isFile() && listOfFiles[indexFile].getName().toLowerCase().endsWith(".csv")) {
                indexFileCSV = indexFile;  // Identifica o arquivo CSV
            }
        }
        int minLimitValue = 0;  // Limite mínimo para os valores da matriz
        int maxLimitValue = 255;  // Limite máximo para os valores da matriz
        String existingCSVFileName = listOfFiles[indexFileCSV].getName();  // Nome do arquivo CSV
        String existingCSVFilePath = listOfFiles[indexFileCSV].getAbsolutePath();  // Caminho do arquivo CSV
        if (MatrixIsSquare(existingCSVFilePath)) {  // Verifica se o arquivo CSV é uma matriz quadrada
            int sizeOfMatrix = SizeOfMatrixComma(existingCSVFilePath);  // Obtém o tamanho da matriz a partir do arquivo CSV
            double[][] globalMatrix = new double[sizeOfMatrix * sizeOfMatrix][counterFilesCSV];  // Cria a matriz global
            int indexCSV = 0;
            for (int indexFile = 0; indexFile < listOfFiles.length; indexFile++) {
                String actualFileName = listOfFiles[indexFile].getName();  // Nome do arquivo atual
                String actualFilePath = listOfFiles[indexFile].getAbsolutePath();  // Caminho do arquivo atual
                if (listOfFiles[indexFile].isFile() && actualFileName.toLowerCase().endsWith(".csv")) {  // Verifica se o arquivo é um CSV
                    if (MatrixIsSquare(actualFilePath)) {  // Verifica se a matriz do arquivo CSV é quadrada
                        double[][] matrix = ReadMatrixLimits(sizeOfMatrix, actualFilePath, minLimitValue, maxLimitValue);  // Lê a matriz do arquivo CSV com limites de valor
                        if (matrix[0][0] != -1) {  // Verifica se a leitura da matriz foi bem-sucedida
                            nameListValidFiles[indexCSV] = actualFileName.replace(".csv", "");  // Armazena o nome do arquivo válido
                            for (int indexRow = 0; indexRow < sizeOfMatrix; indexRow++) {
                                for (int indexColumn = 0; indexColumn < sizeOfMatrix; indexColumn++) {
                                    int number = indexColumn + (indexRow * sizeOfMatrix);  // Calcula a posição da matriz global
                                    globalMatrix[number][indexCSV] = matrix[indexRow][indexColumn];  // Preenche a matriz global com os valores da matriz lida
                                }
                            }
                        } else {  // Caso a matriz não tenha sido lida corretamente (valor inválido)
                            if (interactiveMode) {
                                PrintErrorMessageFilterFile("InvalidValue", true, null, actualFileName);  // Exibe a mensagem de erro interativamente
                            } else {
                                PrintErrorMessageFilterFile("InvalidValue", false, outputFile, actualFileName);  // Exibe a mensagem de erro em arquivo
                            }
                            globalMatrix[0][0] = -1;  // Indica erro na leitura da matriz
                            return globalMatrix;  // Retorna a matriz de erro
                        }
                    } else {  // Caso a matriz não seja quadrada
                        if (interactiveMode) {
                            PrintErrorMessageFilterFile("InvalidSize", true, null, actualFileName);  // Exibe a mensagem de erro interativamente
                        } else {
                            PrintErrorMessageFilterFile("InvalidSize", false, outputFile, actualFileName);  // Exibe a mensagem de erro em arquivo
                        }
                        globalMatrix[0][0] = -1;  // Indica erro na verificação do tamanho da matriz
                        return globalMatrix;  // Retorna a matriz de erro
                    }
                    indexCSV++;
                }
            }
            return globalMatrix;  // Retorna a matriz global preenchida
        } else {  // Caso a matriz inicial não seja quadrada
            if (interactiveMode) {
                PrintErrorMessageFilterFile("InvalidSize", true, null, existingCSVFileName);  // Exibe a mensagem de erro interativamente
            } else {
                PrintErrorMessageFilterFile("InvalidSize", false, outputFile, existingCSVFileName);  // Exibe a mensagem de erro em arquivo
            }
        }
        double[][] matrixValidation = new double[1][1];  // Retorna matriz de erro
        matrixValidation[0][0] = -1;  // Indica erro na leitura
        return matrixValidation;  // Retorna a matriz de erro
    }

    private static double[] ConvertStringArrayToDoubleArray(String[] array) {  // Metodo para converter array de Strings em array de doubles
        double[] convertedArray = new double[array.length];  // Cria um novo array de doubles com o mesmo tamanho do array de Strings
        for (int index = 0; index < array.length; index++) {  // Reitera sobre o array de Strings
            convertedArray[index] = Double.parseDouble(array[index]);  // Converte cada elemento de String para double e armazena no array de doubles
        }
        return convertedArray;  // Retorna o array de doubles convertido
    }

    private static double[][] EigenvectorsMatrix(double[][] matrix) {  // Metodo para calcular os vetores próprios de uma matriz
        RealMatrix matrixA = MatrixUtils.createRealMatrix(matrix);  // Converte a matriz de double para um objeto RealMatrix
        EigenDecomposition eigenDecomposition = new EigenDecomposition(matrixA);  // Realiza a decomposição em valores e vetores próprios
        RealMatrix matrixP = eigenDecomposition.getV();  // Obtém a matriz dos vetores próprios (P)
        return MatrixConverter(matrixP);  // Converte a matriz dos vetores próprios para o formato de array 2D e retorna
    }

    private static double[][] EigenvaluesMatrixDiagonal(double[][] matrix) {  // Metodo para calcular a matriz diagonal dos autovalores
        RealMatrix matrixA = MatrixUtils.createRealMatrix(matrix);  // Converte a matriz de double para um objeto RealMatrix
        EigenDecomposition eigenDecomposition = new EigenDecomposition(matrixA);  // Realiza a decomposição em valores e vetores próprios
        double[] arrayD = eigenDecomposition.getRealEigenvalues();  // Obtém os valores próprios da decomposição
        double[][] matrixD = new double[matrix.length][matrix.length];  // Cria uma matriz quadrada para armazenar os valores próprios na diagonal
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
            for (int indexColumn = 0; indexColumn < matrix.length; indexColumn++) {  // Percorre as colunas da matriz
                if (indexRow == indexColumn) {  // Verifica se está na diagonal principal
                    matrixD[indexRow][indexColumn] = arrayD[indexRow];  // Atribui o valor próprio à diagonal
                }
            }
        }
        return matrixD;  // Retorna a matriz diagonal dos valores próprios
    }

    private static double[][] MatrixTransposer(double[][] matrix) {  // Metodo para transpor uma matriz
        int verticalSizeOfMatrix = matrix.length;  // Obtém o número de linhas da matriz
        int horizontalSizeOfMatrix = matrix[0].length;  // Obtém o número de colunas da matriz
        double[][] transposedMatrix = new double[horizontalSizeOfMatrix][verticalSizeOfMatrix];  // Cria a matriz transposta
        for (int indexRow = 0; indexRow < verticalSizeOfMatrix; indexRow++) {  // Percorre as linhas da matriz original
            for (int indexColumn = 0; indexColumn < horizontalSizeOfMatrix; indexColumn++) {  // Percorre as colunas da matriz original
                transposedMatrix[indexColumn][indexRow] = matrix[indexRow][indexColumn];  // Atribui os valores na posição transposta
            }
        }
        return transposedMatrix;  // Retorna a matriz transposta
    }

    private static double[][] MatrixConverter(RealMatrix matrixToConvert) {  // Metodo para converter uma matriz RealMatrix para uma matriz 2D de doubles
        int sizeOfMatrix = matrixToConvert.getRowDimension();  // Obtém o número de linhas da matriz
        double[][] convertedMatrix = new double[sizeOfMatrix][sizeOfMatrix];  // Cria uma nova matriz 2D para armazenar os dados convertidos
        for (int indexRow = 0; indexRow < sizeOfMatrix; indexRow++) {  // Percorre as linhas da matriz
            for (int indexColumn = 0; indexColumn < sizeOfMatrix; indexColumn++) {  // Percorre as colunas da matriz
                convertedMatrix[indexRow][indexColumn] = matrixToConvert.getEntry(indexRow, indexColumn);  // Atribui os valores da matriz RealMatrix à nova matriz 2D
            }
        }
        return convertedMatrix;  // Retorna a matriz convertida
    }

    private static int ValueInsideOfBounds(int sizeOfMatrix) {  // Metodo para garantir que o número de valores próprios (k) esteja dentro dos limites
        int ValueOfK;  // Declaração da variável para armazenar o valor de k
        System.out.printf("-> Digite o número de valores próprios (k) que pretende (1 - %d): ", sizeOfMatrix);  // Solicita ao utilizador o valor de k dentro do intervalo permitido
        do {  // Inicia um loop para validar a entrada do utilizador
            ValueOfK = scKeyboard.nextInt();  // Lê o valor de k inserido pelo utilizador
            if (ValueOfK <= 0 || ValueOfK > sizeOfMatrix) {  // Verifica se o valor de k está fora dos limites válidos
                System.out.println("\n=======================");  // Exibe uma linha de separação
                System.out.println("=== Valor inválido! ===");  // Informa que o valor inserido é inválido
                System.out.println("=======================\n");  // Exibe outra linha de separação
                System.out.printf("Digite outro (1 - %d): ", sizeOfMatrix);  // Solicita ao utilizador que insira um novo valor
            }
        } while (ValueOfK <= 0 || ValueOfK > sizeOfMatrix);  // Repete até o valor de k ser válido
        return ValueOfK;  // Retorna o valor válido de k
    }

    private static double[][] MultiplicationMatrix(double[][] matrixA, double[][] matrixB) {  // Metodo para multiplicar duas matrizes
        double[][] newMatrix = new double[matrixA.length][matrixB[0].length];  // Cria uma nova matriz com dimensões adequadas para armazenar o resultado
        for (int rowNewMatrix = 0; rowNewMatrix < matrixA.length; rowNewMatrix++) {  // Percorre todas as linhas da matriz A
            for (int columnNewMatrix = 0; columnNewMatrix < matrixB[0].length; columnNewMatrix++) {  // Percorre todas as colunas da matriz B
                for (int counterIndex = 0; counterIndex < matrixB.length; counterIndex++) {  // Realiza a multiplicação elemento a elemento
                    newMatrix[rowNewMatrix][columnNewMatrix] += matrixA[rowNewMatrix][counterIndex] * matrixB[counterIndex][columnNewMatrix];  // Calcula o valor do elemento na nova matriz
                }
            }
        }
        return newMatrix;  // Retorna a matriz resultante da multiplicação
    }

    private static double[][] CompressorOfMatrix(double[][] matrixP, double[][] matrixD, int numOfProperVectors) {  // Metodo para comprimir a matriz usando matrizes P e D
        int sizeOfMatrix = matrixP.length;  // Obtém o tamanho da matriz P
        double[][] properSizeP = SizeConvertor(sizeOfMatrix, numOfProperVectors, matrixP);  // Converte o tamanho da matriz P para o número de vetores próprios
        double[][] properSizeD = SizeConvertor(numOfProperVectors, numOfProperVectors, matrixD);  // Converte o tamanho da matriz D para o número de vetores próprios
        double[][] transposedProperSizeP = MatrixTransposer(properSizeP);  // Transpõe a matriz P com o tamanho adequado
        return MultiplicationMatrix(MultiplicationMatrix(properSizeP, properSizeD), transposedProperSizeP);  // Realiza a multiplicação das matrizes para obter a matriz comprimida
    }

    private static double[][] SizeConvertor(int size1, int size2, double[][] matrix) {  // Metodo para converter o tamanho de uma matriz
        double[][] matrixModified = new double[size1][size2];  // Cria uma nova matriz com o tamanho especifico
        for (int indexRow = 0; indexRow < size1; indexRow++) {  // Percorre as linhas da nova matriz
            for (int indexColumn = 0; indexColumn < size2; indexColumn++) {  // Percorre as colunas da nova matriz
                matrixModified[indexRow][indexColumn] = matrix[indexRow][indexColumn];  // Copia os valores da matriz original para a nova matriz
            }
        }
        return matrixModified;  // Retorna a matriz modificada com o novo tamanho
    }

    private static double CalculateAbsoluteError(double[][] matrix, double[][] compressedMatrix) {  // Metodo para calcular o erro absoluto entre duas matrizes
        double subtraction;  // Variável para armazenar a diferença entre os elementos das duas matrizes
        double summationOfSubtraction = 0;  // Variável para armazenar a soma das diferenças absolutas
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas das matrizes
            for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Percorre as colunas das matrizes
                subtraction = matrix[indexRow][indexColumn] - compressedMatrix[indexRow][indexColumn];  // Calcula a diferença entre os elementos correspondentes
                if (subtraction < 0) {  // Verifica se a diferença é negativa
                    subtraction *= -1;  // Se negativa, converte para positiva (valor absoluto)
                }
                summationOfSubtraction += subtraction;  // Acumula a diferença absoluta
            }
        }
        return summationOfSubtraction / (matrix.length * matrix[0].length);  // Retorna o erro absoluto médio (dividido pelo número total de elementos das matrizes)
    }

    private static BufferedImage ImagesReconstruction(double[][] matrix) {  // Metodo para reconstruir uma imagem a partir de uma matriz de tons de cinza
        BufferedImage image = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_INT_RGB);  // Cria uma nova imagem com o tamanho baseado na matriz
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
            for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Percorre as colunas da matriz
                int colorTone = (int) matrix[indexRow][indexColumn];  // Obtém o valor do tom de cinza para o pixel
                int grayPixel = (colorTone << 16) | (colorTone << 8) | colorTone;  // Converte o tom de cinza em uma cor RGB (R, G, B iguais)
                image.setRGB(indexColumn, indexRow, grayPixel);  // Define o valor de cor do pixel na posição correspondente da imagem
            }
        }
        return image;  // Retorna a imagem reconstruída
    }

    private static double[] MeanVector(double[][] array) {  // Metodo para calcular o vetor médio de uma matriz
        double[] meanVector = new double[array.length];  // Cria um vetor para armazenar a média de cada linha da matriz
        int numbOfColumns = array[0].length;  // Obtém o número de colunas da matriz
        double summation;  // Variável para acumular a soma dos valores de cada linha
        for (int indexRow = 0; indexRow < array.length; indexRow++) {  // Percorre as linhas da matriz
            summation = 0;  // Reinicia a soma para cada nova linha
            for (int indexColumn = 0; indexColumn < numbOfColumns; indexColumn++) {  // Percorre as colunas de cada linha
                summation += array[indexRow][indexColumn];  // Soma os valores da linha
            }
            meanVector[indexRow] = summation / numbOfColumns;  // Calcula a média da linha e armazena no vetor de médias
        }
        return meanVector;  // Retorna o vetor com as médias das linhas da matriz
    }

    private static double[][] ConstructionOfMatrixA(double[][] globalMatrix, double[] meanVector) {  // Metodo para construir a matriz A a partir da matriz global e do vetor médio
        double[][] matrixA = new double[globalMatrix.length][globalMatrix[0].length];  // Cria a matriz A com as mesmas dimensões da matriz global
        for (int indexColumn = 0; indexColumn < globalMatrix[0].length; indexColumn++) {  // Percorre as colunas da matriz global
            double[][] imageDeviation_Column = ImageDeviation_Column(globalMatrix, meanVector, indexColumn);  // Calcula o desvio da coluna com base no vetor médio
            for (int indexRow = 0; indexRow < globalMatrix.length; indexRow++) {  // Percorre as linhas da matriz global
                matrixA[indexRow][indexColumn] = imageDeviation_Column[indexRow][0];  // Atribui o valor do desvio da coluna na matriz A
            }
        }
        return matrixA;  // Retorna a matriz A construída
    }

    private static double[][] ImageDeviation_Column(double[][] globalMatrix, double[] meanVector, int column) {  // Metodo para calcular o desvio de uma coluna da matriz global em relação ao vetor médio
        double[][] imageDeviation = new double[globalMatrix.length][1];  // Cria uma matriz dos desvios com uma coluna e o número de linhas igual ao da matriz global
        for (int indexRow = 0; indexRow < globalMatrix.length; indexRow++) {  // Percorre as linhas da matriz global
            imageDeviation[indexRow][0] = globalMatrix[indexRow][column] - meanVector[indexRow];  // Calcula o desvio de cada elemento da coluna com base no vetor médio
        }
        return imageDeviation;  // Retorna a matriz com os desvios calculados
    }

    private static double[][] NormalizedProperVectors(double[][] matrixA, double[][] properVector_TransposeA_A) {  // Metodo para normalizar os vetores próprios de uma matriz
        double[][] properValues_TransposeA_A = MultiplicationMatrix(matrixA, properVector_TransposeA_A);  // Multiplica a matriz A pelos vetores próprios transpostos de A*A
        for (int indexColumn = 0; indexColumn < matrixA[0].length; indexColumn++) {  // Percorre as colunas do resultado da multiplicação
            double vectorNorm = VectorNorm(properValues_TransposeA_A, indexColumn);  // Calcula a norma do vetor da coluna atual
            for (int indexRow = 0; indexRow < matrixA.length; indexRow++) {  // Percorre as linhas da coluna
                properValues_TransposeA_A[indexRow][indexColumn] /= vectorNorm;  // Normaliza o vetor dividindo cada elemento pela norma calculada
            }
        }
        return properValues_TransposeA_A;  // Retorna os vetores próprios normalizados
    }

    private static double VectorNorm(double[][] matrix, int column) {  // Metodo para calcular a norma de um vetor da matriz
        double summation = 0;  // Variável para acumular a soma dos quadrados dos elementos do vetor
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
            summation += matrix[indexRow][column] * matrix[indexRow][column];  // Soma o quadrado do valor de cada elemento da coluna
        }
        return Math.sqrt(summation);  // Retorna a raiz quadrada da soma dos quadrados
    }

    private static int FindKValue(double[][] matrixA) {  // Metodo para obter o valor de k dentro de um intervalo válido
        int kValue;  // Variável para armazenar o valor de k
        do {  // Loop até que o valor de k seja válido
            kValue = scKeyboard.nextInt();  // Lê o valor de k inserido pelo utilizador
            if(kValue <=0 || kValue > matrixA[0].length) {
                System.out.println("\n=======================");  // Exibe uma linha de separação
                System.out.println("=== Valor inválido! ===");  // Informa que o valor inserido é inválido
                System.out.println("=======================\n");  // Exibe outra linha de separação
                System.out.printf("Digite outro (1 - %d): ", matrixA[0].length);  // Solicita ao utilizador que insira um novo valor
            }
        } while (kValue <= 0 || kValue > matrixA[0].length);  // Verifica se k está dentro do intervalo válido
        return kValue;  // Retorna o valor válido de k
    }

    private static double[][] ImageRestoration(int kValue, double[] meanVector, double[][] matrixA, double[][] normalizedProperVectors, double[][] globalWeight) {  // Metodo para restaurar a imagem a partir dos vetores próprios e outros parâmetros
        double[][] reconstructedImage = new double[matrixA.length][matrixA[0].length];  // Cria uma matriz para armazenar a imagem restaurada
        for (int indexImage = 0; indexImage < matrixA[0].length; indexImage++) {  // Percorre as colunas da matriz A
            double[] summationVector = SummationCalculatorFunction3(kValue, indexImage, matrixA, normalizedProperVectors, globalWeight);  // Calcula o vetor de soma para a coluna atual usando k vetores próprios
            for (int indexRow = 0; indexRow < matrixA.length; indexRow++) {  // Percorre as linhas da matriz A
                reconstructedImage[indexRow][indexImage] = meanVector[indexRow] + summationVector[indexRow];  // Restaura o valor do pixel somando o valor médio ao vetor de soma
            }
        }
        return reconstructedImage;  // Retorna a matriz da imagem restaurada
    }

    private static double[] SummationCalculatorFunction3(int kValue, int iImage, double[][] matrixA, double[][] normalizedProperVectors, double[][] globalWeight) {  // Metodo para calcular o vetor de soma baseado nos k primeiros vetores próprios e pesos
        double[] summationVector = new double[matrixA.length];  // Cria um vetor para armazenar a soma de cada linha
        double[][] multiplication;  // Variável para armazenar o resultado da multiplicação de matrizes
        double[][] weightImageJK = new double[1][1];  // Matriz para armazenar o peso da imagem para o j-ésimo vetor próprio
        for (int jValue = 1; jValue < kValue; jValue++) {  // Percorre os primeiros k vetores próprios
            weightImageJK[0][0] = globalWeight[jValue - 1][iImage];  // Atribui o peso correspondente à imagem
            multiplication = MultiplicationMatrix(MatrixFilterColumn(normalizedProperVectors, jValue - 1), weightImageJK);  // Realiza a multiplicação entre a coluna normalizada do vetor próprio e o peso
            for (int indexRow = 0; indexRow < matrixA.length; indexRow++) {  // Percorre as linhas para acumular os resultados da multiplicação
                summationVector[indexRow] += multiplication[indexRow][0];  // Acumula os valores no vetor soma
            }
        }
        return summationVector;  // Retorna o vetor soma calculado
    }

    private static double[][] globalWeight(int kValue, double[][] matrixA, double[][] normalizedProperVectors) {  // Metodo para calcular os pesos globais das imagens com base nos k vetores próprios normalizados
        double[][] globalWeightOfImages = new double[kValue][matrixA[0].length];  // Cria uma matriz para armazenar os pesos globais das imagens, com k linhas e número de imagens de colunas
        for (int indexImage = 0; indexImage < globalWeightOfImages[0].length; indexImage++) {  // Percorre as imagens
            for (int jValue = 0; jValue < globalWeightOfImages.length; jValue++) {  // Percorre os vetores próprios
                double[][] transposeProperVectorJ = MatrixTransposer(MatrixFilterColumn(normalizedProperVectors, jValue));  // Transpõe a coluna do vetor próprio j-ésimo
                double[][] fiVectorJ = MatrixFilterColumn(matrixA, indexImage);  // Filtra a coluna da matriz A correspondente à imagem indexImage
                double[][] weightOfImageJ = MultiplicationMatrix(transposeProperVectorJ, fiVectorJ);  // Calcula o peso da imagem com o vetor próprio j-ésimo
                globalWeightOfImages[jValue][indexImage] = weightOfImageJ[0][0];  // Armazena o peso calculado na matriz de pesos globais
            }
        }
        return globalWeightOfImages;  // Retorna a matriz com os pesos globais das imagens
    }

    private static void CreateImageWithMatrixForFile(double[][] globalReconstructedMatrix, int nImage, String pathNameFile) throws IOException {  // Metodo para criar uma imagem a partir de uma matriz e guardar no arquivo
        int nLinhasMatrixImage = (int) Math.sqrt(globalReconstructedMatrix.length);  // Calcula o número de linhas da matriz da imagem, assumindo que a imagem é quadrada
        double[][] reconstructedMatrixOfImage = new double[nLinhasMatrixImage][nLinhasMatrixImage];  // Cria uma nova matriz para armazenar a imagem reconstruída
        for (int indexRow = 0; indexRow < reconstructedMatrixOfImage.length; indexRow++) {  // Percorre as linhas da nova matriz da imagem
            for (int indexColumn = 0; indexColumn < reconstructedMatrixOfImage[0].length; indexColumn++) {  // Percorre as colunas da nova matriz da imagem
                int nLineGlobalMatrix = (indexRow * reconstructedMatrixOfImage[0].length) + indexColumn;  // Calcula o índice correspondente na matriz global reconstruída
                reconstructedMatrixOfImage[indexRow][indexColumn] = globalReconstructedMatrix[nLineGlobalMatrix][nImage];  // Atribui o valor da matriz global reconstruída à matriz da imagem
            }
        }
        BufferedImage reconstructedImage = ImagesReconstruction(reconstructedMatrixOfImage);  // Converte a matriz da imagem reconstruída para um objeto BufferedImage
        ImageIO.write(reconstructedImage, "jpg", new File(pathNameFile));  // Guarda a imagem reconstruída como um arquivo JPEG no caminho espefico
    }

    private static double[][] MatrixFilterColumn(double[][] matrix, int nColumn) {  // Metodo para filtrar uma coluna de uma matriz
        double[][] newMatrix = new double[matrix.length][1];  // Cria uma nova matriz com uma única coluna
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz original
            newMatrix[indexRow][0] = matrix[indexRow][nColumn];  // Atribui o valor da coluna nColumn da matriz original para a nova matriz
        }
        return newMatrix;  // Retorna a nova matriz contendo apenas a coluna filtrada
    }

    private static double[][] MatrixFilterRow(double[][] matrix, int nRow) {  // Metodo para filtrar uma linha de uma matriz
        double[][] newMatrix = new double[1][matrix[0].length];  // Cria uma nova matriz com uma única linha
        for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Percorre as colunas da matriz
            newMatrix[0][indexColumn] = matrix[nRow][indexColumn];  // Atribui o valor da linha nRow da matriz original para a nova matriz
        }
        return newMatrix;  // Retorna a nova matriz contendo apenas a linha filtrada
    }

    private static double[][] CalculateFi(double[][] matrixOfImage, double[] meanVector) {  // Metodo para calcular a matriz Fi subtraindo o vetor médio da imagem
        double[][] matrixColumnImage = new double[meanVector.length][1];  // Cria uma nova matriz para armazenar a coluna da imagem
        for (int indexRow = 0; indexRow < matrixOfImage.length; indexRow++) {  // Percorre as linhas da matriz da imagem
            for (int indexColumn = 0; indexColumn < matrixOfImage[0].length; indexColumn++) {  // Percorre as colunas da matriz da imagem
                matrixColumnImage[(indexRow * matrixOfImage[0].length) + indexColumn][0] = matrixOfImage[indexRow][indexColumn];  // Atribui os valores da matriz da imagem para a nova matriz de coluna
            }
        }
        double[][] matrixFi = new double[matrixColumnImage.length][1];  // Cria uma nova matriz Fi com a mesma quantidade de linhas que matrixColumnImage
        for (int indexRow = 0; indexRow < matrixColumnImage.length; indexRow++) {  // Percorre as linhas da matriz de coluna
            matrixFi[indexRow][0] = matrixColumnImage[indexRow][0] - meanVector[indexRow];  // Subtrai o valor do vetor médio de cada elemento
        }
        return matrixFi;  // Retorna a matriz Fi calculada
    }

    private static double[][] CalculateOhm(double[][] matrixOfImage, double[] meanVector, int valueK, double[][] normalizedProperVectors) {  // Metodo para calcular o vetor Ohm
        double[][] fi = CalculateFi(matrixOfImage, meanVector);  // Calcula a matriz Fi (imagem centralizada)
        double[][] vectorWeightJ;  // Variável para armazenar o peso de cada vetor próprio
        double[][] vectorOhm = new double[1][valueK];  // Cria um vetor Ohm com k elementos
        double[][] transpose_properVectors = MatrixTransposer(normalizedProperVectors);  // Transpõe os vetores próprios normalizados
        for (int indexColumn = 0; indexColumn < valueK; indexColumn++) {  // Percorre os primeiros k vetores próprios
            vectorWeightJ = MultiplicationMatrix(MatrixFilterRow(transpose_properVectors, indexColumn), fi);  // Calcula o peso do vetor próprio j-ésimo multiplicando a transposta do vetor próprio pela matriz Fi
            vectorOhm[0][indexColumn] = vectorWeightJ[0][0];  // Armazena o peso calculado no vetor Ohm
        }
        return vectorOhm;  // Retorna o vetor Ohm contendo os pesos dos k primeiros vetores próprios
    }

    private static int CalculateLessEuclideanDistance(double[][] matrixOhmImage, double[][] globalMatrix, double[] meanVector, int valueK, double[][] normalizedProperVectors, String[] listNameFiles, String outputFile, boolean interactiveMode) throws IOException {  // Metodo para calcular a menor distância euclidiana entre os vetores de pesos (Ω) das imagens
        double lessDistance = 999999999;  // Inicializa a menor distância com um valor muito alto
        int indexLessDistanceImage = -1;  // Variável para armazenar o índice da imagem com a menor distância
        if (interactiveMode) {  // Se o modo interativo estiver ativo
            System.out.println(" Imagens:  | Distância: |  Vetor de pesos (omega)i:");  // Exibe o cabeçalho da tabela
            System.out.print("-----------------------------------------------");
            for (int idexImage = 0; idexImage < valueK; idexImage++) {  // Exibe as divisões para cada vetor de pesos
                System.out.print("---------------");
            }
            System.out.println();
            for (int nImage = 0; nImage < globalMatrix[0].length; nImage++) {  // Percorre todas as imagens
                double[][] matrixOhmAttempt = CalculateOhm(MatrixFilterColumn(globalMatrix, nImage), meanVector, valueK, normalizedProperVectors);  // Calcula o vetor Ω para a imagem atual
                double summary = 0;
                for (int index = 0; index < valueK; index++) {  // Calcula a soma das diferenças quadráticas entre os vetores Ω
                    summary += Math.pow(matrixOhmAttempt[0][index] - matrixOhmImage[0][index], 2);
                }
                double distance = Math.sqrt(summary);  // Calcula a distância euclidiana
                System.out.printf("%10s | %10.2f |", listNameFiles[nImage], distance);  // Exibe o nome da imagem e a distância
                PrintMatrixFilterNumColumns(matrixOhmAttempt, matrixOhmAttempt[0].length, 8);  // Exibe o vetor Ω calculado
                if (distance < lessDistance) {  // Se a distância for menor que a menor distância encontrada até agora:
                    lessDistance = distance;  // Atualiza a menor distância
                    indexLessDistanceImage = nImage;  // Atualiza o índice da imagem com a menor distância
                }
            }
        } else {  // Se o modo interativo não estiver ativado, escreve o resultado em um arquivo
            PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile, true));  // Abre o arquivo para escrita
            printWriter.println(" Imagens:  | Distância: |   Vetor de pesos(omega)i:");  // Escreve o cabeçalho da tabela
            printWriter.print("-----------------------------------------------");
            for (int idexImage = 0; idexImage < valueK; idexImage++) {
                printWriter.print("---------------");
            }
            printWriter.println();
            for (int nImage = 0; nImage < globalMatrix[0].length; nImage++) {  // Percorre todas as imagens
                double[][] matrixOhmAttempt = CalculateOhm(MatrixFilterColumn(globalMatrix, nImage), meanVector, valueK, normalizedProperVectors);  // Calcula o vetor Ω para a imagem atual
                double summary = 0;
                for (int index = 0; index < valueK; index++) {
                    summary += Math.pow(matrixOhmAttempt[0][index] - matrixOhmImage[0][index], 2);
                }
                double distance = Math.sqrt(summary);  // Calcula a distância euclidiana
                printWriter.printf("%10s | %10.2f |", listNameFiles[nImage], distance);  // Escreve o nome da imagem e a distância
                for (int indexRow = 0; indexRow < matrixOhmAttempt.length; indexRow++) {  // Escreve os valores do vetor Ω
                    for (int indexColumn = 0; indexColumn < matrixOhmAttempt[0].length; indexColumn++) {
                        printWriter.printf(" %12.2f |", matrixOhmAttempt[indexRow][indexColumn]);
                    }
                    printWriter.println();
                }
                if (distance < lessDistance) {  // Se a distância for menor que a menor distância encontrada até agora
                    lessDistance = distance;  // Atualiza a menor distância
                    indexLessDistanceImage = nImage;  // Atualiza o índice da imagem com a menor distância
                }
            }
            printWriter.close();  // Fecha o arquivo após a escrita
        }
        return indexLessDistanceImage;  // Retorna o índice da imagem com a menor distância
    }

    private static int nDigitsMaxMatrixNum(double[][] matrix) {  // Metodo para calcular o número máximo de dígitos de um número na matriz
        double maxNumber = -1;
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
            for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Percorre as colunas da matriz
                double actualNumber = matrix[indexRow][indexColumn];
                actualNumber = actualNumber < 0 ? -actualNumber : actualNumber;  // Converte o número para valor absoluto
                maxNumber = Math.max(maxNumber, actualNumber);  // Atualiza o valor máximo encontrado
            }
        }
        int maxNumberInt = (int) Math.round(maxNumber);  // Arredonda o valor máximo encontrado e converte para inteiro
        int nDigits = 0;  // Inicializa o contador de dígitos
        while ((maxNumberInt != 0)) {  // Enquanto o número máximo não for zero
            nDigits++;  // Incrementa o contador de dígitos
            maxNumberInt /= 10;  // Divide o número por 10 para contar o próximo dígito
        }
        return nDigits;  // Retorna o número de dígitos do valor máximo
    }

    private static double MinOrMaxValueOfMatrix(double[][] matrix, String option) {  // Metodo para retornar o valor mínimo ou máximo de uma matriz, dependendo da opção fornecida
        double value = matrix[0][0];  // Inicia o valor com o primeiro elemento da matriz
        switch (option) {  // Verifica qual operação (mínimo ou máximo) deve ser executada
            case "min": {  // Caso a opção seja "min", encontra o valor mínimo na matriz
                for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
                    for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Percorre as colunas da matriz
                        value = Math.min(value, matrix[indexRow][indexColumn]);  // Atualiza o valor mínimo encontrado
                    }
                }
            }
            break;  // Finaliza o caso para o valor mínimo
            case "max": {  // Caso a opção seja "max", encontra o valor máximo na matriz
                for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
                    for (int indexColumn = 0; indexColumn < matrix[0].length; indexColumn++) {  // Percorre as colunas da matriz
                        value = Math.max(value, matrix[indexRow][indexColumn]);  // Atualiza o valor máximo encontrado
                    }
                }
            }
        }
        return value;  // Retorna o valor mínimo ou máximo encontrado
    }

    public static String ConvertNumberToScientificNotation(double number) {  // Metodo para converter um número para notação científica
        int exponent = 0;
        while (Math.abs(number) >= 10) {  // Enquanto o número for maior ou igual a 10, divide-o por 10 e incrementa o expoente
            number /= 10;
            exponent++;
        }
        while (Math.abs(number) < 1 && number != 0) {  // Enquanto o número for menor que 1 (mas não zero), multiplica-o por 10 e decrementa o expoente
            number *= 10;
            exponent--;
        }
        if (exponent == 0) {  // Se o expoente for 0, retorna o número com 2 casas decimais
            return String.format("%.2f", number);
        } else {  // Caso contrário, retorna o número em notação científica com 2 casas decimais e o expoente
            return String.format("%.2fE%d", number, exponent);
        }
    }

    private static void PrintMatrixFilterNumColumns(double[][] matrix, int nColumns, int spacingSize) {  // Metodo para imprimir os elementos de uma matriz com formatação de espaço específica
        for (int indexRow = 0; indexRow < matrix.length; indexRow++) {  // Percorre as linhas da matriz
            for (int indexColumn = 0; indexColumn < nColumns; indexColumn++) {  // Percorre as colunas especificadas pela variável nColumns
                switch (spacingSize) {  // Verifica o tamanho de espaçamento solicitado para formatar a saída
                    case 1:
                        System.out.printf(" %5.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 2:
                        System.out.printf(" %6.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 3:
                        System.out.printf("%7.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 4:
                        System.out.printf(" %8.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 5:
                        System.out.printf(" %9.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 6:
                        System.out.printf(" %10.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 7:
                        System.out.printf(" %11.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 8:
                        System.out.printf(" %12.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 9:
                        System.out.printf(" %13.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 10:
                        System.out.printf(" %14.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 11:
                        System.out.printf(" %15.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 12:
                        System.out.printf(" %16.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 13:
                        System.out.printf(" %17.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 14:
                        System.out.printf(" %18.2f |", matrix[indexRow][indexColumn]);
                        break;
                    case 15:
                        System.out.printf(" %19.2f |", matrix[indexRow][indexColumn]);
                        break;
                }
            }
            System.out.println();
        }
    }

    private static void PrintMatrixFilterFile(String initialText, double[][] matrix, String filePath, double absoluteError, int nColumns, int spacingSize, boolean recreateFile, boolean printMatrixCSV) throws IOException {
        File outputFile = new File(filePath);  // Cria uma File com o caminho do ficheiro de saída.
        if (recreateFile) {  // Se for necessário recriar o ficheiro, elimina-o caso já exista e cria um novo.
            if (outputFile.delete()) {
                outputFile.createNewFile();  // Elimina o ficheiro e cria um novo.
            }
        }
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile, true));  // Cria um PrintWriter para escrever no ficheiro de saída.
        printWriter.println(initialText);  // Escreve o texto inicial no ficheiro.
        printWriter.println();  // Linha em branco após o texto inicial.
        if (printMatrixCSV) {  // Se for pedido imprimir em formato CSV.
            for (int indexRow = 0; indexRow < matrix.length; indexRow++) {
                for (int indexColumn = 0; indexColumn < nColumns; indexColumn++) {
                    printWriter.printf("%.0f", matrix[indexRow][indexColumn]);
                    if (indexColumn != matrix[0].length - 1) {  // Se não for a última coluna, imprime uma vírgula.
                        printWriter.printf(",");
                    }
                }
                printWriter.println();  // Linha em branco após cada linha da matriz.
            }
            if (absoluteError != -1) {  // Se o erro absoluto não for igual a -1, imprime-o em notação científica.
                printWriter.printf("\nEAM: %s", ConvertNumberToScientificNotation(absoluteError));
            }
        } else {  // Se não for pedido o formato CSV, imprime a matriz com um formato bonito.
            for (int indexRow = 0; indexRow < matrix.length; indexRow++) {
                for (int indexColumn = 0; indexColumn < nColumns; indexColumn++) {
                    switch (spacingSize) {  // Dependendo do tamanho do espaço (spacingSize), ajusta o formato de saída.
                        case 1:
                            printWriter.printf(" %5.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 2:
                            printWriter.printf(" %6.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 3:
                            printWriter.printf("%7.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 4:
                            printWriter.printf(" %8.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 5:
                            printWriter.printf(" %9.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 6:
                            printWriter.printf(" %10.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 7:
                            printWriter.printf(" %11.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 8:
                            printWriter.printf(" %12.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 9:
                            printWriter.printf(" %13.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 10:
                            printWriter.printf(" %14.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 11:
                            printWriter.printf(" %15.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 12:
                            printWriter.printf(" %16.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 13:
                            printWriter.printf(" %17.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 14:
                            printWriter.printf(" %18.2f |", matrix[indexRow][indexColumn]);
                            break;
                        case 15:
                            printWriter.printf(" %19.2f |", matrix[indexRow][indexColumn]);
                            break;
                    }
                }
                printWriter.println();
            }
        }
        printWriter.close();
    }

    private static void CreateDirectory(String directoryName, boolean interactiveMode, String filePath) throws IOException {
        Path folderPath = Paths.get(directoryName);
        File outputFile = new File(filePath);
        if (!Files.exists(folderPath)) {  // Verifica se a pasta não existe.
            Files.createDirectory(folderPath);  // Cria a pasta no sistema de ficheiros.
            if (interactiveMode) {  // Se estiver no modo interativo.
                System.out.println("\nPasta criada com sucesso: " + directoryName);  // Imprime uma mensagem no console.
            } else {  // Se não estiver no modo interativo.
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(outputFile, true));  // Cria um PrintWriter para escrever no ficheiro de saída.
                printWriter.printf("Pasta criada com sucesso: %s\n\n", directoryName);
                printWriter.close();
            }
        }
    }

// █████████████████████████ GUI INTERFACE █████████████████████████

    private static void PrinterStartFunction(int nFunction) {
        if (nFunction != 0) {
            switch (nFunction) {  // Inicia o switch para determinar o caso de nFunction.
                case 1:
                    System.out.print("\n -------------------> Iniciado -> Decomposição própria de uma matriz simétrica <-------------------\n\n");
                    break;
                case 2:
                    System.out.print("\n ------------------->  Iniciado -> Reconstrução de imagens de uma pasta (usando eigenfaces) <-------------------\n\n");
                    break;
                case 3:
                    System.out.print("\n ------------------->  Iniciado -> Identificar imagem mais parecida numa base de imagens dada outra imagem <------------------- \n\n");
                    break;
                case 4:
                    System.out.println("\n ------------------->  Iniciado -> Gerar novas imagens utilizando eigenfaces <-------------------\n");
                    break;
            }
        }
    }

    private static void PrinterFinishFunction(int nFunction) {
        if (nFunction != 0) {
            switch (nFunction) {
                case 1:
                    System.out.print("\n -------------------> Finalizado -> Decomposição própria de uma matriz simétrica <-------------------\n");
                    break;
                case 2:
                    System.out.print("\n -------------------> Finalizado -> Reconstrução de imagens de uma pasta (usando eigenfaces) <------------------- \n");
                    break;
                case 3:
                    System.out.print("\n -------------------> Finalizado -> Identificar imagem mais parecida numa base de imagens dada outra imagem <------------------- \n");  //
                    break;
                case 4:
                    System.out.println("\n -------------------> Finalizado -> Gerar novas imagens utilizando eigenfaces <------------------- \n");
                    break;
            }
        }
    }

    private static int PrinterGUI_Menu() {
        System.out.println();
        System.out.println("████████████████████████████████████████████ MENU ███████████████████████████████████████████");  // Imprime a borda superior do menu.
        System.out.println("█                                                                                           █");
        System.out.println("█      Digite a funcionalidade que pretende:                                                █");
        System.out.println("█      [1] - Decomposição própria de uma matriz simétrica;                                  █");
        System.out.println("█      [2] - Reconstrução de imagens de uma pasta (usando eigenfaces);                      █");
        System.out.println("█      [3] - Identificar imagem mais parecida numa base de imagens dada outra imagem;       █");
        System.out.println("█      [4] - Gerar novas imagens utilizando eigenfaces;                                     █");
        System.out.println("█      [0] - Encerrar programa.                                                             █");
        System.out.println("█                                                                                           █");
        System.out.println("█████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.print("\n-> ");
        int option;
        do {  // Loop para garantir que a opção seja válida.
            option = scKeyboard.nextInt();
            if (option != 0 && option != 1 && option != 2 && option != 3 && option != 4) {
                System.out.println("███████████████████ Opção Inválida ███████████████████████");
                System.out.println("█                                                        █");
                System.out.println("█      Opção inválida!                                   █");
                System.out.println("█      Digite uma opção entre ([1],[2],[3],[4],[0])      █");
                System.out.println("█                                                        █");
                System.out.println("██████████████████████████████████████████████████████████");
                System.out.print("\n-> ");
            }
        } while (option != 0 && option != 1 && option != 2 && option != 3 && option != 4);
        return option;
    }

    private static void PrinterMenu1() {
        System.out.println("████████████████████████ Decomposição própria de uma matriz simétrica ███████████████████████");
        System.out.println("█                                                                                           █");
        System.out.println("█       Propriedades da matriz a inserir:                                                   █");
        System.out.println("█             - Simétrica                                                                   █");
        System.out.println("█             - Quadrada                                                                    █");
        System.out.println("█             - Nº linhas <= 256                                                            █");
        System.out.println("█             - Com números reais                                                           █");
        System.out.println("█                                                                                           █");
        System.out.println("█████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println("\n-------> Inserir dados <-------");
    }

    private static void PrinterMenu2() {
        System.out.println("██████████ Reconstrução de imagens disponíveis numa pasta utilizando as eigenfaces ██████████");
        System.out.println("█                                                                                           █");
        System.out.println("█       Propriedades das matrizes da pasta:                                                 █");
        System.out.println("█             - Quadrada                                                                    █");
        System.out.println("█             - Nº linhas <= 256                                                            █");
        System.out.println("█             - Com números reais entre 0 e 255                                             █");
        System.out.println("█                                                                                           █");
        System.out.println("█████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println("\n-------> Inserir dados <-------");
    }

    private static void PrinterMenu3() {
        System.out.println("██████████ Identificar imagem mais parecida numa base de imagens dada outra imagem ██████████");
        System.out.println("█                                                                                           █");
        System.out.println("█       Propriedades das matrizes da pasta:                                                 █");
        System.out.println("█             - Quadrada                                                                    █");
        System.out.println("█             - Nº linhas <= 256                                                            █");
        System.out.println("█             - Com números reais entre 0 e 255                                             █");
        System.out.println("█                                                                                           █");
        System.out.println("█████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println("\n-------> Inserir dados <-------");
    }

    private static void PrinterMenu4() {
        System.out.println("█████████████████████████ Gerar novas imagens utilizando eigenfaces █████████████████████████");
        System.out.println("█                                                                                           █");
        System.out.println("█       Propriedades das matrizes da pasta:                                                 █");
        System.out.println("█             - Quadrada                                                                    █");
        System.out.println("█             - Nº linhas <= 256                                                            █");
        System.out.println("█             - Com números reais entre 0 e 255                                             █");
        System.out.println("█                                                                                           █");
        System.out.println("█████████████████████████████████████████████████████████████████████████████████████████████");
        System.out.println("\n-------> Inserir dados <-------");
    }

    private static void PrintErrorMessage(String optionError, boolean interactiveMode, File outPutFile) throws FileNotFoundException {
        PrintWriter printWriter = null;  // Declaração do PrintWriter que será usado para escrever em arquivo, se necessário.
        if (!interactiveMode) {  // Verifica se não está no modo interativo.
            printWriter = new PrintWriter(new FileOutputStream(outPutFile, true));  // Abre o arquivo para escrita (adiciona ao final do arquivo).
        }
        if (interactiveMode) {  // Se estiver no modo interativo, imprime as mensagens de erro.
            switch (optionError) {  // Verifica o tipo de erro.
                case "UnsymmetricalMatrix": {  // Erro de matriz não simétrica.
                    System.out.println("\n===================================");  // Imprime o erro.
                    System.out.println("=== ERRO: Matrix não simétrica! ===");  // Mensagem de erro específica.
                    System.out.println("===================================");  // Finaliza a borda do erro.
                }
                break;
                case "InvalidSize": {  // Erro de tamanho inválido de matriz.
                    System.out.println("\n==========================================");
                    System.out.println("=== ERRO: Matriz com tamanho inválido! ===");
                    System.out.println("==========================================");
                }
                break;
                case "InvalidValue": {  // Erro de valor inválido na matriz.
                    System.out.println("\n========================================");
                    System.out.println("=== ERRO: Matriz com valor inválido! ===");
                    System.out.println("========================================");
                }
                break;
                case "InvalidFile": {  // Erro de arquivo inválido (não existe ou formato incorreto).
                    System.out.println("\n=========================================================");
                    System.out.println("=== ERRO: Ficheiro não existe ou não tem formato csv! ===");
                    System.out.println("=========================================================");
                }
                break;
            }
        } else {  // Se não estiver no modo interativo, escreve a mensagem de erro no arquivo.
            switch (optionError) {  // Verifica o tipo de erro.
                case "UnsymmetricalMatrix": {  // Erro de matriz não simétrica.
                    printWriter.println("\n===================================");
                    printWriter.println("=== ERRO: Matrix não simétrica! ===");
                    printWriter.println("===================================");
                }
                break;
                case "InvalidSize": {  // Erro de tamanho irregular da matriz.
                    printWriter.println("\n==========================================");
                    printWriter.println("=== ERRO: Matriz com tamanho inválido! ===");
                    printWriter.println("==========================================");
                }
                break;
                case "InvalidValue": {  // Erro de valor inválido na matriz.
                    printWriter.println("\n========================================");
                    printWriter.println("=== ERRO: Matriz com valor inválido! ===");
                    printWriter.println("========================================");
                }
                break;
                case "InvalidFile": {  // Erro de arquivo inválido (não existe ou formato incorreto).
                    printWriter.println("\n=========================================================");
                    printWriter.println("=== ERRO: Ficheiro não existe ou não tem formato csv! ===");
                    printWriter.println("=========================================================");
                }
                break;
            }
            printWriter.close();  // Fecha o arquivo após escrever a mensagem de erro.
        }
    }

    private static void PrintErrorMessageFilterFile(String optionError, boolean interactiveMode, File outPutFile, String fileName) throws FileNotFoundException {
        PrintWriter printWriter = null;  // Declara o PrintWriter para escrever no arquivo, se necessário.
        if (!interactiveMode) {  // Verifica se não está no modo interativo.
            printWriter = new PrintWriter(new FileOutputStream(outPutFile, true));  // Abre o arquivo para adicionar conteúdo no final.
        }
        if (interactiveMode) {  // Se estiver no modo interativo, imprime a mensagem de erro.
            System.out.println();
            for (int counter = 2; counter < fileName.length(); counter++) {  // Imprime a borda de "=" antes da mensagem de erro.
                System.out.print("=");
            }
            switch (optionError) {  // Verifica qual é o erro.
                case "InvalidSize": {  // Erro de tamanho inválido do arquivo.
                    System.out.println("=========================================");  // Imprime a borda e a mensagem de erro.
                    System.out.printf("=== Ficheiro %s com tamanho inválido! ===\n", fileName);  // Mensagem específica do erro.
                    System.out.print("=========================================");  // Finaliza a borda.
                }
                break;
                case "InvalidValue": {  // Erro de valor inválido no arquivo.
                    System.out.println("=============================================");
                    System.out.printf("=== ERRO: Ficheiro %s com valor inválido! ===\n", fileName);
                    System.out.print("=============================================");
                }
                break;
                case "InvalidFile": {  // Erro de arquivo inválido (não existe ou formato incorreto).
                    System.out.println("============================================================");
                    System.out.printf("=== ERRO: Ficheiro %s não existe ou não tem formato csv! ===\n", fileName);
                    System.out.print("============================================================");
                }
                break;
                case "InvalidDirectory": {  // Erro de diretório inválido.
                    System.out.println("====================================================");
                    System.out.printf("=== ERRO: <%s> não existe ou não é um diretório! ===\n", fileName);
                    System.out.print("====================================================");
                }
                break;
            }
            for (int counter = 2; counter < fileName.length(); counter++) {  // Imprime a borda de "=" após a mensagem de erro.
                System.out.print("=");
            }
            System.out.println();
        } else {  // Se não estiver no modo interativo, escreve a mensagem de erro no arquivo.
            printWriter.println();
            for (int counter = 2; counter < fileName.length(); counter++) {  // Imprime a borda de "=" antes da mensagem de erro no arquivo.
                printWriter.print("=");
            }
            switch (optionError) {  // Verifica qual é o erro.
                case "InvalidSize": {  // Erro de tamanho irregular do arquivo.
                    printWriter.println("===============================================");  // Escreve a borda e a mensagem de erro no arquivo.
                    printWriter.printf("=== ERRO: Ficheiro %s com tamanho inválido! ===\n", fileName);  // Mensagem de erro no arquivo.
                    printWriter.print("===============================================");  // Finaliza a borda no arquivo.
                }
                break;
                case "InvalidValue": {  // Erro de valor inválido no arquivo.
                    printWriter.println("=============================================");
                    printWriter.printf("=== ERRO: Ficheiro %s com valor inválido! ===\n", fileName);
                    printWriter.print("=============================================");
                }
                break;
                case "InvalidFile": {  // Erro de arquivo inválido (não existe ou formato incorreto).
                    printWriter.println("============================================================");
                    printWriter.printf("=== ERRO: Ficheiro %s não existe ou não tem formato csv! ===\n", fileName);
                    printWriter.print("============================================================");
                }
                break;
                case "InvalidDirectory": {  // Erro de diretório inválido.
                    printWriter.println("====================================================");
                    printWriter.printf("=== ERRO: <%s> não existe ou não é um diretório! ===\n", fileName);
                    printWriter.print("====================================================");
                }
                break;
            }
            for (int counter = 2; counter < fileName.length(); counter++) {  // Imprime a borda de "=" após a mensagem de erro no arquivo.
                printWriter.print("=");
            }
            printWriter.println();
            printWriter.close();  // Fecha o arquivo após escrever a mensagem de erro.
        }
    }
}