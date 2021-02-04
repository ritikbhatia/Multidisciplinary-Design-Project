import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class MapIterator {

    // Dimensions of grid map 
    final static int WIDTH = 15;
    final static int HEIGHT = 20;

    static int[][] mapDescriptorP1;
    static int[][] mapDescriptorP2;

    static String mapDescriptorP1Hex;
    static String mapDescriptorP2Hex;

    static int knownGrids;

    public MapIterator() {
        mapDescriptorP1 = new int[HEIGHT][WIDTH];
        mapDescriptorP2 = new int[HEIGHT][WIDTH];

        knownGrids = 0;
    }

    public static void init() {
        mapDescriptorP1 = new int[HEIGHT][WIDTH];
        mapDescriptorP2 = new int[HEIGHT][WIDTH];

        knownGrids = 0;
    }

    // Parse text file to extract map information 
    public static int[][] IterateTextFile(String fileName) {

        // Initialise file reading object variables 
        int[][] fileMap = new int[HEIGHT][WIDTH];
        File file = new File(fileName);
        BufferedReader br = null;

        // Read TXT file 
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found!");
        }

        int count = HEIGHT - 1;
        String st;

        // Translate map information using ENUMs 
        try {
            while ((st = br.readLine()) != null && count >= 0) {
                for (int i = 0; i < st.length(); i++) {
                    if (Character.getNumericValue(st.charAt(i)) == ExplorationTypes.toInt("EMPTY"))
                        fileMap[count][i] = ExplorationTypes.toInt("UNEXPLORED_EMPTY");
                    else if (Character.getNumericValue(st.charAt(i)) == ExplorationTypes.toInt("OBSTACLE"))
                        fileMap[count][i] = ExplorationTypes.toInt("UNEXPLORED_OBSTACLE");

                }
                count--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileMap;
    }

    // Parse explored grid map results and store in TXT file 
    public static void printExploredResultsToFile(int[][] results, String fileName) {
        
        // Initialise file reading object variables 
        BufferedWriter bw = null;
        FileWriter fw = null;

        if (mapDescriptorP1 == null)
            init();

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            StringBuilder sb = new StringBuilder();
            StringBuilder hexSB = new StringBuilder();

            // Adjust padding of '1's by counting known grids
            knownGrids = 0;

            // Fix padding 
            sb.append("11" + System.getProperty("line.separator"));
            hexSB.append("11");

            // Nested loops to process 2D map
            for (int w = mapDescriptorP1.length - 1; w >= 0; w--) {
                for (int h = 0; h < mapDescriptorP1[0].length; h++) {
                    
                    if (results[w][h] == ExplorationTypes.toInt("EMPTY")
                            || results[w][h] == ExplorationTypes.toInt("OBSTACLE")) {
                        sb.append(1);
                        mapDescriptorP1[w][h] = 1;
                        hexSB.append(1);
                        knownGrids++;
                    } else {
                        sb.append(0);
                        mapDescriptorP1[w][h] = 0;
                        hexSB.append(0);
                    }
                }

                sb.append(System.getProperty("line.separator"));
            }

            // Fix padding
            sb.append("11");
            hexSB.append("11");

            // Write string to TXT file
            bw.write(sb.toString());

            // Store descriptor in class as string 
            mapDescriptorP1Hex = hexSB.toString();

        } catch (IOException e) {
            System.out.println("Not possible to write!");
        }

        finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Convert grid exploration results from binary to hexadecimal format 
    public static void printExploredResultsToHex(String fileName) {

        // Initialised to NULL
        BufferedWriter bw = null;
        FileWriter fw = null;

        // Convert string from binary to hex, then write to given file
        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);

            mapDescriptorP1Hex = formatStringToHexadecimal(mapDescriptorP1Hex);
            System.out.println(mapDescriptorP1Hex);
            bw.write(mapDescriptorP1Hex);
        } catch (IOException e) {
            System.out.println("Not possible to write!");
        }

        finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Write grid map obstacle positions to TXT file 
    public static void printObstacleResultsToFile(int[][] results, String fileName) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        // Count known grids, return 0 if divisible by 4
        int numKnownGrids = knownGrids % 4;
        if (numKnownGrids != 0)
            numKnownGrids += 1;

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            
            StringBuilder sb = new StringBuilder();
            StringBuilder hexSB = new StringBuilder();

            System.out.print("MapDescriptorP1.length: " + mapDescriptorP1.length); // twenty (TODO: ?)
            System.out.print("mapDescriptorP1[0].length: " + mapDescriptorP1[0].length); // fifteen (TODO: ?)
            System.out.println("\n");

            for (int w = mapDescriptorP1.length - 1; w >= 0; w--) {
                for (int h = 0; h < mapDescriptorP1[0].length; h++) {

                    // If map explored, input results accordingly
                    if (mapDescriptorP1[w][h] == 1) {
                        if (results[w][h] == ExplorationTypes.toInt("EMPTY")) {
                            sb.append(0);
                            hexSB.append(0);
                        } else if (results[w][h] == ExplorationTypes.toInt("OBSTACLE")) {
                            sb.append(1);
                            hexSB.append(1);
                        }
                    }
                }

                sb.append(System.getProperty("line.separator"));
            }

            bw.write(sb.toString());

            // Store descriptor in class as string
            mapDescriptorP2Hex = hexSB.toString();

        } catch (IOException e) {
            System.out.println("Not possible to write!");
        }

        finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Convert grid map obstacle positions from binary to hexadecimal format 
    public static void printObstacleResultsToHex(String fileName) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);

            // Convert string from binary to hex, then write to given file
            mapDescriptorP2Hex = formatStringToHexadecimal(mapDescriptorP2Hex);
            System.out.println(mapDescriptorP2Hex);
            bw.write(mapDescriptorP2Hex);

        } catch (IOException e) {
            System.out.println("Not possible to write!");
        }

        finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Convert array to string 
    public static String ArraytoString(int[][] intresults) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("");
        sb.append(intresults);
        
        return sb.toString();
    }

    // Convert array to hex 
    public static void ArraytoHex(int[][] intresults) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("11");
        for (int h = 0; h < intresults[0].length; h++) {
            for (int w = 0; w < intresults.length; w++) {
                sb.append(intresults[w][h]);
            }
        }
        sb.append("11");
        
        String hexi = formatStringToHexadecimal(sb.toString());
        
        System.out.println(hexi);
    }

    // Format String to Hex 
    public static String formatStringToHexadecimal(String string) {
        int start = 0;
        int end = 7;
        String sub;
        int decimal;
        String hexStr = "";

        StringBuilder stringBuilder = new StringBuilder();
        
        try {
            while (start != string.length()) {
                sub = string.substring(start, end + 1);
                decimal = Integer.parseInt(sub, 2);

                if (decimal < 16)
                    stringBuilder.append("0");

                hexStr = Integer.toString(decimal, 16);
                stringBuilder.append(hexStr);
                start += 8;
                end += 8;
            }
        } catch (Exception e) {
            System.out.println("Doing exception for string to hex");
            StringBuilder sb = new StringBuilder();

            int length = string.length() - start;

            // For 0 remaining characters, (len = 0) (start = len)
            // For 1 remaining character, (len = 0) (start = len + 1)
            int count = 8;
            while (length > 0) {
                sb.append(string.charAt(start));
                start += 1;
                length -= 1;
                count -= 1;
            }

            while (count > 0) {
                sb.append("0");
                count -= 1;
            }

            decimal = Integer.parseInt(sb.toString(), 2);

            // If decimal < 16, Hex will be single digit instead of double digit 
            if (decimal < 16) {
                stringBuilder.append("0");
            }

            hexStr = Integer.toString(decimal, 16);
            stringBuilder.append(hexStr);
        }

        return stringBuilder.toString();
    }

}
