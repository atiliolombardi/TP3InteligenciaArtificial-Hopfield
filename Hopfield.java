import java.util.Arrays;

public class Hopfield {
    private double[][] weights;
    private int size;
    private boolean usePseudoinverse; // Variable para elegir el método de entrenamiento

    // Constructor para inicializar la red con el tamaño de la imagen 10x10
    public Hopfield(int dimension, boolean usePseudoinverseTraining) {
        this.size = dimension*dimension;
        this.weights = new double[size][size];
        this.usePseudoinverse = usePseudoinverseTraining;
    }
    public void train(int[] pattern){
    System.out.println("Entrenando patron:");
    printPattern(pattern, 10, 10);
    if (!usePseudoinverse) {
            trainHebb(pattern);
        } else {
            trainPseudoinverse(new int[][]{pattern});
        }
    }
    // Método de entrenamiento usando Hebb
    private void trainHebb(int[] pattern) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    weights[i][j] += pattern[i] * pattern[j];
                }
            }
        }
        System.out.println("Patron entrenado por Hebb");
    }

    // Método de entrenamiento usando pseudoinversa
    private void trainPseudoinverse(int[][] patterns) {
    double[][] P = new double[size][patterns.length]; // Matriz de patrones
    for (int i = 0; i < patterns.length; i++) {
        for (int j = 0; j < size; j++) {
            P[j][i] = patterns[i][j];
        }
    }

    // Pseudoinversa de la matriz de patrones: Pseudoinverse(P) = P * (P^T * P)^-1 * P^T
    double[][] P_transpose = transpose(P);
    double[][] PTP = multiply(P_transpose, P);
    double[][] PTP_inverse = inverse(PTP);
    double[][] pseudoinverse = multiply(multiply(P, PTP_inverse), P_transpose);

    // Actualizar los pesos utilizando la pseudoinversa
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (i != j) {
                weights[i][j] += pseudoinverse[i][j]; // Sumar la actualización
            }
        }
    }
    System.out.println("Patron entrenado por Pseudoinversa");
    }


    // Método para actualizar un estado de la red basado en un patrón
    public int[] update(int[] inputPattern) {
        int[] updatedPattern = Arrays.copyOf(inputPattern, inputPattern.length);
        for (int i = 0; i < size; i++) {
            double sum = 0;
            for (int j = 0; j < size; j++) {
                sum += weights[i][j] * inputPattern[j];
            }
            updatedPattern[i] = (sum >= 0) ? 1 : -1;
        }
        return updatedPattern;
    }


