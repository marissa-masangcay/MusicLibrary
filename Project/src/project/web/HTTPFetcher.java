package project.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HTTPFetcher {
	
	public static int PORT = 80;


	public static JSONObject download(String host, String path) {
		
		StringBuffer buf = new StringBuffer();
		String JSONObject = null;
		JSONObject parsedObject = new JSONObject();
		
		try (
				Socket socket = new Socket(host, PORT); //create a connection to the web server
				OutputStream outputStream = socket.getOutputStream(); //get the output stream from socket
				InputStream inputStream = socket.getInputStream(); //get the input stream from socket
				//wrap the input stream to make it easier to read from
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
		) { 

			//send request
			String request = getRequest(host, path);
			outputStream.write(request.getBytes());
			outputStream.flush();

			//receive response
			String line = reader.readLine();
			String[] firstLine = line.split(" ");
			
			if(!firstLine[1].equals("200") && firstLine[2].equals("OK")){
				System.err.println("Response status not OK");
			}
	
			while(line != null) {
				if(line.isEmpty()){
					JSONObject = reader.readLine();
					break;
				}
				buf.append(line + "\n"); //append the newline stripped by readline
				line = reader.readLine();
			}
			
			JSONParser parser = new JSONParser();
			System.out.println("parsedObject"+parsedObject);
			parsedObject = (JSONObject) parser.parse(JSONObject);
			

		} catch (IOException e) {
			System.out.println("HTTPFetcher::download " + e.getMessage());
		} catch (ParseException e) {
			System.err.println("Problem parsing into JSONObject");
			e.printStackTrace();
		}

		return parsedObject;
	}
	
	
	
	private static String getRequest(String host, String path) {
		String request = "GET " + path + " HTTP/1.1" + "\n" //GET request
				+ "Host: " + host + "\n" //Host header required for HTTP/1.1
				+ "Connection: close\n" //make sure the server closes the connection after we fetch one page
				+ "\r\n";								
		return request;
	}

}
