package com.kolomytsev;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable { // обработчик подключения, опрос ведется постоянно
	private final Socket socket; // переменная
	public ClientHandler(Socket socket) {
		this.socket = socket;
	} // проинизиализировали


	@Override // переопределяем run
	public void run() {
		try (
				DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // входяший поток от подключения (socket)
				DataInputStream in = new DataInputStream(socket.getInputStream()) //исходящий поток от подключения (socket)
		) {
			System.out.printf("Client %s connected\n", socket.getInetAddress());  // сообщение в консоль
			while (true) {
				String command = in.readUTF(); // создали переменную команд
				if ("upload".equals(command)) {  // пришла такая команда
					try {
						File file = new File("server"  + File.separator + in.readUTF());  // читаем наименование
						if (!file.exists()) {  // проверяем есть ли такой файл
							 file.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(file); // записываем файл
						long size = in.readLong();  // читаем размерность

						byte[] buffer = new byte[8 * 1024]; // буфер делаем того же размера [разбиваем таким образом для более быстрой передачи файлов и более корректной ее сборки]

						for (int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) { // такая запись сделана для корректной сборки файла и что бы лишние куски не выкидывало
							int read = in.read(buffer); // считываем в буфер
							fos.write(buffer, 0, read);  // из буфера в файлик
						}
						fos.close(); // передаем статус
						out.writeUTF("OK");
					} catch (Exception e) {
						out.writeUTF("FATAL ERROR");
					}
				}

				if ("download".equals(command)) { // реализовать загрузку с сервера,
					// по идее можно в отдельный класс сунуть
					// TODO: 14.06.2021
					try {
						File file = new File("client"  + File.separator + in.readUTF());  // читаем наименование
						if (!file.exists()) {  // проверяем есть ли такой файл
							file.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(file); // записываем файл
						long size = in.readLong();  // читаем размерность

						byte[] buffer = new byte[8 * 1024]; // буфер делаем того же размера [разбиваем таким образом для более быстрой передачи файлов и более корректной ее сборки]

						for (int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) { // такая запись сделана для корректной сборки файла и что бы лишние куски не выкидывало
							int read = in.read(buffer); // считываем в буфер
							fos.write(buffer, 0, read);  // из буфера в файлик
						}
						fos.close(); // передаем статус
						out.writeUTF("OK");
					} catch (Exception e) {
						out.writeUTF("FATAL ERROR");
					}
				}
				if ("exit".equals(command)) { // если набрать это сообщение то выходишь из програмы
					System.out.printf("Client %s disconnected correctly\n", socket.getInetAddress()); //  сообщение в консоль
					break; // выход
				}

				//System.out.println(command);  // пришла команда и мы ее в консоль отпечатали
				//out.writeUTF(command);  // и отправляем обратно
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
