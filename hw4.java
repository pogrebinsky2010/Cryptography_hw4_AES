import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import sun.audio.*;
/*
* Talaba Pogrebinsky: Apr 8th 2019
* Java Crypto program.
* Currently this class demonstrates the ability
* to take in a plaintext and a key and to demonstate
* crucial methods to the AES encryption algorithm
* including Substitution, shift rows and mix columns.
*
* NOTE currently input is hardcoded.
*/
public class hw4
{
    //TODO: Key schedule and call the helper methods multiple times based on the key size.
    // constructor
    hw4(){}
      public static void main(String args[])
      { 
        //Example input plaintext
        String plaintextString = "32 43 f6 a8 88 5a 30 8d 31 31 98 a2 e0 37 07 34";
        print("Input plaintext: " + plaintextString);
        String plaintextMatrix[][] = new String[4][4];
        String plaintextArray[] = new String[16];
        // Example input key.
        String keyString = "2b 7e 15 16 28 ae d2 a6 ab f7 15 88 09 cf 4f 3c";
        print("Input key: " + keyString);
        String keyMatrix128[][] = new String[4][4];
        String keyArray[] = new String[16];
        String plainTxtXORKeyMatrix[][] = new String[4][4];
        String sBoxMatrix[][] = new String[16][16];
        String stateMatrix[][] = new String[4][4];
        // Matrix to GF(2^8) multiply against
        String staticMatrix[][] = new String[4][4];
        // Write to string transposed b/c my write to matrix function assumes.
        String staticMatrixString = "2 1 1 3 " +
                                    "3 2 1 1 " +
                                    "1 3 2 1 " +
                                    "1 1 3 2 ";
        // When read into matrix I assume it is in a column by column format
        // IE 0,0 - F,0 is the first column of the s-box.
        String sBoxString = "63 ca b7 04 09 53 d0 51 cd 60 e0 e7 ba 70 e1 8c " + //0
                            "7c 82 fd c7 83 d1 ef a3 0c 81 32 c8 78 3e f8 a1 " + //1
                            "77 c9 93 23 2c 00 aa 40 13 4f 3a 37 25 b5 98 89 " + //2
                            "7b 7d 26 c3 1a ed fb 8f ec dc 0a 6d 2e 66 11 0d " + //3
                            "f2 fa 36 18 1b 20 43 92 5f 22 49 8d 1c 48 69 bf " + //4
                            "6b 59 3f 96 6e fc 4d 9d 97 2a 06 d5 a6 03 d9 e6 " + //5
                            "6f 47 f7 05 5a b1 33 38 44 90 24 4e b4 f6 8e 42 " + //6
                            "65 f0 cc 9a a0 5b 85 f5 17 88 5c a9 c6 oe 94 68 " + //7
                            "30 ad 34 07 52 6a 45 bc c4 46 c2 6c e8 61 9b 41 " + //8
                            "01 d4 a5 12 3b cb f9 b6 a7 ee d3 56 dd 35 1e 99 " + //9
                            "67 a2 e5 80 d6 be 02 da 7e b8 ac f4 74 57 87 2d " + //a
                            "2b af f1 e2 b3 39 7f 21 3d 14 62 ea 1f b9 e9 0f " + //b
                            "fe 9c 71 eb 29 4a 50 10 64 de 91 65 4b 86 ce b0 " + //c
                            "d7 a4 d8 27 e3 4c 3c ff 5d 5e 95 7a bd c1 55 54 " + //d
                            "ab 72 31 b2 2f 58 9f f3 19 0b e4 ae 8b 1d 28 bb " + //e
                            "76 c0 15 75 84 cf a8 d2 73 db 79 08 8a 9e df 16"; //f

        String sBoxArray[] = sBoxString.split(" ");
        String staticMatrixArray[] = staticMatrixString.split(" ");

        runArrayIntoMatrix(staticMatrixArray, staticMatrix);

        runArrayIntoMatrix(sBoxArray, sBoxMatrix);

        plaintextArray = plaintextString.split(" ");

        runArrayIntoMatrix(plaintextArray, plaintextMatrix);
        keyArray = keyString.split(" ");
        runArrayIntoMatrix(keyArray, keyMatrix128);

        // XOR plaintext with key (add round key)
        plainTxtXORKeyMatrix = xorMatrices(plaintextMatrix, keyMatrix128);
    
        // Substitution
        stateMatrix = substitution(sBoxMatrix, plainTxtXORKeyMatrix);
        print("State matrix after substitution.");
        printMatrix(stateMatrix);

        // Shift rows
        shiftrows(stateMatrix);
        print("State matrix after shiftrows.");
        printMatrix(stateMatrix);
        // Mix columns
        mixColumns(stateMatrix, staticMatrix);
        print("State matrix after mixColumns.");
        printMatrix(stateMatrix);
        print("");
  }

