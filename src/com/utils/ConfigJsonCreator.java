package com.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.procedureExecutor.Configuration;

public class ConfigJsonCreator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Gson gson = new GsonBuilder().setPrettyPrinting().create();		
		Configuration config = new Configuration();
		String json = gson.toJson(config);
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("config.json"));
			bw.write(json);
			System.out.println(" archivo config.json creado");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}
