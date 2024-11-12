import java.io.*;
import java.net.*;
import java.util.*;

// 서버 클래스: 퀴즈 서버를 실행하고 클라이언트 연결을 처리
public class QuizServer {
    private static final int PORT = 1234; // 서버 포트 번호
    private static final String[][] QUESTIONS = {
        {"What is the capital of France?", "Paris"},
        {"What is 5 + 7?", "12"},
        {"What is the largest planet?", "Jupiter"}
    };

    public static void main(String[] args) {
        // 서버 소켓 생성 및 클라이언트 연결 수신 대기
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz server started on port " + PORT);

            // 클라이언트가 연결될 때마다 새로운 스레드를 생성하여 처리
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트와의 연결을 처리하는 핸들러 클래스
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private int score = 0; // 클라이언트의 총 점수

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                // 각 질문에 대해 클라이언트와 상호작용
                for (int i = 0; i < QUESTIONS.length; i++) {
                    // 질문 전송
                    out.println("QUESTION " + (i + 1) + ": " + QUESTIONS[i][0]);
                    // 클라이언트의 답변 수신
                    String answer = in.readLine();

                    // 답변을 올바른 형식으로 파싱하여 정답과 비교
                    String correctAnswer = QUESTIONS[i][1];
                    String[] answerParts = answer.split(" ", 3); // "ANSWER <number> <answer>" 형식으로 나누기
                    if (answerParts.length >= 3 && answerParts[2].equalsIgnoreCase(correctAnswer)) {
                        score++;
                        out.println("FEEDBACK Correct!");
                    } else {
                        out.println("FEEDBACK Incorrect!");
                    }
                }

                // 최종 점수 전송
                out.println("SCORE " + score);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