    /*
    Method for returning a scanner 
    with the current system.in content.
    */
    private static Scanner promptForInput(String promtText, Scanner scanner) 
    {
        print(promtText);
        scanner = new Scanner(System.in);
        return scanner;
    }

    /*
     Method for getting the size of a string basically.
    */
    private static int getKeySize(String keyString)
    {
        int count = 0;
        for(int i = 0; i < keyString.length(); i++)
        {
            count++;
        }
        return count;
    }

    // shorter print statement. (laziness)
    private static void print(String text)
    {
        System.out.println(text);
    }

    private static String getStringFromScanner(Scanner scanner, String message)
    {
        String returnString = null;
        while(true)
        {
            returnString = scanner.nextLine();
            if(!returnString.isEmpty())
            {
                // PT was already captured above.
                return returnString;
            }
            else
            {
                scanner = promptForInput(message + "\n", scanner);
            } 
        }
    }

    private static String[][] runArrayIntoMatrix(String array[], String matrix[][])
    {
        int index = 0;
        // Assume array size is (matrix column or row size)^2
        // Also assume matrix is a perfect square.
        // Also assume not null (for now).
        int size = matrix.length;
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                // Run array into a matrix
                // column by column.
                index = j + (size*i);
                matrix[j][i] = array[index];
            }
        }
        return matrix;
    }

    /*
    * XOR values of matrices.
    */
    private static String[][] xorMatrices(String matrix1[][], String matrix2[][])
    {
        // Assuming matrices are same size and they are perfect squares.
        int size = matrix1.length;
        String newMatrix[][] = new String[size][size];
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                // Parse string items to hexidecimal.
                long first = Long.parseLong(matrix1[i][j], 16);
                long second = Long.parseLong(matrix2[i][j], 16);
                // XOR pt[i][j] with key[i][j]
                newMatrix[i][j] = String.valueOf(first ^ second);
            }
        }
        
        return newMatrix;
    }

    /*
    * Helper method to substitute matrices.
    */
    private static String[][] substitution(String matrixFrom[][], String matrixTo[][])
    {
        int size = matrixTo.length;
        String newMatrix[][] = new String[size][size];
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                String hexNums = Integer.toHexString(Integer.valueOf(matrixTo[i][j]));

                String tmpArray[] = hexNums.split("");
                int xCoord;
                int yCoord;
                // If only one argument is passed in the make it 0 argument not argument 0.
                xCoord = (tmpArray.length < 2) ? 0 : Integer.valueOf(tmpArray[0], 16);
                yCoord = (tmpArray.length < 2) ? Integer.valueOf(tmpArray[0],16) : Integer.valueOf(tmpArray[1],16);

                newMatrix[i][j] = matrixFrom[xCoord][yCoord];
            }
        }
        return newMatrix;
    }

    /*
    * Helper method to print a matrix.
    */
    private static void printMatrix(String matrix[][])
    {
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix.length; j++)
            {
                System.out.print(matrix[i][j]);
                System.out.print(" ");
            }
            print("");
        }
    }

    /*
    * Shift 1st row 0
    * Shift 2nd row 1
    * Shift 3rd row 2
    * Shift 4th row 3
    */
    private static void shiftrows(String stateMatrix[][])
    {
        String copyMatrix[][] = new String[4][4];
        // Clone original matrix
        for(int i = 0; i < stateMatrix.length; i++)
            copyMatrix[i] = stateMatrix[i].clone();

        for(int i = 0; i < stateMatrix.length; i++)
        {
            for(int j = 0; j < stateMatrix.length; j++)
            {
                // Shift depending on which row we are currently on.
                switch(i)
                {
                    case 1: stateMatrix[i][j] = copyMatrix[i][(j+1)%4];
                        break;
                    case 2: stateMatrix[i][j] = copyMatrix[i][(j+2)%4];
                        break;
                    case 3: stateMatrix[i][j] = copyMatrix[i][(j+3)%4];
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /*
    * Method to multiply each column
    * of the state matrix with
    * each row of our static matrix.
    */
    private static void mixColumns(String stateMatrix[][], String staticMatrix[][])
    {
        int size = stateMatrix.length;
        String copyMatrix[][] = new String[4][4];

        // Clone state matrix
        for(int k = 0; k < size; k++)
        {
            copyMatrix[k] = stateMatrix[k].clone();
        }

        // List to hold results of each column.
        // ArrayList<String> items = new ArrayList<>();

        for(int i = 0; i < size; i++)
        {
            // We will fill in the new matrix one column at a time
            // Since each column needs to be multiplied against the 
            // entire static matrix.
            String originalColumn[] = new String[4];
            String newColumn[] = new String[4];

            //TODO: put these in a four loop.
            originalColumn[0] = copyMatrix[0][i];
            originalColumn[1] = copyMatrix[1][i];
            originalColumn[2] = copyMatrix[2][i];
            originalColumn[3] = copyMatrix[3][i];

            newColumn = multiplyColumn(originalColumn, staticMatrix);

            stateMatrix[0][i] = newColumn[0];
            stateMatrix[1][i] = newColumn[1];
            stateMatrix[2][i] = newColumn[2];
            stateMatrix[3][i] = newColumn[3]; 
        }
    }

    /*
    * Helper function multiply
    * one column against the entire 
    * static matrix.
    */
    private static String[] multiplyColumn(String column[], String staticMatrix[][])
    {
        // We will fill in the new matrix one column at a time.
        String newColumn[] = new String[4];
        for(int i = 0; i < 4; i++)
        {
            // We will have a variable 
            // collectively xoring all the 
            // GF multiplications.
            long xor = 0x00;
            // For each static matrix row multiply 
            // against the state matrix column.
            for(int j = 0; j < 4; j++)
            {
                xor ^= galoisFieldMultiply(column[j], staticMatrix[i][j]);
                // Add the new item to the column.
                if(j == 3)
                {
                    newColumn[i] = Long.toHexString(xor%0x11b);
                }
            }
        }
        return newColumn;
    }

    /*
    * Multiply in GF(2^8)
    */
    private static long galoisFieldMultiply(String first, String second)
    {
        // Get hex value of input
        long firstHex = Long.valueOf(first, 16);
        // Need binary to check for leading one to determine XOR or not.
        String binaryInput = Integer.toBinaryString(Integer.parseInt(first, 16)); 
        // If we are multiplying by one return itself.
        if(second.equals("1"))
        {
            // Return itself 
            return firstHex;
        }
        else if(second.equals("2"))
        {
                // If the leading zero of a 8 bit hex we need to XOR with 0x11b
                if(binaryInput.charAt(0) == '1' && binaryInput.length() > 7)
                {
                    // Return the shift XOR with 283
                    return (firstHex << 1) ^ 0x11b;
                }
                //Just return shift
                return (firstHex << 1);
        }
        else
        {
            // Again check for leading zero; Java concatenates
            // leading zeroes by default so we have to be sure
            // that the length of the string is at least 8.
            if(binaryInput.charAt(0) == '1' && binaryInput.length() > 7)
            {
                return (firstHex << 1) ^ firstHex ^ 0x11b; 
            }
            return (firstHex << 1) ^ firstHex;   
        }
    }
}
