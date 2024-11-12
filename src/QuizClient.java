import java.io.*;
import java.net.*;

// 클라이언트 클래스: 서버에 연결하여 퀴즈 질문을 받고 답변을 전송
public class QuizClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // 기본 서버 주소
        int port = 1234; // 기본 서버 포트

        // 설정 파일(server_info.txt)에서 서버 주소와 포트 번호를 읽음
        try (BufferedReader configReader = new BufferedReader(new FileReader("server_info.txt"))) {
            serverAddress = configReader.readLine();
            port = Integer.parseInt(configReader.readLine());
        } catch (IOException e) {
            System.out.println("Configuration file missing, using default settings.");
        }

        // 서버와 연결 및 퀴즈 시작
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to quiz server at " + serverAddress + ":" + port);

            String serverMessage;
            // 서버로부터 메시지를 수신하고 적절히 처리
            while ((serverMessage = in.readLine()) != null) {
                // 질문 수신
                if (serverMessage.startsWith("QUESTION")) {
                    System.out.println(serverMessage);
                    System.out.print("Your answer: ");
                    String answer = userInput.readLine();
                    // 답변 전송
                    out.println("ANSWER " + serverMessage.split(" ")[1] + " " + answer);
                }
                // 피드백 수신
                else if (serverMessage.startsWith("FEEDBACK")) {
                    System.out.println(serverMessage);
                }
                // 최종 점수 수신
                else if (serverMessage.startsWith("SCORE")) {
                    System.out.println("Final score: " + serverMessage.split(" ")[1]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
