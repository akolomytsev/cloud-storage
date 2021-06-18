package com.kolomytsev;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	// TODO: 14.06.2021
	// организовать корректный вывод статуса
	// подумать почему так реализован цикл в ClientHandler
	// что бы при разбивании файла на части при отправке и приемке не была утеряна чатсь данных
	public Server() {
		ExecutorService service = Executors.newFixedThreadPool(4); //отвечает за многоканальность
		try (ServerSocket server = new ServerSocket(5678)){ // подключение к серверу
			System.out.println("Server started"); // выводим в консоль сообщение
			while (true) {
				service.execute(new ClientHandler(server.accept())); // ожидание подключения
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) { // запуск сервера
		new Server();
	}
}
