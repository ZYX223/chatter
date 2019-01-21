package cn.johnyu.im.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class ServerClient implements Runnable{
	private String userName;
	private Socket socket;
	private Hashtable<String, ServerClient> chatRoom;
	
	private OutputStream out;
	private BufferedReader reader;
	
	private int flag=0;
	
	public ServerClient(Socket socket, Hashtable<String, ServerClient> chatRoom) {
		super(); 
		this.socket = socket;
		this.chatRoom = chatRoom;
		try {
			out=socket.getOutputStream();
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public String getUserName() {
		return userName;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void setChatRoom(Hashtable<String, ServerClient> chatRoom) {
		this.chatRoom = chatRoom;
	}

	@Override
	public void run() {
		try {
			String userName=reader.readLine();
			this.userName=userName;
			if (chatRoom.get(userName)==null) {
				chatRoom.put(userName, this);
				System.out.println("server rec username: "+userName+" connect!");
				flag=1;
			}
			else {
				flag=0;
			}
			String otherWord=null;
			while((otherWord=reader.readLine())!=null) {
				if(flag==0) {
					out.write(("same\n").getBytes());
					continue;
				}
				if(otherWord.trim().equals("bye")) {
					out.write(("bye "+userName+"\n").getBytes());
					break;
				}
				if(otherWord.trim().equals("list")) {
					for(String un:chatRoom.keySet()){
						if(un!=userName) {
							out.write(((un+"\n")).getBytes());
							}
					}
					out.write((("over!\n")).getBytes());
					continue;
				}
				String[] wordParts=otherWord.split(":");
				if("all".equals(wordParts[0])) {
					//广播
					for(String un:chatRoom.keySet()) {
						ServerClient targetClient=chatRoom.get(un);
						if(!targetClient.equals(this)) {
						//	targetClient.getSocket().getOutputStream().write((userName+" to all :"+wordParts[1]+"\n").getBytes());
						System.out.println(userName+":"+wordParts[1]);
					}
						}
				}
				else {
					//p2p
					ServerClient targetClient=chatRoom.get(wordParts[0]);
					if(wordParts.length==1) {
						System.out.println(userName+" to "+wordParts[0]+":null");
					}
					else {
						targetClient.getSocket().getOutputStream().write((wordParts[1]+"\n").getBytes());
						System.out.println(userName+" to "+wordParts[0]+":"+wordParts[1]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				chatRoom.remove(userName);
				System.out.println(userName+":"+"finally....close");
				out.write(("end\n").getBytes());
				reader.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(userName+":"+"end.....");
		
	}
	

}
