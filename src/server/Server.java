package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas Penha de Moura - 1208977
 */
public class Server {

    private String ID_C;
    private String T_C_S_encrypt;
    private String T_C_S;
    private String K_C_S;
    private String K_S;
    private String M5;
    private String M5_Pt1_encrypt;
    private String M5_Pt1_open;
    private String request;
    private String reply;

    private String M6;

    public void startServer() throws IOException {
        String clientMessage;

        ServerSocket welcomeSocket = new ServerSocket();
        welcomeSocket.setReuseAddress(true);
        welcomeSocket.bind(new InetSocketAddress(34567));

        while (true) {

            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader messageFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            DataOutputStream messageToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientMessage = messageFromClient.readLine();

            K_S = newKeyGen("KS");
            opemMsg(clientMessage);
            messageGen();

            messageToClient.writeBytes(M6 + '\n');
        }
    }

    private void opemMsg(String mensagem) {
        String splitMsg[] = mensagem.split(";");
        //separa a mensagem em suas diversas partes
        M5_Pt1_encrypt = splitMsg[0];
        T_C_S_encrypt = splitMsg[1];
        request = splitMsg[2];

        //abre a parte T_C_S e separa suas diversas partes
        T_C_S = Encode.decode(T_C_S_encrypt, K_S);
        String TCS[] = T_C_S.split(";");
        ID_C = TCS[0];
        K_C_S = TCS[2];

    }

    private void messageGen() {
        reply = "Desenvolvido por: Lucas Moura - 1208977";
        M6 = Encode.encode(reply, K_C_S);
    }

    public String newKeyGen(String passwd) {
        String retorno = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(passwd.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder hash = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                hash.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //System.out.println("Hex format : " + sb.toString());
            retorno = hash.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retorno.substring(0, 16);
    }
}
