package com.kolomytsev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
	private final Socket socket;  //  точка подключения
	private final DataOutputStream out; // вход поток
	private final DataInputStream in; // выход поток

	public Client() throws IOException {
		socket = new Socket("localhost", 5678); // инициализация
		out = new DataOutputStream(socket.getOutputStream()); // инициализация
		in = new DataInputStream(socket.getInputStream()); // инициализация

		setSize(300, 300);  // размер окна
		JPanel panel = new JPanel(new GridLayout(2, 1));  // 1 столбец и 2 строки

		JButton btnSend = new JButton("SEND");  // кнопка
		JTextField textField = new JTextField(); // строка ввода

		btnSend.addActionListener(a -> { // установим к кнопке слушателя через лямбду

			String[] cmd = textField.getText().split(" ");  // разбивам команду по пробелам какую часть команды выводить
			if ("upload".equals(cmd[0])) { //
				sendFile(cmd[1]);  // для передачи файла
			} else if ("download".equals(cmd[0])) {
				getFile(cmd[1]);
			}
		});

		panel.add(textField);  // добавили текстовое поле
		panel.add(btnSend); // добавили кнопу

		add(panel); // включили поле

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {  // закрытие на крестик
				super.windowClosing(e);
				sendMessage("exit");
			}
		});
		setVisible(true);  // сделали все видимым
	}

	private void getFile(String filename) {  // метод для передачи файла клиенту
		// TODO: 14.06.2021
		try {
			File file = new File("server" + File.separator + filename); // путь (создаем объект класса файл)
			if (!file.exists()) { // если нет такого файла
				throw  new FileNotFoundException();
			}

			long fileLength = file.length(); // читаем размер
			FileInputStream fis = new FileInputStream(file); // файл оборачиваем в исходящий поток (что бы корректно считать)

			out.writeUTF("download"); // говорим что что то хотим загрузить
			out.writeUTF(filename); // фаил с таким именем
			out.writeLong(fileLength); // с такой длинной

			int read = 0; // читаем
			byte[] buffer = new byte[8 * 1024]; // создаем буфер [разбиваем таким образом для более быстрой передачи файлов и более корректной ее сборки на том конце]
			while ((read = fis.read(buffer)) != -1) { // считываем в цикле до тех пор пока что то есть
				out.write(buffer, 0, read); // и передаем с указанием его размерности
			}

			out.flush();  // и освобождаем канал

			String status = in.readUTF(); // запросим ответ у сервера
			System.out.println("sending status: " + status); // выведем статус в консоль

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendFile(String filename) {  // метод для передачи файла серверу
		try {
			File file = new File("client" + File.separator + filename); // путь (создаем объект класса файл)
			if (!file.exists()) { // если нет такого файла
				throw  new FileNotFoundException();
			}

			long fileLength = file.length(); // читаем размер
			FileInputStream fis = new FileInputStream(file); // файл оборачиваем в исходящий поток (что бы корректно считать)

			out.writeUTF("upload"); // говорим чтo хотим загрузить
			out.writeUTF(filename); // фаил с таким именем
			out.writeLong(fileLength); // с такой длинной

			int read = 0; // читаем
			byte[] buffer = new byte[8 * 1024]; // создаем массив байт [разбиваем таким образом для более быстрой передачи файлов и более корректной ее сборки на том конце]
			while ((read = fis.read(buffer)) != -1) { // считываем в цикле до тех пор пока что то есть
				out.write(buffer, 0, read); // и передаем с указанием его размерности
			}

			out.flush();  // и освобождаем канал

			String status = in.readUTF(); // запросим ответ у сервера
			System.out.println("sending status: " + status); // выведем статус в консоль

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String message) {  // Добавили метод для сообщения
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {  // запуск клиента
		new Client();
	}
}
