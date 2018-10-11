package com.nameless;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Created by boysz on 2018/10/12.
 */
public class SocketTest {

    @Test
    public void testSocketServer() throws IOException {
        int servPort = 8800;
        // Create a server socket to accept client connection requests
        ServerSocket servSock = new ServerSocket(servPort);

        int recvMsgSize;   // Size of received message
        byte[] receiveBuf = new byte[32];  // Receive buffer

        while (true) { // Run forever, accepting and servicing connections
            Socket clntSock = servSock.accept();     // Get client connection

            SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
            System.out.println("Handling client at " + clientAddress);

            InputStream in = clntSock.getInputStream();
            OutputStream out = clntSock.getOutputStream();

            // Receive until client closes connection, indicated by -1 return
            while ((recvMsgSize = in.read(receiveBuf)) != -1) {
                out.write(receiveBuf, 0, recvMsgSize);
            }
            clntSock.close();  // Close the socket.  We are done with this client!
        }
    /* NOT REACHED */
    }

    @Test
    public void testSocketClient() {
        try (Socket client = new Socket("127.0.0.1", 8800)) {
            byte[] data = "Helllllllllllll".getBytes();
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            out.write(data);
            // Receive the same string back from the server
            int totalBytesRcvd = 0;  // Total bytes received so far
            int bytesRcvd;           // Bytes received in last read
            while (totalBytesRcvd < data.length) {
                if ((bytesRcvd = in.read(data, totalBytesRcvd,data.length - totalBytesRcvd)) == -1)
                    throw new SocketException("Connection closed prematurely");
                totalBytesRcvd += bytesRcvd;
            }  // data array is full

            System.out.println("Received: " + new String(data));

//            InputStream is = new ByteArrayInputStream("hello ".getBytes("UTF-8"));
//
//            client.setSoTimeout(10000);
//            //获取键盘输入
//            BufferedReader input = new BufferedReader(new InputStreamReader(is));
//            //获取Socket的输出流，用来发送数据到服务端
//            PrintStream out = new PrintStream(client.getOutputStream());
//            //获取Socket的输入流，用来接收从服务端发送过来的数据
//            BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
//            boolean flag = true;
//            while(flag){
//                System.out.print("输入信息：");
//                String str = input.readLine();
//                //发送数据到服务端
//                out.println(str);
//                if("bye".equals(str)){
//                    flag = false;
//                }else{
//                    try{
//                        //从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
//                        String echo = buf.readLine();
//                        System.out.println(echo);
//                    }catch(SocketTimeoutException e){
//                        System.out.println("Time out, No response");
//                    }
//                }
//            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
