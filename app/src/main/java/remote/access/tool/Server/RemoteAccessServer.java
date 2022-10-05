package remote.access.tool.Server;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.StringTokenizer;
import java.io.*;
import java.net.*;
import java.awt.AWTException;

public class RemoteAccessServer {
    public static void main(String[] args) {
        int port = 8000;

        try (ServerSocket ss = new ServerSocket(port)) {
            while (true) {
                System.out.println("Server listening on port " + port + ".");
                try (Socket conn = ss.accept()) {
                    System.out.println(String.format("Server got a connection from client with address: %s:%d",
                            conn.getLocalAddress().toString(), conn.getPort()));

                    InputStream in = conn.getInputStream();
                    OutputStream out = conn.getOutputStream();

                    String badCommand = "BAD COMMAND\n";
                    String errorMessage = "INTERNAL SERVER ERROR\n";
                    String ok = "OK\n";

                    BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
                    BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));

                    DataOutputStream dataOut = new DataOutputStream(out);

                    String header = headerReader.readLine();
                    String command = new StringTokenizer(header).nextToken();

                    System.out.println("Command:" + command);

                    switch (command.toUpperCase()) {
                        case "REBOOT":
                            headerWriter.write(ok, 0, ok.length());
                            headerWriter.flush();
                            conn.close();
                            ServerUtils.systemReboot();
                            break;

                        case "SHUTDOWN":
                            headerWriter.write(ok, 0, ok.length());
                            headerWriter.flush();
                            conn.close();
                            ServerUtils.systemShutDown();
                            break;

                        case "SCREENSHOT":
                            try {
                                System.out.println("Taking a screenshot");
                                BufferedImage capture = ServerUtils.screenshot();

                                ByteArrayOutputStream temp = new ByteArrayOutputStream();
                                ImageIO.write(capture, "png", temp);
                                int imgSize = temp.size();

                                String message = "OK " + imgSize + " PNG\n";
                                headerWriter.write(message, 0, message.length());
                                headerWriter.flush();

                                dataOut.write(temp.toByteArray());
                                dataOut.flush();
                            } catch (IOException | AWTException e) {
                                headerWriter.write(errorMessage, 0, errorMessage.length());
                                headerWriter.flush();
                                e.printStackTrace(); // should log exception instead
                            }
                            break;

                        case "PROCESSES":
                            try {
                                BufferedReader processes = ServerUtils.getProcessList();
                                headerWriter.write(ok, 0, ok.length());
                                headerWriter.flush();
                                // String line;
                                // String lines = "";
                                // while ((line = processes.readLine()) != null) {
                                // lines += line + '\n';
                                // }
                                // dataOut.writeChars(lines);
                                // dataOut.writeChars("END\n");
                                // dataOut.flush();
                                String line;
                                while ((line = processes.readLine()) != null) {
                                    headerWriter.write(line, 0, line.length());
                                    headerWriter.newLine();
                                }
                                headerWriter.write("END");
                                headerWriter.newLine();
                                headerWriter.flush();
                            } catch (IOException e) {
                                headerWriter.write(errorMessage, 0, errorMessage.length());
                                headerWriter.flush();
                                e.printStackTrace();
                            }
                            break;

                        case "TEST":
                            headerWriter.write("Test\n", 0, 5);
                            headerWriter.flush();
                            break;

                        default:
                            headerWriter.write(badCommand, 0, badCommand.length());
                            headerWriter.flush();
                            break;
                    }
                    conn.close();
                } catch (Exception e) {
                    System.out.println("Probably a connection error: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
