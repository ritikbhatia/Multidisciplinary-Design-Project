import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class MapIterator {

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

    public static int[][] IterateTextFile(String fileName) {
        int[][] fileMap = new int[HEIGHT][WIDTH];

        File file = new File(fileName);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found!");
        }
        int count = HEIGHT - 1;
        String st;
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

    public static void printExploredResultsToFile(int[][] results, String fileName) {

        if (mapDescriptorP1 == null)
            init();

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            StringBuilder sb = new StringBuilder();
            StringBuilder hexSB = new StringBuilder();

            // count the amount of known grids to adjust the padding of '11's
            knownGrids = 0;

            // padding
            sb.append("11" + System.getProperty("line.separator"));
            hexSB.append("11");

            // iterate through the 2d map
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
            // padding
            sb.append("11");
            hexSB.append("11");

            // write the string to the file
            bw.write(sb.toString());

            // save the descriptor as a string in the class
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

    public static void printExploredResultsToHex(String fileName) {

        // initialised to null
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);

            // convert the string of 1s and 0s to hex, then write it to the filename
            // provided
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

    public static void printObstacleResultsToFile(int[][] results, String fileName) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        // count the number of known grids, will return 0 if divisible by 4
        int numKnownGrids = knownGrids % 4;
        if (numKnownGrids != 0)
            numKnownGrids += 1;

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            // bw.write(formatStringToHexadecimal(results));
            StringBuilder sb = new StringBuilder();
            StringBuilder hexSB = new StringBuilder();

            System.out.print("MapDescriptorP1.length: " + mapDescriptorP1.length); // 20
            System.out.print("mapDescriptorP1[0].length: " + mapDescriptorP1[0].length); // 15
            System.out.println("\n");

            for (int w = mapDescriptorP1.length - 1; w >= 0; w--) {
                for (int h = 0; h < mapDescriptorP1[0].length; h++) {

                    // if its explored, then input the information for it
                    if (mapDescriptorP1[w][h] == 1) {
                        if (results[w][h] == ExplorationTypes.toInt("EMPTY")) {
                            sb.append(0);
                            hexSB.append(0);
                        } else if (results[w][h] == ExplorationTypes.toInt("OBSTACLE")) {
                            sb.append(1);
                            hexSB.append(1);
                        }

                        // if(w==0 && h==mapDescriptorP1[0].length-1) { sb.append(0); sb.append(0);
                        // hexSB.append(0); hexSB.append(0); }

                    }

                }
                sb.append(System.getProperty("line.separator"));
            }

            bw.write(sb.toString());

            // save the descriptor as a string in the class
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

    public static void printObstacleResultsToHex(String fileName) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);

            // convert the string of 1s and 0s to hex, then write it to the filename
            // provided
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

    public static String ArraytoString(int[][] intresults) {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(intresults);
        return sb.toString();
    }

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
            // if left with 0 character -> length = -1 ; start = length
            // if left 1 character -> length = 0 ; start = length + 1
            //
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
            if (decimal < 16) {
                // if it is less than 16, the hexadecimal produced will only be one digit
                // instead of 2
                stringBuilder.append("0");
            }
            hexStr = Integer.toString(decimal, 16);
            stringBuilder.append(hexStr);

        }
        return stringBuilder.toString();
    }

}