// Método para reconocer un patrón
public int[] recognize(int[] inputPattern) {
    int[] outputPattern = update(inputPattern);
    int maxIterations = 100; // Establecer un límite de iteraciones
    int iteration = 0;
    
    // Mientras el patrón de salida no sea igual al de entrada y no se excedan las iteraciones
    while (!Arrays.equals(outputPattern, inputPattern) && iteration < maxIterations) {
        inputPattern = Arrays.copyOf(outputPattern, outputPattern.length);
        outputPattern = update(inputPattern);
        iteration++;
    }
    
    // Si se alcanzó el límite de iteraciones, el patrón puede no haber convergido
    if (iteration == maxIterations) {
        System.out.println("El patron no ha convergido despues de " + maxIterations + " iteraciones.");
    }
    
    return outputPattern;
}


    // Método para imprimir la imagen 10x10
    public static void printPattern(int[] pattern, int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print((pattern[i * cols + j] == 1 ? " x" : " -") + " ");
            }
            System.out.println();
        }
    }

    // Función auxiliar para transponer una matriz
    private double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    // Función auxiliar para multiplicar dos matrices
    private double[][] multiply(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = B[0].length;
        int innerDim = B.length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < innerDim; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }

    // Función auxiliar para invertir una matriz
    private double[][] inverse(double[][] matrix) {
        int n = matrix.length;
        double[][] augmented = new double[n][n * 2];

        // Crear la matriz aumentada con la identidad
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = matrix[i][j];
            }
            augmented[i][i + n] = 1;
        }

        // Realizar eliminación gaussiana
        for (int i = 0; i < n; i++) {
            double diagElement = augmented[i][i];
            for (int j = 0; j < n * 2; j++) {
                augmented[i][j] /= diagElement;
            }
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmented[k][i];
                    for (int j = 0; j < n * 2; j++) {
                        augmented[k][j] -= factor * augmented[i][j];
                    }
                }
            }
        }

        // Extraer la inversa
        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = augmented[i][j + n];
            }
        }

        return inverse;
    }

    public static void main(String[] args) {
        int dimension = 10; //matriz 10x10

        // Crear la red de Hopfield con opción de usar pseudoinversa o no
        Hopfield hopfield = new Hopfield(dimension, true); // true para pseudoinversa, false para Hebb

        // Patrones de entrenamiento (imágenes 10x10)
        int[] pattern1 = { 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, 1, 1, -1, -1, -1, -1, -1, -1, 
            -1, 1, 1, 1, 1, -1, -1, -1, -1, -1,
            1, 1, -1, -1, 1, 1, -1, -1, -1, -1,
            1, 1, -1, -1, 1, 1, -1, -1, -1, -1,
            -1, 1, 1, 1, 1, -1, -1, -1, -1, -1,
            -1, -1, 1, 1, -1, -1, -1, -1, -1, -1
        };
        int[] pattern2 = { 
            -1, -1, 1, 1, -1, -1, -1, -1, -1, -1, 
            -1, 1, 1, 1, 1, -1, -1, -1, -1, -1,
            1, 1, -1, -1, 1, 1, -1, -1, -1, -1,
            1, 1, -1, -1, 1, 1, -1, -1, -1, -1,
            -1, 1, 1, 1, 1, -1, -1, -1, -1, -1,
            -1, -1, 1, 1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
        };
        int[] pattern3 = { 
            -1, -1, -1, -1, -1, -1, 1, 1, -1, -1, 
            -1, -1, -1, -1, -1, 1, 1, 1, 1, -1,
            -1, -1, -1, -1, 1, 1, -1, -1, 1, 1,
            -1, -1, -1, -1, 1, 1, -1, -1, 1, 1,
            -1, -1, -1, -1, -1, 1, 1, 1, 1, -1,
            -1, -1, -1, -1, -1, -1, 1, 1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
        };
        int[] pattern4 = { 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, 1, 1, -1, -1, 
            -1, -1, -1, -1, -1, 1, 1, 1, 1, -1,
            -1, -1, -1, -1, 1, 1, -1, -1, 1, 1,
            -1, -1, -1, -1, 1, 1, -1, -1, 1, 1,
            -1, -1, -1, -1, -1, 1, 1, 1, 1, -1,
            -1, -1, -1, -1, -1, -1, 1, 1, -1, -1
        };
        
        //Entrenar patrones
        hopfield.train(pattern1);
        hopfield.train(pattern2);
        hopfield.train(pattern3);        
        hopfield.train(pattern4);
        
        // Crear un patrones de prueba (distorsionados)
        int[] testPattern1 = { 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, 1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, 1, -1, -1, -1, 1, 1, -1, -1, 
            1, -1, -1, -1, -1, 1, 1, -1, 1, -1,
            -1, -1, -1, -1, 1, 1, 1, -1, 1, 1,
            -1, -1, -1, -1, 1, 1, -1, 1, 1, 1,
            -1, -1, -1, -1, -1, 1, 1, 1, 1, -1,
            -1, -1, -1, -1, -1, -1, 1, 1, -1, -1
        };
        int[] testPattern2 = { 
            1, -1, 1, 1, -1, -1, -1, -1, -1, -1, 
            1, 1, 1, 1, 1, -1, -1, -1, 1, 1,
            1, -1, 1, -1, 1, 1, -1, -1, -1, -1,
            1, 1, -1, -1, 1, 1, -1, 1, -1, 1,
            -1, 1, -1, 1, 1, -1, -1, -1, 1, -1,
            -1, -1, -1, 1, -1, -1, 1, -1, -1, -1,
            -1, -1, -1, -1, -1, 1, 1, -1, -1, -1,
            1, -1, -1, -1, -1, 1, -1, -1, 1, -1,
            -1, 1, -1, -1, -1, -1, -1, -1, 1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, 1, -1
        };
        int[] testPattern3 = { 
            -1, -1, -1, -1, -1, 1, 1, -1, -1, -1,
            -1, -1, -1, -1, 1, 1, -1, -1, -1, 1,
            -1, 1, -1, 1, -1, 1, -1, 1, -1, -1,
            1, -1, -1, 1, -1, 1, 1, -1, -1, -1,
            -1, -1, 1, -1, -1, -1, 1, -1, -1, 1, 
            -1, 1, -1, 1, 1, 1, 1, -1, -1, 1,
            -1, -1, 1, -1, 1, -1, -1, -1, 1, 1,
            1, 1, -1, -1, 1, 1, -1, 1, 1, 1,
            -1, -1, -1, -1, 1, 1, -1, -1, -1, -1,
            -1, -1, 1, 1, -1, -1, -1, -1, 1, -1
        };        
        // Reconocer patrones
        
        // Mostrar el patrón distorsionado
        System.out.println("Patron distorsionado:");
        printPattern(testPattern1, 10, 10);

        // Reconocer el patrón
        int[] recognizedPattern = hopfield.recognize(testPattern1);

        // Mostrar el patrón reconocido
        System.out.println("Patron reconocido:");
        printPattern(recognizedPattern, 10, 10);
        

        // Mostrar el patrón distorsionado
        System.out.println("Patron distorsionado:");
        printPattern(testPattern2, 10, 10);

        // Reconocer el patrón
        recognizedPattern = hopfield.recognize(testPattern2);

        // Mostrar el patrón reconocido
        System.out.println("Patron reconocido:");
        printPattern(recognizedPattern, 10, 10);

        // Mostrar el patrón distorsionado
        System.out.println("Patron distorsionado:");
        printPattern(testPattern3, 10, 10);

        // Reconocer el patrón
        recognizedPattern = hopfield.recognize(testPattern3);

        // Mostrar el patrón reconocido
        System.out.println("Patron reconocido:");
        printPattern(recognizedPattern, 10, 10);
    }
}