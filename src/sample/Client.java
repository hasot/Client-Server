import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Runnable{

    private String userName;

    public Client(){
        this.userName = "";
    }

    public void setName(String name){
        this.userName = name;
    }
    public String getName(){
        return userName;
    }


    public static void main(String[] ar) {
        System.out.println("Welcome to Client!");


        int filesize=1022386;
        int bytesRead;
        int currentTot = 0;

        ArrayList<String> selectFiles;
        int serverPort = 6666; // здесь обязательно нужно указать порт к которому привязывается сервер.
        String address = "127.0.0.1"; // это IP-адрес компьютера, где исполняется наша серверная программа. 
        // Здесь указан адрес того самого компьютера где будет исполняться и клиент.


        try {
            InetAddress ipAddress = InetAddress.getByName(address); // создаем объект который отображает вышеописанный IP-адрес.
            Socket socket = new Socket(ipAddress, serverPort); // создаем сокет используя IP-адрес и порт сервера.
            Scanner sc = new Scanner(System.in);

            System.out.println("Соединение с сервером устрановлено. Теперь вы можете сделать интересующее вас действие");
            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом. 
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            System.out.println("Здравствуйте , выберите действие которое хотели бы сделать");
            boolean f = true;
            while (f) {
                System.out.println("1 Запрос списка доступных файлов");
                System.out.println("2 Скачать один файл и сохранить его локально.");
                System.out.println("3 Завершить сессию.");
                Scanner scan = new Scanner(System.in);
                String s = scan.nextLine();
                String line = null;
                if (s.equals("1")) {
                    out.writeUTF("1");
                    out.flush(); // заставляем поток закончить передачу данных.
                    line = in.readUTF(); // ждем пока сервер отошлет строку текста.
                    System.out.println("Папка содержит  файлы: " + line);
                }
                if (s.equals("2")) {

                    out.writeUTF("2");
                    System.out.println("Введите имя файла");
                    out.writeUTF(scan.nextLine());
                    byte[] bytearray = new byte[filesize];
                    InputStream is = socket.getInputStream();

                    FileOutputStream fos = new FileOutputStream("path");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bytesRead = is.read(bytearray, 0, bytearray.length);
                    currentTot = bytesRead;
                    do {
                        bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                        if (bytesRead >= 0) currentTot += bytesRead;

                    }
                    while (bytesRead > -1);
                    System.out.println("Файл получен");
                    bos.write(bytearray, 0, currentTot);
                    bos.flush();
                    fos.close();
                    bos.close();
                }
                if (s.equals("3")) {
                    socket.close();
                    sin.close();
                    sout.close();
                    in.close();
                    out.close();
                    f = false;
                }
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}