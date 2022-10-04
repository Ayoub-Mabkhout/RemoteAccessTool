package remote.access.tool.Client;

import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;

public class RemoteAccessClient {

    private static Scanner sc = new Scanner(System.in);

    private static int getCommand() {
        System.out.println("____________");
        System.out.println("What command would you like to issue to the remote system?\n");
        System.out.println("1. Take a screenshot.");
        System.out.println("2. Get the list of processes currently running.");
        System.out.println("3. Reboot the remote system.");
        while (true) {
            try {
                System.out.print("Your choice: ");
                int command = Integer.parseInt(sc.next());
                if (command <= 0 || command > 3) {
                    sc.close();
                    throw new InputMismatchException();
                }
                System.out.println("____________\n\n");
                return command;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid command.\n");
            } catch (Exception e) {
                e.printStackTrace();
                sc.close();
                System.exit(1);
            }
        }
    }

    private static String getHost() {

        String host;
        System.out.println("____________");
        System.out.println("Would you want to connect local (1) or to a remote (2) server.");
        System.out.println("Enter (0) if you wish to leave the program instead.");
        while (true) {
            try {
                System.out.print("Your choice: ");
                int choice = sc.nextInt();
                if (choice == 0) {
                    System.out.println("Leaving RAT program...\n");
                    System.exit(0);
                }
                if (choice == 2) {
                    System.out.print("Please enter the remote server's host name or IPv4 address: ");
                    host = sc.next();
                } else
                    host = "localhost";
                System.out.println("____________\n\n");
                return host;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid choice.\n");
            } catch (Exception e) {
                e.printStackTrace();
                sc.close();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {

        do {
            String host = getHost();
            int port = 8000;
            try (Socket conn = new Socket(host, port)) {
                System.out.println("Successfully opened connection with host " + host + " at port " + port + ".\n");
                InputStream in = conn.getInputStream();
                OutputStream out = conn.getOutputStream();

                BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
                BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));
                DataInputStream dataIn = new DataInputStream(in);

                int command = getCommand();
                String header;
                StringTokenizer tokenizer;
                switch (command) {
                    case 1:
                        headerWriter.write("SCREENSHOT\n");
                        headerWriter.flush();

                        header = headerReader.readLine();
                        if (header.equals("BAD COMMAND") || header.equals("INTERNAL SERVER ERROR"))
                            throw new Exception(header);

                        tokenizer = new StringTokenizer(header, " ");
                        if (tokenizer.nextToken().equals("OK"))
                            System.out.println("Screenshot Taken\n");
                        int imgSize = Integer.parseInt(tokenizer.nextToken());
                        String fileType = tokenizer.nextToken();

                        Thread.sleep(500);

                        byte[] imgBytes = new byte[imgSize];
                        dataIn.readFully(imgBytes);

                        try (FileOutputStream fileOut = new FileOutputStream("ClientData/screenshot." + fileType)) {
                            fileOut.write(imgBytes, 0, imgSize);
                        }
                        break;

                    case 2:
                        headerWriter.write("PROCESSES\n");
                        headerWriter.flush();

                        header = headerReader.readLine();
                        System.out.println("header: " + header);
                        if (header.equals("BAD COMMAND") || header.equals("INTERNAL SERVER ERROR"))
                            throw new Exception(header);

                        tokenizer = new StringTokenizer(header, " ");
                        if (tokenizer.nextToken().equals("OK"))
                            System.out.println("Here is the list of processes running on the remote server: \n");

                        try (FileWriter fileOut = new FileWriter("ClientData/processes_list.txt")) {
                            String line;
                            int i = 0;
                            while (true) {
                                line = headerReader.readLine();
                                // TO FIX: For some reason does not manage to detect the EOI token, throws
                                // exception.
                                if (line.contains("END") || line.contains("E N D")) {
                                    System.out.println("\nReached end of input\n");
                                    fileOut.write("\nReached end of input\n");
                                    break;
                                }
                                System.out.println(line);
                                fileOut.write(line + '\n');
                            }
                            break;
                        }
                    case 3:
                        headerWriter.write("REBOOT\n");
                        headerWriter.flush();

                        header = headerReader.readLine();
                        System.out.println("header: " + header);
                        if (header.equals("BAD COMMAND") || header.equals("INTERNAL SERVER ERROR"))
                            throw new Exception(header);

                        tokenizer = new StringTokenizer(header, " ");
                        if (tokenizer.nextToken().equals("OK"))
                            System.out.println("Remote Server Shutdown.");
                        Thread.sleep(10000);
                        conn.close();
                        break;

                    default:
                        throw new Exception("Invalid Command");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
