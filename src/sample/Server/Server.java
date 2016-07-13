
import java.net.*;
import java.io.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Server implements Runnable {

    Socket connection;


    public Server(Socket socket){
        this.connection = socket;

    }


    public  Server() {


        }


    public static void main(String[] ar) throws IOException {

        System.out.println("Нажмите Enter чтобы выбрать папку с файлами...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        JFrame d = new JFrame();

        JFileChooser dialog = new JFileChooser();
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (dialog.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
            File folder = dialog.getSelectedFile();
            String[] files = folder.list(new FilenameFilter() {
                @Override
                public boolean accept(File folder, String name) {
                    return name.endsWith("");
                }
            });
            File[] filesInDir = folder.listFiles();
            ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(files));
            String line = "" + arrayList;


            int port = 6666; // случайный порт (может быть любое число от 1025 до 65535)
            ServerSocket ss = null;

        try {
            // создаем сокет сервера и привязываем его к вышеуказанному порту
            // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
            System.out.println("Добро пожаловать ");
            ss = new ServerSocket(port);


        } catch (IOException e) {
            System.out.println("Не могу подсоединиться к порту");
            System.exit(-1);
        }

            while (true) {
                try {
                    System.out.println("Ожиданию клиент");
                    Socket socket = ss.accept();
                    Runnable runnable = new Server(socket);
                    Thread thread = new Thread(runnable);
                    thread.start();
                    System.out.println("Клиент подсоединен!");
                    // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
                    while(true){
                        InputStream sin = socket.getInputStream();
                    OutputStream sout = socket.getOutputStream();
                    // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                    DataInputStream in = new DataInputStream(sin);
                    DataOutputStream out = new DataOutputStream(sout);

                        String lun = in.readUTF();
                    if (lun.equals("1")) {
                        // ; // ожидаем пока клиент пришлет строку текста.

                        System.out.println("Отсылаем клиенту информацию о содержимом");
                        out.writeUTF(line); // отсылаем клиенту обратно ту самую строку текста.
                        out.flush(); // заставляем поток закончить передачу данных.
                        System.out.println("Жду следующей команды...");
                        System.out.println();
                    }

                    if (lun.equals("2")) {

                        String ff = in.readUTF();
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (ff.equals(arrayList.get(i))) {
                                File transferFile = new File(filesInDir[i].toString());
                                byte[] bytearray = new byte[(int) transferFile.length()];
                                FileInputStream fin = new FileInputStream(transferFile);
                                BufferedInputStream bin = new BufferedInputStream(fin);
                                bin.read(bytearray, 0, bytearray.length);
                                OutputStream os = socket.getOutputStream();
                                System.out.println("Sending Files...");
                                os.write(bytearray, 0, bytearray.length);
                                os.flush();
                                os.close();
                                System.out.println("File transfer complete");
                                System.out.println("Жду следующей команды...");
                            }
                        }
                    }
                        if (lun.equals("3")) {
                            socket.close();
                            sin.close();
                            sout.close();
                            in.close();
                            out.close();
                        }
                    }

                } catch (IOException e) {
                    System.exit(-1);
                }
            }
        }
    }


    @Override
    public void run() {

    }
}