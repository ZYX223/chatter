package cn.johnyu.im.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Hashtable;


public class ImServer {
	
	private static Hashtable<String, ServerClient> room=new Hashtable<String,ServerClient>();
	
	public static void main(String[] args) throws Exception{
		
		ServerSocket server=new ServerSocket(9000);
		while(true) {
			final Socket socket=server.accept();
			new Thread(new ServerClient(socket, room)).start();
		}
	}
}
